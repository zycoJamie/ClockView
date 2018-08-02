package com.example.zyco.clockview.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class DimensionUtil {
    private Resources resources;
    private static DimensionUtil mInstance;
    private DimensionUtil(Context context){
        resources=context.getResources();
    }
    public static DimensionUtil  getInstance(Context context){
        if(mInstance==null){
            synchronized (DimensionUtil.class){
                mInstance=new DimensionUtil(context);
            }
        }
        return mInstance;
    }
    public float dp2px(int value){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,resources.getDisplayMetrics());
    }
}
