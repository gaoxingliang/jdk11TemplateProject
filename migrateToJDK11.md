# Guide to migrate to JDK 11
In this page, we will describe how to migrate code to latest jdk 11.
<br>
More details please refer to the build.gradle

# Gradle related
## more than one jdks
if you exists more than one jdk, set it in your ~/.gradle/gradle.properties
```
org.gradle.java.home=/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home
```

## build.gradle
### 1.remove empty runtime block.
```
runtime {
    // remove me
}
```
### 2. set the source & target level
```
sourceCompatibility = JavaVersion.VERSION_11
targetCompatibility = JavaVersion.VERSION_11
```

### 3. Export some optional old depracated api
if you used some apis like jdk.internal.misc.SharedSecrets
```

compileJava {
    options.compilerArgs += ["--add-exports=java.management/sun.management=ALL-UNNAMED"]
    options.compilerArgs += ["--add-exports=java.base/jdk.internal.misc=ALL-UNNAMED"]
}

compileTestJava {
    options.compilerArgs += ["--add-exports=java.management/sun.management=ALL-UNNAMED"]
}

```

### 4. add the options into test tasks if used

if the tests also contains this, add the options in the task:
```
test {
    //....
    // set JVM arguments for the test JVM(s)
    jvmArgs '-noverify'
    jvmArgs "--add-exports=java.management/sun.management=ALL-UNNAMED"
    ...
}
```

## findbugs must be replaced with spotbugs
with version >= 1.6.4
```
    dependencies {
        classpath "gradle.plugin.com.github.spotbugs:spotbugs-gradle-plugin:1.6.4"
    }
    
    
    // at top level of build.gradle
    apply plugin: "com.github.spotbugs"

```
Spotbugs now has a [bug](https://github.com/spotbugs/spotbugs/issues/493) in try-with resource grammar.

## references links

1. [JDK11 release notes](https://www.oracle.com/technetwork/java/javase/11-relnote-issues-5012449.html)
2. [JDK removed modules](http://openjdk.java.net/jeps/320)