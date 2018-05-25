package com.findtech.threePomelos.travel.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.L;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.bluetooth.BlueManager;
import com.findtech.threePomelos.sdk.manger.RxHelper;
import com.findtech.threePomelos.utils.weather.WeatherBean;
import com.findtech.threePomelos.musicserver.Nammu;
import com.findtech.threePomelos.sdk.base.mvp.BaseFragmentMvp;
import com.findtech.threePomelos.travel.activity.CalendarActivity;
import com.findtech.threePomelos.travel.activity.TravelDetailActivity;
import com.findtech.threePomelos.travel.bean.TravelBean;
import com.findtech.threePomelos.travel.present.TravelFragmentPresent;
import com.findtech.threePomelos.travel.view.TravelFragView;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.MyCalendar;
import com.findtech.threePomelos.utils.SpUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;


/**
 * @author Alex
 */
public class TravelFragment extends BaseFragmentMvp<TravelFragment, TravelFragmentPresent> implements
        TravelFragView<TravelBean>, View.OnClickListener, LocationListener {

    @BindView(R.id.img_travel)
    ImageView img_travel;
    @BindView(R.id.img_frequency)
    ImageView img_frequency;
    @BindView(R.id.cardView_plan)
    ImageView cardView_plan;

    @BindView(R.id.tv_mon_calendar)
    TextView tv_mon_calendar;
    @BindView(R.id.tv_tue_calendar)
    TextView tv_tue_calendar;
    @BindView(R.id.tv_wed_calendar)
    TextView tv_wed_calendar;
    @BindView(R.id.tv_thu_calendar)
    TextView tv_thu_calendar;
    @BindView(R.id.tv_fir_calendar)
    TextView tv_fir_calendar;
    @BindView(R.id.tv_sat_calendar)
    TextView tv_sat_calendar;
    @BindView(R.id.tv_sun_calendar)
    TextView tv_sun_calendar;

    @BindView(R.id.tvKiloMeter)
    TextView tvKiloMeter;
    @BindView(R.id.tvFrequency)
    TextView tvFrequency;
    @BindView(R.id.tvContent)
    TextView tvContent;
    @BindView(R.id.tv_currentDate)
    TextView tv_currentDate;
    @BindView(R.id.tv_weather)
    TextView tv_weather;
    @BindView(R.id.tv_quality)
    TextView tv_quality;

    @BindView(R.id.viewStubFir)
    ViewStub viewStub;
    @BindView(R.id.viewStubSat)
    ViewStub viewStubSat;
    @BindView(R.id.viewStubMon)
    ViewStub viewStubMon;
    @BindView(R.id.viewStubTue)
    ViewStub viewStubTue;
    @BindView(R.id.viewStubWed)
    ViewStub viewStubWed;
    @BindView(R.id.viewStubThr)
    ViewStub viewStubThr;
    @BindView(R.id.viewStubSun)
    ViewStub viewStubSun;
    @BindView(R.id.btnConnectTravel)
    Button btnConnectTravel;

    @BindView(R.id.lottieAnimation)
    LottieAnimationView animationView;

    private LocationManager manager;
    private ConnectBluetoothActivity connectBluetoothActivity;

    public static TravelFragment getInstance() {
        return new TravelFragment();
    }

    @Override
    protected TravelFragmentPresent createPresent() {
        return new TravelFragmentPresent();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_travel;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            connectBluetoothActivity = (ConnectBluetoothActivity) activity;
        } catch (ClassCastException e) {
        }
    }


    @Override
    public void initUI(View view, @Nullable Bundle savedInstanceState) {

        manager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        } else {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        initCalendar("0000000");
        Nammu.init(mActivity);



    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

        WeatherBean bean = WeatherBean.getInstance();
        tv_quality.setText(bean.getQuality());
        tv_weather.setText(mActivity.getString(R.string._23, bean.getTemperature()));
        tv_currentDate.setText(mActivity.getString(R.string.current_tv, MyCalendar.getCurrentYear(), MyCalendar.getCurrentMonth()));

        img_travel.setImageDrawable(getResources().getDrawable(R.drawable.img_bac_travel));
        img_frequency.setImageDrawable(getResources().getDrawable(R.drawable.img_bac_freq));
        img_travel.setOnClickListener(this);
        img_frequency.setOnClickListener(this);
        cardView_plan.setOnClickListener(this);
        btnConnectTravel.setOnClickListener(this);
        getPresent().getTravelInfo();
        String address = SpUtils.getString(mActivity, IContent.USER_LOCATION, "39.92:116.46");
        getPresent().getWeather(mActivity, address);

        final AssetManager assetManager = mActivity.getAssets();
        animationView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset asset) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(assetManager.open(asset.getFileName()));
                    return bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
        animationView.playAnimation();

        if (mApplication.manager.cubicBLEDevice != null) {
            btnConnectTravel.setVisibility(View.GONE);
        } else {
            btnConnectTravel.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void loadSuccess(TravelBean travelBean) {
        tvKiloMeter.setText(travelBean.getTodayDistance());
        tvFrequency.setText(travelBean.getWeekFrequency());
        tvContent.setText(travelBean.getTextTravelNotice());
        initCalendar(travelBean.getTravelDate());
    }

    /**
     * 初始化日期
     *
     * @param travelDate
     */
    private void initCalendar(String travelDate) {
        TextView[] textView = {tv_mon_calendar, tv_tue_calendar, tv_wed_calendar,
                tv_thu_calendar, tv_fir_calendar, tv_sat_calendar, tv_sun_calendar,};
        ViewStub[] viewStubs = {viewStubMon, viewStubTue, viewStubWed, viewStubThr, viewStub, viewStubSat, viewStubSun};

        SimpleDateFormat bjSdf = new SimpleDateFormat("dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int weekNumber = calendar.get(Calendar.DAY_OF_WEEK);

        //当今天为周日时,应该向前选择
        if (weekNumber == 1) {
            char c;
            for (int i = 0; i < 7; i++) {
                if (i == 0) {
                    textView[6].setBackground(mActivity.getResources().getDrawable(R.drawable.travel_selected));
                    textView[6].setSelected(true);
                    Date date1 = calendar.getTime();
                    textView[6].setText(bjSdf.format(date1));
                    continue;
                }
                calendar.add(Calendar.DATE, -1);
                Date date2 = calendar.getTime();
                textView[6 - i].setText(bjSdf.format(date2));
                c = travelDate.charAt(6 - i);
                if (c == '1') {
                    textView[6 - i].setBackground(mActivity.getResources().getDrawable(R.drawable.travel_acient));
                    textView[6 - i].setSelected(true);
                }
            }
        } else {
            textView[weekNumber - 2].setBackground(mActivity.getResources().getDrawable(R.drawable.travel_selected));
            textView[weekNumber - 2].setSelected(true);
            Date date1 = calendar.getTime();
            textView[weekNumber - 2].setText(bjSdf.format(date1));
            calendar.add(Calendar.DAY_OF_WEEK, 2 - weekNumber);
            date1 = calendar.getTime();
            textView[0].setText(bjSdf.format(date1));
            char c = travelDate.charAt(0);
            if (c == '1') {
                textView[0].setBackground(mActivity.getResources().getDrawable(R.drawable.travel_acient));
                textView[0].setSelected(true);
            }
            for (int i = 1; i <= 6; i++) {
                if (i == weekNumber - 2) {
                    calendar.add(Calendar.DATE, 1);
                    continue;
                }
                calendar.add(Calendar.DATE, 1);
                Date date2 = calendar.getTime();
                textView[i].setText(bjSdf.format(date2));
                c = travelDate.charAt(i);
                if (c == '1') {
                    textView[i].setBackground(mActivity.getResources().getDrawable(R.drawable.travel_acient));
                    textView[i].setSelected(true);
                } else if (c == '2') {
                    viewStubs[i].inflate();
                }
            }
        }
    }

    @Override
    public void loadFailed(String s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_travel:
                Bundle bundle_k = new Bundle();
                bundle_k.putInt(IContent.TRAVEL_FRAGMRNT, 0);
                startNewActivity(TravelDetailActivity.class, bundle_k);
                break;
            case R.id.img_frequency:
                Bundle bundle_f = new Bundle();
                bundle_f.putInt(IContent.TRAVEL_FRAGMRNT, 1);
                startNewActivity(TravelDetailActivity.class, bundle_f);
                break;
            case R.id.cardView_plan:
                startNewActivity(CalendarActivity.class);
                break;
            case R.id.btnConnectTravel:
                if (mApplication.manager.isEnabledFragment()) {
                    if (mApplication.manager.cubicBLEDevice != null) {
                        mApplication.manager.cubicBLEDevice.disconnectedDevice();
                        mApplication.manager.cubicBLEDevice = null;
                    }
                    if (connectBluetoothActivity != null) {
                        connectBluetoothActivity.scanAndConnect();
                    }
                } else {
                    mApplication.manager.requestBluetooth(TravelFragment.this);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            manager.removeUpdates(this);
            String address = location.getLatitude() + ":" + location.getLongitude();
            getPresent().getWeather(mActivity, address);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                manager.requestLocationUpdates(Context.LOCATION_SERVICE, 0, 0, this);
            }
        }
    }

    @Override
    public void getWeatherSuccess(WeatherBean bean) {
        tv_quality.setText(bean.getQuality());
        tv_weather.setText(mActivity.getString(R.string._23, bean.getTemperature()));
        getPresent().sendWeather(bean.getCodeWeather(), bean.getQuality());

    }

    @Override
    public void getWeatherFailed(String message) {

    }

    @Override
    public void sendWeatherSuccess() {
        getPresent().getTravelInfo();
    }

    @Override
    public void sendWeatherFailed() {

    }

    /**
     * 与MainActivity交互
     */
    public interface ConnectBluetoothActivity {

        /**
         * 发送设定时间指令
         */
        void sendBluetooth();

        /**
         * 请求搜索并连接蓝牙
         */
        void scanAndConnect();
    }

    public void updateConnectButton(boolean b) {
        if (b) {
            btnConnectTravel.setVisibility(View.INVISIBLE);
        } else {
            btnConnectTravel.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BlueManager.REQUEST_CODE && resultCode == RESULT_OK) {
            if (connectBluetoothActivity != null) {
                connectBluetoothActivity.scanAndConnect();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mApplication.manager.cubicBLEDevice != null) {
            btnConnectTravel.setVisibility(View.INVISIBLE);
        } else {
            btnConnectTravel.setVisibility(View.VISIBLE);
        }
    }
}
