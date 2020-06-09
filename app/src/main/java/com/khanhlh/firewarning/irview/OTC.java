package com.khanhlh.firewarning.irview;

import android.content.Context;

public class OTC {

    public static final int IR_WIDTH = 3;
    public static final int IR_HEIGHT = 4;

    private static double minIrTemp = 9999.0, maxIrTemp = -9999.0;

    private static double[] irTemp = new double[IR_WIDTH * IR_HEIGHT];
    private static double[] irTempFlipped = new double[IR_WIDTH * IR_HEIGHT];

    private Context ctx = null;

    //2d temp datat
    public static double[][] tempData2D = new double[OTC.IR_HEIGHT][OTC.IR_WIDTH];

    //returns ir temperature data [height][width]
    public double[][] getIrTemp() {
        return tempData2D;
    }

    public double getMinIrTemp() {
        return minIrTemp;
    }

    public double getMaxIrTemp() {
        return maxIrTemp;
    }
}
