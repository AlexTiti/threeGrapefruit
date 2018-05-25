package com.findtech.threePomelos.travel.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.travel.bean.FrequencyData;
import com.findtech.threePomelos.travel.bean.KilometerData;
import com.findtech.threePomelos.travel.iterator.FrequencyStore;
import com.findtech.threePomelos.travel.iterator.Iterator;
import com.findtech.threePomelos.utils.MyCalendar;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


/**
 * @author Alex
 */
public class TravelFrequencyView extends View {
    private int signHeight = 7;
    private int weekOrMonth = 7;

    /**
     * 2个大刻度之间间距，默认为1
     */
    private int scaleLimit = 1;
    private float scaleHightRate = 1;
    private int diverLine = 10;
    private int diverLineColor = 0x38172058;
    private int triangleHeight = 40;
    /**
     * 数值和坐标的距离
     */
    private int textLineDistance = 10;
    /**
     * 尺子高度
     */
    private int rulerHeight = 100;
    /**
     * 尺子和屏幕顶部以及结果之间的高度
     */
    private int rulerToResultgap = rulerHeight / 10;
    /**
     * 刻度平分多少份
     */
    private int scaleCount = 1;
    /**
     * 刻度间距
     */
    private int scaleGap = 100;
    /**
     * 刻度最小值
     */
    private int minScale = 0;
    /**
     * 第一次显示的刻度
     */
    private int firstScale = -1;
    /**
     * 刻度最大值
     */
    private int maxScale = 100;
    /**
     * 背景颜色
     */
    private int bgColor = 0;
    /**
     * 小刻度的颜色
     */
    private int selectedScaleColor = 0xff999999;
    /**
     * 中刻度的颜色
     */
    private int midScaleColor = 0xff666666;
    /**
     * 大刻度的颜色
     */
    private int signColor = 0xff50b586;
    /**
     * 刻度颜色
     */
    private int scaleNumColor = 0xff333333;
    /**
     * 结果值颜色
     */
    private int resultNumColor = 0xff50b586;

    /**
     * 小刻度粗细大小
     */
    private int smallScaleStroke = 1;
    /**
     * 中刻度粗细大小
     */
    private int midScaleStroke = 2;
    /**
     * 大刻度粗细大小
     */
    private int largeScaleStroke = 3;
    /**
     * 结果字体大小
     */
    private int resultNumTextSize = 14;
    /**
     * 刻度字体大小
     */
    private int scaleNumTextSize = 14;
    /**
     * 单位字体大小
     */
    private int unitTextSize = 13;
    /**
     * 是否显示刻度结果
     */
    private boolean showScaleResult = true;
    /**
     * 背景是否显示圆角
     */
    private boolean isBgRoundRect = true;
    /**
     * 结果回调
     */
    private OnChooseResultListener OnChooseResultListener;
    /**
     * 滑动选择刻度
     */
    private float computeScale = -1;
    /**
     * 当前刻度
     */
    public int currentScale = firstScale;
    private int cavansTranslateDistance = 0;
    private ValueAnimator valueAnimator;
    private VelocityTracker velocityTracker = VelocityTracker.obtain();
    private String resultText = String.valueOf(firstScale);

    private HashMap<String, Integer> frequencyStore;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
    private Calendar calendar;

    /**
     * 小刻度线的画笔
     */
    private Paint selectedPaint;
    /**
     * 每个整数刻度线的画笔
     */
    private Paint midScalePaint;
    /**
     * 中间矩形的画笔
     */
    private Paint rectanglePaint;
    private Paint scaleNumPaint;
    private Paint linePaint;
    /**
     * 结果的画笔
     */
    private Paint resultNumPaint;

    private Rect scaleNumRect;
    /**
     * 结果范围
     */
    private Rect resultNumRect;

    private int height, width;

    /**
     * 中间刻度线的高度
     */
    private int lagScaleHeight;
    private float downX;
    /**
     * 移动的距离
     */
    private float moveX = 0;
    private float lastMoveX = 0;
    /**
     * 手指是否抬起
     */
    private boolean isUp = false;
    private final int half = 2;
    private final int speed = 50;

    public TravelFrequencyView(Context context) {
        this(context, null);
    }

    public TravelFrequencyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TravelFrequencyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttr(attrs, defStyleAttr);
        init();
    }


    private void setAttr(AttributeSet attrs, int defStyleAttr) {

        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.TravelRulerView, defStyleAttr, 0);

        scaleLimit = a.getInt(R.styleable.TravelRulerView_scaleLimit, scaleLimit);

        rulerHeight = a.getDimensionPixelSize(R.styleable.TravelRulerView_rulerHeight, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, rulerHeight, getResources().getDisplayMetrics()));

        textLineDistance = a.getDimensionPixelSize(R.styleable.TravelRulerView_textLineDis, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, textLineDistance, getResources().getDisplayMetrics()));

        diverLine = a.getDimensionPixelSize(R.styleable.TravelRulerView_diverLine, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, diverLine, getResources().getDisplayMetrics()));
        diverLineColor = a.getColor(R.styleable.TravelRulerView_diverLineColor, diverLineColor);
        rulerToResultgap = a.getDimensionPixelSize(R.styleable.TravelRulerView_rulerToResultgap, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, rulerToResultgap, getResources().getDisplayMetrics()));

        triangleHeight = a.getDimensionPixelSize(R.styleable.TravelRulerView_triangleHeight, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, triangleHeight, getResources().getDisplayMetrics()));
        scaleCount = a.getInt(R.styleable.TravelRulerView_scaleCount, scaleCount);

        scaleGap = a.getDimensionPixelSize(R.styleable.TravelRulerView_scaleGap, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, scaleGap, getResources().getDisplayMetrics()));

        minScale = a.getInt(R.styleable.TravelRulerView_minScale, minScale) / scaleLimit;

        firstScale = a.getInt(R.styleable.TravelRulerView_firstScale, firstScale) / scaleLimit;

        maxScale = a.getInt(R.styleable.TravelRulerView_maxScale, maxScale) / scaleLimit;

        bgColor = a.getColor(R.styleable.TravelRulerView_bgColor, bgColor);

        selectedScaleColor = a.getColor(R.styleable.TravelRulerView_selectedScaleColor, selectedScaleColor);

        midScaleColor = a.getColor(R.styleable.TravelRulerView_midScaleColor, midScaleColor);

        signColor = a.getColor(R.styleable.TravelRulerView_signColor, signColor);

        scaleNumColor = a.getColor(R.styleable.TravelRulerView_scaleNumColor, scaleNumColor);

        resultNumColor = a.getColor(R.styleable.TravelRulerView_resultNumColor, resultNumColor);


        smallScaleStroke = a.getDimensionPixelSize(R.styleable.TravelRulerView_smallScaleStroke, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, smallScaleStroke, getResources().getDisplayMetrics()));

        midScaleStroke = a.getDimensionPixelSize(R.styleable.TravelRulerView_midScaleStroke, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, midScaleStroke, getResources().getDisplayMetrics()));
        largeScaleStroke = a.getDimensionPixelSize(R.styleable.TravelRulerView_largeScaleStroke, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, largeScaleStroke, getResources().getDisplayMetrics()));
        resultNumTextSize = a.getDimensionPixelSize(R.styleable.TravelRulerView_resultNumTextSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, resultNumTextSize, getResources().getDisplayMetrics()));

        scaleNumTextSize = a.getDimensionPixelSize(R.styleable.TravelRulerView_scaleNumTextSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, scaleNumTextSize, getResources().getDisplayMetrics()));
        unitTextSize = a.getDimensionPixelSize(R.styleable.TravelRulerView_unitTextSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, unitTextSize, getResources().getDisplayMetrics()));

        showScaleResult = a.getBoolean(R.styleable.TravelRulerView_showScaleResult, showScaleResult);
        isBgRoundRect = a.getBoolean(R.styleable.TravelRulerView_isBgRoundRect, isBgRoundRect);
        a.recycle();
    }


    /**
     * 初始化数据
     */
    private void init() {

        Paint paintScale = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintScale.setColor(selectedScaleColor);
        paintScale.setStyle(Paint.Style.FILL);
        paintScale.setStrokeCap(Paint.Cap.ROUND);
        paintScale.setStrokeWidth(10);
        selectedPaint = paintScale;

        paintScale = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintScale.setColor(diverLineColor);
        paintScale.setStyle(Paint.Style.FILL);
        paintScale.setStrokeCap(Paint.Cap.ROUND);
        paintScale.setStrokeWidth(diverLine);
        linePaint = paintScale;

        paintScale = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintScale.setColor(midScaleColor);
        paintScale.setStyle(Paint.Style.FILL);
        paintScale.setStrokeCap(Paint.Cap.ROUND);
        paintScale.setStrokeWidth(10);
        midScalePaint = paintScale;

        paintScale = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintScale.setColor(signColor);
        paintScale.setStyle(Paint.Style.FILL);
        paintScale.setStrokeWidth(midScaleStroke + 10);
        rectanglePaint = paintScale;

        Paint scaleNum = new Paint(Paint.ANTI_ALIAS_FLAG);
        scaleNum.setColor(scaleNumColor);
        scaleNum.setTextSize(scaleNumTextSize);
        scaleNumPaint = scaleNum;

        scaleNum = new Paint(Paint.ANTI_ALIAS_FLAG);
        scaleNum.setColor(resultNumColor);
        scaleNum.setStyle(Paint.Style.FILL);
        scaleNum.setTextSize(resultNumTextSize);
        resultNumPaint = scaleNum;
        resultNumRect = new Rect();
        scaleNumRect = new Rect();
        resultNumPaint.getTextBounds(resultText, 0, resultText.length(), resultNumRect);

        lagScaleHeight = rulerHeight - rulerToResultgap * 2 - triangleHeight - (showScaleResult ? resultNumRect.height() : 0);
        valueAnimator = new ValueAnimator();

        cavansTranslateDistance = scaleGap * 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightModule = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        switch (heightModule) {
            case MeasureSpec.AT_MOST:
                height = rulerHeight + (showScaleResult ? resultNumRect.height() : 0) + rulerToResultgap * 2 + getPaddingTop() + getPaddingBottom();
                break;
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.EXACTLY:
                height = heightSize + getPaddingTop() + getPaddingBottom();
                break;
            default:
                break;
        }
        width = widthSize + getPaddingLeft() + getPaddingRight();
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.translate(0, (showScaleResult ? resultNumRect.height() : 0) + rulerToResultgap + lagScaleHeight + triangleHeight * 2);

        drawRuler(canvas);
        drawScaleAndNum(canvas);

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float currentX = event.getX();
        isUp = false;
        //计算速度的时间
        velocityTracker.computeCurrentVelocity(500);
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下时如果属性动画还没执行完,就终止,记录下当前按下点的位置
                if (valueAnimator != null && valueAnimator.isRunning()) {
                    valueAnimator.end();
                    valueAnimator.cancel();
                }
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                //滑动时候,通过假设的滑动距离,做超出左边界以及右边界的限制。
                moveX = currentX - downX + lastMoveX;
                if (moveX >= width / half - cavansTranslateDistance - 30) {

                    moveX = width / 2 - cavansTranslateDistance;
                } else if (moveX <= getWhichScaleMoveX(maxScale)) {
                    moveX = getWhichScaleMoveX(maxScale);
                }
                break;
            case MotionEvent.ACTION_UP:
                //手指抬起时候制造惯性滑动
                lastMoveX = moveX;
                int xVelocity = (int) velocityTracker.getXVelocity();
                autoVelocityScroll(xVelocity);
                velocityTracker.clear();
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }

    /**
     * 如果在手抬起后速度过大，就开始惯性滑动
     *
     * @param xVelocity 速度
     */
    private void autoVelocityScroll(int xVelocity) {
        //惯性滑动的代码,速率和滑动距离,以及滑动时间需要控制的很好,应该网上已经有关于这方面的算法了吧。。这里是经过N次测试调节出来的惯性滑动
        if (Math.abs(xVelocity) < speed) {
            isUp = true;
            return;
        }
        if (valueAnimator.isRunning()) {
            return;
        }
        valueAnimator = ValueAnimator.ofInt(xVelocity / 40,0).setDuration(Math.abs(xVelocity / 10));
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveX += (int) animation.getAnimatedValue();
                //--------------------------------------------滑动月结
                if (moveX >= width / half - cavansTranslateDistance - 30) {
                    moveX = width / 2 - cavansTranslateDistance;

                } else if (moveX <= getWhichScaleMoveX(maxScale)) {
                    moveX = getWhichScaleMoveX(maxScale);
                }
                lastMoveX = moveX;
                invalidate();
            }

        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isUp = true;
                invalidate();
            }
        });

        valueAnimator.start();
    }

    /**
     * 计算滑动的距离
     *
     * @param scale 设置的数值 去除canvas的移动
     * @return 距离
     */
    private float getWhichScaleMoveX(float scale) {
        return width / 2 - cavansTranslateDistance - scaleGap * scaleCount * (scale - minScale);
    }

    private void drawRuler(Canvas canvas) {
        String string = "30";
        Rect rect = new Rect();
        scaleNumPaint.getTextBounds(string, 0, string.length(), rect);
        if (frequencyStore == null) {
            return;
        }
        int heightScale = 0;
        int distance = 0;
        while (heightScale >= -lagScaleHeight) {
            String number = String.valueOf(distance);
            scaleNumPaint.getTextBounds(number, 0, number.length(), scaleNumRect);
            canvas.drawText(number, (rect.width() - scaleNumRect.width())/2, heightScale + scaleNumRect.height() / 2, scaleNumPaint);
            canvas.drawLine(textLineDistance + rect.width(), heightScale, width, heightScale, linePaint);
            if (weekOrMonth == 7) {
                distance++;
            } else {
                distance += 3;
            }
            heightScale -= signHeight;
        }
    }

    /**
     * 绘制刻度和数字
     *
     * @param canvas canvas
     */
    private void drawScaleAndNum(Canvas canvas) {
        if (frequencyStore == null) {
            return;
        }
        if (weekOrMonth == 7) {
            calendar = MyCalendar.getMondayCalendar();
        } else {
            calendar = MyCalendar.getMonthCalendar();
        }
        //移动画布到结果值的下面
        canvas.translate(cavansTranslateDistance, 0);
        //确定刻度位置
        int num1;
        float num2;

        //第一次进来的时候计算出默认刻度对应的假设滑动的距离moveX
        if (firstScale != -1) {
            //如果设置了默认滑动位置，计算出需要滑动的距离
            moveX = getWhichScaleMoveX(firstScale);
            lastMoveX = moveX;
            //将结果置为-1，下次不再计算初始位置
            firstScale = -1;
        }
        //弹性滑动到目标刻度
        if (computeScale != -1) {
            lastMoveX = moveX;
            if (valueAnimator != null && !valueAnimator.isRunning()) {
                valueAnimator = ValueAnimator.ofFloat(getWhichScaleMoveX(currentScale), getWhichScaleMoveX(computeScale));
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        moveX = (float) animation.getAnimatedValue();
                        lastMoveX = moveX;
                        invalidate();
                    }
                });
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        //这里是滑动结束时候回调给使用者的结果值
                        computeScale = -1;

                    }
                });
                valueAnimator.setDuration(Math.abs((long) ((getWhichScaleMoveX(computeScale) - getWhichScaleMoveX(currentScale)) / 100)));
                valueAnimator.start();
            }
        }
        //滑动刻度的整数部分
        num1 = -(int) (moveX / scaleGap);
        //滑动刻度的小数部分
        num2 = (moveX % scaleGap);
        //保存当前画布
        canvas.save();
        //准备开始绘制当前屏幕,从最左面开始
        int rulerRight = 0;

        //这部分代码主要是计算手指抬起时，惯性滑动结束时，刻度需要停留的位置
        if (isUp) {
            //计算滑动位置距离整点刻度的小数部分距离
            num2 = ((moveX - width / 2 % scaleGap) % scaleGap);
            if (num2 <= 0) {
                num2 = scaleGap - Math.abs(num2);
            }
            //当前滑动位置距离左边整点刻度的距离
            int leftScroll = (int) Math.abs(num2);
            //当前滑动位置距离右边整点刻度的距离
            int rightScroll = (int) (scaleGap - Math.abs(num2));
            //最终计算出当前位置到整点刻度位置需要滑动的距离
            float moveX2 = num2 <= scaleGap / 2 ? moveX - leftScroll : moveX + rightScroll;
            //手指抬起，并且当前没有惯性滑动在进行，创建一个惯性滑动
            if (valueAnimator != null && !valueAnimator.isRunning()) {
                valueAnimator = ValueAnimator.ofFloat(moveX, moveX2);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        moveX = (float) animation.getAnimatedValue();
                        lastMoveX = moveX;
                        invalidate();
                    }
                });
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        //这里是滑动结束时候回调给使用者的结果值
                        if (OnChooseResultListener != null) {
                            OnChooseResultListener.onEndResult(resultText);
                        }
                    }
                });
                valueAnimator.setDuration(300);
                valueAnimator.start();
                isUp = false;
            }
            //重新计算当前滑动位置的整数以及小数位置
            num1 = (int) -(moveX / scaleGap);
            num2 = (moveX % scaleGap);
        }
        canvas.translate(num2, 0);

        //去除canvas的移动
        float v = (width / 2 - cavansTranslateDistance - moveX) / (scaleGap * scaleCount) + minScale;
        currentScale = new WeakReference<>(new BigDecimal(v * scaleLimit)).get().setScale(1, BigDecimal.ROUND_HALF_UP).intValue();
        if (currentScale >= 0 && currentScale <= maxScale) {
            if (weekOrMonth == 7) {
                calendar.add(Calendar.WEEK_OF_YEAR, currentScale - maxScale);
            } else {
                calendar.add(Calendar.MONTH, currentScale - maxScale);
            }
            String resultDate = dateFormat.format(calendar.getTime().getTime());
            resultText = frequencyStore.containsKey(resultDate) ? String.valueOf(frequencyStore.get(resultDate)) : "0";
        }
        //绘制当前屏幕可见刻度,不需要裁剪屏幕,while循环只会执行·屏幕宽度/刻度宽度·次,大部分的绘制都是if(curDis<width)这样子内存暂用相对来说会比较高。。
        canvas.save();
        if (weekOrMonth == 7) {
            calendar.add(Calendar.WEEK_OF_YEAR, num1 - currentScale);
        } else {
            calendar.add(Calendar.MONTH, num1 - currentScale);
        }
        while (rulerRight < width) {
            if (num1 % scaleCount == 0 && num1 <= maxScale) {
                //绘制整点刻度以及文字
                boolean b = (moveX >= 0 && rulerRight < moveX - scaleGap) || width / 2 - rulerRight <= getWhichScaleMoveX(maxScale + 1) - moveX;

                if (!b) {
                    //绘制日期
                    String data = dateFormat.format(calendar.getTime().getTime());
                    float kilometer = 0f;
                    if (frequencyStore.containsKey(data)) {
                        kilometer = frequencyStore.get(data);
                    }
                    data = data.substring(5);
                    scaleNumPaint.getTextBounds(data, 0, data.length(), scaleNumRect);
                    resultNumPaint.getTextBounds(data, 0, data.length(), resultNumRect);
                    float startY = -kilometer * scaleHightRate;
                    float textDistance = (scaleGap - midScaleStroke) / 2;
                    if (num1 == currentScale) {
                        canvas.drawLine(0, 0, 0, startY, rectanglePaint);
                        canvas.drawLine(-midScaleStroke / 2, startY, midScaleStroke / 2,
                                startY, selectedPaint);
                        canvas.drawText(data + "", -resultNumRect.width() / 2 - midScaleStroke / 2 - textDistance,
                                resultNumRect.height() / 2 + diverLine / 2 + triangleHeight, resultNumPaint);
                    } else if (num1 == currentScale + 1) {
                        canvas.drawLine(-midScaleStroke / 2, startY, midScaleStroke / 2,
                                startY, midScalePaint);
                        canvas.drawText(data + "", -resultNumRect.width() / 2 - midScaleStroke / 2 - textDistance,
                                resultNumRect.height() / 2 + diverLine / 2 + triangleHeight, resultNumPaint);
                    } else {
                        canvas.drawLine(-midScaleStroke / 2, startY, midScaleStroke / 2,
                                startY, midScalePaint);
                        canvas.drawText(data + "", -scaleNumRect.width() / 2 - midScaleStroke / 2 - textDistance,
                                scaleNumRect.height() / 2 + diverLine / 2 + triangleHeight, scaleNumPaint);
                    }
                    if (num1 == maxScale) {
                        if (currentScale == maxScale) {
                            canvas.drawText(data + "", midScaleStroke + textDistance - resultNumRect.width() / 2,
                                    resultNumRect.height() / 2 + diverLine / 2 + triangleHeight, resultNumPaint);
                        } else {
                            canvas.drawText(data + "", midScaleStroke + textDistance - scaleNumRect.width() / 2,
                                    scaleNumRect.height() / 2 + diverLine / 2 + triangleHeight, scaleNumPaint);
                        }
                    }
                }
            }
            if (weekOrMonth == 7) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
            } else {
                calendar.add(Calendar.MONTH, 1);
            }
            ++num1;  //刻度加1
            //绘制屏幕的距离在原有基础上+1个刻度间距
            rulerRight += scaleGap;
            //移动画布到下一个刻度
            canvas.translate(scaleGap, 0);

        }
        canvas.restore();
        canvas.restore();

    }

    public void computeScrollTo(float scale) {
        scale = scale / scaleLimit;
        if (scale < minScale || scale > maxScale) {
            return;
        }
        computeScale = scale;
        invalidate();
    }

    public interface OnChooseResultListener {
        /**
         * 结束是的回调
         *
         * @param result
         */
        void onEndResult(String result);

    }


    /**
     * 注册监听
     *
     * @param OnChooseResultListener
     */
    public void setOnChooseResultListener(OnChooseResultListener OnChooseResultListener) {
        this.OnChooseResultListener = OnChooseResultListener;
    }

    public void setDataArrayList(HashMap<String, Integer> frequencyStore, int firstScale, float kilometer) {
        this.frequencyStore = frequencyStore;
        this.firstScale = firstScale;
        scaleHightRate = lagScaleHeight / kilometer;
        this.maxScale = firstScale;
        signHeight = lagScaleHeight / 7;
        weekOrMonth = 7;
        invalidate();
    }

    public void setDataArrayListMonth(HashMap<String, Integer> frequencyStore, int firstScale, float kilometer) {
        this.frequencyStore = frequencyStore;
        this.firstScale = firstScale;
        scaleHightRate = lagScaleHeight / kilometer;
        this.maxScale = firstScale;
        signHeight = lagScaleHeight / 10;
        weekOrMonth = 10;
        invalidate();
    }


}
