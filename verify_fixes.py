import asyncio
from playwright.async_api import async_playwright
import os
import time

async def verify_mordant_fixes():
    async with async_playwright() as p:
        browser = await p.chromium.launch()
        # Set a larger viewport to avoid wrapping
        page = await browser.new_page(viewport={'width': 1200, 'height': 800})

        # Build the project first to ensure we have the latest
        # We'll skip the build here assuming previous steps did it, or it will run in background

        print("Starting WASM dev server...")
        # Start server in background
        process = await asyncio.create_subprocess_shell(
            "./gradlew :web:wasmJsBrowserDevelopmentRun --no-daemon",
            stdout=asyncio.subprocess.PIPE,
            stderr=asyncio.subprocess.PIPE
        )

        # Wait for server to be ready
        max_retries = 30
        for i in range(max_retries):
            try:
                await page.goto("http://localhost:8080")
                print("Connected to WASM demo!")
                break
            except Exception:
                await asyncio.sleep(2)
                if i == max_retries - 1:
                    print("Could not connect to WASM demo")
                    process.terminate()
                    return

        # Wait for terminal to load and animations to finish
        await asyncio.sleep(8)

        # Take screenshot of the menu
        await page.screenshot(path="verification_fixes_menu.png")
        print("Menu screenshot saved.")

        # Navigate to Selection List screen to check for color bleeding
        # Options are: Layout Showcase, Mordant, Interactive Expandable, Coordinate Explorer, Selection Lists, Multi-Selection, Reset Demo
        # Selection Lists is 5th option.
        await page.keyboard.press("ArrowDown")
        await asyncio.sleep(0.5)
        await page.keyboard.press("ArrowDown")
        await asyncio.sleep(0.5)
        await page.keyboard.press("ArrowDown")
        await asyncio.sleep(0.5)
        await page.keyboard.press("ArrowDown")
        await asyncio.sleep(0.5)
        await page.keyboard.press("Enter")
        await asyncio.sleep(2)

        # Take screenshot of selection list
        await page.screenshot(path="verification_fixes_selection.png")
        print("Selection screen screenshot saved.")

        # Test back button (clicking it)
        # We'll use coordinate based click. Selection list has:
        # Title (1) + 5 options + blank (1) + instructions (2) + wrapInBox (padding 0)
        # Roughly line 10.
        await page.mouse.click(150, 450) # Coords are tricky in xterm.js via playwright pixels
        await asyncio.sleep(2)

        await page.screenshot(path="verification_fixes_after_click.png")
        print("After click screenshot saved.")

        process.terminate()
        await browser.close()

if __name__ == "__main__":
    asyncio.run(verify_mordant_fixes())
