package com.adishar.audex.Utility;

import com.jjoe64.graphview.series.DataPoint;

import java.util.List;

public class ConverterClass {

    public static int[] convertDataPointListToIntegerArray(DataPoint[] list)
    {
        int[] values=new int[list.length];

        for(int i=0;i<list.length;i++)
        {
            values[i]=(int)list[i].getY();
        }

        return values;

    }
}
