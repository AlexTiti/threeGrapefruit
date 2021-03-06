package com.findtech.threePomelos.mydevices.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.bluetooth.BLEDevice;
import com.findtech.threePomelos.bluetooth.server.RFStarBLEService;
import com.findtech.threePomelos.entity.TravelInfoEntity;
import com.findtech.threePomelos.music.utils.DownFileUtils;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.sdk.base.BaseCompatActivity;
import com.findtech.threePomelos.utils.DialogUtil;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Administrator
 */
public class DeviceUpdateActivity extends BaseCompatActivity implements View.OnClickListener, BaseCompatActivity.DialogClick, BLEDevice.RFStarBLEBroadcastReceiver, DialogUtil.ConfirmClick {

    ImageView button_update;
    private NetWorkRequest netWorkRequest;
    private Dialog dialog;
    String code;
    AVFile avFile;
    File file;
    private DialogUtil dialogUtil;
    private String carrier;



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                if (IContent.getInstacne().newCode == null || IContent.getInstacne().code.equals(IContent.getInstacne().newCode)) {
                    ToastUtil.showToast(this, getString(R.string.upDateNew));
                    return;
                }
                if (!IContent.getInstacne().isBind) {
                    ToastUtil.showToast(DeviceUpdateActivity.this, getString(R.string.device_nolink));
                    return;
                }
                dialogUtil.showDialogConfirm(DeviceUpdateActivity.this);

                break;

            default:
                break;
        }
    }

    private void update() {
        File fileDir = DownFileUtils.creatFileDir(this, IContent.UPDATE);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        file = new File(fileDir, IContent.getInstacne().newCode + ".mva");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        downProgress();
        netWorkRequest.downUpDateOr(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        AVObject avObject = list.get(0);
                        code = avObject.getString("fileVersion");
                        if (code != null && !code.equals(IContent.getInstacne().code)) {
                            avFile = avObject.getAVFile("file");
                            if (avFile != null) {
                                send(file);
                            }
                        }
                    }
                } else {
                    checkNetWork();
                }
            }
        });
    }

    ProgressDialog pd;

    protected void downProgress() {
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCanceledOnTouchOutside(false);
        pd.setMax(100);
        pd.setMessage(getString(R.string.down_loading));
        pd.show();
    }

    private void send(final File file) {
        if (avFile != null) {
            netWorkRequest.downUpdateFile(avFile, file, new ProgressCallback() {
                @Override
                public void done(Integer integer) {
                    pd.setProgress(integer);
                    if (integer == 100) {
                        pd.dismiss();
                        if (carrier != null && carrier.equals(IContent.MEI_ZU)){
                            meiZuUpdate();
                        }else{
                            showDialogConfirm(getString(R.string.notice), getString(R.string.blue_tooth_update));
                        }
                    }
                }
            });
        }
    }

    public void meiZuUpdate(){

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            String filePath = file.getPath();
            intent.setType("text/*");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            startActivityForResult(Intent.createChooser(intent,getString(R.string.blue_tooth_update_meizu)), 100);
            return;

    }

    @Override
    public void configClick() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        String filePath = file.getPath();
        intent.setType("text/*");
        intent.setClassName("com.android.bluetooth", "com.android.bluetooth.opp.BluetoothOppLauncherActivity");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
        startActivityForResult(intent, 100);
    }


    @Override
    public void cancleClick() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            showProgressDialog(getString(R.string.gujian_shengji), 30000, getString(R.string.update_failed));
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

        button_update = findViewById(R.id.btn_update);
        button_update.setOnClickListener(this);
        netWorkRequest = new NetWorkRequest(this);
        setClickListening(this);
        dialogUtil = DialogUtil.getIntence();
        dialogUtil.setConfirmClick(this);
        Intent intent = getIntent();
        carrier= android.os.Build.MANUFACTURER;

        if (intent != null && intent.getBooleanExtra("update", false)) {
            update();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_update;
    }

    @Override
    public void onReceive(Context context, Intent intent, String macData, String uuid) {
        String action = intent.getAction();
        if (RFStarBLEService.ACTION_GATT_CONNECTED.equals(action)) {
            IContent.getInstacne().isBind = true;
            IContent.getInstacne().address = macData;
            netWorkRequest.updateUUIDCreatAt(macData, new SaveCallback() {
                @Override
                public void done(AVException e) {

                }
            });
        } else if (RFStarBLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
            IContent.getInstacne().isBind = false;
            IContent.getInstacne().address = null;
            dismissProgressDialog();

        } else if (action.equals(RFStarBLEService.ACTION_DATA_AVAILABLE)) {
            byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
            L.e("AAAAA==================", Tools.byte2Hex(data) + "==" + data.length);
            if (data[3] == (byte) 0x83) {
                doMusic(data);
            }
            if (data[3] == (byte) 0x8c && data[4] == 0x01) {
                dismissProgressDialog();
                Intent intent1 = new Intent();
                intent1.putExtra("updateDone", "updateDone");
                setResult(RESULT_OK, intent1);
                finish();
            } else {
                dismissProgressDialog();
                ToastUtil.showToast(DeviceUpdateActivity.this, getResources().getString(R.string.update_failed));
            }
        }
    }

    @Override
    public void onReceiveDataAvailable(String dataType, String data, TravelInfoEntity travelInfoEntity, String time) {

    }


    @Override
    public void onConfirm() {
        update();

    }

}
