package com.example.zyco.clockview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.example.zyco.clockview.R;
import com.example.zyco.clockview.app.MyApplication;
import com.example.zyco.clockview.util.DimensionUtil;

import java.util.Date;

public class ClockView extends View {
    private DimensionUtil mDimensionUtil; //dimension工具类实例
    private Paint mPaint; //绘制表盘
    private Paint mPaintNumber; //绘制数字
    private int mNumberSize; //数字大小
    private Rect rectNumber; //数字测量边框
    private Paint mPaintHourHand; //绘制时针
    private Path mHourHandPath;   //时针path,避免每次重绘时针 都去new一个Path
    private Paint mPaintMinHand; //绘制分针
    private Path mMinHandPath;  //分针path,避免每次重绘分针 都去new一个Path
    private Paint mPaintSecondHand; //绘制秒针
    private Path mSecondHandPath;   //秒针path,避免每次重绘秒针 都去new一个Path
    private Paint mCenterCirclePaint; //绘制圆心
    private int mColor;
    private int bigDegreeCount=12; //时针刻度数
    private int smallDegreeCount=60; //分针刻度数
    private float centerX; //圆心x坐标
    private float centerY; //圆心y坐标
    private float radius; //圆半径
    private float radius2; //分针刻度end端点构成的圆的半径
    private float radius3; //时针刻度end端点构成的圆的半径
    private Point[] mBigPointsStart=new Point[bigDegreeCount+1]; //存储时针刻度的start坐标点，数组[0]不存储，刻度与数组index成对应关系
    private Point[] mSmallPointsStart=new Point[smallDegreeCount+1]; //存储分针刻度的start坐标点，数组[0]不存储，刻度与数组index成对应关系
    private Point[] mBigPointsEnd=new Point[bigDegreeCount+1]; //存储时针刻度的end坐标点，数组[0]不存储，刻度与数组index成对应关系
    private Point[] mSmallPointsEnd=new Point[smallDegreeCount+1]; //存储分针刻度的end坐标点，数组[0]不存储，刻度与数组index成对应关系
    private Date mDate;

    public ClockView(Context context){
        this(context,null);
    }
    public ClockView(Context context, AttributeSet attributeSet){
        this(context,attributeSet,0);
    }
    public ClockView(Context context,AttributeSet attributeSet,int defStyle){
        super(context,attributeSet,defStyle);
        TypedArray typedArray=context.obtainStyledAttributes(attributeSet,R.styleable.ClockView);
        mColor=typedArray.getColor(R.styleable.ClockView_color,Color.WHITE);
        mNumberSize=typedArray.getDimensionPixelSize(R.styleable.ClockView_numberSize,100);
        init();
        typedArray.recycle();
    }

    private void init(){
        mDimensionUtil=DimensionUtil.getInstance(MyApplication.getAppContext());
        mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mDimensionUtil.dp2px(1));
        mPaint.setColor(mColor);

        mPaintHourHand=new Paint();
        mPaintHourHand.setAntiAlias(true);
        mPaintHourHand.setStyle(Paint.Style.STROKE);
        mPaintHourHand.setStrokeWidth(mDimensionUtil.dp2px(3));
        mPaintHourHand.setColor(mColor);

        mPaintMinHand=new Paint();
        mPaintMinHand.setAntiAlias(true);
        mPaintMinHand.setStyle(Paint.Style.STROKE);
        mPaintMinHand.setStrokeWidth(mDimensionUtil.dp2px(2));
        mPaintMinHand.setColor(mColor);

        mPaintSecondHand=new Paint();
        mPaintSecondHand.setAntiAlias(true);
        mPaintSecondHand.setStyle(Paint.Style.STROKE);
        mPaintSecondHand.setStrokeWidth(1.2f);
        mPaintSecondHand.setColor(mColor);

        mPaintNumber=new Paint();
        mPaintNumber.setAntiAlias(true);
        mPaintNumber.setStyle(Paint.Style.FILL);
        mPaintNumber.setStrokeWidth(1);
        mPaintNumber.setTextSize(mNumberSize);
        mPaintNumber.setColor(mColor);

        rectNumber=new Rect();

        mCenterCirclePaint=new Paint();
        mCenterCirclePaint.setAntiAlias(true);
        mCenterCirclePaint.setStyle(Paint.Style.FILL);
        mCenterCirclePaint.setColor(mColor);

        mDate=new Date();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize=MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode=MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecSize=MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode=MeasureSpec.getMode(heightMeasureSpec);
        if(widthSpecMode==MeasureSpec.AT_MOST && heightSpecMode==MeasureSpec.AT_MOST){
            setMeasuredDimension((int)mDimensionUtil.dp2px(200),(int)mDimensionUtil.dp2px(200));
        }else if(widthSpecMode==MeasureSpec.AT_MOST){
            int width=(int)mDimensionUtil.dp2px(200);
            setMeasuredDimension(width>heightSpecSize?width:heightSpecSize,width>heightSpecSize?width:heightSpecSize);
        }else if(heightSpecMode==MeasureSpec.AT_MOST){
            int height=(int)mDimensionUtil.dp2px(200);
            setMeasuredDimension(widthSpecSize>height?widthSpecSize:height,widthSpecSize>height?widthSpecSize:height);
        }else{
            setMeasuredDimension(widthSpecSize>heightSpecSize?widthSpecSize:heightSpecSize,widthSpecSize>heightSpecSize?widthSpecSize:heightSpecSize);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX=getMeasuredWidth()/2;
        centerY=getMeasuredHeight()/2;
        radius=Math.max(getMeasuredWidth(),getMeasuredHeight())/2*0.8f;
        radius2=Math.max(getMeasuredWidth(),getMeasuredHeight())/2*0.7f;
        radius3=Math.max(getMeasuredWidth(),getMeasuredHeight())/2*0.6f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制圆心
        canvas.drawCircle(centerX,centerY,7,mCenterCirclePaint);
        //绘制表盘
        drawCompass(canvas);
        //绘制时针
        drawHourHand(canvas);
        //绘制分针
        drawMinHand(canvas);
        //绘制秒针
        drawSecond(canvas);
    }

    /**
     * 绘制表盘
     */
    private void drawCompass(Canvas canvas){
        //确定时针刻度start坐标
        confirmPoint(mBigPointsStart,bigDegreeCount,radius,3,6,12);
        //确定时针刻度end坐标
        confirmPoint(mBigPointsEnd,bigDegreeCount,radius3,3,6,12);
        //确定分针刻度start坐标
        confirmPoint(mSmallPointsStart,smallDegreeCount,radius,15,30,60);
        //确定分针刻度end坐标
        confirmPoint(mSmallPointsEnd,smallDegreeCount,radius2,15,30,60);
        //绘制刻度线
        drawLine(canvas,mBigPointsStart,mBigPointsEnd);
        drawLine(canvas,mSmallPointsStart,mSmallPointsEnd);
        //画圆
        canvas.drawCircle(centerX,centerY,radius,mPaint);
    }

    /**
     * 确定刻度的坐标
     * @param points 存储坐标
     * @param count  刻度数
     * @param radius 圆半径
     * @param degree 坐标从哪一个刻度(degree)开始'确定'并存储
     * @param factor 该因子决定刻度间的角度是多少
     * @param degreeFactor 该因子影响坐标存储的index
     */
    private void confirmPoint(Point[] points,int count,float radius,int degree,int factor,int degreeFactor){
        for(int i=0;i<count;i++,degree++){
            Point point=new Point();
            point.y= (float) (radius*Math.sin(i*Math.PI/factor))+centerY;
            point.x= (float) (radius*Math.cos(i*Math.PI/factor))+centerX;
            if(degree>degreeFactor){ //从刻度3开始存储坐标，超过刻度12时，将刻度-12，存储刻度1和刻度2的坐标
                points[degree-degreeFactor]=point;
            }else{
                points[degree]=point;
            }
        }
    }

    /**
     * 确定时针和分针端点的坐标
     */
    private  Point confirmPoint(boolean isH,int hour,int minute){
        if(isH){
            double hourBase= (hour%12-3)>=0?(hour%12-3)*Math.PI/6:(hour%12+12-3)*Math.PI/6;
            Point point=new Point();
            point.y= (float) ((radius*0.5)*Math.sin(minute/60f*Math.PI/6+hourBase))+centerY;
            point.x= (float) ((radius*0.5)*Math.cos(minute/60f*Math.PI/6+hourBase))+centerX;
            return point;
        }else{
            double minBase= (minute%60-15)>=0?(minute%60-15):(minute%60+45);
            Point point=new Point();
            point.y= (float) ((radius*0.57)*Math.sin(Math.PI/30f*minBase))+centerY;
            point.x= (float) ((radius*0.57)*Math.cos(Math.PI/30f*minBase))+centerX;
            return point;
        }
    }

    /**
     * 确定秒针端点的坐标
     */
    private  Point confirmPoint(int second){
        double secondBase= (second%60-15)>=0?(second%60-15):(second%60+45);
        Point point=new Point();
        point.y= (float) ((radius*0.65)*Math.sin(Math.PI/30f*secondBase))+centerY;
        point.x= (float) ((radius*0.65)*Math.cos(Math.PI/30f*secondBase))+centerX;
        return point;
    }

    private void drawLine(Canvas canvas,Point[] pointStart,Point[] pointEnd){
        Path path=new Path();
        for(int i=1;i<pointStart.length;i++){
            path.reset();
            path.moveTo(pointStart[i].x,pointStart[i].y);
            path.lineTo(pointEnd[i].x,pointEnd[i].y);
            canvas.drawPath(path,mPaint);
            //绘制表盘上的4个数字：12 3 6 9
            if(pointStart.length==bigDegreeCount+1){
                float adjust=0;
                if(i==3){
                    adjust=(mPaintNumber.getFontMetrics().bottom-mPaintNumber.getFontMetrics().top)/2f-mPaintNumber.getFontMetrics().bottom;
                    canvas.drawText(String.valueOf(i),pointEnd[i].x-mNumberSize,pointEnd[i].y+adjust,mPaintNumber);
                }if(i==6){
                    mPaintNumber.getTextBounds(String.valueOf(i),0,String.valueOf(i).length(),rectNumber);
                    adjust=rectNumber.width()/2f;
                    canvas.drawText(String.valueOf(i),pointEnd[i].x-adjust,pointEnd[i].y-mNumberSize/2,mPaintNumber);
                }if(i==9){
                    adjust=(mPaintNumber.getFontMetrics().bottom-mPaintNumber.getFontMetrics().top)/2f-mPaintNumber.getFontMetrics().bottom;
                    canvas.drawText(String.valueOf(i),pointEnd[i].x+mNumberSize/2,pointEnd[i].y+adjust,mPaintNumber);
                }if(i==12){
                    canvas.drawText(String.valueOf(i),pointEnd[i].x-mNumberSize/2,pointEnd[i].y+mNumberSize,mPaintNumber);
                }
            }

        }
    }

    /**
     * 绘制时针
     */
    private void drawHourHand(Canvas canvas){
        Point hPoint=confirmPoint(true,mDate.getHours(),mDate.getMinutes());
        if(mHourHandPath==null){
            mHourHandPath=new Path();
        }
        mHourHandPath.reset();
        mHourHandPath.moveTo(hPoint.x,hPoint.y);
        mHourHandPath.lineTo(centerX,centerY);
        canvas.drawPath(mHourHandPath,mPaintHourHand);
    }

    /**
     * 绘制分针
     */
    private void drawMinHand(Canvas canvas){
        Point minPoint=confirmPoint(false,mDate.getHours(),mDate.getMinutes());
        if(mMinHandPath==null){
            mMinHandPath=new Path();
        }
        mMinHandPath.reset();
        mMinHandPath.moveTo(minPoint.x,minPoint.y);
        mMinHandPath.lineTo(centerX,centerY);
        canvas.drawPath(mMinHandPath,mPaintMinHand);
    }

    /**
     * 绘制秒针
     */
    private void drawSecond(Canvas canvas){
        Point sPoint=confirmPoint(mDate.getSeconds());
        if(mSecondHandPath==null){
            mSecondHandPath=new Path();
        }
        mSecondHandPath.reset();
        mSecondHandPath.moveTo(sPoint.x,sPoint.y);
        mSecondHandPath.lineTo(centerX,centerY);
        canvas.drawPath(mSecondHandPath,mPaintSecondHand);
    }

    public void start(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (true){
                        mDate=new Date();
                        Thread.sleep(1000);
                        postInvalidate();
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    class Point{
        float x;
        float y;
    }
}
