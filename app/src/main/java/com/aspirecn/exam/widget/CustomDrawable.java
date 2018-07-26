package com.aspirecn.exam.widget;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import com.aspirecn.exam.AppLogger;

/**
 * 自定义Drawable
 *
 * @author ding
 *         Created by ding on 2018/4/27.
 */

public class CustomDrawable extends Drawable {

    private int mAlpha = 255;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mCenterX = 0, mCenterY = 0;

    private int mRadius = 150;

    private int mWidth, mHeight;

    private int mMaxRadius = 0;

    private boolean mIsStartAnim = false;

    private ValueAnimator animator = new ValueAnimator();

    private int mNormalColor = 0xFF00FF00;

    public CustomDrawable() {

        this(0xFF00FF00);
    }

    public CustomDrawable(int normalColor) {

        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mNormalColor = normalColor;
        mPaint.setColor(Color.GRAY);
    }


    @Override
    public void draw(@NonNull Canvas canvas) {

        Rect bound = getBounds();

        mWidth = bound.right - bound.left;
        mHeight = bound.bottom - bound.top;

        mMaxRadius = (int) Math.sqrt(mWidth * mWidth + mHeight * mHeight);

        AppLogger.i("mMaxRadius="+mMaxRadius);

        if (mIsStartAnim) {
            canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
            increase();
        } else {
            canvas.drawColor(mNormalColor);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mCenterX = (int) event.getX();
                mCenterY = (int) event.getY();

                if (!mIsStartAnim) {
                    mIsStartAnim = true;
                    increase();
                }
                break;
            case MotionEvent.ACTION_UP:
                mIsStartAnim = false;
                mRadius = 0;
                invalidateSelf();
                break;
            default:
        }
        return true;
    }

    private void increase() {

        if (mCenterX + mRadius > mMaxRadius && mCenterY + mRadius > mMaxRadius) {
            mIsStartAnim = false;
            return;
        }

        mRadius += 20;
        invalidateSelf();
    }

    @Override
    public void setAlpha(int alpha) {

        mAlpha = alpha;
    }


    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

        mPaint.setColorFilter(colorFilter);
    }


    @Override
    public int getOpacity() {

        return PixelFormat.TRANSLUCENT;
    }
}
