package com.findtech.threePomelos.mydevices.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.bluetooth.server.RFStarBLEService;
import com.findtech.threePomelos.mydevices.bean.DeviceStateBean;
import com.findtech.threePomelos.sdk.base.BaseCompatActivity;
import com.findtech.threePomelos.utils.IContent;

import butterknife.BindView;

/**
 * @author Administrator
 */
public class VoiceTypeActivity extends BaseCompatActivity implements
         View.OnClickListener {

    @BindView(R.id.tvFirst)
    TextView tvFirst;
    @BindView(R.id.textViewSecond)
    TextView textViewSecond;
    @BindView(R.id.textViewThird)
    TextView textViewThird;
    @BindView(R.id.textViewFour)
    TextView textViewFour;

    @BindView(R.id.ivFirst)
    ImageView ivFirst;
    @BindView(R.id.imageView)
    ImageView ivSecond;
    @BindView(R.id.imageViewThird)
    ImageView imageViewThird;
    @BindView(R.id.imageViewFour)
    ImageView imageViewFour;
    private ImageView[] imageView = new ImageView[4];
    private IContent content = IContent.getInstacne();

    @Override
    protected void showLoading() {

    }

    @Override
    protected void onErrorViewClick(View v) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        imageView[0] = ivFirst;
        imageView[1] = ivSecond;
        imageView[2] = imageViewThird;
        imageView[3] = imageViewFour;

        tvFirst.setOnClickListener(this);
        textViewSecond.setOnClickListener(this);
        textViewThird.setOnClickListener(this);
        textViewFour.setOnClickListener(this);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_voice_type;
    }


    @Override
    public void dataAvailableRead(Intent intent) {
        super.dataAvailableRead(intent);

        byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
        if (data[3] == (byte) 0x8B && data[4] == 0x12) {
            setVoiceModel(data[5]);
            return;
        }
    }

    public void setVoiceModel(byte b) {

        int i = b & 0xff;
        byte[] b1 = new byte[8];
        for (int index = 0;index<8;index++){
            if ((i & (1 << index)) == 0){
                b1[7-index] = 0;
            }else {
                b1[7-index] = 1;
            }
        }

       int soundIndex = (b1[4] <<3) + (b1[5]<<2) +( b1[6]<<1) + b1[7]  ;

        DeviceStateBean.getInstance().setIndex(soundIndex);
        if (soundIndex >=  imageView.length){
            return;
        }

        for (int index = 0; index < imageView.length; index++) {
            if (index == soundIndex) {
                imageView[soundIndex].setVisibility(View.VISIBLE);
            } else {
                imageView[soundIndex].setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {

        int defaultInt = 0;
        switch (v.getId()) {
            case R.id.tvFirst:
                defaultInt = 0;
                break;
            case R.id.textViewSecond:
                defaultInt = 1;
                break;
            default:
                break;
        }

        DeviceStateBean bean = DeviceStateBean.getInstance();
        int byteSound = (bean.getBrakeNoticeModel() << 6) + (bean.getOtherNoticeModel() << 4) + defaultInt;
        byte[] byteSounds = {0x55, (byte) 0xAA, 0x01, (byte) 0x0B, 0x12, (byte) byteSound, (byte) (0 - (0x01 + 0x0B + 0x12 + byteSound))};
        if (app.manager.cubicBLEDevice != null) {
            app.manager.cubicBLEDevice.writeValue(IContent.SERVERUUID_BLE, IContent.WRITEUUID_BLE, byteSounds);
        }

    }
}
