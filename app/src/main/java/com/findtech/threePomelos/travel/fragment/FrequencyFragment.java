package com.findtech.threePomelos.travel.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.utils.HandlerUtil;
import com.findtech.threePomelos.sdk.base.mvp.BaseFragmentMvp;
import com.findtech.threePomelos.travel.present.FrequencyPresent;
import com.findtech.threePomelos.travel.view.FrequentyMvpView;
import com.findtech.threePomelos.travel.view.TravelFrequencyView;
import com.findtech.threePomelos.utils.MyCalendar;

import java.util.Date;

import butterknife.BindView;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/23
 */

public class FrequencyFragment extends BaseFragmentMvp<FrequencyFragment, FrequencyPresent>
        implements View.OnClickListener, FrequentyMvpView<ArrayMap<String, Integer>>,
        TravelFrequencyView.OnChooseResultListener {

    @BindView(R.id.tv_week)
    TextView tvWeek;
    @BindView(R.id.tv_month)
    TextView tvMonth;
    @BindView(R.id.tv_show_detail)
    TextView tvShowDetail;
    @BindView(R.id.tv_show_notice)
    TextView tv_show_notice;

    private Handler handler;
    private int top;
    private int bottom;
    @BindView(R.id.frequencyView)
    TravelFrequencyView travelFrequencyView;
    ArrayMap<String, Integer> dataArrayListWeek;
    ArrayMap<String, Integer> dataArrayListMonth;

    public static FrequencyFragment newInstance() {
        Bundle args = new Bundle();
        FrequencyFragment fragment = new FrequencyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_frequency;
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
    }

    @Override
    public void initUI(View view, @Nullable Bundle savedInstanceState) {

        handler = HandlerUtil.getInstance(mContext);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                top = travelFrequencyView.getTop();
                bottom = travelFrequencyView.getBottom();

            }
        }, 300);
        getPresent().getDataList();
        travelFrequencyView.setOnChooseResultListener(this);
        tvWeek.setSelected(true);
        tvMonth.setOnClickListener(this);
        tvWeek.setOnClickListener(this);

        travelFrequencyView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                travelFrequencyView.getParent().requestDisallowInterceptTouchEvent(true);
                float y;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        y = event.getY();
                        if (y >= top && y <= bottom) {
                            travelFrequencyView.getParent().requestDisallowInterceptTouchEvent(true);
                        } else {
                            travelFrequencyView.getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_week:
                if (!tvWeek.isSelected()) {
                    tvWeek.setSelected(true);
                    tvMonth.setSelected(false);
                    setFrequencyWeek();
                }
                break;
            case R.id.tv_month:
                if (!tvMonth.isSelected()) {
                    tvWeek.setSelected(false);
                    tvMonth.setSelected(true);
                    setFrequencyMonth();
                }
                break;
            default:
                break;

        }
    }

    private void setFrequencyWeek() {
        if (dataArrayListWeek != null) {
            String text = String.valueOf(dataArrayListWeek.get(MyCalendar.getMonday()));
            if (text == null) {
                tvShowDetail.setText("0");
            } else {
                tvShowDetail.setText(text);
            }
            travelFrequencyView.setDataArrayList(dataArrayListWeek, 30, 7);
        } else {
            getPresent().getDataList();
        }

    }

    private void setFrequencyMonth() {
        if (dataArrayListMonth != null) {
            String text = String.valueOf(dataArrayListMonth.get(MyCalendar.getFirstOfMonth()));
            if (text == null) {
                tvShowDetail.setText("0");
            } else {
                tvShowDetail.setText(text);
            }
            travelFrequencyView.setDataArrayListMonth(dataArrayListMonth, 30, 30);
        } else {
            getPresent().getDataListMonth();
        }
    }

    @Override
    public void loadSuccess(ArrayMap<String, Integer> frequencyStore) {
        dataArrayListWeek = frequencyStore;

        String text = String.valueOf(dataArrayListWeek.get(MyCalendar.getMonday()));

        if (text == null || "null".equals(text)) {
            tvShowDetail.setText("0");
            tv_show_notice.setText(R.string.no_travel_week);
        } else {
            Date date = MyCalendar.getToday(MyCalendar.getMonday() + " 00:00:00");
            getPresent().getAnalysis(date);
            tvShowDetail.setText(text);
        }
        travelFrequencyView.setDataArrayList(frequencyStore, 30, 7);

    }

    @Override
    public void loadFailed(String s) {

        checkNetWork();
    }


    @Override
    protected FrequencyPresent createPresent() {
        return new FrequencyPresent(mContext);
    }

    @Override
    public void onEndResult(String result, String date) {
        tvShowDetail.setText(result);
        Date date1 = MyCalendar.getToday(date + " 00:00:00");
        if ("0".equals(result)) {
            tv_show_notice.setText(R.string.no_travel_week);
        } else {
            getPresent().getAnalysis(date1);
        }
    }

    @Override
    public void getFrequencyMonth(ArrayMap<String, Integer> frequencyStore) {
        dataArrayListMonth = frequencyStore;
        String text = String.valueOf(dataArrayListMonth.get(MyCalendar.getFirstOfMonth()));

        if (text == null || "null".equals(text)) {
            tvShowDetail.setText("0");
            tv_show_notice.setText(R.string.no_travel_week);
        } else {
            Date date = MyCalendar.getToday(MyCalendar.getFirstOfMonth() + " 00:00:00");
            getPresent().getAnalysis(date);
            tvShowDetail.setText(text);
        }
        travelFrequencyView.setDataArrayListMonth(frequencyStore, 30, 30);
    }

    @Override
    public void getFrequencyMonthFaild(String message) {

    }

    @Override
    public void getFrequencyMonthNetError() {

    }

    @Override
    public void getFrequencyAnalysis(String message) {
        tv_show_notice.setText(message);
    }

    @Override
    public void getFrequencyAnalysisFailed(String message) {

    }
}
