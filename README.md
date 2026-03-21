# Boba Tea - Kotlin TUI

<p>
    <a href="https://github.com/dylanwatsonsoftware/bobatea"><img src="https://raw.githubusercontent.com/dylanwatsonsoftware/bobatea/refs/heads/main/bobatea.png" width="400" alt="Boba Tea"/></a><br/>
    <a href="https://github.com/dylanwatsonsoftware/bobatea/actions"><img src="https://github.com/dylanwatsonsoftware/bobatea/actions/workflows/build.yml/badge.svg" alt="Build Status"/></a>
    <a href="https://jitpack.io/#dylanwatsonsoftware/bobatea"><img src="https://jitpack.io/v/dylanwatsonsoftware/bobatea.svg" alt="JitPack"/></a>
    <a href="https://docs.oracle.com/javase/11/"><img src="https://img.shields.io/badge/Java-11+-informational?logo=openjdk&amp;logoColor=white" alt="Java"/></a>
</p>

## Download

```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.dylanwatsonsoftware:bobatea:$version'
}
```

## Examples
The full code for the examples can be viewed [here](https://github.com/dylanwatsonsoftware/bobatea/blob/main/sample/src/main/kotlin/com/example/App.kt)

### Loading
<img src="https://raw.githubusercontent.com/dylanwatsonsoftware/bobatea/refs/heads/main/images/loading.gif" /><br/>
```kotlin
runLoading("Loading yo!") {
    Thread.sleep(10000)
}

// With styling
runLoading(
    message = "Boxed Loading!",
    borderStyle = BorderStyle.DOUBLE,
    padding = 1,
    color = GREEN
) {
    Thread.sleep(5000)
}
```

### Multi-select
<img src="https://raw.githubusercontent.com/dylanwatsonsoftware/bobatea/refs/heads/main/images/multiselect.gif" /><br/>
```kotlin
val multiSelections = selectMultipleFromList(
    question = "What are all your favourite numbers?",
    options = listOf("one", "two", "three"),
    borderStyle = BorderStyle.SINGLE,
    padding = 1
)
println("You selected: $multiSelections")
```

### Single Select
<img src="https://raw.githubusercontent.com/dylanwatsonsoftware/bobatea/refs/heads/main/images/singleselect.gif" /><br/>
```kotlin
val selection = selectFromList(
    question = "What's your favourite number?",
    options = listOf("one", "two", "three"),
    borderStyle = BorderStyle.ROUNDED,
    color = YELLOW
)
println("You selected: $selection")
```

### Click to Expand
Create interactive components that expand when clicked or when the space/enter key is pressed. Supports hover effects and chevrons.

```kotlin
expandable(
    title = "Click me to see more!",
    content = "You expanded the section! Surprise!",
    borderStyle = BorderStyle.SINGLE,
    padding = 1
)
```

### Box Model Layout
Boba Tea provides a flexible box model to format your text in the terminal.

```kotlin
val box = Box(
    content = "Welcome to Boba Tea!",
    padding = 1,
    borderStyle = BorderStyle.ROUNDED,
    color = CYAN
)
println(box.render())
```

### Advanced Component Usage
All UI components inherit from `BobaComponent`, allowing for a consistent API.

```kotlin
val myComp = SelectionList(
    question = "Styled List",
    options = listOf("Option 1", "Option 2"),
    padding = 1,
    margin = 2,
    borderStyle = BorderStyle.DOUBLE,
    color = BLUE
)
val result = myComp.interact()
```

### Co-ordinate Selection
This simply shows how easy it easy to create custom navigation with the bobatea framework

<img src="https://raw.githubusercontent.com/dylanwatsonsoftware/bobatea/refs/heads/main/images/coords.gif" /><br/>
View the code for this example [here](https://github.com/dylanwatsonsoftware/bobatea/blob/main/sample/src/main/kotlin/com/example/App.kt)

## Sample

To run the sample:
```shell
./gradlew -p sample fatJar
java -jar ./sample/build/libs/sample-0.1-standalone.jar
```
