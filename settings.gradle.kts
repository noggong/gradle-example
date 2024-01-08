// gradle project 의 진입점

// gradle plugin
pluginManagement {
    repositories.gradlePluginPortal()
//    repositories.google()
//
//    repositories.maven("https://my.location/repo") {
//        // 저장소 자격증명도 가능하다.
//        credentials.username = "..."
//        credentials.password = "..."
//    }
//
    includeBuild("gradle/plugins")
}

// 의존성이 존재 하는 저장소 정의
dependencyResolutionManagement {
//    repositories.mavenCentral()
//    repositories.google()
//
//    // 개인 저장소
//    repositories.maven("https://my.location/repo") {
//        // 저장소 자격증명도 가능하다.
//        credentials.username = "..."
//        credentials.password = "..."
//    }
//
//    // gradle 이 또다른 빌드 할것 있음을 작성, 패치가 필요한 경우 아래처럼 사
//    includeBuild("../my-other-project")
}

// 프로젝트 이름 (없으면 폴터이름으로 셋팅)
rootProject.name = "my-project"

// gradle 은 하위 프로젝트를 가질수 있다.
include("app") // app
include("business-logic") // business-logic
include("data-model") // data-model
