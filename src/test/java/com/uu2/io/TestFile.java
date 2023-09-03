package com.uu2.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class TestFile extends File {

    public TestFile(String pathname) {
        super(pathname);
    }

    public void writeTestData(int r, int c, int min, int max)
            throws FileNotFoundException {
        try (var pw = new PrintWriter(this)) {
            for (int i = 0; i < r; i++) {
                for (int j = 0; j < c; j++) {
                    if (j > 0) {
                        pw.print(' ');
                    }
                    pw.print(getRandomInt(min, max));
                }
                pw.println();
            }
        }
    }

    private int getRandomInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

}
