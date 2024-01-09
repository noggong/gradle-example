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

