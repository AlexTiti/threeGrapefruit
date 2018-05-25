package com.findtech.threePomelos.travel.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.findtech.threePomelos.R;
import com.findtech.threePomelos.music.utils.HandlerUtil;
import com.findtech.threePomelos.sdk.base.mvp.BaseFragmentMvp;
import com.findtech.threePomelos.travel.activity.TravelDetailActivity;
import com.findtech.threePomelos.travel.adapter.TravelRecyclerAdapter;
import com.findtech.threePomelos.travel.bean.KilometerBean;
import com.findtech.threePomelos.travel.present.KilometerPresent;
import com.findtech.threePomelos.travel.bean.KilometerData;
import com.findtech.threePomelos.travel.view.KilometerView;
import com.findtech.threePomelos.travel.view.TravelRulerView;
import com.findtech.threePomelos.utils.MyCalendar;
import com.findtech.threePomelos.utils.SpUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/23
 */

public class KilometerFragment extends BaseFragmentMvp<KilometerFragment, KilometerPresent>
        implements KilometerView<HashMap<String, Float>>, TravelRulerView.OnChooseResultListener,
        View.OnClickListener {

    @BindView(R.id.rulerView)
    TravelRulerView travelRulerView;
    @BindView(R.id.tv_show_detail)
    TextView tvShowDetail;

    @BindView(R.id.tv_detail_month)
    ImageView tvDetailMonth;
    @BindView(R.id.tv_detail_week)
    ImageView tvDetailWeek;
    @BindView(R.id.recyclerView_travel)
    RecyclerView recyclerView_travel;


    private int top;
    private int bottom;
    private HashMap<String, Float> kilometerData;
    private TravelRecyclerAdapter adapter;
    private Handler handler;
    private String keyLast;

    public static KilometerFragment newInstance() {
        Bundle args = new Bundle();
        KilometerFragment fragment = new KilometerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void loadSuccess(HashMap<String, Float> kilometerData) {
        this.kilometerData = kilometerData;
        float kilometer = 0;
        keyLast = MyCalendar.getToday();
        if (kilometerData.containsKey(keyLast)) {
            kilometer = kilometerData.get(keyLast);
        }
        tvShowDetail.setText(String.valueOf(kilometer));

        travelRulerView.setdataHashMap(kilometerData, 1000,
                SpUtils.getInt(mContext, "MaxMileage", 500));

        getPresent().getInfoWithDate(MyCalendar.getToday(keyLast+" 00:00:00"));

    }

    @Override
    public void loadFailed(String s) {

    }

    @Override
    protected KilometerPresent createPresent() {
        return new KilometerPresent(mContext);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_kilometer;
    }

    @Override
    public void initUI(View view, @Nullable Bundle savedInstanceState) {
        handler = HandlerUtil.getInstance(mContext);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                top = travelRulerView.getTop();
                bottom = travelRulerView.getBottom();
            }
        }, 300);
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

        getPresent().getDataList();
        tvDetailMonth.setVisibility(View.INVISIBLE);
        travelRulerView.setOnChooseResultListener(this);
        travelRulerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                travelRulerView.getParent().requestDisallowInterceptTouchEvent(true);
                float y;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        y = event.getY();
                        if (y >= top && y <= bottom) {
                            travelRulerView.getParent().requestDisallowInterceptTouchEvent(true);
                        } else {
                            travelRulerView.getParent().requestDisallowInterceptTouchEvent(false);
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

        tvDetailMonth.setOnClickListener(this);
        tvDetailWeek.setOnClickListener(this);
        tvDetailWeek.setSelected(true);

        adapter = new TravelRecyclerAdapter();
        recyclerView_travel.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView_travel.setAdapter(adapter);

    }

    @Override
    public void onEndResult(String result, String date) {
        tvShowDetail.setText(result);

        getPresent().getInfoWithDate(MyCalendar.getToday(date+" 00:00:00"));
    }

    @Override
    public void isCurrentResult(boolean b) {
        if (b){
            tvDetailMonth.setVisibility(View.INVISIBLE);
        }else {
            tvDetailMonth.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_detail_week:
                if (tvDetailWeek.isSelected()) {
                    travelRulerView.setdataHashMapMonth(kilometerData, 1000,
                            SpUtils.getInt(mContext, "MaxMileage", 500));
                } else {
                    travelRulerView.setdataHashMapWeek(kilometerData, 1000,
                            SpUtils.getInt(mContext, "MaxMileage", 500));
                }
                tvDetailWeek.setSelected(!tvDetailWeek.isSelected());
                break;
            case R.id.tv_detail_month:
                travelRulerView.computeScrollTo(1000);
                break;

            default:
                break;
        }
    }

    @Override
    public void getDaySuccess(ArrayList<KilometerBean> travelBeans) {
        adapter.setTravelBeans(travelBeans);
    }

    @Override
    public void getEmptyData() {

    }

}
