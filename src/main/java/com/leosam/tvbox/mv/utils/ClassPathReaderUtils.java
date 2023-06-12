package com.leosam.tvbox.mv.utils;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author admin
 * @since 2023/6/10 17:31
 */
public class ClassPathReaderUtils {

    public static int getSize(String path) {
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
            ClassPathResource resource = new ClassPathResource(path);


            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream(),StandardCharsets.UTF_8);
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


        public static ClassLoader getDefaultClassLoader() {
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

