package com.smallworld.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.ParseException;

public class JsonParser {


    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.ALWAYS, JsonInclude.Include.NON_NULL));
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * Convert <code>object</code> to json string.
     *
     * @param object object to be converted.
     * @param <T>    Type of object.
     * @return json as string.
     */
    public <T> String toString(final T object) throws ParseException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ParseException("Failed to convert " + object.getClass().getName() + " to json: " + e.getMessage(), 0);
        }
    }

    /**
     * Convert json to Object of type <code>clazz</code>
     *
     * @param json  Source json ot be converted.
     * @param clazz Destination object type.
     * @param <T>   Type of object
     * @return object representing json.
     */
    public <T> T toObject(final String json, final Class<T> clazz) throws ParseException {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new ParseException("Failed to convert json '" + json + "' to " + clazz.getName(), 0);
        }
    }
}
