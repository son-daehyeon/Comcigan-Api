# ComciganApi
[![](https://jitpack.io/v/ioloolo/ComciganApi.svg)](https://jitpack.io/#ioloolo/ComciganApi)

자바에서 [컴시간 알리미](http://xn--s39aj90b0nb2xw6xh.kr/)의 시간표를 파싱하기 위한 라이브러리입니다.

타 라이브러리와 다르게 웹 소스를 기반으로 데이터를 불러오지 않고 JSON을 통해 데이터를 파싱하기 때문에 빠르고 안정적입니다.

## 의존성
### Maven
```maven
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.son-daehyeon</groupId>
    <artifactId>ComciganApi</artifactId>
    <version>2.0.0</version>
</dependency>
```
### Gradle
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.son-daehyeon:ComciganApi:2.0.0'
}
```

## API
아래 코드에서 사용중인 `ComciganApi`는 학생용 API 사용시 `ComciganStudentApi`, 교사용 API 사용시 `ComciganTeacherApi` 사용해주세요.



### 학교 검색
```java
ComciganApi.searchSchool(String query)
```
```
ComciganApi.searchSchool("서울")

[
    School(code=20449, name=서울대학교사범대학부설중학교, location=서울),
    School(code=67524, name=서울문화고등학교, location=서울),
    ...
]
```

### 일과 시간
```java
ComciganApi.getRange(int code)
```
```
ComciganApi.searchSchool(20449)

{
    1=08:35,
    2=09:25,
    ...
}
```

### 학생용
#### 시간표
```java
ComciganApi.getWeeklyTimeTable(int code, int grade, int clazz)
```
```
ComciganApi.getWeeklyTimeTable(20449, 1, 1)

[
    DailyTimeTable(dayOfWeek=MONDAY, timeTable=[
        PeriodTimeTable(period=1, lecture=국어, teacher=***, isModify=false, original=null),
        ...
    ]),
    ...
]
```

### 교사용
#### 교사 목록
```java
ComciganTeacherApi.getTeacherList(int code)
```
```
ComciganTeacherApi.getTeacherList(20449)

[
    Teacher(id=1, name=***),
    Teacher(id=2, name=***),
    ...
]
```

#### 시간표
```java
ComciganApi.getWeeklyTimeTable(int code, int id)
```
```
ComciganApi.getWeeklyTimeTable(20449, 1)

[
    DailyTimeTable(dayOfWeek=MONDAY, timeTable=[
        PeriodTimeTable(period=1, lecture=국어, teacher=***, isModify=false, original=null),
        ...
    ]),
    ...
]
```
