package com.wds.bannerlib.banner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CircleIndicator extends View implements Indicator {

    private static final int MAX_COUNT = 10;//最大显示个数
    private int mCurrentIndex;
    private int unSelectColor;//颜色
    private int selectColor;//颜色
    private int mRadio;//半径
    private int mCount;// 最终显示个数
    private int mCurrent; // 实际个数
    private int mMargin;//间距
    private int mHeight;
    private int mWidth;
    private Paint mPaint;


    public CircleIndicator(Context context) {
        super(context);
        initPain();
    }

    public CircleIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPain();
    }

    public CircleIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPain();
    }

    //创建画笔
    private void initPain(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//图片去锯齿
        mPaint.setStyle(Paint.Style.FILL);//填充样式
        mPaint.setColor(Color.WHITE);//默认白色
    }
    //计算圆点的高和宽
    private void calculation(){
        //获取高度
        mHeight = mRadio * 2;
        //最终显示个数    如果定了显示个数，或最大显示个数，找最小的为最终显示个数。
        mCount = Math.min(mCurrent,MAX_COUNT);
        //获取宽度
        mWidth = (mCount * mRadio * 2) + (mCount - 1) * mMargin;
        invalidate(); // 刷新页面，重新onMeasure onLayout,onDraw
    }
    //测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量的尺寸为  规格  宽  mWidth   高 mHeight
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(mWidth,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(mHeight,MeasureSpec.EXACTLY));
    }
    //绘制
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //显示的个数 for循环    绘制圆点
        for (int i = 0; i < mCount; i++) {
            if (i==mCurrentIndex){
               mPaint.setColor(selectColor);
            }else{
                mPaint.setColor(unSelectColor);
            }
            //宽，高，半径，画笔
            canvas.drawCircle((i*(mRadio*2)+(i*mMargin)+mRadio),mRadio,mRadio,mPaint);
        }
    }

    @Override
    public void setRadio(int radio) {
        mRadio =radio;
    }
    @Override
    public void setMargin(int margin) {
        mMargin=margin;
    }
    @Override
    public void setCount(int count) {
        //如果实际个数不等于显示的个数就复值 并计算圆点容器的高和宽
        if (mCurrent!=count){
            mCurrent=count;
            calculation();
        }

    }

    @Override
    public void setCurrent(int index) {
        //传入的数与mCurrentIndex 不同复值，并刷新页面
        if (mCurrentIndex!=index){
            mCurrentIndex=index;
            invalidate(); // 刷新页面，重新onMeasure onLayout,onDraw
        }

    }

    @Override
    public void setUnSelectColor(int color) {
        unSelectColor=color;
    }

    @Override
    public void setSelectColor(int color) {
        selectColor=color;
    }


}
