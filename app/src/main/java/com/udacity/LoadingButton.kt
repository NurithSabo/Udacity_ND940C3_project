package com.udacity

import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var buttonText = ""
    private var buttonTextColor : Int = Color.GREEN
    private var buttonColor: Int = 0
    private var buttBgCol = 0
    private var roundColor = 0
    private var valueAnimator = ValueAnimator()
    private var isChecked = false
    @Volatile
    private var progress: Double = 0.0
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when(new){
            ButtonState.Loading -> {
                isClickable = false
                valueAnimator.start()
                invalidate()
                requestLayout()
                animate()
            }
            ButtonState.Completed -> {
                isClickable = true
                valueAnimator.cancel()
                invalidate()
                requestLayout()
            }

            ButtonState.Clicked -> {
                Toast.makeText(context, R.string.ToastText, Toast.LENGTH_SHORT).show()
                buttonState = ButtonState.Completed
            }
        }
    }


    fun hasCompletedDownload()
    {
        valueAnimator.cancel()
        buttonState = ButtonState.Completed
        invalidate()
        requestLayout()
    }

    init {
        isClickable = true
        buttonState = ButtonState.Completed

        valueAnimator = AnimatorInflater.loadAnimator(
                context, R.animator.loading_animations
        ) as ValueAnimator

        valueAnimator = ValueAnimator.ofInt(0, 100).apply {
            duration = 3000
            interpolator = LinearInterpolator()
            repeatCount = -1
            repeatMode = ObjectAnimator.RESTART
            addUpdateListener {
                progress = (this.animatedValue as Int).toDouble()
                invalidate()
                requestLayout()
            }
        }
        context.withStyledAttributes(attrs, R.styleable.LoadingButton)
        {
            buttonText = getString(R.styleable.LoadingButton_text) ?: "Button"
            buttonColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            buttonTextColor = getColor(R.styleable.LoadingButton_textColor, 0)
            buttBgCol = getColor(R.styleable.LoadingButton_downloadingBackgroundColor, 0)
            roundColor = getColor(R.styleable.LoadingButton_circleColor, 0)
        }

        }
    //end init

    override fun performClick() : Boolean
    {
        super.performClick()
        when
        {
            buttonState == ButtonState.Completed && isChecked ->
            {
                buttonState = ButtonState.Loading
            }
        }
        return true
    }

    fun checked(Checked : Boolean)
    {
        isChecked = Checked
    }



        @SuppressLint("DrawAllocation")
        override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //Set button background
        paint.color = buttonColor

        //Draw the button
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

            //process on the button:
            if (buttonState == ButtonState.Loading )
            {
                paint.color = buttBgCol     //2nd color

                //draw the process:
                canvas?.drawRect( 0f, 0f, (widthSize * (progress / 100)).toFloat(), height.toFloat(), paint)
                buttonText = resources.getString(R.string.loading)

                //circle:
                val rectEf = RectF(
                        800f,
                        25f,
                        925f,
                        150f
                )

                //color of the circle:
                paint.color = roundColor

                //draw the circle:
                canvas?.drawArc(rectEf, 0f, (360 * (progress / 100)).toFloat(), true, paint)
            }

            //Text of the button:
            if (buttonState == ButtonState.Loading)
            {
                buttonText = resources.getString(R.string.loading)
            }
            else
            {
                buttonText = resources.getString(R.string.button_text)
            }

            //button text color
            paint.color = buttonTextColor

            //draw button text:
            canvas?.drawText(buttonText, (widthSize / 2).toFloat(), ((heightSize + 30) / 2).toFloat(), paint)

        } //End onDraw

    override fun onMeasure(widthMeasureSpec : Int, heightMeasureSpec : Int)
    {
        val minw : Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w : Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h : Int = resolveSizeAndState(
                MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun setProgressAndButtonState(progress: Double, state: ButtonState) {
        this.progress = progress
        this.buttonState = state
    }
}