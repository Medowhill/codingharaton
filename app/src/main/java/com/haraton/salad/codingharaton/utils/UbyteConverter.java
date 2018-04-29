package com.haraton.salad.codingharaton.utils;

public class UbyteConverter {
    public static int ubyteToInt(byte ub) {
        if (ub >= 0) return ub;
        else return ub + 256;
    }

    public static byte intToUbyte(int i) {
        if (0 <= i && i <= 127) return (byte) i;
        else return (byte) (i - 256);
    }
}
