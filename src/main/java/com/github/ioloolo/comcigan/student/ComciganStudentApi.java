package com.github.ioloolo.comcigan.student;

import com.github.ioloolo.comcigan.ComciganBaseApi;
import com.github.ioloolo.comcigan.student.timetable.DailyTimeTable;
import com.github.ioloolo.comcigan.student.timetable.PeriodTimeTable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public final class ComciganStudentApi extends ComciganBaseApi {

    public static Optional<PeriodTimeTable> getPeriodTimeTable(int code, int grade, int clazz, DayOfWeek dow, int period) {
        return getPeriodTimeTable(ComciganBaseApi.getComciganJson(code), grade, clazz, dow, period);
    }
    
    public static DailyTimeTable getDailyTimeTable(int code, int grade, int clazz, DayOfWeek dow) {
        return getDailyTimeTable(ComciganBaseApi.getComciganJson(code), grade, clazz, dow);
    }

    public static List<DailyTimeTable> getWeeklyTimeTable(int code, int grade, int clazz) {
        return getWeeklyTimeTable(ComciganBaseApi.getComciganJson(code), grade, clazz);
    }
    
    private static Optional<PeriodTimeTable> getPeriodTimeTable(JsonObject comciganJson, int grade, int clazz, DayOfWeek dow, int period) {
        PeriodTimeTable todayTable = parsePeriodTimeTable(comciganJson, grade, clazz, dow, TimeTableType.TODAY, period).orElse(null);
        PeriodTimeTable originalTable = parsePeriodTimeTable(comciganJson, grade, clazz, dow, TimeTableType.ORIGINAL, period).orElse(null);

        if (todayTable == null || originalTable == null)
            return Optional.empty();

        if (!todayTable.equals(originalTable)) {
            todayTable.setModify(true);
            todayTable.setOriginal(PeriodTimeTable.OriginalTimeTable.of(originalTable));
        }

        return Optional.of(todayTable);
    }

    private static DailyTimeTable getDailyTimeTable(JsonObject comciganJson, int grade, int clazz, DayOfWeek dow) {
        return DailyTimeTable.of(dow, new LinkedList<PeriodTimeTable>() {{
            Optional<PeriodTimeTable> timeTable;
            int period = 1;

            while ((timeTable = getPeriodTimeTable(comciganJson, grade, clazz, dow, period++)).isPresent()) {
                add(timeTable.get());
            }
        }});
    }

    private static List<DailyTimeTable> getWeeklyTimeTable(JsonObject comciganJson, int grade, int clazz) {
        return new LinkedList<DailyTimeTable>() {{
            for (DayOfWeek dow : EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)) {
                add(getDailyTimeTable(comciganJson, grade, clazz, dow));
            }
        }};
    }

    private static Optional<PeriodTimeTable> parsePeriodTimeTable(JsonObject comciganJson, int grade, int clazz, DayOfWeek dow, TimeTableType type, int period) {
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

    @RequiredArgsConstructor
    private enum TimeTableType {

        TODAY("자료147"),
        ORIGINAL("자료481"),
        ;

        @Getter
        private final String jsonKey;
    }
}
