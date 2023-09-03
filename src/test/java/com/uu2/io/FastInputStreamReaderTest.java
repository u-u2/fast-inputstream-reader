package com.uu2.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.jupiter.api.Test;

public class FastInputStreamReaderTest {
    public String[] createStringLines(int r, String line) {
        var lines = new String[r];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = line + i;
        }
        return lines;
    }

    public String[] createIntLines(int r, int v) {
        var lines = new String[r];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = String.valueOf(v + i);
        }
        return lines;
    }

    //#region readLine
    @Test
    public void testReadLineWithCr() throws IOException {
        var s = String.join("\r", createStringLines(4, "test"));
        var r = new FastInputStreamReader(new ByteArrayInputStream(s.getBytes()));
        assertEquals("test0", r.readLine());
        assertEquals("test1", r.readLine());
        assertEquals("test2", r.readLine());
        assertEquals("test3", r.readLine());
    }

    @Test
    public void testReadLineWithLf() throws IOException {
        var s = String.join("\n", createStringLines(4, "test"));
        var r = new FastInputStreamReader(new ByteArrayInputStream(s.getBytes()));
        assertEquals("test0", r.readLine());
        assertEquals("test1", r.readLine());
        assertEquals("test2", r.readLine());
        assertEquals("test3", r.readLine());
    }

    @Test
    public void testReadLineWithCrLf() throws IOException {
        var s = String.join("\r\n", createStringLines(4, "test"));
        var r = new FastInputStreamReader(new ByteArrayInputStream(s.getBytes()));
        assertEquals("test0", r.readLine());
        assertEquals("test1", r.readLine());
        assertEquals("test2", r.readLine());
        assertEquals("test3", r.readLine());
    }

    @Test
    public void testReadLineCorrectly() throws IOException {
        var f = new TestFile(String.format("testfile.txt"));
        var result = new File(String.format("result.txt"));
        f.writeTestData(10, 2, 1, 1000);
        f.deleteOnExit();
        result.deleteOnExit();
        try (var r = new FastInputStreamReader(new FileInputStream(f), 2)) {
            try (var pw = new PrintWriter(result)) {
                while (r.hasNext()) {
                    pw.print(r.readLine());
                    pw.println();
                }
            }
        }
        assertEquals(f.length(), result.length());
    }

    //#endregion
    //#region readInt
    @Test
    public void testReadIntWithLf() throws IOException {
        var s = String.join("\n", createIntLines(4, -3));
        var r = new FastInputStreamReader(new ByteArrayInputStream(s.getBytes()));
        assertEquals(-3, r.readInt());
        assertEquals(-2, r.readInt());
        assertEquals(-1, r.readInt());
        assertEquals(0, r.readInt());
    }

    @Test
    public void testReadIntWithCr() throws IOException {
        var s = String.join("\r", createIntLines(4, -3));
        var r = new FastInputStreamReader(new ByteArrayInputStream(s.getBytes()));
        assertEquals(-3, r.readInt());
        assertEquals(-2, r.readInt());
        assertEquals(-1, r.readInt());
        assertEquals(0, r.readInt());
    }

    @Test
    public void testReadIntWithCrLf() throws IOException {
        var s = String.join("\r\n", createIntLines(4, -3));
        var r = new FastInputStreamReader(new ByteArrayInputStream(s.getBytes()));
        assertEquals(-3, r.readInt());
        assertEquals(-2, r.readInt());
        assertEquals(-1, r.readInt());
        assertEquals(0, r.readInt());
    }
    //#endregion
}
