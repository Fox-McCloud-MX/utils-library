package com.mx.util.library.file;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;

@Slf4j
public class FileUtil {

    /* Method to save a vfs2 FileObject for Apache Commons */
    public static void saveFileObject(FileObject file, String tempDirectory, String fileName) throws IOException {

        Path dir = Paths.get(tempDirectory);

        try {
            if (!new File(tempDirectory + fileName).exists()) {
                Files.createFile(dir.resolve(fileName));
            }

            String myString = IOUtils.toString(
                    file.getContent().getInputStream(),
                    "ISO-8859-1"
            );

            Files.write(
                    dir.resolve(fileName),
                    myString.getBytes(),
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            log.info("File created at {}", dir.resolve(fileName));

        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    /*Method to fill an Object with data from a file*/
    public static <T extends Object> List<T> toCollection(
            File file,
            final List<String> headers,
            Class<T> clazz,
            String delimiter
    ) throws IOException, InstantiationException, IllegalAccessException, 
            IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        
        List<String[]> sList = new ArrayList<>();
        List<Method> lMethods = Arrays.asList(clazz.getMethods());
        List<T> lClass = new ArrayList<>();

        List<String> rows = Files.readAllLines(
                file.toPath(),
                Charset.forName("ISO-8859-1")
        );

        lMethods = lMethods.stream()
                .filter(m -> m.getName().startsWith("set"))
                .collect(Collectors.toList());

        lMethods.sort(Comparator.comparing(
                s -> headers.indexOf(s.getName().replace("set", "")))
        );

        rows.forEach(row -> sList.add(row.split(delimiter)));

        for (String[] sListArray : sList) {
            int i = 0;
            Object o = Class.forName(clazz.getName()).newInstance();
            for (String s : Arrays.asList(sListArray)) {
                lMethods.get(i++).invoke(o, s.trim());
            }
            lClass.add((T) o);
        }
        
        return lClass;
    }
}

