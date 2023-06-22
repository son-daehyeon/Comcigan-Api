package com.github.ioloolo.comcigan;

import com.github.ioloolo.comcigan.data.School;
import com.github.ioloolo.comcigan.util.Base64;
import com.github.ioloolo.comcigan.util.ComciganRequest;
import com.github.ioloolo.comcigan.util.EucKr;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public abstract class ComciganBaseApi {

    public static List<School> searchSchool(String name) throws IOException {
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
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    public static Map<Integer, LocalTime> getRange(int code) throws IOException {
        JsonObject comciganJson = ComciganBaseApi.getComciganJson(code);

        return new HashMap<Integer, LocalTime>() {{
            comciganJson.getAsJsonArray("일과시간")
                    .asList()
                    .stream()
                    .map(JsonElement::getAsString)
                    .forEach(str -> {
                        int hour = Integer.parseInt(str.substring(2, 4));
                        int minute = Integer.parseInt(str.substring(5, 7));

                        int key = Integer.parseInt(str.charAt(0) + "");
                        LocalTime value = LocalTime.of(hour, minute);

                        put(key, value);
                    });
        }};
    }

    protected static JsonObject getComciganJson(int code) throws IOException {
        String subUrl = "36179?" + Base64.encode("73629_"+code+"_0_1");
        Optional<JsonObject> request = ComciganRequest.request(subUrl);

        return request
                .map(JsonElement::getAsJsonObject)
                .orElse(null);
    }
}
