package com.uu2.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

class Result implements Comparable<Result> {
    private final String name;
    private final int lineLength;
    private final double elapsedTime;

    public Result(String name, int lineLength, double elapsedTime) {
        this.name = name;
        this.lineLength = lineLength;
        this.elapsedTime = elapsedTime;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(String.format("name: %s\n", name))
                .append(String.format("lineLength: %d\n", lineLength))
                .append(String.format("elapsedTime: %.3fsec\n", elapsedTime))
                .toString();
    }

    @Override
    public int compareTo(Result o) {
        return this.elapsedTime < o.elapsedTime
            ? -1
            : 1;
    }

}

public class BenchmarkTest {

    @Test
    public void test() throws IOException {
        var f = new TestFile("test.txt");
        var rows = (int) 1e7;
        var columns = 1;
        var min = 1;
        var max = (int) 1e9;
        var detail = new StringBuilder("---TestFile Details---\n")
                .append(String.format("rows: %d\n", rows))
                .append(String.format("columns: %d\n", columns))
                .append(String.format("min: %d\n", min))
                .append(String.format("max: %d\n", max))
                .append("----------------------")
                .toString();
        System.out.println(detail);
        f.writeTestData(rows, columns, min, max);
        f.deleteOnExit();
        var readLineResults = new Result[] {
                readLineScanner(f),
                readLineBufferedReader(f),
                readLineFastInputStreamReader(f) };
        var readIntResults = new Result[] {
                readIntScanner(f),
                readIntBufferedReader(f),
                readIntFastInputStreamReader(f) };

        Arrays.sort(readLineResults);
        System.out.printf("---readLine---\n%s\n--------------\n",
                          Arrays.toString(readLineResults)
                                  .replaceAll(", ", "\n"));
        Arrays.sort(readIntResults);
        System.out.printf("---readInt---\n%s\n--------------\n",
                          Arrays.toString(readIntResults)
                                  .replaceAll(", ", "\n"));
    }

    //#region readLine
    public Result readLineScanner(File file) throws IOException {
        try (var r = new Scanner(new FileInputStream(file))) {
            var start = System.nanoTime();
            var i = 0;
            while (r.hasNext()) {
                i++;
                r.next();
            }
            return new Result("Scanner",
                    i,
                    (System.nanoTime() - start) / 1e9);
        }
    }

    public Result readLineBufferedReader(File file) throws IOException {
        try (var r = new BufferedReader(new InputStreamReader(
                new FileInputStream(file)))) {
            var start = System.nanoTime();
            var i = 0;
            while (r.readLine() != null) {
                i++;
            }
            return new Result("BufferedReader",
                    i,
                    (System.nanoTime() - start) / 1e9);
        }
    }

    public Result readLineFastInputStreamReader(File file) throws IOException {
        try (var r = new FastInputStreamReader(new FileInputStream(file))) {
            var start = System.nanoTime();
            var i = 0;
            while (r.hasNext()) {
                i++;
                r.readLine();
            }
            return new Result("FastInputStreamReader",
                    i,
                    (System.nanoTime() - start) / 1e9);
        }
    }
    //#endregion

    //#region readInt
    public Result readIntScanner(File file) throws IOException {
        try (var r = new Scanner(new FileInputStream(file))) {
            var start = System.nanoTime();
            var i = 0;
            while (r.hasNext()) {
                i++;
                r.nextInt();
            }
            return new Result("Scanner",
                    i,
                    (System.nanoTime() - start) / 1e9);
        }
    }

    public Result readIntBufferedReader(File file) throws IOException {
        try (var r = new BufferedReader(new InputStreamReader(
                new FileInputStream(file)))) {
            var start = System.nanoTime();
            var i = 0;
            var s = "";
            while ((s = r.readLine()) != null) {
                Integer.valueOf(s);
                i++;
            }
            return new Result("BufferedReader",
                    i,
                    (System.nanoTime() - start) / 1e9);
        }
    }

    public Result readIntFastInputStreamReader(File file) throws IOException {
        try (var r = new FastInputStreamReader(new FileInputStream(file))) {
            var start = System.nanoTime();
            var i = 0;
            while (r.hasNext()) {
                i++;
                r.readInt();
            }
            return new Result("FastInputStreamReader",
                    i,
                    (System.nanoTime() - start) / 1e9);
        }
    }
    //#endregion

}
