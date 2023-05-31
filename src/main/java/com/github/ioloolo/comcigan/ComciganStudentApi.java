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

public final class ComciganStudentApi extends ComciganBaseApi {

    /**
     * 특정 학년, 반의 주간 시간표를 가져옵니다.
     *
     * @param grade 학년
     * @param clazz 반
     * @return 주간 시간표
     */
    public Map<DayOfWeek, List<PeriodTimeTable>> getWeeklyTimeTable(int grade, int clazz) throws Exception {
        if (school == null) {
            throw new Exception("Please set the school first.");
        }

        fetchComciganJson();

        return new LinkedHashMap<DayOfWeek, List<PeriodTimeTable>>() {{
            EnumSet<DayOfWeek> range = EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY);

            for (DayOfWeek dow : range) {
                put(dow, getDailyTimeTable(grade, clazz, dow));
            }
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
    public List<PeriodTimeTable> getDailyTimeTable(int grade, int clazz, DayOfWeek dow) throws Exception {
        if (school == null) {
            throw new Exception("Please set the school first.");
        }

        fetchComciganJson();

        return new ArrayList<PeriodTimeTable>() {{
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
    public Optional<PeriodTimeTable> getPeriodTimeTable(int grade, int clazz, DayOfWeek dow, int period) throws Exception {
        if (school == null) {
            throw new Exception("Please set the school first.");
        }

        fetchComciganJson();

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
