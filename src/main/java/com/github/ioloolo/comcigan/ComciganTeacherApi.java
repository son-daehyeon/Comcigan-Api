package com.github.ioloolo.comcigan;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.ioloolo.comcigan.data.Teacher;
import com.github.ioloolo.comcigan.data.timetable.TeacherTimeTable;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public final class ComciganTeacherApi extends ComciganBaseApi {

    protected static JsonObject getComciganJson(String code) throws IOException {
        JsonObject comciganJson = ComciganBaseApi.getComciganJson(code);
        createSubTimetable(comciganJson);

        return comciganJson;
    }

    public static List<Teacher> getTeacherList(String code) throws Exception {
        return new ArrayList<Teacher>() {{
            List<String> list = getComciganJson(code)
                    .get("자료446")
                    .getAsJsonArray()
                    .asList()
                    .stream()
                    .map(JsonElement::getAsString)
                    .collect(Collectors.toList());

            for (int i = 1; i < list.size(); i++) {
                this.add(Teacher.of(i, list.get(i)));
            }
        }};
    }

    public static Map<DayOfWeek, List<TeacherTimeTable>> getTimeTable(String code, int id) throws Exception {
        return new LinkedHashMap<DayOfWeek, List<TeacherTimeTable>>() {{
            JsonObject comciganJson = getComciganJson(code);

            for (int dow = 1; dow < 6; ++dow) {
                put(DayOfWeek.values()[dow-1], new ArrayList<>());
            }

            for (int dow = 1; dow < 6; ++dow) {
                for (int period = 1; period <= 8; ++period) {
                    DayOfWeek dayOfWeek = DayOfWeek.values()[dow-1];

                    JsonArray date1Arr = comciganJson.get("자료542").getAsJsonArray()
                            .get(id).getAsJsonArray()
                            .get(dow).getAsJsonArray();

                    JsonArray date2Arr = comciganJson.get("시간표2").getAsJsonArray()
                            .get(id).getAsJsonArray()
                            .get(dow).getAsJsonArray();

                    if (period >= date1Arr.size())
                        break;

                    int data1 = date1Arr.get(period).getAsInt();
                    int data2 = date2Arr.get(period).getAsInt();

                    boolean isChangedTable = data1 != data2;

                    if (data1 > 100) {
                        int idx = data1 / 100;

                        int grade = idx / 100;
                        int clazz = idx - grade * 100;

                        int sb = data1 - idx * 100;

                        String lecture = comciganJson.get("자료492").getAsJsonArray()
                                .get(sb).getAsString();

                        this.get(dayOfWeek).add(TeacherTimeTable.builder()
                                .period(period)
                                .lecture(lecture)
                                .location(grade+"학년 "+clazz+"반")
                                .isModify(isChangedTable)
                                .build());
                    }
                }
            }
        }};
    }

    private static void createSubTimetable(JsonObject comciganJson) {
        comciganJson.add("시간표2", new JsonArray());

        createTeacherSubTimetable(comciganJson);
        processGradeData(comciganJson);
    }

    private static void createTeacherSubTimetable(JsonObject comciganJson) {
        for (int teacher = 0; teacher <= comciganJson.get("교사수").getAsInt(); ++teacher) {
            comciganJson.get("시간표2").getAsJsonArray().add(new JsonArray());
            if (teacher == 0) continue;
            addPeriodsToDayOfWeek(comciganJson, teacher);
        }
    }

    private static void addPeriodsToDayOfWeek(JsonObject comciganJson, int teacher) {
        for (int dow = 0; dow < 6; ++dow) {
            comciganJson.get("시간표2").getAsJsonArray().get(teacher).getAsJsonArray().add(new JsonArray());
            if (dow == 0) continue;
            addPeriodsToTeacher(comciganJson, teacher, dow);
        }
    }

    private static void addPeriodsToTeacher(JsonObject comciganJson, int teacher, int dow) {
        for (int period = 0; period <= 8; ++period) {
            comciganJson.get("시간표2").getAsJsonArray().get(teacher).getAsJsonArray().get(dow).getAsJsonArray().add(0);
        }
    }

    private static void processGradeData(JsonObject comciganJson) {
        for (int grade = 1; grade <= 3; ++grade) {
            int size = comciganJson.get("학급수").getAsJsonArray().get(grade).getAsInt();
            processClassData(comciganJson, grade, size);
        }
    }

    private static void processClassData(JsonObject comciganJson, int grade, int size) {
        for (int clazz = 1; clazz <= size; ++clazz) {
            processDayOfWeek(comciganJson, grade, clazz);
        }
    }

    private static void processDayOfWeek(JsonObject comciganJson, int grade, int clazz) {
        for (int dow = 1; dow < 6; ++dow) {
            processPeriod(comciganJson, grade, clazz, dow);
        }
    }

    private static void processPeriod(JsonObject comciganJson, int grade, int clazz, int dow) {
        for (int period = 1; period <=8; ++period) {
            JsonArray data = comciganJson.get("자료481").getAsJsonArray().get(grade).getAsJsonArray().get(clazz).getAsJsonArray().get(dow).getAsJsonArray();
            if (period >= data.size()) break;
            updateTeacherData(comciganJson, grade, clazz, dow, period, data);
        }
    }

    private static void updateTeacherData(JsonObject comciganJson, int grade, int clazz, int dow, int period, JsonArray data) {
        int index = data.get(period).getAsInt();
        int teacherTmp = 0;
        if (index > 10000) {
            teacherTmp = index / 100000;
            index = index - teacherTmp * 100000;
        }

        if (index > 0) {
            int teacher = index / 100;
            if (teacher <= comciganJson.get("교사수").getAsInt()) {
                int sb = index - teacher * 100;
                updateTeacherPeriod(comciganJson, grade, clazz, dow, period, sb, teacher, teacherTmp);
            }
        }
    }

    private static void updateTeacherPeriod(JsonObject comciganJson, int grade, int clazz, int dow, int period, int sb, int teacher, int teacherTmp) {
        comciganJson.get("시간표2").getAsJsonArray().get(teacher).getAsJsonArray().get(dow).getAsJsonArray().set(period, new JsonPrimitive((grade*100+clazz)*100+sb));
        if (teacherTmp > 0) {
            comciganJson.get("시간표2").getAsJsonArray().get(teacherTmp).getAsJsonArray().get(dow).getAsJsonArray().set(period, new JsonPrimitive((grade*100+clazz)*100+sb));
        }
    }
}
