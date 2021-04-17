package webcrawler.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FileUtilsEx {
    public static boolean isDirectoryExist(final String dir) {
        File file = new File(dir);
        return file.isDirectory() && file.exists();
    }

    public static String generateRandomFileName() {
        int length = 10;
        boolean useLetters = true;
        boolean useNumbers = false;
        return RandomStringUtils.random(length, useLetters, useNumbers);
    }

    public static void removeDir(final String dir) throws IOException {
        FileUtils.deleteDirectory(new File(dir));
    }

    public static boolean isFileExists(final String filePath) {
        File file = new File(filePath);
        return file.isFile() && file.exists();
    }

    public static boolean createDirs(final String dirPath) {
        if (isDirectoryExist(dirPath))
            return true;

        return new File(dirPath).mkdirs();
    }

    public static String readFile(final String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        Files.readAllLines(Paths.get(filePath), StandardCharsets.ISO_8859_1).
                stream().
                forEach(s -> contentBuilder.append(s).append("\n"));
        return contentBuilder.toString();
    }

    public static List<String> readFileAllLines(final String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        return Files.readAllLines(Paths.get(filePath), StandardCharsets.ISO_8859_1);
    }

    public static Path writeFile(final String filePath, final String content) throws IOException {
        return Files.write(Paths.get(filePath), content.getBytes());
    }

    public static Path writeFile(final String filePath, final String content, final OpenOption... openOptions) throws IOException {
        return Files.write(Paths.get(filePath), content.getBytes(), openOptions);
    }

    public static void deleteDir(final String dirPath) throws IOException {
        if (!isDirectoryExist(dirPath))
            return;

        FileUtils.deleteDirectory(new File(dirPath));
    }

    public static void write2zip(final String targetFilePath, String content) throws IOException {
        try (GZIPOutputStream gos = new GZIPOutputStream(
                new FileOutputStream(targetFilePath+".gz"))) {

            gos.write(content.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static String readFromZip(final String targetFilePath) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (GZIPInputStream gis = new GZIPInputStream(
                new FileInputStream(targetFilePath+".gz"))) {

            // copy GZIPInputStream to ByteArrayOutputStream
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) > 0) {
                output.write(buffer, 0, len);
            }

        }

        return output.toString();
    }
}
