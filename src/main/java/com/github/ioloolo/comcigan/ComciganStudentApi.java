package com.github.ioloolo.comcigan;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.ioloolo.comcigan.data.timetable.OriginalTimeTable;
import com.github.ioloolo.comcigan.data.timetable.PeriodTimeTable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public final class ComciganStudentApi extends ComciganBaseApi {

    public static Map<DayOfWeek, List<PeriodTimeTable>> getWeeklyTimeTable(int code, int grade, int clazz) throws Exception {
        JsonObject comciganJson = ComciganBaseApi.getComciganJson(code);

        return new LinkedHashMap<DayOfWeek, List<PeriodTimeTable>>() {{
            EnumSet<DayOfWeek> range = EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);

            for (DayOfWeek dow : range) {
                put(dow, getDailyTimeTable(comciganJson, grade, clazz, dow));
            }
        }};
    }

    public static List<PeriodTimeTable> getDailyTimeTable(int code, int grade, int clazz, DayOfWeek dow) throws Exception {
        return getDailyTimeTable(ComciganBaseApi.getComciganJson(code), grade, clazz, dow);
    }

    private static List<PeriodTimeTable> getDailyTimeTable(JsonObject comciganJson, int grade, int clazz, DayOfWeek dow) throws Exception {
        return new ArrayList<PeriodTimeTable>() {{
            int period = 1;
            Optional<PeriodTimeTable> timeTable;

            while ((timeTable = getPeriodTimeTable(comciganJson, grade, clazz, dow, period++)).isPresent())
                add(timeTable.get());
        }};
    }

    public static Optional<PeriodTimeTable> getPeriodTimeTable(int code, int grade, int clazz, DayOfWeek dow, int period) throws Exception {
        return getPeriodTimeTable(ComciganBaseApi.getComciganJson(code), grade, clazz, dow, period);
    }

    private static Optional<PeriodTimeTable> getPeriodTimeTable(JsonObject comciganJson, int grade, int clazz, DayOfWeek dow, int period) {
        PeriodTimeTable todayTable = parsePeriodTimeTable(comciganJson, grade, clazz, dow, PeriodTimeTable.TimeTableType.TODAY, period).orElse(null);
        PeriodTimeTable originalTable = parsePeriodTimeTable(comciganJson, grade, clazz, dow, PeriodTimeTable.TimeTableType.ORIGINAL, period).orElse(null);

        if (todayTable == null || originalTable == null)
            return Optional.empty();

        if (!todayTable.equals(originalTable)) {
            todayTable.setModify(true);
            todayTable.setOriginal(OriginalTimeTable.from(originalTable));
        }

        return Optional.of(todayTable);
    }

    private static Optional<PeriodTimeTable> parsePeriodTimeTable(JsonObject comciganJson, int grade, int clazz, DayOfWeek dow, PeriodTimeTable.TimeTableType type, int period) {
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
