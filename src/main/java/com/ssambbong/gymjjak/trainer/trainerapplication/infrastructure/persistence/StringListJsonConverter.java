package com.ssambbong.gymjjak.trainer.trainerapplication.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;


/* Comment
*   해당 클래스는 qualifications, award_histories 가 DB에선
*   TEXT로 저장되지만, 도메인에서는 List로 받기 땜에 변환하는 클래스입니다!
* */
@Converter
public class StringListJsonConverter implements AttributeConverter<List<String>, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<List<String>>() {};

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            // 빈 값이면 빈 List 반환
            return OBJECT_MAPPER.writeValueAsString(attribute == null ? List.of() : attribute);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("List<String> JSON 직렬화에 실패", exception);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return List.of();
        }

        try {
            return OBJECT_MAPPER.readValue(dbData, STRING_LIST_TYPE);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("List<String> JSON 역직렬화에 실패했습니다.", exception);
        }
    }
}
