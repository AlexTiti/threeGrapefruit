package com.findtech.threePomelos.mydevices.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.bluetooth.server.RFStarBLEService;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.mydevices.bean.DeviceCarBean;
import com.findtech.threePomelos.mydevices.bean.DeviceStateBean;
import com.findtech.threePomelos.sdk.base.BaseCompatActivity;
import com.findtech.threePomelos.user.bean.UserInfo;
import com.findtech.threePomelos.utils.Tools;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Administrator
 */
public class BrakeFastActivity extends BaseCompatActivity {


    @BindView(R.id.ivFirst)
    ImageView ivFirst;
    @BindView(R.id.imageView)
    ImageView ivSecond;
    @BindView(R.id.imageViewThird)
    ImageView imageViewThird;
    @BindView(R.id.imageViewFour)
    ImageView imageViewFour;
    @BindView(R.id.imageViewFive)
    ImageView imageViewFive;

    @BindView(R.id.include)
    View include;

    private ImageView[] imageView = new ImageView[5];


    @Override
    protected void showLoading() {

    }

    @Override
    protected void onErrorViewClick(View v) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        initToolBar(include, getString(R.string.brake_fast));
        imageView[0] = ivFirst;
        imageView[1] = ivSecond;
        imageView[2] = imageViewThird;
        imageView[3] = imageViewFour;
        imageView[4] = imageViewFive;

        initImage(DeviceStateBean.getInstance().getBrakeFast());


    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_brake_fast;
    }

    @OnClick({R.id.tvFirst, R.id.textViewSecond, R.id.textViewThird, R.id.textViewFour, R.id.textViewFive})
    public void click(View view) {

        switch (view.getId()) {
            case R.id.tvFirst:
                createBytesAndSend(0);
                break;
            case R.id.textViewSecond:
                createBytesAndSend(1);
                break;
            case R.id.textViewThird:
                createBytesAndSend(2);
                break;
            case R.id.textViewFour:
                createBytesAndSend(3);
                break;
            case R.id.textViewFive:
                createBytesAndSend(4);
                break;
            default:
                break;

        }

    }

    private void createBytesAndSend(int i) {
        byte[] bytes = {0x55, (byte) 0xAA, 0x01, (byte) 0x0B, 0x16, (byte) i, (byte) (0 - (0x01 + 0x16 + (byte) 0x0B + (byte) i))};
        sendBlueToothByte(bytes);
    }

    private void initImage(int position) {
        for (int i = 0; i < imageView.length; i++) {
            if (i == position) {
                imageView[i].setVisibility(View.VISIBLE);
            } else {
                imageView[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void dataAvailable(Intent intent) {
        super.dataAvailable(intent);
        byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
        L.e("=======", Tools.byte2Hex(data));
        if (data.length < 6) {
            return;
        }

        if (data[3] == (byte) 0x8B && data[4] == 0x16) {
            int position = data[5] & 0xff;
            initImage(position);
            DeviceStateBean.getInstance().setBrakeFast(position);
            return;
        }
    }

    @Override
    public void dataAvailableRead(Intent intent) {
        super.dataAvailableRead(intent);

        byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
        L.e("=======", Tools.byte2Hex(data));
        if (data.length < 6) {
            return;
        }
        if (data[3] == (byte) 0x8B && data[4] == 0x16) {
            int position = data[5] & 0xff;
            initImage(position);
            DeviceStateBean.getInstance().setBrakeFast(data[5] & 0xff);
            return;
        }
    }
}
