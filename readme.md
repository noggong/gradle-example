## Plugins 
- 클린 빌드 파일의 최상단에는 항상 plugin 블록이 있어야 한다.
- 특정 ID 를 가진 gradle plugin 은 프로젝트에서 제공하는 "유형" 으로 볼수 있다
- 다른 gradle 파일을 id 를 통해 불러올수 있다.
ex1)
```kotlin
plugins {
    id("java-library")
}
```
ex2) 
```kotlin
plugins {
    id("application")
}
```

### 빌드 구성 중앙 집중화
1. `/gradle/plugins/settings.gradle.kts` 생성
2. 1번에서 생성된 settings.gradle.kts 를 컴파일하기 위해 `/settings.gradle.kts` 에 `includeBuild("gradle/plugins")` 추가
3. /gradle/plugins/settings.gradle.kts 에서 /gradle/plugins/java-plugins 를 include
4. /gradle/plugins/java-plugins 내부에 각각 필요한 `{name}.gradle.kts` 를 통해 plugin 생성
5. 필요한 plugin 을 필요한 곳에서 include 

## Aplplication 실행
- application 블럭내 app 이 실행될 main class 를 지정한다
```agsl
application {
    // main class gradle 에 알려줌
    mainClass.set("com.example.MyApplication")
}
```
- idea 혹은 터미널에서 실행 가능하다
```shell
./gradlew :app:run --console=plain  
```

- run task 를 실행하면 필요한 task 를 gradle 이 나열하여 순차적으로 실행한다. 
```shell
## run 실행시 실행되는 task
> Task :plugins:java-plugins:generateExternalPluginSpecBuilders UP-TO-DATE
> Task :plugins:java-plugins:extractPrecompiledScriptPluginPlugins UP-TO-DATE
> Task :plugins:java-plugins:compilePluginsBlocks UP-TO-DATE
> Task :plugins:java-plugins:generatePrecompiledScriptPluginAccessors UP-TO-DATE
> Task :plugins:java-plugins:generateScriptPluginAdapters UP-TO-DATE
> Task :plugins:java-plugins:compileKotlin
> Task :plugins:java-plugins:compileJava NO-SOURCE
> Task :plugins:java-plugins:pluginDescriptors UP-TO-DATE
> Task :plugins:java-plugins:processResources UP-TO-DATE
> Task :plugins:java-plugins:classes UP-TO-DATE
> Task :plugins:java-plugins:inspectClassesForKotlinIC UP-TO-DATE
> Task :plugins:java-plugins:jar UP-TO-DATE
> Task :data-model:compileJava UP-TO-DATE
> Task :business-logic:compileJava UP-TO-DATE
> Task :app:compileJava UP-TO-DATE
> Task :app:processResources NO-SOURCE
> Task :app:classes UP-TO-DATE
> Task :business-logic:processResources NO-SOURCE
> Task :business-logic:classes UP-TO-DATE
> Task :business-logic:jar UP-TO-DATE
> Task :data-model:processResources NO-SOURCE
> Task :data-model:classes UP-TO-DATE
> Task :data-model:jar UP-TO-DATE
```
> UP-TO-DATE 는 이미 최신 상태이기에 실제 작업 수행은 하지 않는 task 이다

