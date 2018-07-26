package com.aspirecn.exam.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Button
import com.aspirecn.exam.AppLogger
import com.aspirecn.exam.R

/**
 * 标准按钮
 * @author ding
 * Created by ding on 30/03/2018.
 */
class MsButton : Button {

    /**
     * 画笔
     */
    private var mPaint = Paint()

    /**
     * 默认颜色
     */
    private var mNormalColor = 0x0

    /**
     * 默认颜色
     */
    private var mPressedColor = 0x0

    /**
     * 不可用的颜色
     */
    private var mDisabledColor = 0x0

    /**
     * 圆角半径
     */
    private var mCornerRadius: Int = 0

    /**
     * 背景颜色
     */
    private var mBackgroundColor = mNormalColor

    private var mCustomDrawable: CustomDrawable? = null


    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs, 0) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    /**
     * 初始化
     */
    private fun init(attrs: AttributeSet?) {

        AppLogger.i("init")

        val typedArray = resources.obtainAttributes(attrs, R.styleable.MsButton)

        mNormalColor = typedArray.getColor(R.styleable.MsButton_normal_color, Color.parseColor("#FF2B5CDC"))
        mPressedColor = typedArray.getColor(R.styleable.MsButton_pressed_color, Color.parseColor("#FF173FB1"))
        mDisabledColor = typedArray.getColor(R.styleable.MsButton_disabled_color, Color.LTGRAY)
        mCornerRadius = typedArray.getDimensionPixelSize(R.styleable.MsButton_corner, 10)

        typedArray?.recycle()

        mBackgroundColor = when (isEnabled) {
            true -> mNormalColor
            false -> mDisabledColor
        }

        mPaint.color = mBackgroundColor
        mPaint.isAntiAlias = true

        mCustomDrawable = CustomDrawable(mNormalColor)
        mCustomDrawable?.callback = this

    }

    override fun verifyDrawable(who: Drawable?): Boolean {
        super.verifyDrawable(who)
        return who == mCustomDrawable
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mCustomDrawable?.setBounds(0, 0, w, h)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {}
            MotionEvent.ACTION_UP -> {}
        }
        return mCustomDrawable!!.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {

        mCustomDrawable?.draw(canvas!!)

        super.onDraw(canvas)
    }

}