package com.monew.monew_api.useractivity.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/*
    CommaSeparatedToListDeserializer
    - 콤마(,)로 구분된 문자열을 List<String>으로 변환하는 Jackson Deserializer
    single 쿼리로 조회한 활동 내역에서, 관심사 키워드(interest_keywords) 필드가
    콤마로 구분된 문자열로 반환되기 때문에 이를 List<String>으로 변환하기 위해 사용
 */
public class CommaSeparatedToListDeserializer extends JsonDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getValueAsString();
        if (text == null || text.isEmpty()) return Collections.emptyList();
        return Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}