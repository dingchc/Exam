package com.cmcc.exam;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 两行文字
 *
 * @author ding
 *         Created by ding on 11/20/17.
 */

public class Line2TextView extends View {

    enum Align {

        LEFT(1), RIGHT(2);

        private int value;

        Align(int value) {

            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 第一行文字：文字颜色
     */
    private int mLine1TextColor;

    /**
     * 第一行文字：文字大小
     */
    private int mLine1TextSize;

    /**
     * 第一行文字：文字对齐
     */
    private int mLine1TextAlign;

    /**
     * 第一行文字：文字边距
     */
    private int mLine1TextMargin;

    /**
     * 第一行文字：文字内容
     */
    private String mLine1TextContent;

    /**
     * 第二行文字：文字颜色
     */
    private int mLine2TextColor;

    /**
     * 第二行文字：文字大小
     */
    private int mLine2TextSize;

    /**
     * 第二行文字：文字对齐
     */
    private int mLine2TextAlign;

    /**
     * 第二行文字：文字边距
     */
    private int mLine2TextMargin;

    /**
     * 第二行文字：文字内容
     */
    private String mLine2TextContent;

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 宽度
     */
    private int mWidth = 100;

    /**
     * 高度
     */
    private int mHeight = 100;

    /**
     * 两行文字的间距
     */
    private int mLineSpace = 20;

    /**
     * 画文字时候，文字X轴的位置
     */
    private int[] mTextOriginX = new int[2];

    /**
     * 画文字时候，文字Y轴的位置
     */
    private int[] mTextOriginY = new int[2];


    public Line2TextView(Context context) {
        this(context, null);
    }

    public Line2TextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Line2TextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        TypedArray typedArray = context.getResources().obtainAttributes(attrs, R.styleable.Line2TextView);

        if (typedArray != null) {

            // 第一行文字
            mLine1TextColor = typedArray.getColor(R.styleable.Line2TextView_ms_line1_text_color, 0xFF000000);
            mLine1TextSize = typedArray.getDimensionPixelSize(R.styleable.Line2TextView_ms_line1_text_size, 20);

            mLine1TextAlign = typedArray.getInt(R.styleable.Line2TextView_ms_line1_align, 1);
            mLine1TextMargin = typedArray.getDimensionPixelSize(R.styleable.Line2TextView_ms_line1_margin, 20);
            mLine1TextContent = typedArray.getString(R.styleable.Line2TextView_ms_line1_text);

            // 第二行文字
            mLine2TextColor = typedArray.getColor(R.styleable.Line2TextView_ms_line2_text_color, 0xFF000000);
            mLine2TextSize = typedArray.getDimensionPixelSize(R.styleable.Line2TextView_ms_line2_text_size, 20);

            mLine2TextAlign = typedArray.getInt(R.styleable.Line2TextView_ms_line2_align, 1);
            mLine2TextMargin = typedArray.getDimensionPixelSize(R.styleable.Line2TextView_ms_line2_margin, 20);
            mLine2TextContent = typedArray.getString(R.styleable.Line2TextView_ms_line2_text);

            // 两行文字的间距
            mLineSpace = typedArray.getDimensionPixelSize(R.styleable.Line2TextView_ms_line_space, 20);

            typedArray.recycle();

        }

        init();
    }

    /**
     * 初始化
     */
    private void init() {

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mWidth = getWidth();
        mHeight = getHeight();

        // 测量及计算文字的位置
        measureText();

        // 画文字
        drawLinesText(canvas);
    }

    /**
     * 测量及计算文字的位置
     */
    private void measureText() {

        if (mHeight <= 0) {
            return;
        }

        // 测量第一行的文字
        mPaint.setColor(mLine1TextColor);
        mPaint.setTextSize(mLine1TextSize);

        TextPaint textPaint1 = new TextPaint(mPaint);

        Paint.FontMetrics fontMetrics1 = textPaint1.getFontMetrics();

        int line1Height = (int) (fontMetrics1.descent - fontMetrics1.ascent + fontMetrics1.leading);


        // 测量第二行的文字
        mPaint.setColor(mLine2TextColor);
        mPaint.setTextSize(mLine2TextSize);

        TextPaint textPaint2 = new TextPaint(mPaint);

        Paint.FontMetrics fontMetrics2 = textPaint2.getFontMetrics();

        int line2Height = (int) (fontMetrics2.descent - fontMetrics2.ascent + fontMetrics2.leading);

        int topMargin = (mHeight - mLineSpace - line1Height - line2Height) / 2;

        // 计算第一行文字的X、Y轴的起始位置
        if (mLine1TextAlign == Align.LEFT.getValue()) {
            mTextOriginX[0] = mLine1TextMargin;

        } else if (mLine1TextAlign == Align.RIGHT.getValue()) {

            mTextOriginX[0] = mWidth - mLine1TextMargin - (int) textPaint1.measureText(mLine1TextContent);

            if (mTextOriginX[0] <= 0) {
                mTextOriginX[0] = 0;
            }
        }

        mTextOriginY[0] = topMargin + (int) Math.abs(fontMetrics1.ascent);

        // 计算第二行文字的X、Y轴的起始位置
        if (mLine2TextAlign == Align.LEFT.getValue()) {
            mTextOriginX[1] = mLine2TextMargin;

        } else if (mLine2TextAlign == Align.RIGHT.getValue()) {
            mTextOriginX[1] = mWidth - mLine2TextMargin - (int) textPaint2.measureText(mLine2TextContent);

            if (mTextOriginX[1] <= 0) {
                mTextOriginX[1] = 0;
            }
        }

        mTextOriginY[1] = topMargin + line1Height + mLineSpace + (int) Math.abs(fontMetrics2.ascent);

    }

    /**
     * 画文字
     *
     * @param canvas 画布
     */
    private void drawLinesText(Canvas canvas) {

        mPaint.setColor(mLine1TextColor);
        mPaint.setTextSize(mLine1TextSize);

        canvas.drawText(mLine1TextContent, mTextOriginX[0], mTextOriginY[0], mPaint);

        mPaint.setColor(mLine2TextColor);
        mPaint.setTextSize(mLine2TextSize);

        canvas.drawText(mLine2TextContent, mTextOriginX[1], mTextOriginY[1], mPaint);

    }
}
