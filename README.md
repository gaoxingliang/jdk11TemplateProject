# jdk11TemplateProject
A template project using jdk 11

# Build it
```
./gradlew build
```

# Run
```
java --add-exports=java.base/jdk.internal.misc=ALL-UNNAMED --add-exports=java.management/sun.management=ALL-UNNAMED -cp build/libs/jdk11TemplateProject.jar TestInternalAPI
```

# how to migrate to jdk 11
[DOC](migrateToJDK11.md)

