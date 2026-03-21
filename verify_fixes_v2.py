import asyncio
from playwright.async_api import async_playwright
import os
import time

async def verify_mordant_fixes():
    async with async_playwright() as p:
        browser = await p.chromium.launch()
        page = await browser.new_page(viewport={'width': 1200, 'height': 800})

        print("Starting WASM dev server...")
        process = await asyncio.create_subprocess_shell(
            "./gradlew :web:wasmJsBrowserDevelopmentRun --no-daemon",
            stdout=asyncio.subprocess.PIPE,
            stderr=asyncio.subprocess.PIPE
        )

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

        await asyncio.sleep(8)

        # Go to Selection Lists (5th option)
        # ❯ Layout Showcase
        #   Mordant
        #   Interactive Expandable
        #   Coordinate Explorer
        #   Selection Lists
        for _ in range(4):
            await page.keyboard.press("ArrowDown")
            await asyncio.sleep(0.3)

        await page.keyboard.press("Enter")
        await asyncio.sleep(2)

        await page.screenshot(path="verification_selection_screen.png")
        print("Selection screen screenshot saved.")

        # Test back button (clicking it)
        # Selection list should show:
        # Question (1)
        # Milk Tea
        # Matcha
        # Taro
        # Brown Sugar
        # Go Back
        # (blank)
        # UP/DOWN
        # SPACE/ENTER
        # Total ~10 lines of content.
        # Back button is rendered after it.
        # Let's try to click multiple spots where it might be.
        # Line height in xterm.js with current font might be around 18-20px.
        # Vertical offset roughly 10-12 lines down.
        # xterm.js padding is usually 10-20px.

        # Just use keyboard 'q' for one check, and mouse for another if we can.
        # But user wants to see it CLICKABLE.
        # Let's try to click around line 13.
        for y_offset in range(350, 500, 20):
            await page.mouse.click(100, y_offset)
            await asyncio.sleep(1)
            # Check if we are back at menu (question should change)
            # If we are back, the screenshot will show the menu.
            await page.screenshot(path=f"verification_click_y_{y_offset}.png")

        process.terminate()
        await browser.close()

if __name__ == "__main__":
    asyncio.run(verify_mordant_fixes())
