package com.leosam.tvbox.mv.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author admin
 * @since 2023/6/10 17:31
 */
public class ClassPathReaderUtils {

    public static InputStreamReader getInputStreamReader(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream(),StandardCharsets.UTF_8);
            return inputStreamReader;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static BufferedReader getBufferedReader(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream(),StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            return reader;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Properties getProperties(String path) {
        Properties properties = new Properties();
        try {
            ClassPathResource resource = new ClassPathResource(path);
            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream());
            properties.load(new BufferedReader(inputStreamReader));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return properties;
    }

    public static String getContent(String path) {
        StringBuilder content = new StringBuilder();
        try {
            ClassPathResource resource = new ClassPathResource(path);
            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String data = null;
            while ((data = reader.readLine()) != null) {
                content.append(data).append("\n");
            }
            reader.close();
            return content.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) {
        String json = getContent("./tvbox/16wMV.txt");
        System.out.println(json);
    }

}

