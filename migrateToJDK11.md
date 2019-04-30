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

#### Note 
you may got error when has <font color="red">inner class in test case</font>:
```
    Caused by: java.lang.UnsupportedOperationException

            at org.objectweb.asm.ClassVisitor.visitNestMemberExperimental(ClassVisitor.java:248)

            at org.objectweb.asm.ClassReader.accept(ClassReader.java:651)

            at org.objectweb.asm.ClassReader.accept(ClassReader.java:391)

            at org.gradle.api.internal.tasks.testing.detection.AbstractTestFrameworkDetector.classVisitor(AbstractTestFrameworkDetector.java:124)

            ... 66 more
```

```
sourceCompatibility = JavaVersion.VERSION_1_10
targetCompatibility = JavaVersion.VERSION_1_10
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

### 5. leftShift grammar is not support
When you found below error:
```
 Could not find method leftShift() for arguments [build_d5u7w4cjojjb7weuzumne60s2$_run_closure10@5655f44e] on task ':createJavaProject' of type org.gradle.api.DefaultTask.

```
you have to replace it with `doLast{}` block. check our build.gradle


## findbugs must be replaced with spotbugs
with version >= 1.7.1
```
    dependencies {
            classpath "gradle.plugin.com.github.spotbugs:spotbugs-gradle-plugin:1.7.1"
            classpath "com.google.guava:guava:27.0.1-jre"
    }
    
    
    // at top level of build.gradle
    apply plugin: "com.github.spotbugs"

```
Spotbugs now has a [bug](https://github.com/spotbugs/spotbugs/issues/493) in try-with resource grammar.


# How to build jre base on jdk
Here we need to use [jlink](https://docs.oracle.com/javase/9/tools/jlink.htm) to build jre.<br>
and we used the [AMAZON JDK](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)<br>
The command will like below:
```
/bin/jlink --module-path amazon-corretto-11.0.3.7.1-linux-x64/jmods/ --compress=2 --no-header-files --output generated  --add-modules jdk.zipfs,jdk.xml.dom,jdk.unsupported,jdk.unsupported.desktop,jdk.security.jgss,jdk.security.auth,jdk.sctp,jdk.scripting.nashorn.shell,jdk.scripting.nashorn,jdk.rmic,jdk.pack,jdk.net,jdk.naming.rmi,jdk.naming.dns,jdk.management,jdk.management.jfr,jdk.management.agent,jdk.localedata,jdk.jstatd,jdk.jsobject,jdk.jshell,jdk.jlink,jdk.jfr,jdk.jdwp.agent,jdk.jdi,jdk.jdeps,jdk.jconsole,jdk.jcmd,jdk.javadoc,jdk.jartool,jdk.internal.vm.compiler.management,jdk.internal.vm.compiler,jdk.internal.vm.ci,jdk.internal.opt,jdk.internal.le,jdk.internal.jvmstat,jdk.internal.ed,jdk.httpserver,jdk.hotspot.agent,jdk.editpad,jdk.dynalink,jdk.crypto.ec,jdk.crypto.cryptoki,jdk.compiler,jdk.charsets,jdk.attach,jdk.aot,jdk.accessibility,java.xml,java.xml.crypto,java.transaction.xa,java.sql.rowset,java.sql,java.smartcardio,java.se,java.security.sasl,java.security.jgss,java.scripting,java.rmi,java.prefs,java.net.http,java.naming,java.management.rmi,java.management,java.logging,java.instrument,java.desktop,java.datatransfer,java.compiler,java.base,

```
I uploaded a shell to help to use to build jre base on amazon jdk.
[prepareLinuxJre](prepareLinuxJre.sh)



## references links

1. [JDK11 release notes](https://www.oracle.com/technetwork/java/javase/11-relnote-issues-5012449.html)
2. [JDK removed modules](http://openjdk.java.net/jeps/320)