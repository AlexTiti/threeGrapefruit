package com.findtech.threePomelos.travel.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.travel.bean.KilometerData;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


/**
 * @author Alex
 */
public class TravelRulerView extends View {

    /**
     * 2个大刻度之间间距，默认为1
     */
    private int scaleLimit = 1;
    private float scaleHightRate = 1;
    private int diverLine = 10;
    private int diverLineColor = 0x38172058;
    /**
     * 尺子高度
     */
    private int rulerHeight = 100;
    /**
     * 尺子和屏幕顶部以及结果之间的高度
     */
    private int rulerToResultgap = rulerHeight / 6;
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
    private int triangleHeight = 40;
    private int triangleWidth = 60;
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
    private int originalScale = 0;

    private ValueAnimator valueAnimator;
    private VelocityTracker velocityTracker = VelocityTracker.obtain();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
    private Calendar calendar = Calendar.getInstance();
    private String resultText = String.valueOf(firstScale);
    private String resultDate;
    private HashMap<String, Float> dataHashMap = new HashMap<>();


    /**
     * 小刻度线的画笔
     */
    private Paint selectedPaint;
    /**
     * 每个整数刻度线的画笔
     */
    private Paint midScalePaint;

    private Paint linePaint;
    /**
     * 中间刻度线的画笔
     */
    private Paint lagScalePaint;
    private Paint scaleNumPaint;
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
    /**
     * 显示为周还是月 默认为周
     */
    private boolean weekOrMonth = true;

    public TravelRulerView(Context context) {
        this(context, null);
    }

    public TravelRulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TravelRulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttr(attrs, defStyleAttr);
        init();
    }


    private void setAttr(AttributeSet attrs, int defStyleAttr) {

        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.TravelRulerView, defStyleAttr, 0);

        scaleLimit = a.getInt(R.styleable.TravelRulerView_scaleLimit, scaleLimit);

        rulerHeight = a.getDimensionPixelSize(R.styleable.TravelRulerView_rulerHeight, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, rulerHeight, getResources().getDisplayMetrics()));

        rulerToResultgap = a.getDimensionPixelSize(R.styleable.TravelRulerView_rulerToResultgap, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, rulerToResultgap, getResources().getDisplayMetrics()));

        scaleCount = a.getInt(R.styleable.TravelRulerView_scaleCount, scaleCount);

        scaleGap = a.getDimensionPixelSize(R.styleable.TravelRulerView_scaleGap, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, scaleGap, getResources().getDisplayMetrics()));

        triangleHeight = a.getDimensionPixelSize(R.styleable.TravelRulerView_triangleHeight, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, triangleHeight, getResources().getDisplayMetrics()));

        triangleWidth = a.getDimensionPixelSize(R.styleable.TravelRulerView_triangleHeight, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, triangleWidth, getResources().getDisplayMetrics()));

        diverLine = a.getDimensionPixelSize(R.styleable.TravelRulerView_diverLine, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, diverLine, getResources().getDisplayMetrics()));

        minScale = a.getInt(R.styleable.TravelRulerView_minScale, minScale) / scaleLimit;

        firstScale = a.getInt(R.styleable.TravelRulerView_firstScale, firstScale) / scaleLimit;

        maxScale = a.getInt(R.styleable.TravelRulerView_maxScale, maxScale) / scaleLimit;

        bgColor = a.getColor(R.styleable.TravelRulerView_bgColor, bgColor);

        selectedScaleColor = a.getColor(R.styleable.TravelRulerView_selectedScaleColor, selectedScaleColor);

        diverLineColor = a.getColor(R.styleable.TravelRulerView_diverLineColor, diverLineColor);
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
        paintScale.setStrokeWidth(midScaleStroke);
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
        paintScale.setStrokeWidth(midScaleStroke);
        midScalePaint = paintScale;

        paintScale = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintScale.setColor(signColor);
        paintScale.setStyle(Paint.Style.FILL);
        paintScale.setStrokeCap(Paint.Cap.ROUND);
        paintScale.setStrokeWidth(largeScaleStroke);
        paintScale.setDither(true);
        lagScalePaint = paintScale;

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
                if (moveX >= width / half) {
                    moveX = width / 2;
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

        if (Math.abs(xVelocity) < speed) {
            isUp = true;
            return;
        }
        if (valueAnimator.isRunning()) {
            return;
        }

        valueAnimator = ValueAnimator.ofInt(xVelocity / 40, 0).setDuration(Math.abs(xVelocity / 5));


        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                moveX += (int) animation.getAnimatedValue();
                if (moveX >= width / half) {
                    moveX = width / 2;
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
     * @param scale 设置的数值
     * @return 距离
     */
    private float getWhichScaleMoveX(float scale) {
        return width / 2 - scaleGap * scaleCount * (scale - minScale);
    }

    /**
     * 绘制刻度和数字
     *
     * @param canvas canvas
     */
    private void drawScaleAndNum(Canvas canvas) {
        //移动画布到结果值的下面
        canvas.translate(0, (showScaleResult ? resultNumRect.height() : 0) + rulerToResultgap + lagScaleHeight);
        calendar = Calendar.getInstance();
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
                valueAnimator.setDuration(0);
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
                        if (OnChooseResultListener != null && currentScale != originalScale) {
                            OnChooseResultListener.onEndResult(resultText, resultDate);
                            OnChooseResultListener.isCurrentResult(currentScale == maxScale);
                            originalScale = currentScale;


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
        float v = (width / 2 - moveX) / (scaleGap * scaleCount) + minScale;
        currentScale = new WeakReference<>(new BigDecimal(v * scaleLimit)).get().setScale(1, BigDecimal.ROUND_HALF_UP).intValue();
        if (currentScale <= maxScale) {
            calendar.add(Calendar.DATE, currentScale - maxScale);
            resultDate = dateFormat.format(calendar.getTime().getTime());
            resultText = dataHashMap.containsKey(resultDate) ? String.valueOf(dataHashMap.get(resultDate)) : "0";
        }
        //绘制当前屏幕可见刻度,不需要裁剪屏幕,while循环只会执行·屏幕宽度/刻度宽度·次,大部分的绘制都是if(curDis<width)这样子内存暂用相对来说会比较高。。
        calendar.add(Calendar.DATE, num1 - currentScale);

        while (rulerRight <= width + scaleGap) {
            if (num1 % scaleCount == 0 && num1 <= maxScale) {
                //绘制整点刻度以及文字
                boolean b = (moveX >= 0 && rulerRight < moveX - scaleGap) || width / 2 - rulerRight <= getWhichScaleMoveX(maxScale + 1) - moveX;
                if (!b) {
                    //绘制日期
                    String data = dateFormat.format(calendar.getTime().getTime());
                    float kilometer = 0f;
                    if (dataHashMap.containsKey(data)) {
                        kilometer = dataHashMap.get(data);
                    }
                    data = data.substring(5);
                    scaleNumPaint.getTextBounds(data, 0, data.length(), scaleNumRect);
                    resultNumPaint.getTextBounds(data, 0, data.length(), resultNumRect);
                    if (kilometer == 0) {
                        if (num1 == currentScale) {
                            canvas.drawLine(0, 0, 0, -1, selectedPaint);
                            canvas.drawText(data + "", -resultNumRect.width() / 2,
                                    resultNumRect.height() / 2 + 3 * triangleHeight + diverLine, resultNumPaint);
                        } else {
                            canvas.drawLine(0, 0, 0, -1, midScalePaint);
                            if (weekOrMonth) {
                                canvas.drawText(data + "", -scaleNumRect.width() / 2,
                                        scaleNumRect.height() / 2 + 3 * triangleHeight + diverLine, scaleNumPaint);
                            }
                        }
                    } else {
                        if (num1 == currentScale) {
                            canvas.drawLine(0, 0, 0, -kilometer * scaleHightRate, selectedPaint);
                            canvas.drawText(data + "", -resultNumRect.width() / 2,
                                    resultNumRect.height() / 2 + 3 * triangleHeight + diverLine, resultNumPaint);
                        } else {
                            canvas.drawLine(0, 0, 0, -kilometer * scaleHightRate, midScalePaint);
                            if (weekOrMonth) {
                                canvas.drawText(data + "", -scaleNumRect.width() / 2,
                                        scaleNumRect.height() / 2 + 3 * triangleHeight + diverLine, scaleNumPaint);
                            }
                        }
                    }
                }
            }
            calendar.add(Calendar.DATE, 1);
            ++num1;  //刻度加1
            //绘制屏幕的距离在原有基础上+1个刻度间距
            rulerRight += scaleGap;
            //移动画布到下一个刻度
            canvas.translate(scaleGap, 0);
        }
        canvas.restore();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_triangle);
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect rect1 = new Rect(width / 2 - triangleWidth / 2, triangleHeight, width / 2 + triangleWidth / 2, triangleHeight * 2);

        canvas.drawBitmap(bitmap, rect, rect1, lagScalePaint);
        canvas.drawLine(0, triangleHeight * 2, width, triangleHeight * 2, linePaint);

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
         * @param result
         * @param date
         */
        void onEndResult(String result, String date);

        /**
         * 判断是否为最新值
         * @param b
         */
        void isCurrentResult(boolean b);

    }

    /**
     * 注册监听
     *
     * @param OnChooseResultListener
     */
    public void setOnChooseResultListener(OnChooseResultListener OnChooseResultListener) {
        this.OnChooseResultListener = OnChooseResultListener;
    }

    public void setdataHashMap(HashMap<String, Float> dataHashMap, int firstScale, float kilometer) {
        this.dataHashMap = dataHashMap;
        this.firstScale = firstScale;
        scaleHightRate = lagScaleHeight / kilometer;
        this.maxScale = firstScale;
        invalidate();
    }

    public void setdataHashMapWeek(HashMap<String, Float> dataHashMap, int firstScale, float kilometer) {
        this.dataHashMap = dataHashMap;
        this.firstScale = firstScale;
        scaleHightRate = lagScaleHeight / kilometer;
        this.maxScale = firstScale;
        scaleGap *= 3;
        weekOrMonth = true;
        invalidate();
    }

    public void setdataHashMapMonth(HashMap<String, Float> dataHashMap, int firstScale, float kilometer) {
        this.dataHashMap = dataHashMap;
        this.firstScale = firstScale;
        scaleHightRate = lagScaleHeight / kilometer;
        this.maxScale = firstScale;
        scaleGap /= 3;
        weekOrMonth = false;
        invalidate();
    }
}
