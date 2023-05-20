# Comcigan Api
자바에서 [컴시간 알리미](http://xn--s39aj90b0nb2xw6xh.kr/)의 시간표를 파싱하기 위한 라이브러리입니다.

타 라이브러리와 다르게 웹 소스를 기반으로 데이터를 불러오지 않고 JSON을 통해 데이터를 파싱하기 때문에 빠르고 안정적입니다.

## 사용법
### Gradle
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.0x00000FF:comcigan-api:1.0.0'
}
```

### Api 객체
```java
ComciganApi comciganApi = new ComciganApi();
```

### 학교 검색
```java
List<School> schools = comciganApi.searchSchool("검색어");
comciganApi.setSchool(schools.get(0));
```
```java
comciganApi.setSchool("학교명");
```

### 시간표 파싱
#### 특정 교시
```java
/**
 * @param grade 학년
 * @param clazz 반
 * @param dow 요일
 * @param period 교시
 */
Optional<PeriodTimeTable> timeTable = comciganApi.getPeriodTimeTable(int grade, int clazz, DayOfWeek dow, int period);
```
#### 일간
```java
/**
 * @param grade 학년
 * @param clazz 반
 * @param dow 요일
 */
List<PeriodTimeTable> timeTable = comciganApi.getDailyTimeTable(int grade, int clazz, DayOfWeek dow);
```
#### 주간
```java
/**
 * @param grade 학년
 * @param clazz 반
 */
Map<DayOfWeek, List<PeriodTimeTable>> timeTable = comciganApi.getWeeklyTimeTable(int grade, int clazz);
```
