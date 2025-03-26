package com.example.demo;

import static com.example.demo.HelloApplication.degreesToDir;

public class degreesToDirTest {
    public static void main(String[] args) {
        int start = 1;
        int end = 0;
        String val = "N";
        for (int i = 1; i <= 360; i++) {
            String result = degreesToDir(i);
            if (!result.equals(val)) {
                System.out.println(val + ": " + start + "-" + end);
                start = i;
                val = result;
            }
            end = i;
        }
        System.out.println(val + ": " + start + "-" + end);
    }
}
