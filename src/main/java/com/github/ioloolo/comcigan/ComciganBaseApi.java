package com.github.ioloolo.comcigan;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.ioloolo.comcigan.data.School;
import com.github.ioloolo.comcigan.util.Base64;
import com.github.ioloolo.comcigan.util.ComciganRequest;
import com.github.ioloolo.comcigan.util.EucKr;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class ComciganBaseApi {

    /**
     * 주어진 학교 이름으로 학교를 검색합니다.
     *
     * @param name 검색할 학교의 이름
     * @return 검색된 학교 리스트
     * @throws IOException 요청 처리 중 오류 발생 시
     */
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

    protected static JsonObject getComciganJson(int code) throws IOException {
        String subUrl = "36179?" + Base64.encode("73629_"+code+"_0_1");
        Optional<JsonObject> request = ComciganRequest.request(subUrl);

        return request
                .map(JsonElement::getAsJsonObject)
                .orElse(null);
    }
}
