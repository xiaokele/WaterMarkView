package com.kele.watermarkviewdemo.watermark;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.kele.watermarkviewdemo.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Des: 自定义水印View
 * Created by kele on 2020/11/12.
 * E-mail:984127585@qq.com
 */
public class WaterMarkView extends View {

    private static final String TAG = WaterMarkView.class.getSimpleName();

    private int mDrawWidth = 300;//画的宽度 为实际高度的mScaleSize倍（作用是在旋转后防止有些地方没有水印）
    private int mDrawHeight = 300;//画的高度 同上
    private String str = "可乐";//水印文字
    private float hSpace = 50f;//水平间距
    private float vSpace = 20f;//垂直间距
    private int mScaleSize = 2;//缩放大小 固定：2
    private int mDegrees = -30;//旋转角度
    private int bgColor = Color.TRANSPARENT;//背景颜色
    private int textColor = Color.BLACK;//字体颜色
    private float textSize = 13f;//字体大小
    private int mWidth;//布局实际宽度
    private int mHeight;//布局实际高度

    public WaterMarkView(@NonNull Context context) {
        this(context, null);
    }

    public WaterMarkView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context
     * @param attrs
     */
    public void init(Context context, AttributeSet attrs) {
        if (null != attrs) {
            //获取XML中的参数
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WaterMarkView);
            str = ta.getString(R.styleable.WaterMarkView_text);
            hSpace = ta.getDimension(R.styleable.WaterMarkView_hSpace, 50f);
            vSpace = ta.getDimension(R.styleable.WaterMarkView_vSpace, 20f);
            mDegrees = ta.getInteger(R.styleable.WaterMarkView_degrees, -30);
            bgColor = ta.getColor(R.styleable.WaterMarkView_bgColor, Color.TRANSPARENT);
            textColor = ta.getColor(R.styleable.WaterMarkView_textColor, Color.BLACK);
            textSize = ta.getDimension(R.styleable.WaterMarkView_textSize, 13f);
            ta.recycle();
        }
    }

    /**
     * 设置参数
     *
     * @param text 水印文字
     */
    public void setParams(String text) {
        setParams(text, hSpace, vSpace);
    }

    /**
     * 设置参数
     *
     * @param text  水印文字
     * @param hSize 水平方向水印文字个数
     * @param vSize 垂直方向水印文字个数
     */
    public void setParams(String text, float hSize, float vSize) {
        setParams(text, hSize, vSize, mDegrees);
    }

    /**
     * 设置参数
     *
     * @param text    水印文字
     * @param hSize   水平方向水印文字个数
     * @param vSize   垂直方向水印文字个数
     * @param degrees 旋转角度
     */
    public void setParams(String text, float hSize, float vSize, int degrees) {
        setParams(text, hSize, vSize, degrees, bgColor, textColor, textSize);
    }

    /**
     * 参数设置
     *
     * @param text
     * @param hSize
     * @param vSize
     * @param degrees
     * @param bgColor   背景颜色
     * @param textColor 字体颜色
     * @param textSize  字体大小
     */
    public void setParams(String text, float hSize, float vSize, int degrees, int bgColor, int textColor, float textSize) {
        this.str = text;
        this.hSpace = hSize;
        this.vSpace = vSize;
        this.mDegrees = degrees;
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.textSize = textSize;
        invalidate();
    }

    /**
     * 在View的源码当中并没有对AT_MOST和EXACTLY两个模式做出区分，
     * 也就是说View在wrap_content和match_parent两个模式下是完全相同的，
     * 都会是match_parent，
     * 显然这与我们平时用的View不同，
     * 所以我们要重写onMeasure方法。
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mDrawWidth, mDrawHeight);
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(mDrawWidth, heightSize);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, mDrawHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        //获取实际宽高
        mWidth = getWidth();
        mHeight = getHeight();
        //设置需要画的宽高
        mDrawWidth = mWidth * mScaleSize;
        mDrawHeight = mHeight * mScaleSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画水印
        drawWaterMark(canvas);
    }

    /**
     * 绘制水印
     *
     * @param canvas
     */
    private void drawWaterMark(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Rect rect = new Rect();
        paint.setTextSize(textSize);
        //获取文字长度和宽度
        paint.getTextBounds(str, 0, str.length(), rect);
        int textWidth = rect.width();
        int textHeight = rect.height();
        //获取每个单独的item的宽高
        float itemWidth = textWidth + hSpace;
        float itemHeight = textHeight + vSpace;
        //获取水平、垂直方向需要绘制的个数
        int hSize = (int) ((mDrawWidth / itemWidth) + 0.5);
        int vSize = (int) ((mDrawHeight / itemHeight) + 0.5);

        //X轴开始坐标
        float xStart = hSpace / 2;
        //Y轴开始坐标
        float yStart = vSpace / 2 + textHeight;

        //创建透明画布
        canvas.drawColor(bgColor);

        paint.setColor(textColor);
        //paint.setAlpha((int) (0.1 * 255));
        // 获取跟清晰的图像采样
        paint.setDither(true);
        paint.setFilterBitmap(true);

        canvas.save();
        //平移
        canvas.translate(-(mDrawWidth / 4), -(mDrawHeight / 4));
        //旋转对应角度
        canvas.rotate(mDegrees, mDrawWidth / 2, mDrawHeight / 2);
        //画X轴方向
        for (int i = 0; i < hSize; i++) {
            float xDraw = xStart + itemWidth * i;
            //画Y轴方向
            for (int j = 0; j < vSize; j++) {
                float yDraw = yStart + itemHeight * j;
                canvas.drawText(str, xDraw, yDraw, paint);
            }
        }
        canvas.restore();
    }
}
