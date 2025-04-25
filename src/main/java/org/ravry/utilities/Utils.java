package org.ravry.utilities;

import org.lwjgl.BufferUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.ravry.utilities.Logger.LOG_STATE.DEFAULT_LOG;

public class Utils {
    public static ByteBuffer readFileToByteBuffer(String file) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(file));
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(bytes.length);
        byteBuffer.put(bytes).flip();
        return byteBuffer;
    }

    private static String[] extensions = {".ttf", ".otf", ".ttc", ".dfont"};
    private static List<String> fontDirectories = getSystemFontDirectories();

    public static void dumpSystemFonts() {
        for (String directory : fontDirectories) {
            File fontDir = new File(directory);
            if (fontDir.exists() && fontDir.isDirectory()) {
                File[] files = fontDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        String filename = file.getName().toLowerCase();
                        Logger.LOG(DEFAULT_LOG, filename);
                    }
                }
            }
        }
    }

    public static String getSystemFontPath(String fontName) {
        for (String directory : fontDirectories) {
            File fontDir = new File(directory);
            if (fontDir.exists() && fontDir.isDirectory()) {
                File[] files = fontDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        String filename = file.getName().toLowerCase();
                        String searchName = fontName.toLowerCase();
                        if (filename.startsWith(searchName)) {
                            for (String ext : extensions) {
                                if (filename.equals(searchName + ext.toLowerCase()) ||
                                        filename.equals(searchName.replace(" ", "") + ext.toLowerCase())) {
                                    return file.getAbsolutePath();
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private static List<String> getSystemFontDirectories() {
        List<String> directories = new ArrayList<>();
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            String winDir = System.getenv("WINDIR");
            directories.add(winDir + "\\Fonts\\");
            directories.add(System.getenv("LOCALAPPDATA") + "\\Microsoft\\Windows\\Fonts\\");
        }
        else if (os.contains("mac")) {
            directories.add("/Library/Fonts/");
            directories.add("/System/Library/Fonts/");
            directories.add(System.getProperty("user.home") + "/Library/Fonts/");
        }
        else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            directories.add("/usr/share/fonts/");
            directories.add("/usr/local/share/fonts/");
            directories.add(System.getProperty("user.home") + "/.fonts/");
            directories.add(System.getProperty("user.home") + "/.local/share/fonts/");
        }

        return directories;
    }
}
