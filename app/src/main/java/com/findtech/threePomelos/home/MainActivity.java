package com.findtech.threePomelos.home;


import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.bluetooth.BlueManager;
import com.findtech.threePomelos.bluetooth.server.RFStarBLEService;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.home.fragment.UserFragment;
import com.findtech.threePomelos.home.model.UserFragmentModelImpl;
import com.findtech.threePomelos.music.fragment.MusicNewFragment;
import com.findtech.threePomelos.musicserver.Nammu;
import com.findtech.threePomelos.mydevices.bean.DeviceCarBean;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.sdk.base.mvp.BaseActivityMvp;
import com.findtech.threePomelos.sdk.base.mvp.BasePresenterMvp;
import com.findtech.threePomelos.sdk.manger.AppManager;
import com.findtech.threePomelos.sdk.manger.RxHelper;
import com.findtech.threePomelos.travel.fragment.TravelFragment;
import com.findtech.threePomelos.user.bean.UserInfo;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.MyCalendar;
import com.findtech.threePomelos.utils.ToastUtil;

import java.util.ArrayList;

import butterknife.BindView;
import io.reactivex.functions.Consumer;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * @author Administrator
 */
public class MainActivity extends BaseActivityMvp implements
        BlueManager.RFStarManageListener, TravelFragment.ConnectBluetoothActivity {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    private SupportFragment currentFragment;
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;

    private SupportFragment[] mFragments = new SupportFragment[3];
    private static long DOUBLE_CLICK_TIME = 0L;

    @Override
    public void initDataFromServer() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected BasePresenterMvp createPresent() {
        return null;
    }

    @Override
    protected void showLoading() {
    }


    @Override
    protected void onErrorViewClick(View v) {
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            mFragments[FIRST] = TravelFragment.getInstance();
            mFragments[SECOND] = MusicNewFragment.getInstance();
            mFragments[THIRD] = UserFragment.getInstance();

            loadMultipleRootFragment(R.id.container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD]);
            currentFragment = mFragments[FIRST];
        } else {
            mFragments[FIRST] = findFragment(TravelFragment.class);
            mFragments[SECOND] = findFragment(MusicNewFragment.class);
            mFragments[THIRD] = findFragment(UserFragment.class);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        showHideFragment(mFragments[FIRST], currentFragment);
                        currentFragment = mFragments[FIRST];
                        break;
                    case R.id.navigation_dashboard:
                        showHideFragment(mFragments[SECOND], currentFragment);
                        currentFragment = mFragments[SECOND];
                        break;
                    case R.id.navigation_notifications:
                        showHideFragment(mFragments[THIRD], currentFragment);
                        currentFragment = mFragments[THIRD];
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        Nammu.init(this);

        UserFragmentModelImpl model = new UserFragmentModelImpl();
        model.getUserDeviceArray().compose(RxHelper.<ArrayList<DeviceCarBean>>rxSchedulerHelper())
                .subscribe(new Consumer<ArrayList<DeviceCarBean>>() {
                    @Override
                    public void accept(ArrayList<DeviceCarBean> beanArrayList) throws Exception {
                        scanBluetoothAuto();
                    }
                });
    }

    private void scanBluetoothAuto() {

        app.manager.setRFstarBLEManagerListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && Nammu.checkPermission(Nammu.PERMISSIONS_BLUETOOTH[0])
                && Nammu.checkPermission(Nammu.PERMISSIONS_BLUETOOTH[1])) {
            Nammu.requestPermission(this, Nammu.PERMISSIONS_BLUETOOTH, 101, "请打开定位权限!");
        } else {
            app.manager.startScanBluetoothDevice();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                app.manager.startScanBluetoothDevice();
            }
        }
    }

    @Override
    public void RFstarBLEManageListener(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device == null || UserInfo.getInstance().getCarBeanArrayList() == null) {
            return;
        }
        for (DeviceCarBean b : UserInfo.getInstance().getCarBeanArrayList()) {
            if (b.getDeviceaAddress().equals(device.getAddress())) {
                connectBluetooth(device.getAddress(), b.getDeviceName());
                IContent iContent = IContent.getInstacne();
                iContent.address = device.getAddress();
                iContent.deviceName = b.getDeviceName();
                iContent.functionType = b.getFunctionType();
                iContent.company = b.getCompany();
                app.manager.stopScanBluetoothDevice();

            }
        }
    }

    @Override
    public void RFstarBLEManageStartScan() {

    }

    @Override
    public void RFstarBLEManageStopScan() {

    }

    @Override
    public void describeDown(Intent intent) {
        super.describeDown(intent);
        //发送Notify数据指令
        sendBlueToothByte(IContent.NOTIFY_DATA);
        notifyFragment(true);
    }

    private void notifyFragment(boolean b) {
        TravelFragment fragment = findFragment(TravelFragment.class);
        if (fragment != null) {
            fragment.updateConnectButton(b);
        }
    }

    @Override
    public void disConnect(Intent intent) {
        super.disConnect(intent);
        notifyFragment(false);
    }

    @Override
    public void dataAvailableRead(Intent intent) {
        super.dataAvailableRead(intent);
        byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
        if (data[3] == (byte) 0x8B && data[4] == 0x0A) {
            observableSend();
            return;
        }
    }



    @Override
    public void dataAvailable(Intent intent) {
        super.dataAvailable(intent);
        byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
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

            IContent iContent = IContent.getInstacne();
            sendDataToServer(iContent.address, todayMileage, totalMileage,
                    String.valueOf(averageSpeed), iContent.functionType);
            updateAndSend();
        }
    }

    private void sendDataToServer(String selectAddress, final double today, final double total, String speed, String type) {
        final TravelInfoEntity entity = TravelInfoEntity.getInstance();
        NetWorkRequest.saveDataToServerWithDay(selectAddress, today, total, speed, type, new SaveCallback() {
            @Override
            public void done(AVException exception) {
                if (exception == null) {
                    addServerToDB(String.valueOf(today));
                    entity.setTodayMileage(String.valueOf(today));
                    entity.setTodayMileage(String.valueOf(total));
                } else {

                }
            }
        });
    }

    private void addServerToDB(String today) {
        String travelDate = MyCalendar.getToday();
        OperateDBUtils operateDBUtils = new OperateDBUtils(this);
        operateDBUtils.insertTravelToDB(travelDate, today);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - DOUBLE_CLICK_TIME > 2000) {
                ToastUtil.showToast(this, getString(R.string.double_click_exit));
                DOUBLE_CLICK_TIME = System.currentTimeMillis();
            } else {
                stopService(intent);
                AppManager.getAppManager().appExit(this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void sendBluetooth() {
    }

    @Override
    public void scanAndConnect() {
        scanBluetoothAuto();
    }


}
