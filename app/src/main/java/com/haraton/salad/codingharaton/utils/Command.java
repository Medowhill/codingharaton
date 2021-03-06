package com.haraton.salad.codingharaton.utils;

public class Command {
    public static final byte LEFT_SLOW = 0, LEFT_FAST = 1, RIGHT_SLOW = 2, RIGHT_FAST = 3;
    public static int getDelay(byte cmd) {
        switch (cmd) {
            case LEFT_FAST: case RIGHT_FAST: return 750;
            default: return 4000;
        }
    }

    public static boolean available(byte cmd, int degree) {
        switch (cmd) {
            case LEFT_FAST: case LEFT_SLOW: return degree > 0;
            default: return degree < 180;
        }
    }

    public static int degreeDiff(byte cmd) {
        switch (cmd) {
            case LEFT_FAST: case LEFT_SLOW: return -60;
            default: return 60;
        }
    }
}
