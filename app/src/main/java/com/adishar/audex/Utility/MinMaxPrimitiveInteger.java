package com.adishar.audex.Utility;

public class MinMaxPrimitiveInteger {


    public static int findMinInteger(int[] values) {
        if (values == null)
            return -1;

        int min = values[0];
        for (int i : values) {
            min = Math.min(min, i);
        }
        return min;
    }

    public static int findMaxInteger(int[] values) {
        if (values == null) {
            return -1;
        }
        int max = values[0];

        for (int i : values) {
            max = Math.max(max, i);
        }

        return max;
    }
}
