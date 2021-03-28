package com.adishar.audex.Utility;

public class MinMaxPrimitiveInteger {



    public static int findMinInteger(int[] values)
    {
        if(values!=null)
        {
            int min=values[0];

            for(int i:values)
            {
                if(i<min)
                {
                    min=i;
                }
            }

            return min;
        }
        else
        {
            return -1;
        }

    }

    public static int findMaxInteger(int[] values)
    {
        if(values!=null)
        {
            int max=values[0];

            for(int i:values)
            {
                if(i>max)
                {
                    max=i;
                }
            }

            return max;
        }
        else
        {
            return -1;
        }

    }
}
