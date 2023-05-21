package com.github.ioloolo.comcigan;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.ioloolo.comcigan.data.School;
import com.github.ioloolo.comcigan.data.timetable.OriginalTimeTable;
import com.github.ioloolo.comcigan.data.timetable.PeriodTimeTable;
import com.github.ioloolo.comcigan.util.Base64;
import com.github.ioloolo.comcigan.util.ComciganRequest;
import com.github.ioloolo.comcigan.util.EucKr;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class ComciganApi {

    private School school;
    private JsonObject comciganJson;

    /**
     * 주어진 학교 이름으로 학교를 검색합니다.
     *
     * @param name 검색할 학교의 이름
     * @return 검색된 학교 리스트
     * @throws IOException 요청 처리 중 오류 발생 시
     */
    public List<School> searchSchool(String name) throws IOException {
        return ComciganRequest.request("36179?17384l" + EucKr.convert(name))
                .map(jsonObject -> jsonObject
                        .getAsJsonArray("학교검색")
                        .asList()
                        .stream()
                        .map(JsonElement::getAsJsonArray)
                        .map(jsonArray -> {
                            int code = jsonArray.get(3).getAsInt();
                            String school = jsonArray.get(2).getAsString();
                            String location = jsonArray.get(1).getAsString();

                            return School.of(code, school, location);
                        })
                        .filter(school -> school.getCode() != 0)
                        .toList())
                .orElse(Collections.emptyList());
    }

    /**
     * 학교를 설정합니다. 설정된 학교의 시간표를 가져오는 데 사용됩니다.
     *
     * @param school 설정할 학교
     * @throws IOException 요청 처리 중 오류 발생 시
     */
    public void setSchool(School school) throws IOException {
        this.school = school;
        fetchComciganJson();
    }

    /**
     * 주어진 학교 이름으로 학교를 설정합니다.
     *
     * @param schoolName 설정할 학교의 이름
     * @throws IOException 요청 처리 중 오류 발생 시
     */
    public void setSchool(String schoolName) throws IOException {
        this.school = searchSchool(schoolName).get(0);
        fetchComciganJson();
    }

    private void fetchComciganJson() throws IOException {
        String subUrl = "36179?" + Base64.encode("73629_%d_0_1".formatted(school.getCode()));
        Optional<JsonObject> request = ComciganRequest.request(subUrl);

        this.comciganJson = request
                .map(JsonElement::getAsJsonObject)
                .orElseThrow();
    }

    /**
     * 특정 학년, 반의 주간 시간표를 가져옵니다.
     *
     * @param grade 학년
     * @param clazz 반
     * @return 주간 시간표
     */
    public Map<DayOfWeek, List<PeriodTimeTable>> getWeeklyTimeTable(int grade, int clazz) {
        return new LinkedHashMap<>() {{
            EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY).forEach(dow -> put(dow, getDailyTimeTable(grade, clazz, dow)));
        }};
    }

    /**
     * 특정 학년, 반, 요일의 일간 시간표를 가져옵니다.
     *
     * @param grade 학년
     * @param clazz 반
     * @param dow 요일
     * @return 일간 시간표
     */
    public List<PeriodTimeTable> getDailyTimeTable(int grade, int clazz, DayOfWeek dow) {
        return new ArrayList<>() {{
            int period = 1;
            Optional<PeriodTimeTable> timeTable;

            while ((timeTable = getPeriodTimeTable(grade, clazz, dow, period++)).isPresent())
                add(timeTable.get());
        }};
    }

    /**
     * 특정 학년, 반, 요일, 교시의 시간표를 가져옵니다.
     *
     * @param grade 학년
     * @param clazz 반
     * @param dow 요일
     * @param period 교시
     * @return 해당 교시의 시간표 (Optional)
     */
    public Optional<PeriodTimeTable> getPeriodTimeTable(int grade, int clazz, DayOfWeek dow, int period) {
        PeriodTimeTable todayTable = parsePeriodTimeTable(grade, clazz, dow, PeriodTimeTable.TimeTableType.TODAY, period).orElse(null);
        PeriodTimeTable originalTable = parsePeriodTimeTable(grade, clazz, dow, PeriodTimeTable.TimeTableType.ORIGINAL, period).orElse(null);

        if (todayTable == null || originalTable == null)
            return Optional.empty();

        if (!todayTable.equals(originalTable)) {
            todayTable.setModify(true);
            todayTable.setOriginal(OriginalTimeTable.from(originalTable));
        }

        return Optional.of(todayTable);
    }

    private Optional<PeriodTimeTable> parsePeriodTimeTable(int grade, int clazz, DayOfWeek dow, PeriodTimeTable.TimeTableType type, int period) {
        JsonArray data = comciganJson
                .get(type.getJsonKey()).getAsJsonArray()
                .get(grade).getAsJsonArray()
                .get(clazz).getAsJsonArray()
                .get(dow.getValue()).getAsJsonArray();

        if (period >= data.size())
            return Optional.empty();

        int index = data.get(period).getAsInt();

        int th = index / 100;
        int sb = index - (th * 100);

        String lecture = comciganJson
                .get("자료492").getAsJsonArray()
                .get(sb).getAsString();

        String teacher = comciganJson
                .get("자료446").getAsJsonArray()
                .get(th).getAsString();

        return Optional.of(PeriodTimeTable.of(period, lecture, teacher));
    }
}
