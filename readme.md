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
> includeBuild 를 통해 디렉토리를 지정하는 대신 /buildSrc 를 만들어서 gradle plugin을 작성한다면 gradle compile 시 자동으로 buildSrc 를 읽어서 컴파일한다.

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

## 빌드 캐시
- gradle.properties 에 `org.gradle.caching = true` 를 설정하여 빌드 캐시 할수 있다
- 컴파일 대상 task 의 내용이 기존과 같은것이 있다면 캐시된 컴파일 결과를 가져온다.
- 캐시에서 task 결과를 가져올 경우 결과
- 캐시 내용은 사용자 홈디렉토리의 .gradle/ 에 저장된다.
```shell
> Task :app:compileJava FROM-CACHE
```
> 앱 코드를 변경하면 캐시된 내용을 가져오는것이 아닌 변경된 코드를 새로 컴파일한다.

## Tasks
###  tasks 에 세부 내용 설정 
```kotlin
tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.compileTestJava {
    options.encoding = "UTF-8"
}
```
- complieJava, compileTestJava 모두 반환 타입이 JavaCompile 이기 때문에 아래처럼 한번에 추가 가능하다
```kotlin
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
```
or 
```kotlin
tasks.named<JavaCompile>("compileJava") {
    // ...    
}
```
### task 등록
- 빌드 결과를 압축하는 task 
- 이미 gradle 에서 제공하는 기능
```kotlin
// tasks.register<{출력타입}>({이름})

tasks.register<Zip>("bundle") {
    group = "My group"
    description = "packages it all!"

    from(tasks.jar) // jar task 의 결과물을 받음
    from(configurations.runtimeClasspath)

    destinationDirectory.set(layout.buildDirectory.dir("distribution"))
}
```


![register_tasks.png](assests%2Fregister_tasks.png)

<*등록된 task my group-bundle*>

./gradlew :app:tasks 결과
```shell
tasks - Displays the tasks runnable from project ':app'.

My group tasks
--------------
bundle - packages it all!

Verification tasks
------------------
check - Runs all checks.
``` 

#### 실행
```shell
./gradlew :app:bundle --console=plain
```
##### 결과
```shell
> Task :data-model:classes
> Task :data-model:jar UP-TO-DATE
> Task :app:bundle

BUILD SUCCESSFUL in 4s
17 actionable tasks: 5 executed, 12 up-to-date
```
![bundle-task-resule.png](assests%2Fbundle-task-resule.png)

*<압축파일 생성>*

### gradle 에서 제공하지 않는 기능 task 추가
1. gradle 에 task 추가 
```kotlin
tasks.register<JarCount>("countJars") {
    group = "My Group"
    description = "Count Jar count"

    allJars.from(tasks.jar)
    allJars.from(configurations.runtimeClasspath)

    countFile.set(layout.buildDirectory.file("gen/count.txt"))
}
```
2. JarCount 라는 task 생성 
```java
package com.example.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Set;

public abstract class JarCount extends DefaultTask {

    // ConfigurableFileCollection <- @InputFiles

    // RegularFileProperty <- @OutputFile

    // DirectoryProperty <- @InputDirectory

    // task 의 입력
    @InputFiles
    public abstract ConfigurableFileCollection getAllJars();

    // task 의 출력
    @OutputFile
    public abstract RegularFileProperty getCountFile();

    // task 작업 구현 함수 @taskAction
    @TaskAction
    public void doCount() throws IOException {
        Set<File> jarFiles = getAllJars().getFiles();
        int count = jarFiles.size();
        File out = getCountFile().get().getAsFile();
        Files.write(out.toPath(), Collections.singleton("" + count));
    }
}

```

### 라이프 사이클 tasks
- 아무런 작업없이 task 들을 수행한다. 
- 아래 task는 defaultTask 타입을 갖는다

#### 라이프 사이클 task 생성
```kotlin
// 라이프 사이클 task 생성
tasks.register("buildAll") {
    description = "Build even more!"

    dependsOn(tasks.build)
    dependsOn(tasks.named("countJars"))
}
```

#### base 플로그인의 라이프 사이클 작업들
- base 플로그인 추가
```kotlin
id("base")
```
##### build
- 빌드 
##### assemble
- 코드의 컴파일과 패키징만 수행
##### check
- 테스트와 코드 품질 검사

#### build 에 task 추가
```kotlin
tasks.build {
    dependsOn(tasks.named("bundle"))
}
```

## Dependencies  
### Dependency scopes (configurations)
#### runtimeOnly 
- 런타임에만 존재하는 의존성
```kotlin
//    runtimeOnly("group:name")
dependencies {
    implementation(project(":data-model"))
    implementation(project(":business-logic"))

    runtimeOnly("org.slf4j:slf4j-simple:1.7.36")
}
```

#### compileOnly
- 컴파일 단계에서만 존재하는 의존성
```kotlin
dependencies {
    compileOnly("group:name")    
}
```

#### API
- app 이란 플젝이 business-logic 이란 프로젝트를 의존할 경우,
- business-logic 에서 A란 의존성을 가질때
- api("A") 를 하면 app에서는 A 의존성의 기능을 호출 가능하다
```kotlin
dependencies {
    api("org.apache.commons:commons-lang3:3.12.0")
}
```
- 하지만 api 를 사용하지 않고 implementation 을 통해 명시적으로 의존성을 추가 하는것을 추천한다.

