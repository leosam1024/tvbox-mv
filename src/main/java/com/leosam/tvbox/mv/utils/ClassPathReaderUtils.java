package com.leosam.tvbox.mv.utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author admin
 * @since 2023/6/10 17:31
 */
public class ClassPathReaderUtils {

    public static final String CLASS_PATH_PREFIX = "classpath:";

    public static Map<String, String> getDirectoryFiles(String filePath) throws IOException {
        Map<String, String> pathMap = new LinkedHashMap<>();
        if (StringUtils.isEmpty(filePath) || filePath.startsWith(CLASS_PATH_PREFIX)) {
            return pathMap;
        }
        File data = new File(filePath);
        if (data.exists() && data.isFile()) {
            pathMap.put(data.getCanonicalPath(), data.getName());
        }
        if (data.exists() && data.isDirectory()) {
            File[] files = data.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    pathMap.put(filePath + file.getName(), file.getName());
                }
            }
        }
        return pathMap;
    }

    public static String extractFileNameWithoutExtension(String filePath) {
        filePath = filePath.replace(CLASS_PATH_PREFIX, "");
        Path path = Paths.get(filePath);
        String fileNameWithExtension = path.getFileName().toString();
        int lastDotIndex = fileNameWithExtension.lastIndexOf(".");
        String fileNameWithoutExtension = lastDotIndex == -1 ? fileNameWithExtension
                : fileNameWithExtension.substring(0, lastDotIndex);
        return fileNameWithoutExtension;
    }

    public static void deleteAllFile(File file) {
        if (file == null) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                if (file1.isFile()) {
                    file1.delete();
                }
            }
        }
    }

    public static long getAllFileSize(Set<String> dataPath) {
        long size = 0;
        for (String path : dataPath) {
            if (path.startsWith(CLASS_PATH_PREFIX)) {
                String pathName = path.replace(CLASS_PATH_PREFIX, "");
                size += getClassPathFileSize(pathName);
            } else {
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    size += file.length();
                }
            }
        }
        return size;
    }

    public static int getClassPathFileSize(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            InputStream inputStream = resource.getInputStream();
            int available = inputStream.available();
            return available;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static InputStreamReader getInputStreamReader(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            return inputStreamReader;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    public static BufferedReader getBufferedReader(String path) {
        try {
            InputStreamReader inputStreamReader = null;
            if (path.startsWith(CLASS_PATH_PREFIX)) {
                path = path.replace(CLASS_PATH_PREFIX, "");
                ClassPathResource resource = new ClassPathResource(path);
                inputStreamReader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            } else {
                inputStreamReader = new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8);
            }
            BufferedReader reader = new BufferedReader(inputStreamReader);
            return reader;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
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

    public static class ClassPathResource {
        private final String path;

        private final ClassLoader classLoader;

        public ClassPathResource(String path) {
            if (path.startsWith(CLASS_PATH_PREFIX)) {
                path = path.replace(CLASS_PATH_PREFIX, "");
            }
            this.path = path;
            this.classLoader = getDefaultClassLoader();
        }

        public InputStream getInputStream() throws IOException {
            InputStream is;
            if (this.classLoader != null) {
                is = this.classLoader.getResourceAsStream(this.path);
            } else {
                is = ClassLoader.getSystemResourceAsStream(this.path);
            }
            if (is == null) {
                throw new FileNotFoundException(path + " cannot be opened because it does not exist");
            }
            return is;
        }

        private static ClassLoader getDefaultClassLoader() {
            ClassLoader cl = null;
            try {
                cl = Thread.currentThread().getContextClassLoader();
            } catch (Throwable ex) {
                // Cannot access thread context ClassLoader - falling back...
            }
            if (cl == null) {
                // No thread context class loader -> use class loader of this class.
                cl = ClassPathResource.class.getClassLoader();
                if (cl == null) {
                    // getClassLoader() returning null indicates the bootstrap ClassLoader
                    try {
                        cl = ClassLoader.getSystemClassLoader();
                    } catch (Throwable ex) {
                        // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                    }
                }
            }
            return cl;
        }
    }

}

