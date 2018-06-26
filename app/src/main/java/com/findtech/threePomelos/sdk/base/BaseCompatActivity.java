package com.findtech.threePomelos.sdk.base;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.MediaAidlInterface;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.bluetooth.BlueManager;
import com.findtech.threePomelos.bluetooth.CubicBLEDevice;
import com.findtech.threePomelos.bluetooth.server.RFStarBLEService;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.home.MainActivity;
import com.findtech.threePomelos.music.activity.PlayDetailActivity;
import com.findtech.threePomelos.music.info.MusicInfo;
import com.findtech.threePomelos.music.utils.HandlerUtil;
import com.findtech.threePomelos.music.utils.IConstants;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.musicserver.MediaService;
import com.findtech.threePomelos.musicserver.Nammu;
import com.findtech.threePomelos.musicserver.control.MusicPlayer;
import com.findtech.threePomelos.musicserver.control.MusicStateListener;
import com.findtech.threePomelos.musicserver.server.FloatingService;
import com.findtech.threePomelos.musicserver.server.WatcherHome;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.sdk.AppUtils;
import com.findtech.threePomelos.sdk.MyApplication;
import com.findtech.threePomelos.sdk.manger.AppManager;
import com.findtech.threePomelos.sdk.manger.RxHelper;
import com.findtech.threePomelos.travel.bean.TravelOnceBean;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.NetUtils;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.StatusBarUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.dialog.CustomDialog;

import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import me.yokeyword.fragmentation.SupportActivity;

import static com.findtech.threePomelos.musicserver.control.MusicPlayer.mService;


/**
 * activity基类
 *
 * @author Administrator
 */
public abstract class BaseCompatActivity extends SupportActivity implements ServiceConnection {

    protected Context mContext;
    protected boolean isTransAnim;
    private Unbinder mUnbinder;
    private boolean isSend = false;

    static {
        //5.0以下兼容vector
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    // /**
//     * 网络异常View
//     */
//    protected View errorView;
//    /**
//     * loadingView
//     */
//    protected View loadingView;
//    /**
//     * 没有内容view
//     */
//    protected View emptyView;
    protected static MyApplication app = null;
    // 用于关闭Service
    private MusicPlayer.ServiceToken mToken;
    /**
     * receiver 接受播放状态变化等
     */
    private PlaybackStatus mPlaybackStatus;
    private ArrayList<MusicStateListener> mMusicListener = new ArrayList<>();
    private static int activityNumber = 0;
    public Intent intent;
    WatcherHome mHomeWatcher;
    FloatViewReceiver floatViewReceiver;
    public CustomDialog.Builder builder;
    private DialogClick dialogClick;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    public Handler mPlayHandler;
    public static final int NEXT_MUSIC = 0;
    public static final int PRE_MUSIC = 1;
    private PlayMusic mPlayThread;
    private MusicInterface musicInterface;
    public static String DEVICE_CLOSE_ONPAGE = "android.receive.device.close";
    private String noTimeNotice = null;
    private String hasTimeNotice = null;

    BluetoothAdapter bleAdapter;
    BluetoothManager manager;
    Handler myHandler;
    private IContent content = IContent.getInstacne();
    private String nameBluetooth;
    BluetoothBroadcast bluetoothBroadcast;
    public String bluetoothAddress;

    Dialog dialog;
    private Disposable disposable;


    /**
     * 进度提示框
     *
     * @param message 提示的信息
     * @param notice  超时的提示信息
     */
    public void showProgressBar(String message,final String notice , int time) {
        createDialog(message);
        dialog.show();

        disposable = Observable.timer(time, TimeUnit.SECONDS)
                .compose(RxHelper.<Long>rxSchedulerHelper())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                            dialog = null;
                            if (!TextUtils.isEmpty(notice)) {
                                ToastUtil.showToast(BaseCompatActivity.this, notice);
                            }
                        }
                    }
                });
    }

    private void createDialog(String message) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.progress, null);
        TextView textView = view.findViewById(R.id.tvMessage);
        textView.setText(message);
        dialog = new Dialog(this, R.style.MyDialogStyleBottom);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        DisplayMetrics dm = new DisplayMetrics();
        final Window dialogWindow = dialog.getWindow();
        WindowManager m = dialogWindow.getWindowManager();
        m.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        p.width = dm.widthPixels;
        p.alpha = 1.0f;
        p.dimAmount = 0.6f;
        p.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(p);
    }

    /**
     * 进度提示框
     *
     * @param message 提示的信息
     * @param notice  超时的提示信息
     */
    public void showProgressBar(String message, int time, final String notice, final boolean disMiss) {
        createDialog(message);
        dialog.show();

        disposable = Observable.timer(time, TimeUnit.SECONDS)
                .compose(RxHelper.<Long>rxSchedulerHelper())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                            dialog = null;
                            if (!TextUtils.isEmpty(notice)) {
                                ToastUtil.showToast(BaseCompatActivity.this, notice);
                            }
                        }
                        if (disMiss) {
                            if (app.manager.cubicBLEDevice != null) {
                                app.manager.cubicBLEDevice.disconnectedDevice();
                            }
                        }
                    }
                });
    }


//    /**
//     * 进度提示框
//     *
//     * @param message 提示的信息
//     * @param time    时间
//     * @param notice  超时的提示信息
//     */
//    public void showProgressDialog(String message, final long time, final String notice) {
//        this.hasTimeNotice = notice;
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.setMessage(message);
//        progressDialog.setIndeterminate(false);
//        progressDialog.setCancelable(true);
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();
//        myHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                    if (!TextUtils.isEmpty(hasTimeNotice)) {
//                        ToastUtil.showToast(BaseCompatActivity.this, hasTimeNotice);
//                    }
//                }
//            }
//        }, time);
//    }

//    /**
//     * 进度提示框
//     *
//     * @param message
//     * @param time
//     * @param notice
//     */
//    public void showProgressDialogDis(String message, final long time, final String notice) {
//        this.hasTimeNotice = notice;
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.setMessage(message);
//        progressDialog.setIndeterminate(false);
//        progressDialog.setCancelable(true);
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();
//        myHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                    if (!TextUtils.isEmpty(hasTimeNotice)) {
//                        ToastUtil.showToast(BaseCompatActivity.this, hasTimeNotice);
//                    }
//                    if (app.manager.cubicBLEDevice != null) {
//                        app.manager.cubicBLEDevice.disconnectedDevice();
//                    }
//                }
//            }
//        }, time);
//    }


    /**
     * 取消提示框
     */
    public void dismissProgressDialog() {

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;

        }
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void setMusicInterface(MusicInterface musicInterface) {
        this.musicInterface = musicInterface;

    }

    public void setClickListening(DialogClick dialogClick) {
        this.dialogClick = dialogClick;
    }

    public void showDialogConfirm(String title, String message) {
        builder.setTitle(title);
        builder.setShowBindInfo(message);
        builder.setShowButton(true);
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                dialogClick.configClick();
            }
        });
        builder.setNegativeButton(getString(R.string.cancle),
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dialogClick.cancleClick();
                    }
                });

        builder.create().show();
    }

    /**
     * 检测网络问题
     */
    public void checkNetWork() {
        if (!NetUtils.isConnectInternet(this)) {
            ToastUtil.showToast(this, getResources().getString(R.string.net_exception));
        }
    }

    /**
     * 更新播放队列
     */
    public void updateQueue() {

    }

    /**
     * 更新歌曲状态信息
     */
    public void updateTrackInfo() {

        for (final MusicStateListener listener : mMusicListener) {
            if (listener != null) {
                listener.reloadAdapter();
                listener.updateTrackInfo();
            }
        }
    }

    /**
     * fragment界面刷新
     */
    public void refreshUI() {
        for (final MusicStateListener listener : mMusicListener) {
            if (listener != null) {
                listener.reloadAdapter();
            }
        }

    }

    /**
     * 更新播放时间
     */
    public void updateTime() {
        for (final MusicStateListener listener : mMusicListener) {
            if (listener != null) {
                listener.updateTime();
            }
        }
    }

    /**
     * 歌曲切换
     */
    public void updateTrack() {
    }

    public void updateLrc() {
    }

    /**
     * @param p 更新歌曲缓冲进度值，p取值从0~100
     */
    public void updateBuffer(int p) {
    }

    /**
     * @param l 歌曲是否加载中
     */
    public void loading(boolean l) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MyApplication) getApplication();
        init(savedInstanceState);
        showLoading();
    }


    public void sendBlueToothByte(byte[] b) {
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, b);
        }
    }

    protected void connectBluetooth(String selectAddress, String nameBluetooth) {
        if (selectAddress == null) {
            return;
        }

        this.nameBluetooth = nameBluetooth;
        if (app.manager.cubicBLEDevice == null) {
            bluetoothAddress = selectAddress;
            manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bleAdapter = manager.getAdapter();
            app.manager.bluetoothDevice = bleAdapter.getRemoteDevice(selectAddress);
            app.manager.isEnabled(this);
            app.manager.cubicBLEDevice = new CubicBLEDevice(
                    getApplicationContext(), app.manager.bluetoothDevice);
        }
    }

    public void scanAndConnectBluetooth(String selectAddress, String nameBluetooth) {

        if (selectAddress == null) {
            return;
        }
        this.nameBluetooth = nameBluetooth;
        scanBluetoothAuto(selectAddress);

    }

    private void scanBluetoothAuto(final String selectAddress) {

        app.manager.setRFstarBLEManagerListener(new BlueManager.RFStarManageListener() {
            @Override
            public void RFstarBLEManageListener(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (device != null && device.getAddress().equals(selectAddress)) {
                    connectBluetooth(selectAddress, nameBluetooth);
                    app.manager.stopScanBluetoothDevice();
                }
            }

            @Override
            public void RFstarBLEManageStartScan() {

            }

            @Override
            public void RFstarBLEManageStopScan() {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void scanBluetoothListener(int callbackType, ScanResult result) {
                if (result.getDevice() != null && result.getDevice().getAddress().equals(selectAddress)) {
                    connectBluetooth(selectAddress, nameBluetooth);
                    app.manager.stopScanBluetoothDevice();
                }
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && Nammu.checkPermission(Nammu.PERMISSIONS_BLUETOOTH[0])
                && Nammu.checkPermission(Nammu.PERMISSIONS_BLUETOOTH[1])) {
            Nammu.requestPermission(this, Nammu.PERMISSIONS_BLUETOOTH, 101, "请打开定位权限!");
        } else {
            app.manager.startScanBluetoothDevice();
        }
    }


    /**
     * 发送时间指令
     */
    public void sendSetTime() {
        byte[] bb = Tools.getDateTimeSplitCurrent();
        byte[] bytess = {0x55, (byte) 0xAA, 0x07, 0x0B, 0x0A, bb[0], bb[1], bb[2], bb[3], bb[4], bb[5], bb[6],
                (byte) (0 - (0x07 + 0x0B + 0x0A + bb[0] + bb[1] + bb[2] + bb[3] + bb[4] + bb[5] + bb[6]))};
        sendBlueToothByte(bytess);
    }

//    public void updateAndSend() {
//        Observable.timer(2, TimeUnit.SECONDS)
//                .compose(RxHelper.<Long>rxSchedulerHelper())
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
//                        sendSetTime();
//                    }
//                });
//    }

    public void sendNotify() {
        Observable.timer(1000, TimeUnit.MILLISECONDS)
                .compose(RxHelper.<Long>rxSchedulerHelper())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        sendBlueToothByte(IContent.NOTIFY_DATA);
                    }
                });
    }

    public void observableSend() {

        Observable.create(new ObservableOnSubscribe<Date>() {
            @Override
            public void subscribe(final ObservableEmitter<Date> e) throws Exception {
                NetWorkRequest.getLastTime(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException exception) {
                        if (exception == null) {
                            if (list.size() > 0) {
                                AVObject avObject = list.get(0);
                                e.onNext(avObject.getDate("endTime"));
                            } else {
                                e.onError(exception);
                            }
                        } else {
                            e.onError(exception);
                        }
                    }
                });
            }
        }).compose(RxHelper.<Date>rxSchedulerHelper())
                .subscribe(new Consumer<Date>() {
                    @Override
                    public void accept(Date date) throws Exception {
                        //获取多少段推行数据
                        byte[] times = Tools.getDateTimeSplitBeforeDays(date);
                        byte[] timeSend = {0x55, (byte) 0xAA, 0x07, 0x0B, 0x10, times[0], times[1], times[2], times[3], times[4], times[5], times[6], (byte) (0 - (0x07 + 0x0B + 0x10 + times[0] + times[1] + times[2] + times[3] + times[4] + times[5] + times[6]))};
                        sendBlueToothByte(timeSend);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        byte[] times = Tools.getDateTimeSplitThreeDays();
                        byte[] timeSend = {0x55, (byte) 0xAA, 0x07, 0x0B, 0x10, times[0], times[1], times[2], times[3], times[4], times[5], times[6], (byte) (0 - (0x07 + 0x0B + 0x10 + times[0] + times[1] + times[2] + times[3] + times[4] + times[5] + times[6]))};
                        sendBlueToothByte(timeSend);
                    }
                });

    }


    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        mService = MediaAidlInterface.Stub.asInterface(service);
    }

    @Override
    public void onServiceDisconnected(final ComponentName name) {
        mService = null;
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (activityNumber == 0 && MusicPlayer.isPlaying()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                startService(intent);
            } else if (Settings.canDrawOverlays(this)) {
                startService(intent);
            }
        }
        activityNumber++;
    }

    public void startFloat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForPermission();
        } else {
            startService(intent);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void askForPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        } else {
            startService(intent);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(BaseCompatActivity.this, "授权失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
            } else {
                startService(intent);
            }
        }
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
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
        mHomeWatcher.startWatch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
        mHomeWatcher.stopWatch();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (activityNumber > 0) {
            activityNumber--;
        }
        if (activityNumber == 0) {
            stopService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
        unbindService();
        try {
            unregisterReceiver(mPlaybackStatus);
            unregisterReceiver(floatViewReceiver);
            unregisterReceiver(bluetoothBroadcast);

        } catch (final Throwable e) {
        }
        mPlayHandler.removeCallbacksAndMessages(null);
        mPlayHandler.getLooper().quit();
        mPlayHandler = null;
        mMusicListener.clear();

        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        if (app.manager != null) {
            app.manager.clearListenering();
        }
        AppManager.getAppManager().finishActivity(this);


    }

    /**
     * 关闭音乐服务
     */
    public void unbindService() {
        if (mToken != null) {
            MusicPlayer.unbindFromService(mToken);
            mToken = null;
        }
    }


    /**
     * 接收广播执行相应的操作
     */
    private final static class PlaybackStatus extends BroadcastReceiver {

        private final WeakReference<BaseCompatActivity> mReference;


        public PlaybackStatus(final BaseCompatActivity activity) {
            mReference = new WeakReference<>(activity);
        }


        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (action == null) {
                return;
            }
            BaseCompatActivity baseActivity = mReference.get();
            if (baseActivity != null) {
                if (action.equals(MediaService.META_CHANGED)) {
                    baseActivity.updateTrackInfo();
                } else if (action.equals(MediaService.PLAYSTATE_CHANGED)) {
                } else if (action.equals(MediaService.TRACK_PREPARED)) {
                    baseActivity.updateTime();
                } else if (action.equals(MediaService.BUFFER_UP)) {
                    baseActivity.updateBuffer(intent.getIntExtra("progress", 0));
                } else if (action.equals(MediaService.MUSIC_LODING)) {
                    baseActivity.loading(intent.getBooleanExtra("isloading", false));
                } else if (action.equals(IConstants.MUSIC_COUNT_CHANGED)) {
                    baseActivity.refreshUI();
                } else if (action.equals(IConstants.PLAYLIST_COUNT_CHANGED)) {
                    baseActivity.refreshUI();
                } else if (action.equals(MediaService.QUEUE_CHANGED)) {
                    baseActivity.updateQueue();
                } else if (action.equals(MediaService.TRACK_ERROR)) {
                    final String errorMsg = "退出";
                    Toast.makeText(baseActivity, errorMsg, Toast.LENGTH_SHORT).show();
                } else if (action.equals(MediaService.MUSIC_CHANGED)) {
                    baseActivity.updateTrack();
                } else if (action.equals(MediaService.LRC_UPDATED)) {
                    baseActivity.updateLrc();
                }
            }
        }
    }


    //-----------------------------------------------BaseCompatActivity------------------

    /**
     * 显示加载进度对话框
     */
    protected abstract void showLoading();


    private void init(Bundle savedInstanceState) {
        setContentView(getLayoutId());
        mUnbinder = ButterKnife.bind(this);
        StatusBarUtils.setTransparent(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        LayoutInflater inflater = LayoutInflater.from(this);
//        errorView = inflater.inflate(R.layout.view_network_error, null);
//        loadingView = inflater.inflate(R.layout.view_loading, null);
//        emptyView = inflater.inflate(R.layout.view_empty, null);
//        errorView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onErrorViewClick(v);
//            }
//        });
        AppManager.getAppManager().addActivity(this);
        myHandler = HandlerUtil.getInstance(this);
        initData();
        initMusic();
        initBlueBroadCast();
        initFloatWindow();
        initView(savedInstanceState);

    }

    protected void initBlueBroadCast() {
        bluetoothBroadcast = new BluetoothBroadcast(this);
        registerReceiver(bluetoothBroadcast, bleIntentFilter());
    }

    private void initFloatWindow() {
        floatViewReceiver = new FloatViewReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IContent.SINGLE_CLICK);
        intentFilter.addAction(IContent.DOUBLE_CLICK);
        registerReceiver(floatViewReceiver, intentFilter);
        intent = new Intent(BaseCompatActivity.this, FloatingService.class);
        mHomeWatcher = new WatcherHome(this);
        mHomeWatcher.setOnHomePressedListener(new WatcherHome.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                stopService(intent);

            }

            @Override
            public void onHomeLongPressed() {
                stopService(intent);
            }
        });
    }


    /**
     * 读取数据的回调
     *
     * @param intent
     */
    public void dataAvailableRead(Intent intent) {
//        final byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
//        if (data[3] == (byte) 0x8B && data[4] == (byte) 0x0A){
//            sendBlueToothByte(IContent.NOTIFY_DATA);
//        }

    }

    /**
     * notify 数据回调
     *
     * @param intent
     */
    public void dataAvailable(Intent intent) {

        final byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);

        if (data[3] == (byte) 0x8B && data[4] == (byte) 0x10) {

            Observable.create(new ObservableOnSubscribe<TravelOnceBean>() {
                @Override
                public void subscribe(ObservableEmitter<TravelOnceBean> e) throws Exception {
                    final int distance = (data[14] << 8) + (data[15] & 0xff);
                    final int travelTime = (data[16] << 8) + (data[17] & 0xff);

                    int date = data[6] & 0xff;
                    long b = (data[7] & 0xff) << 16;
                    long c = (data[8] & 0xff) << 8;
                    long d = data[9] & 0xff;

                    long a;
                    Date startTime;
                    Date endTime;

                    Date dateBase = new Date(80, 0, 2, 0, 0, 0);
                    Calendar calendarStart = Calendar.getInstance();
                    if (date <= 127) {
                        a = date << 24;
                        long total = a + b + c + d;
                        calendarStart.setTimeInMillis(dateBase.getTime() + total * 1000);
                    } else {
                        a = (date - 127) << 24;
                        long tem = 127 << 24;
                        BigInteger integer = BigInteger.valueOf(a);
                        BigInteger total = integer.add(BigInteger.valueOf(tem))
                                .add(BigInteger.valueOf(b))
                                .add(BigInteger.valueOf(c))
                                .add(BigInteger.valueOf(d));
                        calendarStart.setTimeInMillis(dateBase.getTime() + total.longValue() * 1000);

                    }
                    startTime = Tools.getCurrentDateSecond(calendarStart.getTime());
                    int dateEnd = data[10] & 0xff;
                    long bEnd = (data[11] & 0xff) << 16;
                    long cEnd = (data[12] & 0xff) << 8;
                    long dEnd = data[13] & 0xff;

                    Calendar calendarEnd = Calendar.getInstance();
                    if (dateEnd <= 127) {
                        long aEnd = dateEnd << 24;
                        long totalEnd = aEnd + bEnd + cEnd + dEnd;
                        calendarEnd.setTimeInMillis(dateBase.getTime() + totalEnd * 1000);

                    } else {
                        long aEnd = (dateEnd - 127) << 24;
                        long tem = 127 << 24;
                        BigInteger integer = BigInteger.valueOf(aEnd);
                        BigInteger total = integer.add(BigInteger.valueOf(tem))
                                .add(BigInteger.valueOf(bEnd))
                                .add(BigInteger.valueOf(cEnd))
                                .add(BigInteger.valueOf(dEnd));
                        calendarEnd.setTimeInMillis(dateBase.getTime() + total.longValue() * 1000);
                    }
                    endTime = calendarEnd.getTime();
                    TravelOnceBean bean = new TravelOnceBean(startTime, endTime, travelTime, distance);
                    e.onNext(bean);

                }
            }).flatMap(new Function<TravelOnceBean, ObservableSource<String>>() {
                @Override
                public ObservableSource<String> apply(final TravelOnceBean travelOnceBean) throws Exception {

                    return Observable.create(new ObservableOnSubscribe<String>() {
                        @Override
                        public void subscribe(ObservableEmitter<String> e) throws Exception {
                            if (NetUtils.isConnectInternet(BaseCompatActivity.this)) {
                                NetWorkRequest.saveDataToServerWithOnce(travelOnceBean.getMileage(), travelOnceBean.getStartTime(),
                                        travelOnceBean.getEndTime(), travelOnceBean.getUseTime(),
                                        Tools.getDateFromTimeStr(Tools.getTimeFromDate(travelOnceBean.getStartTime())), new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                            }
                                        });
                            } else {
                                OperateDBUtils operateDBUtils = new OperateDBUtils(BaseCompatActivity.this);
                                operateDBUtils.insertTravelOnceToDB(OperateDBUtils.TABLE_TRAVEL_ONCE_URI, Tools.getStringTimeFromDate(travelOnceBean.getStartTime())
                                        , Tools.getStringTimeFromDate(travelOnceBean.getEndTime()),
                                        (int) travelOnceBean.getUseTime(), (int) travelOnceBean.getMileage(),
                                        Tools.getTimeFromDate(travelOnceBean.getStartTime()));
                            }
                            e.onNext("Success");
                        }
                    });
                }
            }).compose(RxHelper.<String>rxSchedulerHelper())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            if (!isSend) {
                                getNewData();
                            }
                            isSend = true;
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
            return;
        }

        if (data[3] == (byte) 0x8B && data[4] == 0x08) {
            Observable.create(new ObservableOnSubscribe<ArrayList<Integer>>() {
                @Override
                public void subscribe(ObservableEmitter<ArrayList<Integer>> e) throws Exception {
                    ArrayList<Integer> integers = new ArrayList<>();
                    int timeBreakHundred = data[5] << 24;
                    int timeBreakNext = data[6] << 16;
                    int timeBreak = data[7] << 8;
                    int timeBreakZero = data[8] & 0xff;

                    int autoBreakHundred = data[9] << 24;
                    int autoBreakNext = data[10] << 16;
                    int autoBreak = data[11] << 8;
                    int autoBreakZero = data[12] & 0xff;

                    int timeSleepZero = data[14] & 0xff;
                    int timeSleepNext = data[13] << 8;

                    int timeCloseZero = data[16] & 0xff;
                    int timeCloseNext = data[15] << 8;

                    int aiBreakTime = timeBreakHundred + timeBreakNext + timeBreak + timeBreakZero;
                    integers.add(aiBreakTime);
                    int autoBreakTime = autoBreakHundred + autoBreakNext + autoBreak + autoBreakZero;
                    integers.add(autoBreakTime);
                    int sleepTime = timeSleepNext + timeSleepZero;
                    integers.add(sleepTime);
                    int closeTime = timeCloseNext + timeCloseZero;
                    integers.add(closeTime);

                    e.onNext(integers);
                }
            }).flatMap(new Function<ArrayList<Integer>, ObservableSource<String>>() {
                @Override
                public ObservableSource<String> apply(ArrayList<Integer> integers) throws Exception {
                    return NetWorkRequest.sendDeviceUseInfo(integers, IContent.getInstacne().address);
                }
            }).compose(RxHelper.<String>rxSchedulerHelper())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
            return;
        }

        if (data[3] == (byte) 0x8B && data[4] == (byte) 0xAA) {
            ToastUtil.showToast(this, getResources().getString(R.string.close_sucess));
            disConnect(intent);
            return;
        }
    }

    private void getNewData() {

        Observable.timer(3, TimeUnit.SECONDS)
                .compose(RxHelper.<Long>rxSchedulerHelper())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                        NetWorkRequest netWorkRequest = new NetWorkRequest(BaseCompatActivity.this);
                        netWorkRequest.getTodayFrequencyMonthToDB();
                        netWorkRequest.getTodayFrequencyWeekToDB();
                        netWorkRequest.getTodayTravelInfoSaveToDB();

//                        netWorkRequest.getTravelInfoSaveToDB();
//                        netWorkRequest.getFrequencyMonthToDB();
//                        netWorkRequest.getFrequencyWeekToDB();
                        isSend = false;
                    }
                });

    }

    /**
     * 连接服务成功
     *
     * @param intent
     */
    public void describeDown(Intent intent) {
        Log.e("currentTime", "======");
    }

    /**
     * 连接断开
     *
     * @param intent
     */
    public void disConnect(Intent intent) {
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.disconnectedDevice();
        }
        IContent.getInstacne().address = null;
        IContent.getInstacne().deviceName = null;
        IContent.isSended = false;
        app.manager.cubicBLEDevice = null;
    }


    /**
     * 蓝牙收取数据的回调
     *
     * @param intent
     */
    public void bluetoothReceive(Intent intent) {

    }


    private void initMusic() {
        //绑定音乐服务
        mToken = MusicPlayer.bindToService(this, this);
        mPlaybackStatus = new PlaybackStatus(this);
        IntentFilter f = new IntentFilter();
        f.addAction(MediaService.PLAYSTATE_CHANGED);
        f.addAction(MediaService.META_CHANGED);
        f.addAction(MediaService.QUEUE_CHANGED);
        f.addAction(IConstants.MUSIC_COUNT_CHANGED);
        f.addAction(MediaService.TRACK_PREPARED);
        f.addAction(MediaService.BUFFER_UP);
        f.addAction(IConstants.EMPTY_LIST);
        f.addAction(MediaService.MUSIC_CHANGED);
        f.addAction(MediaService.LRC_UPDATED);
        f.addAction(IConstants.PLAYLIST_COUNT_CHANGED);
        f.addAction(MediaService.MUSIC_LODING);
        f.setPriority(1000);
        registerReceiver(mPlaybackStatus, new IntentFilter(f));

        mPlayThread = new PlayMusic();
        mPlayThread.start();
        builder = new CustomDialog.Builder(this);
    }

    /**
     * 加载错误时点击重新加载
     *
     * @param v
     */
    protected abstract void onErrorViewClick(View v);


    /**
     * 初始化数据
     * <p>
     * 子类可以复写此方法初始化子类数据
     */
    protected void initData() {
        mContext = AppUtils.getContext();
        isTransAnim = true;
    }

    /**
     * 初始化view
     * <p>
     * 子类实现 控件绑定、视图初始化等内容
     *
     * @param savedInstanceState savedInstanceState
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 获取当前layouty的布局ID,用于设置当前布局
     * <p>
     * 交由子类实现
     *
     * @return layout Id
     */
    protected abstract int getLayoutId();


    /**
     * [页面跳转]
     *
     * @param clz 要跳转的Activity
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(this, clz));
        if (isTransAnim) {
            overridePendingTransition(R.anim.activity_start_zoom_in, R.anim
                    .activity_start_zoom_out);
        }
    }

    /**
     * [页面跳转]
     *
     * @param clz    要跳转的Activity
     * @param intent intent
     */
    public void startActivity(Class<?> clz, Intent intent) {
        intent.setClass(this, clz);
        startActivity(intent);
        if (isTransAnim) {
            overridePendingTransition(R.anim.activity_start_zoom_in, R.anim
                    .activity_start_zoom_out);
        }
    }

    /**
     * [携带数据的页面跳转]
     *
     * @param clz    要跳转的Activity
     * @param bundle bundel数据
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        if (isTransAnim) {
            overridePendingTransition(R.anim.activity_start_zoom_in, R.anim
                    .activity_start_zoom_out);
        }
    }

    /**
     * [含有Bundle通过Class打开编辑界面]
     *
     * @param clz         要跳转的Activity
     * @param bundle      bundle数据
     * @param requestCode requestCode
     */
    public void startActivityForResult(Class<?> clz, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
        if (isTransAnim) {
            overridePendingTransition(R.anim.activity_start_zoom_in, R.anim
                    .activity_start_zoom_out);
        }
    }

    public void initToolBar(View view, String title) {
        TextView tvBarTitle = view.findViewById(R.id.tvBarName);
        tvBarTitle.setText(title);
        ImageView ivBack = view.findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void initToolBarMain(View view, String title) {
        TextView tvBarTitle = view.findViewById(R.id.tvBarName);
        tvBarTitle.setText(title);
        ImageView ivBack = view.findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!RequestUtils.getSharepreference(BaseCompatActivity.this).getBoolean(RequestUtils.IS_LOGIN, false)) {
                    RequestUtils.getSharepreferenceEditor(BaseCompatActivity.this)
                            .putBoolean(RequestUtils.IS_LOGIN, true).apply();
                    startActivity(MainActivity.class);
                }
                finish();
            }
        });
    }


    @Override
    public void finish() {
        super.finish();
        if (isTransAnim) {
            overridePendingTransition(R.anim.activity_finish_trans_in, R.anim
                    .activity_finish_trans_out);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 是否使用overridePendingTransition过度动画
     *
     * @return 是否使用overridePendingTransition过度动画，默认使用
     */
    protected boolean isTransAnim() {
        return isTransAnim;
    }

    /**
     * 设置是否使用overridePendingTransition过度动画
     */
    protected void setIsTransAnim(boolean b) {
        isTransAnim = b;
    }

    /**
     * 悬浮窗广播
     */
    private class FloatViewReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent0) {
            final String action = intent0.getAction();
            if (action.equals(IContent.SINGLE_CLICK)) {
                startActivity(new Intent(BaseCompatActivity.this, PlayDetailActivity.class));
            } else if (action.equals(IContent.DOUBLE_CLICK)) {
                stopService(intent);
            }

        }
    }

    public interface DialogClick {
        /**
         * 确认按钮
         */
        void configClick();

        /**
         * 取消按钮
         */
        void cancleClick();
    }

    public class PlayMusic extends Thread {
        @Override
        public void run() {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            mPlayHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case PRE_MUSIC:
                            MusicPlayer.previous(BaseCompatActivity.this, true);
                            break;
                        case NEXT_MUSIC:
                            MusicPlayer.next();
                            break;
                        case 3:
                            MusicPlayer.setQueuePosition(msg.arg1);
                            break;
                        default:
                            break;
                    }
                }
            };
            Looper.loop();
        }
    }


    public void doMusic(byte[] data) {

        if (data[3] == (byte) 0x83 && data[4] == 0x01) {
            if (!IContent.getInstacne().SD_Mode) {
                if (MusicPlayer.getQueueSize() != 0) {
                    MusicPlayer.playOrPause();
                }
                if (MusicPlayer.isPlaying()) {
                    IContent.isModePlay = true;
                } else {
                    IContent.isModePlay = false;
                }
            } else {
                IContent.isModePlay = !IContent.isModePlay;
            }
        }
        if (data[3] == (byte) 0x83 && data[4] == 0x04) {
            if (!IContent.getInstacne().SD_Mode) {
                Message msg = new Message();
                msg.what = NEXT_MUSIC;
                mPlayHandler.sendMessage(msg);
            } else {
                IContent.isModePlay = true;
            }
        }
        if (data[3] == (byte) 0x83 && data[4] == 0x05) {
            if (!IContent.getInstacne().SD_Mode) {
                Message msg = new Message();
                msg.what = PRE_MUSIC;
                mPlayHandler.sendMessage(msg);
            } else {
                IContent.isModePlay = true;
            }
        }
    }

    public interface MusicInterface {
        /**
         * 音乐接受回调
         *
         * @param intent
         */
        void musicReciver(Intent intent);
    }


    //----------------------播放音乐

    public class PlayMusicBase implements Runnable {
        int position;
        private ArrayList<MusicInfo> infoArrayList;

        public PlayMusicBase(int position, ArrayList<MusicInfo> infoArrayList) {
            this.position = position;
            this.infoArrayList = infoArrayList;
        }

        @Override
        public void run() {

            long[] list = new long[infoArrayList.size()];
            HashMap<Long, MusicInfo> infoArray = new HashMap(infoArrayList.size());
            for (int i = 0; i < infoArrayList.size(); i++) {
                MusicInfo info = infoArrayList.get(i);
                list[i] = info.songId;
                infoArray.put(list[i], infoArrayList.get(i));
            }
            if (position > -1) {
                MusicPlayer.playAll(infoArray, list, position, false);
            }

        }
    }

    //-------------------------------------注册蓝牙广播

    /**
     * 监视广播的属性
     *
     * @return
     */
    protected IntentFilter bleIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RFStarBLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(RFStarBLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(RFStarBLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(RFStarBLEService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(RFStarBLEService.ACTION_GAT_RSSI);
        intentFilter.addAction(RFStarBLEService.ACTION_WRITE_DONE);
        intentFilter.addAction(RFStarBLEService.DESCRIPTOR_WRITER_DONE);
        intentFilter.addAction(RFStarBLEService.NOTIFY_WRITE_DONE);
        intentFilter.addAction(RFStarBLEService.ACTION_DATA_AVAILABLE_READ);
        intentFilter.addAction(RFStarBLEService.ACTION_GATT_CONNECTING);
        intentFilter.setPriority(1000);
        return intentFilter;
    }


    private static final class BluetoothBroadcast extends BroadcastReceiver {

        private final WeakReference<BaseCompatActivity> mReference;


        public BluetoothBroadcast(final BaseCompatActivity activity) {
            mReference = new WeakReference<>(activity);
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            BaseCompatActivity activity = mReference.get();
            String characteristicUUID = intent
                    .getStringExtra(RFStarBLEService.RFSTAR_CHARACTERISTIC_ID);
            String action = intent.getAction();
            if (action.equals(RFStarBLEService.ACTION_WRITE_DONE)) {
                byte[] data = intent
                        .getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
                if (IContent.getInstacne().WRITEVALUE != null && app.manager.cubicBLEDevice != null) {
                    app.manager.cubicBLEDevice.readValue(IContent.SERVERUUID_BLE, IContent.READUUID_BLE, IContent.getInstacne().WRITEVALUE);
                }
                L.e("currentTime WRITE_DONE", Tools.byte2Hex(data));
            }
            if (RFStarBLEService.ACTION_GATT_CONNECTED.equals(action)) {
                IContent.getInstacne().isBind = true;

            } else if (RFStarBLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                IContent.getInstacne().isBind = false;
                IContent.getInstacne().address = null;
                IContent.getInstacne().deviceName = null;
                activity.disConnect(intent);
            } else if (RFStarBLEService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                activity.connect();
                if (app.manager.cubicBLEDevice != null) {
                    app.manager.cubicBLEDevice.setNotification(true);
                }
            } else if (RFStarBLEService.DESCRIPTOR_WRITER_DONE
                    .equals(action)) {

                activity.describeDown(intent);
            } else if (RFStarBLEService.ACTION_DATA_AVAILABLE.equals(action)) {
                byte[] data = intent
                        .getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
                L.e("currentTime", Tools.byte2Hex(data));
                if (IContent.isBLE) {
                    if (data[3] == (byte) 0x81 && data[4] == 0x02) {
                        if (data[5] == 0x03) {
                            IContent.getInstacne().SD_Mode = false;
                        }
                    }
                    activity.dataAvailable(intent);
                }
                if (characteristicUUID.contains("ffe4") && !IContent.isBLE) {
                    if (app.manager.cubicBLEDevice != null) {
                        app.manager.cubicBLEDevice.dealWithPoemlosG(characteristicUUID, data);
                    }
                }

            } else if (action.equals(RFStarBLEService.ACTION_DATA_AVAILABLE_READ)) {
                byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
                L.e("currentTimeRead", Tools.byte2Hex(data));
                activity.dataAvailableRead(intent);
            }
            activity.bluetoothReceive(intent);
        }

    }

    public void connect() {
        content.isBind = true;
        content.address = bluetoothAddress;
        content.deviceName = nameBluetooth;
    }


}
