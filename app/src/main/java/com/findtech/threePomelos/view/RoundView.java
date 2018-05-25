package com.findtech.threePomelos.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.findtech.threePomelos.R;


/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/09
 */

public class RoundView extends View {

    private Paint paint;
    private Paint textPaint;
    private Paint paintBg;
    private int radius  ;
    private int strokeWidth;
    private int widthDefault;
    private int color;
    private int width = 0;
    private float degree = 288f;
    private String text = "80%";
    private int textSize;
    private Rect textRect;


    public RoundView(Context context) {
        this(context, null);
    }

    public RoundView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundView);
        color = typedArray.getColor(R.styleable.RoundView_colorRound, Color.RED);
        strokeWidth = typedArray.getDimensionPixelSize(R.styleable.RoundView_strokeWidthRound,0);
        radius = typedArray.getDimensionPixelSize(R.styleable.RoundView_radiusRound,100);
        textSize = typedArray.getDimensionPixelSize(R.styleable.RoundView_textSizeRound,40);
        typedArray.recycle();
        widthDefault =radius * 2;
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth / 2);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(color);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.argb(255,34,34,34));
        textPaint.setTextSize(textSize);
        textRect = new Rect();
        textPaint.getTextBounds(text,0,text.length(),textRect);

        paintBg = new Paint();
        paintBg.setStyle(Paint.Style.STROKE);
        paintBg.setStrokeWidth(strokeWidth / 2);
        paintBg.setColor(Color.argb(255,238,238,238));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(
                clampSize(widthDefault,widthMeasureSpec),
                clampSize(widthDefault,heightMeasureSpec));
    }

    private  int clampSize(int size, int spec) {
        int specMode = MeasureSpec.getMode(spec);
        int specSize = MeasureSpec.getSize(spec);
        switch (specMode) {
            case MeasureSpec.EXACTLY: {
                return specSize;
            }
            case MeasureSpec.AT_MOST: {
                return size;
            }
            case MeasureSpec.UNSPECIFIED:
            default: {
                return size;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getMeasuredWidth();
        canvas.drawCircle(width/2,width/2,radius - strokeWidth/2,paintBg);

        RectF rectF = new RectF(strokeWidth/2,strokeWidth/2,
                width -strokeWidth/2,width -strokeWidth/2);
        canvas.drawText(text,width/2 - textRect.width()/2,width/2 + textRect.height()/2,textPaint);
        canvas.drawArc(rectF,-90,degree,false,paint);

    }

    public void setDegree(int text) {
        this.degree = (text *1.0f /100) * 360;
        this.text = text + "%";
        invalidate();
    }
}
