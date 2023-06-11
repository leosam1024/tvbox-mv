package com.leosam.tvbox.mv.utils;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

/**
 *
 */
public class JsonUtils {

    private static final Logger LOG = LoggerFactory.getLogger(JsonUtils.class);
    private static final TypeReference<HashMap<String, String>> TYPE_MAP =
            new TypeReference<HashMap<String, String>>() {
            };

    public static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
    }

    public static String writeValue(Object o) {
        try {
            return writeValueThrowException(o);
        } catch (Exception e) {
            return "";
        }
    }

    public static String writeValuePrettyPrinter(Object o) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (Exception e) {
            LOG.error("exception occur when Json serializePretty", e);
            return "";
        }
    }


    public static String writeValueThrowException(Object o) throws JsonProcessingException {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            LOG.error("exception occur when Json serialize", e);
            throw e;
        }
    }

    public static <T> T readValue(String json, Class<T> clazz) {
        try {
            return readValueThrowException(json, clazz);
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> T readValueThrowException(String json, Class<T> clazz) throws IOException {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            LOG.warn("Failed to deserialize from JSON string", e);
            throw e;
        }
    }

    public static <T> T readValue(String json, TypeReference<T> typeReference) {
        try {
            return readValueThrowException(json, typeReference);
        } catch (IOException e) {
            return null;
        }
    }

    public static <T> T readValueThrowException(String json, TypeReference<T> typeReference) throws IOException {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (IOException e) {
            LOG.warn("Failed to deserialize from JSON string", e);
            throw e;
        }
    }

}
