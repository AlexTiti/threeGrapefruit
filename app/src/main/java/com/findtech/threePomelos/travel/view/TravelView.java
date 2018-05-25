package com.findtech.threePomelos.travel.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.findtech.threePomelos.R;

import java.util.ArrayList;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/28
 */

public class TravelView extends View {

    private int width, height;
    private int lineWidth;
    private int dashWidth;
    private Paint mPaint, circlePaint;
    private int lineColor;
    private int circleRadius, circleCenterX, circleCenterY, circleStroke;
    private int paintWidth;
    private int heightSize;
    private boolean isShowLine = true;

    public TravelView(Context context) {
        super(context);
    }

    public TravelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TravelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
        initPaint();
    }


    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.TravelView, defStyleAttr, 0);
        lineColor = a.getColor(R.styleable.TravelView_lineColor, lineColor);
        lineWidth = a.getDimensionPixelSize(R.styleable.TravelView_lineWith, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, lineWidth, getResources().getDisplayMetrics()));
        dashWidth = a.getDimensionPixelSize(R.styleable.TravelView_dashWith, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dashWidth, getResources().getDisplayMetrics()));
        circleRadius = a.getDimensionPixelSize(R.styleable.TravelView_circleRadius, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, circleRadius, getResources().getDisplayMetrics()));
        circleCenterX = a.getDimensionPixelSize(R.styleable.TravelView_circleCenterX, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, circleCenterX, getResources().getDisplayMetrics()));
        circleCenterY = a.getDimensionPixelSize(R.styleable.TravelView_circleCenterY, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, circleCenterY, getResources().getDisplayMetrics()));
        circleStroke = a.getDimensionPixelSize(R.styleable.TravelView_circleStroke, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, circleStroke, getResources().getDisplayMetrics()));

        paintWidth = a.getDimensionPixelSize(R.styleable.TravelView_paintWidth, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, paintWidth, getResources().getDisplayMetrics()));
        heightSize = a.getDimensionPixelSize(R.styleable.TravelView_heightSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, heightSize, getResources().getDisplayMetrics()));
        a.recycle();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawVertical(canvas);
    }

    private void initPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setColor(lineColor);
        paint.setStrokeWidth(paintWidth);
        mPaint = paint;

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setColor(lineColor);
        paint.setStrokeWidth(circleStroke);
        paint.setStyle(Paint.Style.STROKE);
        circlePaint = paint;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightModule = MeasureSpec.getMode(heightMeasureSpec);
        switch (heightModule) {
            case MeasureSpec.AT_MOST:
                height = 200;
                break;
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.EXACTLY:
                height = heightSize - getPaddingTop() - getPaddingBottom();
                break;
            default:
                break;
        }
        width = widthSize + getPaddingLeft() + getPaddingRight();
        setMeasuredDimension(width, height);
    }

    private void drawVertical(Canvas canvas) {
        canvas.save();
        canvas.translate(circleCenterX, circleCenterY + circleStroke);
        canvas.drawCircle(0, 0, circleRadius, circlePaint);
        canvas.translate(0, circleRadius + circleStroke);
        drawLine(canvas);
        canvas.restore();
    }

    public void drawLine(Canvas canvas) {
        if (!isShowLine) {
            return;
        }
        float totalHeight = 0;
        float[] pts = {0, 0, 0, lineWidth};
        while (totalHeight <= heightSize) {
            canvas.drawLines(pts, mPaint);
            canvas.translate(0, lineWidth + dashWidth);
            totalHeight += lineWidth + dashWidth;
        }
    }

    public void setShowLine(boolean showLine) {
        isShowLine = showLine;
        invalidate();
    }
}
