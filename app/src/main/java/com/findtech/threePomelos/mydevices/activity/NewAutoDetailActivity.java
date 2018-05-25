package com.findtech.threePomelos.mydevices.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.bluetooth.server.RFStarBLEService;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.mydevices.bean.DeviceStateBean;
import com.findtech.threePomelos.mydevices.present.NewAutoDetailPresent;
import com.findtech.threePomelos.mydevices.view.NewAutoDeviceView;
import com.findtech.threePomelos.sdk.base.mvp.BaseActivityMvp;
import com.findtech.threePomelos.sdk.manger.RxHelper;
import com.findtech.threePomelos.user.bean.UserInfo;
import com.findtech.threePomelos.user.activity.CommendProblemActivity;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.SpUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.RoundView;
import com.findtech.threePomelos.view.datepicker.AdultWeightPickerDialog;
import com.findtech.threePomelos.view.datepicker.DatepickerDialog;
import com.findtech.threePomelos.view.dialog.CustomDialog;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * @author Administrator
 */
public class NewAutoDetailActivity extends BaseActivityMvp<NewAutoDetailActivity, NewAutoDetailPresent> implements
        View.OnClickListener, NewAutoDeviceView<String>, AdultWeightPickerDialog.OnWeightListener {

    private String selectAddress;
    private String selectName;
    private String deviceFunction;
    private IContent content = IContent.getInstacne();
    private int seekBarProgress;
    private Dialog mPopupdialog;

    @BindView(R.id.roundViewBreak)
    RoundView roundViewBreak;
    @BindView(R.id.roundView)
    RoundView roundView;


    @BindView(R.id.switch_close)
    SwitchCompat switch_close;
    @BindView(R.id.switchSafe)
    SwitchCompat switchSafe;
    @BindView(R.id.switchVoiceClose)
    SwitchCompat switchVoiceClose;
    @BindView(R.id.seekBar)
    AppCompatSeekBar seekBar;
    @BindView(R.id.total_calories)
    TextView total_calories;
    @BindView(R.id.text_today_calor)
    TextView text_today_calor;
    @BindView(R.id.text_today_mileage)
    TextView text_today_mileage;
    @BindView(R.id.text_total_mileage)
    TextView text_total_mileage;
    @BindView(R.id.tvAutoBrake)
    TextView tvAutoBrake;
    @BindView(R.id.tvAutoBrakeHint)
    TextView tvAutoBrakeHint;
    @BindView(R.id.tvAIBrake)
    TextView tvAIBrake;
    @BindView(R.id.tvAIBrakeHint)
    TextView tvAIBrakeHint;
    @BindView(R.id.tvVoiceType)
    TextView tvVoiceType;
    @BindView(R.id.tvLanguangeType)
    TextView tvLanguangeType;
    @BindView(R.id.tvRepair)
    TextView tvRepair;
    @BindView(R.id.tvChangeName)
    TextView tvChangeName;
    @BindView(R.id.tvVersionTitle)
    TextView tvVersionTitle;
    @BindView(R.id.tvUnbind)
    TextView tvUnbind;
    @BindView(R.id.tvUse)
    TextView tvUse;
    @BindView(R.id.tvCodeSystem)
    TextView tvCodeSystem;
    @BindView(R.id.weight_parent)
    TextView weight_parent;


    @BindView(R.id.ivAiBrake)
    ImageView ivAiBrake;
    @BindView(R.id.ivAutoBrake)
    ImageView ivAutoBrake;
    @BindView(R.id.ivCloseDevice)
    ImageView ivCloseDevice;
    @BindView(R.id.image_edit_detail)
    ImageView image_edit_detail;
    @BindView(R.id.include)
    View include;


    int brakeNoticeModel = 0;
    int otherNoticeModel = 0;
    int soundIndex = 0;
    private AdultWeightPickerDialog weightPickerDialog;


    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        if (intent != null) {
            selectAddress = intent.getStringExtra(content.clickPositionAddress);
            selectName = intent.getStringExtra(content.clickPositionName);
            deviceFunction = intent.getStringExtra(content.clickPositionFunction);
        }
    }

    @Override
    protected void showLoading() {

    }

    @Override
    protected void onErrorViewClick(View v) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        initToolBar(include, selectName);
        tvAutoBrake.setOnClickListener(this);
        tvAutoBrakeHint.setOnClickListener(this);
        tvAIBrake.setOnClickListener(this);
        tvAIBrakeHint.setOnClickListener(this);

        tvRepair.setOnClickListener(this);
        tvVoiceType.setOnClickListener(this);
        tvChangeName.setOnClickListener(this);
        tvUse.setOnClickListener(this);
        tvUnbind.setOnClickListener(this);
        ivCloseDevice.setOnClickListener(this);
        image_edit_detail.setOnClickListener(this);
        ivCloseDevice.setEnabled(false);
        seekBar.setEnabled(false);

        if (app.manager.cubicBLEDevice == null) {
            scanAndConnectBluetooth(selectAddress, selectName);
        } else {
            initEnable();
            sendBlueToothByte(IContent.NOTIFY_DATA);
            updateAndSend();
            Observable.timer(3, TimeUnit.SECONDS)
                    .compose(RxHelper.<Long>rxSchedulerHelper())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            observableSend();
                        }
                    });
        }
        initSwitchCompat();
    }

    private void initEnable() {
        switch_close.setEnabled(true);
        seekBar.setEnabled(true);
        switchSafe.setEnabled(true);
        switchVoiceClose.setEnabled(true);
        ivCloseDevice.setEnabled(true);
        ivCloseDevice.setImageDrawable(getResources().getDrawable(R.drawable.btn_close_new));

    }

    private void initEnableFalse() {
        switch_close.setEnabled(false);
        seekBar.setEnabled(false);
        switchSafe.setEnabled(false);
        switchVoiceClose.setEnabled(false);
        ivCloseDevice.setEnabled(false);
        ivCloseDevice.setImageDrawable(getResources().getDrawable(R.drawable.btn_bac_close));

    }

    private void initSwitchCompat() {

        switchVoiceClose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    brakeNoticeModel = 2;
                    switchVoiceClose.setThumbDrawable(getResources().getDrawable(R.drawable.play_plybar_btn));
                } else {
                    brakeNoticeModel = 0;
                    switchVoiceClose.setThumbDrawable(getResources().getDrawable(R.drawable.seek_button_close));
                }

                int byteBrake = (brakeNoticeModel << 6) + (otherNoticeModel << 4) + soundIndex;
                byte[] bytesBrake = {0x55, (byte) 0xAA, 0x01, (byte) 0x0B, 0x12, (byte) byteBrake, (byte) (0 - (0x01 + 0x0B + 0x12 + byteBrake))};
                sendBlueToothByte(bytesBrake);

            }
        });

        switch_close.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    switch_close.setThumbDrawable(getResources().getDrawable(R.drawable.play_plybar_btn));
                }else {
                    switch_close.setThumbDrawable(getResources().getDrawable(R.drawable.seek_button_close));
                }

                if (isChecked && DeviceStateBean.getInstance().getBrakeModel() == 0) {

                    sendBlueToothByte(IContent.BREAK_MODEL_AI);
                } else if (!isChecked) {
                    sendBlueToothByte(IContent.BREAK_MODEL_CLOSE);
                }

            }
        });

        switchSafe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    switchSafe.setThumbDrawable(getResources().getDrawable(R.drawable.play_plybar_btn));
                    sendBlueToothByte(IContent.BRAKE_CAR);
                } else {
                    switchSafe.setThumbDrawable(getResources().getDrawable(R.drawable.seek_button_close));
                    sendBlueToothByte(IContent.BRAKE_CAR_CLEAR);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarProgress = (int) (progress * 0.32);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                byte[] bytes = {0x55, (byte) 0xAA, 0x01, 0x0B, 0x13, (byte) seekBarProgress, (byte) (0 - (0x01 + 0x0B + 0x13 + (byte) seekBarProgress))};
                sendBlueToothByte(bytes);
            }
        });

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_detail;
    }


//        if (intent == null) {
//            return;
//        }
//
//        String action = intent.getAction();
//        if (action == null) {
//            return;
//        }
//        if (RFStarBLEService.ACTION_GATT_CONNECTED.equals(action)) {
//
//        } else if (RFStarBLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
//            content.isBind = false;
//            content.address = null;
//            content.deviceName = null;
//        } else if (RFStarBLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
//
//            content.isBind = true;
//            content.address = macData;
//            content.deviceName = selectName;
//
//        } else if (RFStarBLEService.DESCRIPTOR_WRITER_DONE
//                .equals(action)) {
//            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.NOTIFY_DATA);
//        } else if (action.equals(RFStarBLEService.ACTION_WRITE_DONE)) {
//            if (content.WRITEVALUE != null) {
//                app.manager.cubicBLEDevice.readValue(IContent.SERVERUUID_BLE, IContent.READUUID_BLE, content.WRITEVALUE);
//            }
//        } else if (action.equals(RFStarBLEService.ACTION_DATA_AVAILABLE)) {
//            dismissProgressDialog();
//            byte data[] = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
//            if (!IContent.isBLE) {
//                return;
//            }
//            if (data[3] == (byte) 0x81 && data[4] == 0x03) {
//
//                return;
//            }
//            if (data[3] == (byte) 0x8B && data[4] == 0x08) {
//
//                int timeBreakZero = data[7] & 0xff;
//                int timeBreakNext = data[6] << 8;
//                int timeBreakHundred = data[5] << 16;
//
//                int timeCloseZero = data[11] & 0xff;
//                int timeCloseNext = data[10] << 8;
//                int timeSleepZero = data[9] & 0xff;
//                int timeSleepNext = data[8] << 8;
//
//                int timeBreak = timeBreakHundred + timeBreakNext + timeBreakZero;
//                int timeClose = timeCloseNext + timeCloseZero;
//                int timeSleep = timeSleepNext + timeSleepZero;
//
//                return;
//            }
//            if (data[3] == (byte) 0x8B && data[4] == 0x01) {
//                int averageSpeedZero = data[6] & 0xff;
//                int todayMileageZero = data[8] & 0xff;
//                int totalMileageZero = data[10] & 0xff;
//                int averageSpeedNext = (data[5] << 8) & 0xff;
//                int todayMileageNext = (data[7] << 8) & 0xff;
//                int totalMileageNext = (data[9] << 8) & 0xff;
//
//                double today_mileage = (todayMileageZero + todayMileageNext) * 1.0 / 100;
//                double total_mileage = (totalMileageZero + totalMileageNext) * 1.0 / 10;
//                double averageSpeed = (averageSpeedZero + averageSpeedNext) * 1.0 / 10;
//
//                text_today_mileage.setText(String.valueOf(today_mileage));
//                text_total_mileage.setText(String.valueOf(total_mileage));
//
//                return;
//            }
//            if (data[3] == (byte) 0x8B && data[4] == 0x07) {
//                int elec = data[5] & 0xff;
//                int elecHand = data[6] & 0xff;
//                roundViewBreak.setDegree(switchElectrical(elec));
//                roundView.setDegree(switchElectrical(elecHand));
//                return;
//            }
//
//            if (data[3] == (byte) 0x8B && data[4] == 0x05) {
//                switchSafe.setChecked(true);
//                return;
//            }
//            if (data[3] == (byte) 0x8B && data[4] == 0x06) {
//                switchSafe.setChecked(false);
//                return;
//            }
//            if (data[3] == (byte) 0x8B && data[4] == 0x09) {
//                return;
//            }
//            if (data[3] == (byte) 0x8B && data[4] == (byte) 0xAA) {
//                ToastUtil.showToast(NewAutoDetailActivity.this, getResources().getString(R.string.close_sucess));
//                return;
//            }
//            if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0D) {
//                return;
//            }
//            if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0E) {
//                return;
//            }
//            //一键修复的信息：
//            if (data[3] == (byte) 0x8B && data[4] == 0x55) {
//                int a = data[5] & 0xff;
//                int b = data[6] & 0xff;
//                int c = data[7] & 0xff;
//                int d = data[8] & 0xff;
//                int e = data[9] & 0xff;
//                //   repairMessage.setText("修复信息为 = 主控版本: V " + a + "." + b + "." + c + "TLSR版本: V " + d + "Psoc版本: V " + e);
//                return;
//            }
//
//            if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0F) {
//                int number = data[5] & 0xff;
//                if (number == 0) {
//                    switch_close.setChecked(false);
//                    ivAiBrake.setVisibility(View.INVISIBLE);
//                    ivAutoBrake.setVisibility(View.INVISIBLE);
//                    return;
//                } else {
//                    switch_close.setChecked(true);
//                }
//                if (number == 1) {
//                    ivAiBrake.setVisibility(View.INVISIBLE);
//                    ivAutoBrake.setVisibility(View.VISIBLE);
//                } else {
//                    ivAiBrake.setVisibility(View.VISIBLE);
//                    ivAutoBrake.setVisibility(View.INVISIBLE);
//                }
//
//                return;
//            }
//
//            if (data[3] == (byte) 0x8B && data[4] == 0x10) {
//
//                double distance = (data[14] << 8) + (data[15] & 0xff);
//                double travelTime = (data[16] << 8) + (data[17] & 0xff);
//
//                int date = data[6] & 0xff;
//                long b = (data[7] & 0xff) << 16;
//                long c = (data[8] & 0xff) << 8;
//                long d = data[9] & 0xff;
//
//                long a = 0;
//                String startTime = null;
//                String endTime = null;
//
//                if (date <= 127) {
//                    a = date << 24;
//                    long total = a + b + c + d;
//                    CalendarDay day = CalendarDay.from(new Date(total * 1000));
//                    startTime = String.valueOf(day.getYear() + "=" + day.getMonth() + "=" + day.getDay());
//                } else {
//                    a = (date - 127) << 24;
//                    long tem = 127 << 24;
//                    BigInteger integer = BigInteger.valueOf(a);
//                    BigInteger total = integer.add(BigInteger.valueOf(tem))
//                            .add(BigInteger.valueOf(b))
//                            .add(BigInteger.valueOf(c))
//                            .add(BigInteger.valueOf(d));
//                    Date date1 = new Date(total.longValue() * 1000);
//                }
//
//                L.e("data======", "distance=" + distance + "travelTime" + travelTime);
//                int dateEnd = data[10] & 0xff;
//                long bEnd = (data[11] & 0xff) << 16;
//                long cEnd = (data[12] & 0xff) << 8;
//                long dEnd = data[13] & 0xff;
//
//                if (dateEnd <= 127) {
//                    long aEnd = dateEnd << 24;
//                    long totalEnd = aEnd + bEnd + cEnd + dEnd;
//                    CalendarDay day = CalendarDay.from(new Date(totalEnd * 1000));
//                    endTime = String.valueOf(day.getYear() + "=" + day.getMonth() + "=" + day.getDay());
//                } else {
//                    long aEnd = (dateEnd - 127) << 24;
//                    long tem = 127 << 24;
//                    BigInteger integer = BigInteger.valueOf(aEnd);
//                    BigInteger total = integer.add(BigInteger.valueOf(tem))
//                            .add(BigInteger.valueOf(bEnd))
//                            .add(BigInteger.valueOf(cEnd))
//                            .add(BigInteger.valueOf(dEnd));
//                    Date date1 = new Date(total.longValue() * 1000);
//                }
//                return;
//            }
//
//            if (data[3] == (byte) 0x8B && data[4] == 0x12) {
//                setVoiceModel(data[5]);
//            }
//
//        } else if (action.equals(RFStarBLEService.ACTION_DATA_AVAILABLE_READ)) {
//            byte data[] = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
//            L.e("currentTime", Tools.byte2Hex(data));
//            if (data[3] == (byte) 0x8B && data[4] == (byte) 0xAA) {
//                ToastUtil.showToast(NewAutoDetailActivity.this, getResources().getString(R.string.close_sucess));
//                if (app.manager.cubicBLEDevice != null) {
//                    app.manager.cubicBLEDevice.disconnectedDevice();
//                }
//                return;
//            }
//            if (data[3] == (byte) 0x8B && data[4] == 0x05) {
//                if (data[05] == 0x01) {
//                    switchSafe.setChecked(true);
//                }
//                return;
//            }
//            if (data[3] == (byte) 0x8B && data[4] == 0x06) {
//                if (data[05] == 0x00) {
//                    switchSafe.setChecked(false);
//                }
//                return;
//            }
//            if (data[3] == (byte) 0x8B && data[4] == 0x09) {
//                return;
//            }
//
//            if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0D) {
//                ToastUtil.showToast(NewAutoDetailActivity.this, getResources().getString(R.string.close_shake), Toast.LENGTH_SHORT);
//                return;
//            }
//            if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0E) {
//                return;
//            }
//
//            if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0B) {
//                int a = data[5] & 0xff;
//                int b = data[6] & 0xff;
//                int c = data[7] & 0xff;
//                int d = data[8] & 0xff;
//                int e = data[9] & 0xff;
//                // tvCodeMessage.setText("固件版本号: V " + a + "." + b + "." + c + "TLSR版本: V " + d + "Psoc版本: V " + e);
//                return;
//            }
//
//            if (data[3] == (byte) 0x8B && data[4] == 0x11) {
//                int a = data[5] << 8;
//                int b = data[6] & 0xff;
//                ToastUtil.showToast(this, "温度：" + a + "." + b + "摄氏度");
//            }
//
//            if (data[3] == (byte) 0x8B && data[4] == 0x12) {
//                setVoiceModel(data[5]);
//
//            }
//            if (data[3] == (byte) 0x8B && data[4] == 0x13) {
//                seekBar.setProgress((int) ((data[5] & 0xff) / 0.32));
//
//            }
//
//        }

    @Override
    public void describeDown(Intent intent) {
        super.describeDown(intent);
        initEnable();
        sendBlueToothByte(IContent.NOTIFY_DATA);
        updateAndSend();
        Observable.timer(3, TimeUnit.SECONDS)
                .compose(RxHelper.<Long>rxSchedulerHelper())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        observableSend();
                    }
                });
    }

    @Override
    public void dataAvailable(Intent intent) {
        super.dataAvailable(intent);
        dismissProgressDialog();
        byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
        if (!IContent.isBLE) {
            return;
        }
        if (data[3] == (byte) 0x81 && data[4] == 0x03) {

            return;
        }

        if (data[3] == (byte) 0x8B && data[4] == 0x01) {

            int averageSpeedZero = data[6] & 0xff;
            int todayMileageZero = data[8] & 0xff;
            int totalMileageZero = data[10] & 0xff;
            int averageSpeedNext = (data[5] << 8) & 0xff;
            int todayMileageNext = (data[7] << 8) & 0xff;
            int totalMileageNext = (data[9] << 8) & 0xff;

            double todayMileage = (todayMileageZero + todayMileageNext) * 1.0 / 100;
            double totalMileage = (totalMileageZero + totalMileageNext) * 1.0 / 10;
            double averageSpeed = (averageSpeedZero + averageSpeedNext) * 1.0 / 10;

            text_today_mileage.setText(String.valueOf(todayMileage));
            text_total_mileage.setText(String.valueOf(totalMileage));
            getPresent().senTravelInfoToServer(selectAddress, todayMileage,
                    totalMileage, String.valueOf(averageSpeed), deviceFunction);

            return;
        }
        if (data[3] == (byte) 0x8B && data[4] == 0x07) {

            int elec = data[5] & 0xff;
            int elecHand = data[6] & 0xff;
            roundViewBreak.setDegree(switchElectrical(elec));
            roundView.setDegree(switchElectrical(elecHand));
            return;
        }

        if (data[3] == (byte) 0x8B && data[4] == 0x05) {
            switchSafe.setChecked(true);
            return;
        }
        if (data[3] == (byte) 0x8B && data[4] == 0x06) {
            switchSafe.setChecked(false);
            return;
        }
        if (data[3] == (byte) 0x8B && data[4] == 0x09) {
            return;
        }

        if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0D) {
            return;
        }
        if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0E) {
            return;
        }

        if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0B) {
            int a = data[5] & 0xff;
            int b = data[6] & 0xff;
            int c = data[7] & 0xff;
            int d = data[8] & 0xff;
            int e = data[9] & 0xff;
            String code = " V " + a + "." + b;
            tvCodeSystem.setText(code);
            DeviceStateBean.getInstance().setCodeSystem(code);

            return;
        }

        //一键修复的信息：
        if (data[3] == (byte) 0x8B && data[4] == 0x55) {
            int a = data[5] & 0xff;
            int b = data[6] & 0xff;
            int c = data[7] & 0xff;
            int d = data[8] & 0xff;
            int e = data[9] & 0xff;

            return;
        }

        if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0F) {
            int number = data[5] & 0xff;
            DeviceStateBean.getInstance().setBrakeModel(number);
            if (number == 0) {
                switch_close.setChecked(false);
                ivAiBrake.setVisibility(View.INVISIBLE);
                ivAutoBrake.setVisibility(View.INVISIBLE);
                return;
            } else {
                switch_close.setChecked(true);
            }

            if (number == 1) {
                ivAiBrake.setVisibility(View.INVISIBLE);
                ivAutoBrake.setVisibility(View.VISIBLE);
            } else {
                ivAiBrake.setVisibility(View.VISIBLE);
                ivAutoBrake.setVisibility(View.INVISIBLE);
            }

            return;
        }

        if (data[3] == (byte) 0x8B && data[4] == 0x12) {
            setVoiceModel(data[5]);
        }

    }

    @Override
    public void dataAvailableRead(Intent intent) {
        super.dataAvailableRead(intent);

        byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);

        if (data[3] == (byte) 0x8B && data[4] == (byte) 0xAA) {
            ToastUtil.showToast(NewAutoDetailActivity.this, getResources().getString(R.string.close_sucess));
            if (app.manager.cubicBLEDevice != null) {
                app.manager.cubicBLEDevice.disconnectedDevice();
            }
            return;
        }
        if (data[3] == (byte) 0x8B && data[4] == 0x05) {
            if (data[5] == 0x01) {
                switchSafe.setChecked(true);
            }
            return;
        }
        if (data[3] == (byte) 0x8B && data[4] == 0x06) {
            if (data[5] == 0x00) {
                switchSafe.setChecked(false);
            }
            return;
        }
        if (data[3] == (byte) 0x8B && data[4] == 0x09) {
            return;
        }

        if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0D) {
            ToastUtil.showToast(NewAutoDetailActivity.this, getResources().getString(R.string.close_shake), Toast.LENGTH_SHORT);
            return;
        }
        if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0E) {
            return;
        }


        if (data[3] == (byte) 0x8B && data[4] == 0x11) {
            int a = data[5] << 8;
            int b = data[6] & 0xff;
        }

        if (data[3] == (byte) 0x8B && data[4] == 0x12) {

            setVoiceModel(data[5]);

        }
        if (data[3] == (byte) 0x8B && data[4] == 0x13) {

            seekBar.setProgress((int) ((data[5] & 0xff) / 0.32));

        }
    }

    public void setVoiceModel(byte b) {

        int i = b & 0xff;
        byte[] b1 = new byte[8];
        for (int index = 0; index < 8; index++) {

            if ((i & (1 << index)) == 0) {
                b1[7 - index] = 0;
            } else {
                b1[7 - index] = 1;
            }
        }

        soundIndex = (b1[4] << 3) + (b1[5] << 2) + (b1[6] << 1) + b1[7];
        otherNoticeModel = 1;
        brakeNoticeModel = (b1[0] << 1) + b1[1];
        switchVoiceClose.setChecked(brakeNoticeModel == 2);

        DeviceStateBean.getInstance().setBrakeNoticeModel(brakeNoticeModel);
        DeviceStateBean.getInstance().setOtherNoticeModel(otherNoticeModel);
        DeviceStateBean.getInstance().setIndex(soundIndex);

    }


    @Override
    public void disConnect(Intent intent) {
        super.disConnect(intent);
        initEnableFalse();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tvAutoBrake:
            case R.id.tvAutoBrakeHint:
                sendBlueToothByte(IContent.BREAK_MODEL_AUTO);
                break;
            case R.id.tvAIBrake:
            case R.id.tvAIBrakeHint:
                sendBlueToothByte(IContent.BREAK_MODEL_AI);
                break;
            case R.id.tvVoiceType:
                startActivity(VoiceTypeActivity.class);
                break;
            case R.id.tvRepair:
                startActivity(RepairActivity.class);
                break;
            case R.id.tvChangeName:
                showTextDialog();
                break;
            case R.id.tvUse:
                startActivity(CommendProblemActivity.class);
                break;
            case R.id.tvUnbind:
                unBindDevice();
                break;
            case R.id.ivCloseDevice:
                closeDevice();
                break;
            case R.id.image_edit_detail:
                showAdultWeightDialog();
                break;
            default:
                break;
        }
    }

    private void closeDevice() {
        final CustomDialog.Builder builder = new CustomDialog.Builder(NewAutoDetailActivity.this);
        builder.setTitle(getResources().getString(R.string.notice))
                .setShowBindInfo(getResources().getString(R.string.close_message))
                .setShowButton(true)
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        if (app.manager.cubicBLEDevice != null) {
                            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.CLOSE_DEVICE);
                        }
                        getPresent().deleteBindDevice(selectAddress);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancle),
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .create().show();
    }

    private void unBindDevice() {

        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.notice))
                .setShowBindInfo(getResources().getString(R.string.unbound_message))
                .setShowButton(true)
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        if (app.manager.cubicBLEDevice != null) {
                            app.manager.cubicBLEDevice.disconnectedDevice();
                        }
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancle),
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void removeIContent(String address) {

        for (int i = 0; i < content.addressList.size(); i++) {
            if (content.addressList.get(i).getDeviceaAddress().equals(address)) {
                content.addressList.remove(i);
            }
        }
        UserInfo.getInstance().setCarBeanArrayList(content.addressList);
    }

    private void showTextDialog() {

        View viewDialog = getLayoutInflater().inflate(R.layout.textview_popwindow, null);
        final EditText editText = viewDialog.findViewById(R.id.editText_weight);
        editText.setHint(selectName);
        TextView leftButton = viewDialog.findViewById(R.id.cancle_button);
        TextView rightButton = viewDialog.findViewById(R.id.confirm_button);
        mPopupdialog = new Dialog(this, R.style.MyDialogStyleBottom);
        mPopupdialog.setContentView(viewDialog, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        DisplayMetrics dm = new DisplayMetrics();
        Window dialogWindow = mPopupdialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        m.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = dm.widthPixels;
        p.alpha = 1.0f;
        p.dimAmount = 0.6f;
        p.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(p);
        mPopupdialog.show();
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupdialog.dismiss();
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupdialog.dismiss();
                String name = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(name)) {
                    getPresent().sendNameToServer(name, selectAddress);
                } else {
                    ToastUtil.showToast(NewAutoDetailActivity.this, getResources().getString(R.string.name_not));
                }
            }
        });
    }


    private int switchElectrical(int e) {
        int i = 0;
        switch (e) {
            case 80:
                i = 100;
                break;
            case 40:
                i = 60;
                break;
            case 15:
                i = 20;
                break;
            case 5:
                i = 10;
                break;
            case 0:
                i = 0;
                break;
            default:
                break;
        }
        return i;
    }


    @Override
    public void loadSuccess(String s) {
        initToolBar(include, s);
        getPresent().updateDeviceList();
    }

    @Override
    public void loadFailed(String s) {

    }

    @Override
    public void initDataFromServer() {
        getPresent().getCalories();
        getPresent().getTravelInfo(selectAddress, this);
    }

    @Override
    protected NewAutoDetailPresent createPresent() {
        return new NewAutoDetailPresent();
    }

    private void showAdultWeightDialog() {
        if (weightPickerDialog != null && weightPickerDialog.isShowing()) {
            return;
        }
        weightPickerDialog = new AdultWeightPickerDialog(this);
        weightPickerDialog.setOnWeightListener(this);
        DatepickerDialog.setWindowSize(weightPickerDialog);
        weightPickerDialog.show();
    }

    @Override
    public void onClick(String weight) {
        weightPickerDialog.dismiss();
        getPresent().sendWeightToServer(weight);
    }


    @Override
    public void getCaloriesSuccess(TravelInfoEntity entity) {
        text_today_calor.setText(entity.getTodayCalor());
        total_calories.setText(entity.getTotalCalor());
        weight_parent.setText(entity.getAdultWeight());

    }

    @Override
    public void getCaloriesFailed(String message) {

    }

    @Override
    public void getTravelInfoSuccess(TravelInfoEntity entity) {
        text_today_mileage.setText(entity.getTodayMileage());
        text_total_mileage.setText(entity.getTodayMileage());

    }

    @Override
    public void getTravelInfoFailed(String message) {

    }

    @Override
    public void sendTravelInfoToServerSuccess(TravelInfoEntity entity) {
        text_today_mileage.setText(entity.getTodayMileage());
        text_total_mileage.setText(entity.getTodayMileage());
        text_today_calor.setText(entity.getTodayCalor());
        total_calories.setText(entity.getTotalCalor());
        weight_parent.setText(entity.getAdultWeight());

        OperateDBUtils operateDBUtils = new OperateDBUtils(this);
        operateDBUtils.saveTravelInfoToDB(entity, Tools.getCurrentTime());
        present.addServerToDB(entity.getTodayMileage(), this);

    }


    @Override
    public void sendTravelInfoToServerFailed(String message) {

    }

    @Override
    public void sendWeightSuccess(String weight) {
        weight_parent.setText(weight);
        SpUtils.putString(this, OperateDBUtils.ADULT_WEIGHT, weight);
    }

    @Override
    public void sendWeightFailed(String message) {

    }

    @Override
    public void unBindSuccess(String address) {
        ToastUtil.showToast(NewAutoDetailActivity.this, getResources().getString(R.string.unbound_success));
        removeIContent(address);
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void unBindFailed(String message) {

    }
}
