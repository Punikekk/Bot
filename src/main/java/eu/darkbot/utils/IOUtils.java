package eu.darkbot.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Some IO utilities.
 */
public class IOUtils {
    /**
     * Writes provided String to output stream.
     *
     * @param output stream where String will be written.
     * @param str    to write into output stream.
     * @throws IOException of {@link OutputStream#write(byte[])}
     */
    public static void write(OutputStream output, String str) throws IOException {
        output.write(str.getBytes());
    }

    /**
     * Converts InputStream to String in UTF-8 encoding.
     *
     * @param input to convert
     * @return converted String.
     * @throws IOException of {@link InputStream#read(byte[])}
     *                     and {@link ByteArrayOutputStream#toString(String)}
     */
    public static String read(InputStream input) throws IOException {
        int length;
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        while ((length = input.read(buffer)) != -1)
            result.write(buffer, 0, length);

        return result.toString(StandardCharsets.UTF_8.name());
    }
}
