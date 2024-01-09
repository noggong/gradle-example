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

### dependencies 제약 조건
```kotlin
dependencies {
    // 버전 명시 안함
    implementation("org.apache.commons:commons-lang3") 
}
dependencies.constraints {
    // 의존성 추가처럼 보이지만 실제 추가가 아니고 단지 사용할 버전 추가임
    implementation("org.apache.commons:commons-lang3:3.12.0")
}
```
### dependencies version 중앙 관리
- 좋은 프로젝트는 같은 종류의 의존성은 동일한 버전을 가져야 하며, 중앙에서 관리해야한다.
- 버전을 중앙관리 하는 방법은 두 가지 방법이 있다. 두 가지 중 편한것을 사용하면 된다. 
  - 플랫폼 프로젝트
  - 종속적 버전 카탈로그
#### 플랫폼 프로젝트
1. /gradle/plugins 를 생성하고 build.gradle.kts 를 생성한다.
2. 아래 처럼 작성한다
```kotlin
plugins {
    // 플랫폼 방식의 버전 중앙 관리
    // 의존성 제약 조건과 의존성만 정의 한다 그 외에는 java-library 와 같음
    // jar 같은 것이 포함되어있지 않음
    id("java-platform")
}

// 외부에서 해당 플랫폼의 의존성 제약을 받고 싶을때 사용하는 그룹이름
group = "com.example"

javaPlatform.allowDependencies()
dependencies {
    // "com.fasterxml.jackson:jackson-bom:2.13.3" 의 platform 제약조건을 상속받을수 있다.
    api(platform("com.fasterxml.jackson:jackson-bom:2.13.3"))
}

// 플랫폼 방식의 버전 중앙 관리 (의존성 제약조건)
dependencies.constraints {
    // 의존성 추가처럼 보이지만 실제 추가가 아니고 단지 사용할 버전 추가임
    api("org.apache.commons:commons-lang3:3.12.0")
    api("org.slf4j:slf4j-api:1.7.36")
    api("org.slf4j:slf4j-simple1.7.36")
}
```
3. 플랫폼 의존성 제약조건을 각 원하는 프로젝트 build.gradle.kts 에 삽입한다.
```kotlin
dependencies {
    implementation(platform("com.example:platform"))
    implementation(project(":data-model"))
    implementation(project(":business-logic"))
    runtimeOnly("org.slf4j:slf4j-simple")
}
```
#### 종속적 버전 카탈로그
1. /gradle/libs.version.toml 추가한다 (gradle 이 자동으로 이를 선택하고 확인한다.)
2. libs.version.toml 에 의존성을 (`별칭 = 그룹:패키지:버전`) 작성한다. 
```
[libraries]
commons-lang = "org.apache.commons:commons-lang3:3.12.0"
slf4j-api = "org.slf4j:slf4j-api:1.7.36"
slf4j-simple = "org.slf4j:slf4j-simple:1.7.36"
```
4. 별치의 dash (-) 를 점(.) 으로 변경하여 의존성을 추가한다.
```kotlin
dependencies {
    implementation(project(":data-model"))
    implementation(libs.slf4j.api)
    implementation(libs.commons.lang)
}
```
5. 중앙 플랫폼식을 사용하면 종속적 버전 카탈로그를 사용 할 수 없다.

### 의존성의 전이적 의존성에 의한 버전 충돌
- 의존성내의 의존성이 전이적 의존성 api("package:higher-version") 을 사용한다면 내가 설정한 버전보다 높은 버전의 jar를 받을수 있음
- 이럴때 의존성에 제약을 걸수 있다
```kotlin
api("dependency") {
    version {
        strictyly("...")
        reject("...")
    }
}
```
- 그전에 의존성들이 어떤 의족성을 가지고 있는지 확인이 중요하다
```bash
./gradlew :app:dependencies --configuration runtimeClasspath
```
- 결과
```shell
runtimeClasspath - Runtime classpath of source set 'main'.
+--- project :data-model
|    \--- com.example:platform -> project :platform
|         +--- com.fasterxml.jackson:jackson-bom:2.13.3
|         +--- org.apache.commons:commons-lang3:3.12.0 (c)
|         \--- org.slf4j:slf4j-api:1.7.36 (c)
+--- project :business-logic
|    +--- project :data-model (*)
|    +--- org.slf4j:slf4j-api:1.7.36
|    \--- org.apache.commons:commons-lang3:3.12.0
\--- org.slf4j:slf4j-simple:1.7.36
     \--- org.slf4j:slf4j-api:1.7.36

(c) - dependency constraint
(*) - dependencies omitted (listed previously)

A web-based, searchable dependency report is available by adding the --scan option.

BUILD SUCCESSFUL in 1m 7s
```
#### 메타데이터 규칙 추니
##### slf4j-simple 내 slf4j-api 의존성을 삭제 
1. 의존성 rule 기입
```kotlin
dependencies.components {
    withModule<Slf4jSimpleRule>("org.slf4j:slf4j-simple")
}
```
2. 의존성 rule 생성 (/gradle/plugins/java-plugins/src/main/java/com/example/gradle/Slf4jSimpleRule)
3. 아래 내용 작성
```java
package com.example.gradle;

import org.gradle.api.artifacts.CacheableRule;
import org.gradle.api.artifacts.ComponentMetadataContext;
import org.gradle.api.artifacts.ComponentMetadataRule;

@CacheableRule
public class Slf4jSimpleRule implements ComponentMetadataRule {
    @Override
    public void execute(ComponentMetadataContext context) {
        context.getDetails().allVariants(v ->
                v.withDependencies(d -> d.removeIf(gav -> gav.getName().equals("slf4j-api"))));
    }
}

```
4. 결과 - org.slf4j:slf4j-simple 에서 slf4j-api 의존성이 없어짐을 확인   
```shell
+--- project :data-model
|    \--- com.example:platform -> project :platform
|         +--- com.fasterxml.jackson:jackson-bom:2.13.3
|         +--- org.apache.commons:commons-lang3:3.12.0 (c)
|         \--- org.slf4j:slf4j-api:1.7.36 (c)
+--- project :business-logic
|    +--- project :data-model (*)
|    +--- org.slf4j:slf4j-api:1.7.36
|    \--- org.apache.commons:commons-lang3:3.12.0
\--- org.slf4j:slf4j-simple:1.7.36

(c) - dependency constraint
(*) - dependencies omitted (listed previously)

```
## Test
> Source set ? 디렉토리와 파일의 위치를 명시하는 개념 - 기본 : src/main/{java|kotlin}/
> 이것은 명확하기때문에 intelij 에서도 인지 하고 디렉토리 추가시 아래처럼 source set 을 선책할수 있다
> 
> ![sourceset.png](assests%2Fsourceset.png)
>
> 기본 source set 의 위치를 변경 할수 있다
> 
> sourceSets.main {
> 
>   java.setSrcDirs(listOf(layout.projectDirectory.dir("sources)))
> 
> }
- test source set : /src/test/{java|kotlin}
### 통합 테스트 source set 추가
```kotlin
sourceSets.create("integrationTest")
```
- sourceSet 을 생성하면 각 sourceSet별 의존성을 추가할수 있다
```kotlin
integrationTestImplementation("org.junit.jupiter:junit-jupiter-api")
```

### custom test task 추가
- integrationTest 테스트 태그 추가
```kotlin
sourceSets.create("integrationTest")

tasks.register<Test>("integrationTest") {
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath

    useJUnitPlatform()
}
```