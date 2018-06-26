package com.findtech.threePomelos.mydevices.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.bluetooth.BLEDevice;
import com.findtech.threePomelos.bluetooth.server.RFStarBLEService;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.mydevices.present.DeviceDetailPresent;
import com.findtech.threePomelos.mydevices.view.NewAutoDeviceView;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.sdk.MyApplication;
import com.findtech.threePomelos.sdk.base.mvp.BaseActivityMvp;
import com.findtech.threePomelos.utils.DialogUtil;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.datepicker.AdultWeightPickerDialog;
import com.findtech.threePomelos.view.datepicker.DatepickerDialog;
import com.findtech.threePomelos.view.dialog.CustomDialog;

import java.util.List;


/**
 * @author Administrator
 */
public class DeviceDetailActivity extends BaseActivityMvp<DeviceDetailActivity, DeviceDetailPresent> implements View.OnClickListener,
        BLEDevice.RFStarBLEBroadcastReceiver, AdultWeightPickerDialog.OnWeightListener, DialogUtil.ConfirmClick
        , NewAutoDeviceView<String> {

    private ImageView mImage, isnot_ble_car, isnot_ble_car_bac;
    private ImageView image_wheel_detail, iamge_bac__;
    private ImageView image_brake_detail;
    private ImageView img_is_tempurature;
    private RelativeLayout idBle_device;
    private RelativeLayout is_tempurature;
    private LinearLayout layout_ble_detail;
    private LinearLayout is_notBledevice;
    private MyApplication app;
    private SeekBar mSeekBar;
    private NetWorkRequest netWorkRequest;
    private TravelInfoEntity travelInfoEntity = TravelInfoEntity.getInstance();
    private MyBroadcastReceiver mBroadcastReceiver;
    private TextView electric_rate;
    private TextView text_tempurature;
    private TextView text_speed_detail;
    private TextView text_today_mileage, text_today_calor;
    private TextView text_total_mileage, total_calories, weight_parent;
    private ImageView electricity_layout;
    private RelativeLayout image_elec_relayout, protect_layout;
    private int height;
    public static int RED_COLOR_LOW = 0xffED487A;
    public static int RED_COLOR_HIGH = 0xffB33647;
    public static int YELLO_COLOR_LOW = 0xffFCCA6D;
    public static int YELLO_COLOR_HIGH = 0xffCE6435;
    public static int GREEN_COLOR_LOW = 0xff4EE77A;
    public static int GREEN_COLOR_HIGH = 0xff117E47;
    boolean isRun = false;
    private TextView text_brake_state;
    private Button mButton;
    private ImageView image_back_speed;
    private RelativeLayout layout_hhh;
    private SwitchCompat mSwitchCompat, switch_brake_close;
    private ImageView image_edit_detail;

    private AdultWeightPickerDialog weightPickerDialog;
    private String selectAddress;
    private String selectName;
    private String deviceFunction;
    boolean isopen_Auto_brake = true;
    private RelativeLayout layout_seekbar_detail;
    private int progres;
    int elec;
    private OperateDBUtils operateDBUtils;
    private boolean car_protect_state;
    boolean low_elec = false;
    private DialogUtil dialogUtil;
    private IContent content = IContent.getInstacne();
    private View include;
    private ImageView actionMore;


    /**
     * to refresh
     */
    private void refresh_Unlink_Close() {
        if (deviceFunction != null && "0".equals(deviceFunction)) {
            idBle_device.setVisibility(View.GONE);
            layout_ble_detail.setVisibility(View.GONE);
            is_tempurature.setVisibility(View.VISIBLE);
            is_notBledevice.setVisibility(View.VISIBLE);
            text_speed_detail.setVisibility(View.GONE);
        }
    }


    private void initToolBar() {

        actionMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopWindow();
            }
        });
    }

    private ObjectAnimator animator;
    Animation a;
    Animation y;
    AnimationSet animationSet;

    private void initAnimation() {


        a = new RotateAnimation(0, -12, Animation.RELATIVE_TO_SELF,
                0f, Animation.RELATIVE_TO_SELF, 0.5f);
        y = new TranslateAnimation(0, 0, 0, -10);
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(a);
        animationSet.addAnimation(y);
        animationSet.setDuration(1000);
        animationSet.setInterpolator(new LinearInterpolator());
        animationSet.setFillAfter(true);
        image_brake_detail.setAnimation(animationSet);
        if (isRun) {
            text_brake_state.setText(getResources().getString(R.string.device_running));
            iamge_bac__.setVisibility(View.VISIBLE);
            animator.start();
            animationSet.start();
        } else {
            text_brake_state.setText(getResources().getString(R.string.device_isbrake));
            iamge_bac__.setVisibility(View.GONE);
            animator.cancel();
            image_wheel_detail.clearAnimation();
            image_brake_detail.clearAnimation();
        }
    }

    private void initColor(int i) {
        switch (i) {
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
        electric_rate.setText(getResources().getString(R.string.electricity, i + "%"));
        int[] color = new int[2];
        image_elec_relayout.setPadding(0, height * (100 - i) / 100, 0, 0);
        if (i <= 20) {
            low_elec = true;
            electricity_layout.setImageResource(R.drawable.elec_bac_low);
            color[0] = RED_COLOR_LOW;
            color[1] = RED_COLOR_HIGH;
        } else if (i > 20 && i <= 60) {
            low_elec = false;
            electricity_layout.setImageResource(R.drawable.elec_back);
            color[0] = YELLO_COLOR_LOW;
            color[1] = YELLO_COLOR_HIGH;
        } else if (i > 60 && i <= 100) {
            low_elec = false;
            electricity_layout.setImageResource(R.drawable.elec_bac_high);
            color[0] = GREEN_COLOR_LOW;
            color[1] = GREEN_COLOR_HIGH;
        }
        GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, color);
        mImage.setImageDrawable(bg);

        if (IContent.isBLE) {
            if (app.manager.cubicBLEDevice != null) {
                byte[] bytes = {0x55, (byte) 0xAA, 0x00, 0x0B, 0x0B, (byte) 0xEA};
                app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, bytes);
            }
        }
    }

    private void initView() {
        if (IContent.isBLE) {
            mButton.setOnClickListener(this);
            mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (app.manager.cubicBLEDevice != null) {
                            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.BRAKE_CAR);
                        }
                    } else {
                        if (app.manager.cubicBLEDevice != null) {
                            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.BRAKE_CAR_CLEAR);
                        }
                    }
                }
            });
            switch_brake_close.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (app.manager.cubicBLEDevice != null) {
                            byte[] b = {0x55, (byte) 0xAA, 0x00, 0x0B, (byte) 0x0E, (byte) 0xE7};
                            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, b);
                        }
                    } else {
                        byte[] b = {0x55, (byte) 0xAA, 0x00, 0x0B, (byte) 0x0D, (byte) 0xE8};
                        if (app.manager.cubicBLEDevice != null) {
                            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, b);
                        }
                    }
                }
            });
        }
        mBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OperateDBUtils.QUERY_FINISH);
        intentFilter.addAction(RequestUtils.RECEIVE_TEMPERATURE_ELECTRIC_ACTION);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_edit_detail:
                showNameBlueToothDialog();
                break;
            case R.id.btn_close_open:
                closeDevice();
                break;
            case R.id.layout_repair_popwindow:
                if (car_protect_state) {
                    ToastUtil.showToast(DeviceDetailActivity.this, getString(R.string.pro_action_notice));
                    mPicChooserDialog.dismiss();
                    return;
                }
                mPicChooserDialog.dismiss();
                startActivityForResult(new Intent(DeviceDetailActivity.this, RepairActivity.class), 100);
                break;
            case R.id.layout_update_popwindow:
                mPicChooserDialog.dismiss();
                if (low_elec) {
                    ToastUtil.showToast(DeviceDetailActivity.this, getString(R.string.update_low_elec));
                    return;
                }
                startActivityForResult(new Intent(DeviceDetailActivity.this, DeviceUpdateActivity.class), 100);
                break;
            case R.id.layout_changeName_popwindow:
                mPicChooserDialog.dismiss();
                showTextDialog();
                break;
            case R.id.cancle_button:
                mPopupdialog.dismiss();
                break;
            case R.id.layout_unbind_popwindow:
                mPicChooserDialog.dismiss();

                final CustomDialog.Builder builder = new CustomDialog.Builder(DeviceDetailActivity.this);
                builder.setTitle(getResources().getString(R.string.notice));
                builder.setShowBindInfo(getResources().getString(R.string.unbound_message));
                builder.setShowButton(true);
                builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        if (app.manager.cubicBLEDevice != null) {
                            app.manager.cubicBLEDevice.disconnectedDevice();
                        }
                        present.deleteBindDevice(selectAddress);
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
                break;
            default:
                break;
        }
    }

    private void removeIContent(String address) {

        for (int i = 0; i < content.addressList.size(); i++) {
            if (content.addressList.get(i).getDeviceaAddress().equals(address)) {
                content.addressList.remove(i);
            }
        }
    }

    private void closeDevice() {
        final CustomDialog.Builder builder = new CustomDialog.Builder(DeviceDetailActivity.this);
        builder.setTitle(getResources().getString(R.string.notice));
        builder.setShowBindInfo(getResources().getString(R.string.close_message));
        builder.setShowButton(true);
        builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if (app.manager.cubicBLEDevice != null) {
                    app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.CLOSE_DEVICE);
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

    private Dialog mPopupdialog;

    private void showTextDialog() {
        View viewDialog = getLayoutInflater().inflate(R.layout.textview_popwindow, null);
        final EditText editText = viewDialog.findViewById(R.id.editText_weight);
        editText.setHint(selectName);
        TextView leftButton = viewDialog.findViewById(R.id.cancle_button);
        TextView rightButtton = viewDialog.findViewById(R.id.confirm_button);
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
        leftButton.setOnClickListener(this);
        rightButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupdialog.dismiss();
                String name = editText.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    present.sendNameToServer(name, selectAddress);
                } else {
                    ToastUtil.showToast(DeviceDetailActivity.this, getResources().getString(R.string.name_not));
                }
            }
        });
    }


    private Dialog mPicChooserDialog = null;

    TextView text_code;
    Button text_update_notice;

    private void showPopWindow() {

        View viewDialog = getLayoutInflater().inflate(R.layout.popwindow_item, null);
        LinearLayout layout_repair_popwindow = viewDialog.findViewById(R.id.layout_repair_popwindow);
        TextView textV_repaire = viewDialog.findViewById(R.id.textV_repaire);
        if (car_protect_state) {
            textV_repaire.setTextColor(getResources().getColor(R.color.hint_text));
        } else {
            textV_repaire.setTextColor(getResources().getColor(R.color.grey_color));
        }
        LinearLayout layout_unbind_popwindow = viewDialog.findViewById(R.id.layout_unbind_popwindow);
        LinearLayout layout_update_popwindow = viewDialog.findViewById(R.id.layout_update_popwindow);
        TextView textV_more = viewDialog.findViewById(R.id.textV_more);
        if (low_elec) {
            textV_more.setTextColor(getResources().getColor(R.color.hint_text));
        } else {
            textV_more.setTextColor(getResources().getColor(R.color.grey_color));
        }
        LinearLayout layout_code_popwindow = viewDialog.findViewById(R.id.layout_code_popwindow);
        LinearLayout layout_nameChange_popwindow = viewDialog.findViewById(R.id.layout_changeName_popwindow);
        text_update_notice = viewDialog.findViewById(R.id.text_update_notice);
        layout_nameChange_popwindow.setOnClickListener(this);
        layout_update_popwindow.setOnClickListener(this);
        layout_repair_popwindow.setOnClickListener(this);
        layout_unbind_popwindow.setOnClickListener(this);
        TextView textView = viewDialog.findViewById(R.id.code_text);
        text_code = viewDialog.findViewById(R.id.text_update_code);
        text_code.setText(content.code);
        if (content.newCode != null && content.newCode.compareToIgnoreCase(content.code) == 1) {
            text_update_notice.setVisibility(View.VISIBLE);
        } else {
            text_update_notice.setVisibility(View.INVISIBLE);
        }
        if (IContent.isBLE && deviceFunction != null && "1".equals(deviceFunction)) {
            layout_update_popwindow.setVisibility(View.VISIBLE);
            layout_repair_popwindow.setVisibility(View.VISIBLE);
            layout_code_popwindow.setVisibility(View.GONE);
        } else {
            layout_update_popwindow.setVisibility(View.GONE);
            layout_repair_popwindow.setVisibility(View.GONE);
            layout_code_popwindow.setVisibility(View.VISIBLE);
            textView.setText(getResources().getString(R.string.code, "V 1.0.1"));
        }
        mPicChooserDialog = new Dialog(this, R.style.MyDialogStyleBottom);
        mPicChooserDialog.setContentView(viewDialog, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        DisplayMetrics dm = new DisplayMetrics();
        Window dialogWindow = mPicChooserDialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        m.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = dm.widthPixels;
        p.alpha = 1.0f;
        p.dimAmount = 0.6f;
        p.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(p);
        mPicChooserDialog.show();
    }

    private void showNameBlueToothDialog() {
        if (weightPickerDialog != null && weightPickerDialog.isShowing()) {
            return;
        }
        weightPickerDialog = new AdultWeightPickerDialog(this);
        weightPickerDialog.setOnWeightListener(this);
        DatepickerDialog.setWindowSize(weightPickerDialog);
        weightPickerDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isopen_Auto_brake) {
            if (IContent.isBLE && deviceFunction != null && "1".equals(deviceFunction)) {
                idBle_device.setVisibility(View.VISIBLE);
                layout_ble_detail.setVisibility(View.VISIBLE);
                is_tempurature.setVisibility(View.GONE);
                is_notBledevice.setVisibility(View.GONE);
                initAnimation();
            } else {
                idBle_device.setVisibility(View.GONE);
                layout_ble_detail.setVisibility(View.GONE);
                is_tempurature.setVisibility(View.VISIBLE);
                is_notBledevice.setVisibility(View.VISIBLE);
            }
        }
        text_speed_detail.setText(travelInfoEntity.getAverageSpeed());
        text_today_mileage.setText(travelInfoEntity.getTodayMileage());
        text_total_mileage.setText(travelInfoEntity.getTotalMileage());
    }

    public void refresh_close() {
        if (IContent.isBLE) {
            image_wheel_detail.setVisibility(View.GONE);
            image_brake_detail.setVisibility(View.GONE);
            iamge_bac__.setVisibility(View.GONE);
            electricity_layout.setImageResource(R.drawable.elec_back_close);
            image_back_speed.setImageResource(R.drawable.brake_bac_close);
            mButton.setEnabled(false);
            mButton.setBackgroundResource(R.drawable.btn_bac_close);
            initSeekBar(-1);
            mSwitchCompat.setEnabled(false);
            mSwitchCompat.setThumbResource(R.drawable.seek_button_close);
            switch_brake_close.setEnabled(false);
            switch_brake_close.setThumbResource(R.drawable.seek_button_close);
            mSeekBar.setEnabled(false);
            content.address = null;
            content.isBind = false;
            mSeekBar.setThumb(getResources().getDrawable(R.drawable.seek_button_close));
            image_elec_relayout.setVisibility(View.GONE);
            if (animator != null) {
                animator.cancel();
            }
            if (a != null) {
                image_brake_detail.clearAnimation();
            }
            layout_hhh.setVisibility(View.GONE);
            electric_rate.setText(getResources().getString(R.string.electricity, 0 + "%"));

        } else {
            img_is_tempurature.setImageResource(R.drawable.temperature_bac_close);
            image_elec_relayout.setVisibility(View.GONE);
            electricity_layout.setImageResource(R.drawable.elec_back_close);
            text_tempurature.setText("");
            electric_rate.setText(getResources().getString(R.string.electricity, "0"));
            text_speed_detail.setText("");
            is_tempurature.setVisibility(View.VISIBLE);
            isnot_ble_car.setVisibility(View.GONE);
            isnot_ble_car_bac.setImageResource(R.drawable.average_speed_close);
        }
    }

    public void refreshOpne() {
        if (IContent.isBLE) {
            electricity_layout.setImageResource(R.drawable.elec_back);
            image_back_speed.setImageResource(R.drawable.brake_bac);
            mButton.setBackgroundResource(R.drawable.button_close);
            mButton.setEnabled(true);
            mSwitchCompat.setThumbResource(R.drawable.play_plybar_btn);
            mSwitchCompat.setEnabled(true);
            switch_brake_close.setThumbResource(R.drawable.play_plybar_btn);
            switch_brake_close.setEnabled(true);
            if (!isRun || !isopen_Auto_brake) {
                mSeekBar.setEnabled(true);
            }
            mSeekBar.setThumb(getResources().getDrawable(R.drawable.play_plybar_btn));
            image_elec_relayout.setVisibility(View.VISIBLE);
            layout_hhh.setVisibility(View.VISIBLE);
            electric_rate.setText(getResources().getString(R.string.electricity, 0 + "%"));

            if (isopen_Auto_brake) {
                image_wheel_detail.setVisibility(View.VISIBLE);
                image_brake_detail.setVisibility(View.VISIBLE);
                if (isRun) {
                    iamge_bac__.setVisibility(View.VISIBLE);
                } else {
                    iamge_bac__.setVisibility(View.GONE);
                }
            }
            initColor(elec);

        } else {
            initColor(Integer.valueOf(travelInfoEntity.getElectric_rate()));
            text_tempurature.setText(travelInfoEntity.getText_tempurature() + " ℃");
            image_elec_relayout.setVisibility(View.VISIBLE);
            img_is_tempurature.setImageResource(R.drawable.temperature_bac);
            isnot_ble_car.setVisibility(View.VISIBLE);
            isnot_ble_car_bac.setImageResource(R.drawable.average_speed);
            text_speed_detail.setVisibility(View.VISIBLE);
        }
    }

    private void setTextWithData() {
        text_speed_detail.setText(travelInfoEntity.getAverageSpeed());
        text_today_mileage.setText(travelInfoEntity.getTodayMileage());
        text_total_mileage.setText(travelInfoEntity.getTotalMileage());
    }

    @Override
    public void initDataFromServer() {
        present.getAdultWeight();
        present.getTravelInfo(selectAddress, this);
    }

    @Override
    protected DeviceDetailPresent createPresent() {
        return new DeviceDetailPresent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.removeDelegate();
        }
        unregisterReceiver(mBroadcastReceiver);

        if (dialogUtil != null){
            dialogUtil.removeConfirmClick();
        }
        IContent.IS_DETAIL = false;
    }

    @Override
    protected void showLoading() {

    }

    @Override
    protected void onErrorViewClick(View v) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            selectAddress = intent.getStringExtra(content.clickPositionAddress);
            selectName = intent.getStringExtra(content.clickPositionName);
            deviceFunction = intent.getStringExtra(content.clickPositionFunction);
        }

        IContent.IS_DETAIL = true;

        include = findViewById(R.id.include);
        actionMore = findViewById(R.id.actionMore);
        initToolBar(include, selectName);
        mImage = findViewById(R.id.image_electricity_bac);
        image_edit_detail = findViewById(R.id.image_edit_detail);
        image_wheel_detail = findViewById(R.id.image_wheel_detail);
        iamge_bac__ = findViewById(R.id.iamge_bac__);
        image_brake_detail = findViewById(R.id.image_brake_detail);
        idBle_device = findViewById(R.id.idBle_device);
        is_tempurature = findViewById(R.id.is_tempurature);
        img_is_tempurature = findViewById(R.id.img_is_tempurature);
        text_total_mileage = findViewById(R.id.text_total_mileage);
        text_today_mileage = findViewById(R.id.text_today_mileage);
        text_tempurature = findViewById(R.id.text_tempurature);
        electric_rate = findViewById(R.id.electric_rate);
        text_speed_detail = findViewById(R.id.text_speed_detail);
        electricity_layout = findViewById(R.id.electricity_layout);
        image_elec_relayout = findViewById(R.id.image_elec_relayout);
        layout_hhh = findViewById(R.id.layout_hhh);
        text_brake_state = findViewById(R.id.text_brake_state);
        weight_parent = findViewById(R.id.weight_parent);
        total_calories = findViewById(R.id.total_calories);
        text_today_calor = findViewById(R.id.text_today_calor);
        image_back_speed = findViewById(R.id.image_back_speed);
        mSwitchCompat = findViewById(R.id.switch_close);
        switch_brake_close = findViewById(R.id.switch_brake_close);
        isnot_ble_car_bac = findViewById(R.id.isnot_ble_car_bac);
        isnot_ble_car = findViewById(R.id.isnot_ble_car);
        protect_layout = findViewById(R.id.protect_layout);
        mSeekBar = findViewById(R.id.voice_seekbar);
        mSeekBar.setEnabled(false);
        layout_seekbar_detail = findViewById(R.id.layout_seekbar_detail);
        layout_ble_detail = findViewById(R.id.layout_ble_detail);
        is_notBledevice = findViewById(R.id.is_notBledevice);
        mButton = findViewById(R.id.btn_close_open);
        refresh_Unlink_Close();
        layout_seekbar_detail.setFocusable(false);
        animator = (ObjectAnimator.ofFloat(image_wheel_detail, "rotation", 0.0F, -360.0F));
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(3000L);
        animator.setRepeatCount(ValueAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animation.end();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mSeekBar.setMax(6);
        mSeekBar.setProgress(0);
//        for (int i = 0; i < imageView.length; i++) {
//            imageView[i] = findViewById(id[i]);
//        }
        image_edit_detail.setOnClickListener(this);
        app = (MyApplication) getApplication();

        operateDBUtils = new OperateDBUtils(this);
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        image_elec_relayout.measure(w, h);
        height = image_elec_relayout.getMeasuredHeight();
        netWorkRequest = new NetWorkRequest(this);


        initToolBar();
        initView();
        if (app.manager.cubicBLEDevice != null) {
            if (IContent.isBLE) {
                showProgressBar(getResources().getString(R.string.data_update), getString(R.string.data_update_fail),10);
                app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, IContent.NOTIFY_DATA);
            } else {
                refreshOpne();
            }
        } else {
            showProgressBar(getResources().getString(R.string.data_update), 25000, getString(R.string.data_update_fail), true);
            scanAndConnectBluetooth(selectAddress, selectName);

        }
        dialogUtil = DialogUtil.getIntence();
    }

    @Override
    public void connect() {
        super.connect();
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.setBLEBroadcastDelegate(this);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_detail;
    }

    @Override
    public void describeDown(Intent intent) {
        super.describeDown(intent);
        sendBlueToothByte(IContent.NOTIFY_DATA);
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
            if (data[5] == 0x01) {
                content.SD_Mode = true;
            }
        }
        if (data[3] == (byte) 0x8B && data[4] == 0x08) {
            String str = "V1.0." + data[5];
            content.code = str;

            if ("V1.0.3".equals(str)) {
                SharedPreferences sp = getSharedPreferences(IContent.IS_FIRST_USE, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                if (sp.getBoolean(IContent.IS_VERSION, true)) {
                    dialogUtil.showDialogNoConfirm(DeviceDetailActivity.this);
                    editor.putBoolean(IContent.IS_VERSION, false).apply();
                }
            }
            if (!low_elec && content.newCode != null && content.newCode.compareToIgnoreCase(content.code) == 1) {
                SharedPreferences sp = getSharedPreferences(IContent.IS_FIRST_USE, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                if (!content.newCode.equals(sp.getString(IContent.VERSION_UPDATE, "V1.0.2"))) {
                    dialogUtil.setConfirmClick(this);
                    dialogUtil.showDialogConfirm(DeviceDetailActivity.this);
                    editor.putString(IContent.VERSION_UPDATE, content.newCode).apply();
                }
            }

            toSendData(data);

        }
        if (data[3] == (byte) 0x8B && data[4] == 0x01) {
            int averageSpeed0 = data[6] & 0xff;
            int today_mileage0 = data[8] & 0xff;
            int total_mileage0 = data[10] & 0xff;
            int averageSpeed1 = (data[5] << 8) & 0xff;
            int today_mileage1 = (data[7] << 8) & 0xff;
            int total_mileage1 = (data[9] << 8) & 0xff;

            double today_mileage = (today_mileage0 + today_mileage1) * 1.0 / 100;
            double total_mileage = (total_mileage0 + total_mileage1) * 1.0 / 10;
            double averageSpeed = (averageSpeed0 + averageSpeed1) * 1.0 / 10;

            travelInfoEntity.setTodayMileage(String.valueOf(today_mileage));
            travelInfoEntity.setTotalMileage(String.valueOf(total_mileage));
            travelInfoEntity.setAverageSpeed(String.valueOf(averageSpeed));
            present.senTravelInfoToServer(today_mileage, total_mileage, averageSpeed, deviceFunction);
            setTextWithData();
        }
        if (data[3] == (byte) 0x8B && data[4] == 0x07) {
            elec = data[5] & 0xff;
            refreshOpne();
        }
        if (data[3] == (byte) 0x8B && data[4] == 0x04) {
            if (data[5] == 0x00) {
                isRun = true;
                if (isopen_Auto_brake) {
                    mSeekBar.setEnabled(false);
                    layout_seekbar_detail.setEnabled(true);
                    layout_seekbar_detail.setFocusable(true);
                    layout_seekbar_detail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ToastUtil.showToast(DeviceDetailActivity.this, getString(R.string.run_action_notice));
                        }
                    });
                }
            } else if (data[5] == 0x55) {
                isRun = false;
                mSeekBar.setEnabled(true);
                layout_seekbar_detail.setFocusable(false);
                layout_seekbar_detail.setOnClickListener(null);
            }
            onResume();
        }
        if (data[3] == (byte) 0x8B && data[4] == 0x05) {

            if (data[5] == 0x01) {
                mSwitchCompat.setChecked(true);
                switch_brake_close.setEnabled(false);
                switch_brake_close.setThumbResource(R.drawable.seek_button_close);
                car_protect_state = true;
            } else {
                mSwitchCompat.setChecked(false);
                switch_brake_close.setEnabled(true);
                switch_brake_close.setThumbResource(R.drawable.play_plybar_btn);
                car_protect_state = false;
            }
        }
        if (data[3] == (byte) 0x8B && data[4] == 0x09) {
            mSeekBar.setProgress(data[5]);
            initSeekBar(data[5]);
        }
        if (data[3] == (byte) 0x8B && data[4] == (byte) 0xAA) {
            ToastUtil.showToast(DeviceDetailActivity.this, getResources().getString(R.string.close_sucess));
            if (app.manager.cubicBLEDevice != null) {
                app.manager.cubicBLEDevice.disconnectedDevice();
            }
            refresh_close();
            return;
        }
        if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0D) {
            switch_brake_close.setChecked(false);
            protect_layout.setAlpha(0.3f);
            mSwitchCompat.setThumbResource(R.drawable.seek_button_close);
            mSwitchCompat.setEnabled(false);
            refresh_wheel_close();

        }
        if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0E) {
            switch_brake_close.setChecked(true);
            protect_layout.setAlpha(1f);
            mSwitchCompat.setThumbResource(R.drawable.play_plybar_btn);
            mSwitchCompat.setEnabled(true);
            refresh_wheel_open();
        }
        if (data[3] == (byte) 0x83) {
            doMusic(data);
        }
    }

    @Override
    public void disConnect(Intent intent) {
        super.disConnect(intent);
        content.isBind = false;
        content.address = null;
        content.deviceName = null;
        refresh_close();
    }

    @Override
    public void dataAvailableRead(Intent intent) {
        super.dataAvailableRead(intent);
        byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);

        if (data[3] == (byte) 0x8B && data[4] == (byte) 0xAA) {
            ToastUtil.showToast(DeviceDetailActivity.this, getResources().getString(R.string.close_sucess));
            if (app.manager.cubicBLEDevice != null) {
                app.manager.cubicBLEDevice.disconnectedDevice();
            }

            refresh_close();
            return;
        }
        if (data[3] == (byte) 0x8B && data[4] == 0x05) {
            if (data[5] == 0x01) {
                mSwitchCompat.setChecked(true);
                switch_brake_close.setEnabled(false);
                switch_brake_close.setThumbResource(R.drawable.seek_button_close);
                car_protect_state = true;
            }
            return;
        }
        if (data[3] == (byte) 0x8B && data[4] == 0x06) {
            if (data[5] == 0x00) {
                mSwitchCompat.setChecked(false);
                switch_brake_close.setEnabled(true);
                switch_brake_close.setThumbResource(R.drawable.play_plybar_btn);
                car_protect_state = false;
            }
            return;
        }
        if (data[3] == (byte) 0x8B && data[4] == 0x09) {
            mSeekBar.setProgress(data[5]);
            initSeekBar(data[5]);
        }

        if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0D) {
            switch_brake_close.setChecked(false);
            protect_layout.setAlpha(0.3f);
            mSwitchCompat.setThumbResource(R.drawable.seek_button_close);
            mSwitchCompat.setEnabled(false);
            ToastUtil.showToast(DeviceDetailActivity.this, getResources().getString(R.string.close_shake), Toast.LENGTH_SHORT);
            refresh_wheel_close();
            return;
        }
        if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0E) {
            switch_brake_close.setChecked(true);
            mSwitchCompat.setThumbResource(R.drawable.play_plybar_btn);
            protect_layout.setAlpha(1f);
            mSwitchCompat.setEnabled(true);
            refresh_wheel_open();
            return;
        }
    }


    private void toSendData(byte[] b) {
        String data_message = Tools.byte2Hex(b);
        netWorkRequest.sendDataMessage(data_message, Tools.getCurrentDateHour(), selectAddress, content.clickPositionType, new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e != null) {
                    checkNetWork();
                }
            }
        });

    }

    private void refresh_wheel_close() {
        mSeekBar.setEnabled(true);
        layout_seekbar_detail.setEnabled(false);
        layout_seekbar_detail.setFocusable(false);
        layout_seekbar_detail.setOnClickListener(null);
        text_brake_state.setText(getResources().getString(R.string.device_close));
        isopen_Auto_brake = false;
        image_wheel_detail.setVisibility(View.GONE);
        image_brake_detail.setVisibility(View.GONE);
        iamge_bac__.setVisibility(View.GONE);
        image_back_speed.setImageResource(R.drawable.brake_bac_close);
        if (animator != null) {
            animator.cancel();
        }
    }

    private void refresh_wheel_open() {
        isopen_Auto_brake = true;
        if (!isRun) {
            mSeekBar.setEnabled(true);
            layout_seekbar_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.showToast(DeviceDetailActivity.this, getString(R.string.run_action_notice));
                }
            });
        }
        image_wheel_detail.setVisibility(View.VISIBLE);
        image_brake_detail.setVisibility(View.VISIBLE);
        if (isRun) {
            text_brake_state.setText(getResources().getString(R.string.device_running));
            iamge_bac__.setVisibility(View.VISIBLE);
        } else {
            text_brake_state.setText(getResources().getString(R.string.device_isbrake));
            iamge_bac__.setVisibility(View.GONE);
        }
        image_back_speed.setImageResource(R.drawable.brake_bac);
    }

    private void initSeekBar(int data) {
//        for (int i = 0; i < imageView.length; i++) {
//            if (i <= data) {
//                imageView[i].setImageResource(R.drawable.red_circle);
//            } else {
//                imageView[i].setImageResource(R.drawable.gray_circle);
//            }
//        }
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progres = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                byte[] bytes = {0x55, (byte) 0xAA, 0x01, 0x0B, 0x09, (byte) progres, (byte) (0 - (0x01 + 0x0B + 0x09 + (byte) progres))};
                app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, bytes);
            }
        });
    }

    @Override
    public void onReceiveDataAvailable(String dataType, String data, TravelInfoEntity travelInfoEntity, String time) {
        if (RequestUtils.TRAVEL_INFO.equals(dataType)) {
            if (travelInfoEntity != null) {
                text_speed_detail.setText(travelInfoEntity.getAverageSpeed());
                text_today_mileage.setText(travelInfoEntity.getTodayMileage());
                text_total_mileage.setText(travelInfoEntity.getTotalMileage());
            }
        }
        if (RequestUtils.WEIGHT.equals(dataType)) {
            RequestUtils.getSharepreferenceEditor(this).putString(RequestUtils.WEIGHT, data).commit();
            operateDBUtils.queryUserWeightData();
        }
    }

    @Override
    public void onClick(String weight) {
        weightPickerDialog.dismiss();
        present.sendWeightToServer(weight);
    }

    @Override
    public void onConfirm() {
        Intent intent = new Intent(this, DeviceUpdateActivity.class);
        intent.putExtra("update", true);
        startActivity(intent);

    }

    @Override
    public void getCaloriesSuccess(TravelInfoEntity entity) {
        weight_parent.setText(entity.getAdultWeight());
    }

    @Override
    public void getCaloriesFailed(String message) {
        checkNetWork();
    }

    @Override
    public void getTravelInfoSuccess(TravelInfoEntity entity) {
        text_today_calor.setText(entity.getTodayCalor());
        total_calories.setText(entity.getTotalCalor());
        text_today_mileage.setText(entity.getTodayMileage());
        text_total_mileage.setText(entity.getTotalMileage());
    }

    @Override
    public void getTravelInfoFailed(String message) {
        checkNetWork();
    }

    @Override
    public void sendTravelInfoToServerSuccess(TravelInfoEntity entity) {
        OperateDBUtils operateDBUtils = new OperateDBUtils(this);
        operateDBUtils.saveTravelInfoToDB(entity, Tools.getCurrentTime());
        present.addServerToDB(entity.getTodayMileage(), this);


    }

    @Override
    public void sendTravelInfoToServerFailed(String message) {
        checkNetWork();
    }

    @Override
    public void sendWeightSuccess(String weight) {
        weight_parent.setText(weight);
    }

    @Override
    public void sendWeightFailed(String message) {
        checkNetWork();
    }

    @Override
    public void unBindSuccess(String address) {
        ToastUtil.showToast(DeviceDetailActivity.this, getResources().getString(R.string.unbound_success));
        removeIContent(address);
        Intent intent = new Intent();
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void unBindFailed(String message) {
        checkNetWork();
    }

    @Override
    public void loadSuccess(String s) {
        ToastUtil.showToast(DeviceDetailActivity.this, getResources().getString(R.string.name_change_sucess));
        initToolBar(include, s);
        present.updateDeviceList();
    }

    @Override
    public void loadFailed(String s) {
        checkNetWork();
    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null && intent.getExtras() != null) {

                if (intent.getAction().equals(OperateDBUtils.QUERY_FINISH)) {
                    String data = intent.getExtras().getString(OperateDBUtils.QUERY_DATA);
                }
                if (intent.getAction().equals(RequestUtils.RECEIVE_TEMPERATURE_ELECTRIC_ACTION)) {
                    String temperature = intent.getExtras().getString(RequestUtils.TEMPERATURE);
                    String electric = intent.getExtras().getString(RequestUtils.CURRENT_ELECTRIC) + "%";
                    electric_rate.setText(getResources().getString(R.string.electricity, electric));
                    electric = electric.replace("%", "");
                    initColor(Integer.valueOf(electric));
                    text_tempurature.setText(temperature + " ℃");
                    travelInfoEntity.setElectric_rate(electric);
                    travelInfoEntity.setText_tempurature(temperature);
                    refreshOpne();
                    onResume();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (app.manager.cubicBLEDevice != null) {
            byte[] b = Tools.getDateTimeSplit();
            byte[] bytes = {0x55, (byte) 0xAA, 0x07, 0x0B, 0x0A, b[0], b[1], b[2], b[3], b[4], b[5], b[6], (byte) (0 - (0x07 + 0x0B + 0x0A + b[0] + b[1] + b[2] + b[3] + b[4] + b[5] + b[6]))};
            if (app.manager.cubicBLEDevice != null) {
                app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, bytes);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (app.manager.cubicBLEDevice != null) {
            if (IContent.isBLE) {

            }
        }

        if (requestCode == 100 && resultCode == RESULT_OK) {
            String s = data.getStringExtra("updateDone");
            if (!TextUtils.isEmpty(s) && "updateDone".equals(s)) {
                restartProgress();
            }
        }
    }

    ProgressDialog pd;

    protected void restartProgress() {
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCanceledOnTouchOutside(false);
        pd.setMax(15);
        pd.setMessage(getString(R.string.restarting));
        timer.start();
        pd.show();
    }

    CountDownTimer timer = new CountDownTimer(15000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            pd.setProgress(15 - (int) (millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            pd.dismiss();
        }
    };


}
