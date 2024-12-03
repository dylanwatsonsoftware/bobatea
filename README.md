# Boba Tea - Kotlin TUI

<p>
    <a href="https://github.com/dylanwatsonsoftware/bobatea"><img src="https://raw.githubusercontent.com/dylanwatsonsoftware/bobatea/refs/heads/main/bobatea.png" width="400" alt="Boba Tea"/></a><br/>
    <a href="https://github.com/dylanwatsonsoftware/bobatea/actions"><img src="https://github.com/dylanwatsonsoftware/bobatea/actions/workflows/build.yml/badge.svg" alt="Build Status"/></a>
    <a href="https://jitpack.io/#dylanwatsonsoftware/bobatea"><img src="https://jitpack.io/v/dylanwatsonsoftware/bobatea.svg" alt="JitPack"/></a>
    <a href="https://docs.oracle.com/javase/8/"><img src="https://img.shields.io/badge/java-11+-informational" alt="Java"/></a>
</p>

# Examples
The full code for the examples can be viewed [here](https://github.com/dylanwatsonsoftware/bobatea/blob/main/sample/src/main/kotlin/com/example/App.kt)

### Loading
<img src="https://raw.githubusercontent.com/dylanwatsonsoftware/bobatea/refs/heads/main/images/loading.gif" /><br/>
```kotlin
runLoading("Loading yo!") {
    Thread.sleep(10000)
}
```

### Multi-select
<img src="https://raw.githubusercontent.com/dylanwatsonsoftware/bobatea/refs/heads/main/images/multiselect.gif" /><br/>
```kotlin
val multiSelections = selectMultipleFromList(
    question = "What are all your favourite numbers?",
    options =
    listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"),
)
println("You selected: $multiSelections")
```
### Single Select
<img src="https://raw.githubusercontent.com/dylanwatsonsoftware/bobatea/refs/heads/main/images/singleselect.gif" /><br/>
```kotlin
val selection = selectFromList(
    question = "What's your favourite number?",
    options =
    listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"),
)
println("You selected: $selection")
```

### Co-ordinate Selection
This simply shows how easy it easy to create custom navigation with the bobatea framework

<img src="https://raw.githubusercontent.com/dylanwatsonsoftware/bobatea/refs/heads/main/images/coords.gif" /><br/>
View the code for this example [here](https://github.com/dylanwatsonsoftware/bobatea/blob/main/sample/src/main/kotlin/com/example/App.kt#L65-L103)

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

# Sample

To run the sample:
```shell
./gradlew -p sample fatJar
java -jar ./sample/build/libs/sample-0.1-standalone.jar
```

## Usage

Work in progress.
