package com.cmcc.exam.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.AttributeSet
import android.widget.Button
import com.cmcc.exam.AppLogger
import com.cmcc.exam.R

/**
 * 标准按钮
 * @author ding
 * Created by ding on 30/03/2018.
 */
class MsRippleButton : Button {

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

    private var mRippleDrawable: RippleDrawable? = null


    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs, R.attr.buttonBarButtonStyle) {
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

        mCustomDrawable = CustomDrawable()

        val stateList = ColorStateList.valueOf(Color.parseColor("#EEEEEE"))

        val contentDrawable = GradientDrawable()
        contentDrawable.setColor(Color.BLUE)
        contentDrawable.cornerRadius = mCornerRadius.toFloat()
        contentDrawable.setStroke(1, Color.GRAY)

        mRippleDrawable = RippleDrawable(stateList, contentDrawable, null)

        background = mRippleDrawable!!

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    private fun getShape(): Drawable {

        val drawable = ShapeDrawable(object : RectShape() {

            override fun draw(canvas: Canvas?, paint: Paint?) {
                super.draw(canvas, paint)

                AppLogger.i("width=$width , height=$height")
                canvas?.drawRect(Rect(0, 0, width.toInt(), height.toInt()), paint)
            }
        })

        return drawable
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)

        mBackgroundColor = when (enabled) {
            true -> mNormalColor
            else -> mDisabledColor
        }

        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {

//        mPaint.color = mBackgroundColor
//        mPaint.style = Paint.Style.FILL
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            canvas?.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), mCornerRadius.toFloat(), mCornerRadius.toFloat(), mPaint)
//        } else {
//            canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), mPaint)
//        }


        super.onDraw(canvas)

//        mRippleDrawable?.draw(canvas!!)
    }

}