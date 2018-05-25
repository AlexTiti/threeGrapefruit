package com.findtech.threePomelos.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.sdk.base.BaseCompatActivity;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.ToastUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Administrator
 */
public class SetBabySexActivity extends BaseCompatActivity implements View.OnClickListener {

    private Button mNextButton;
    private ImageView boyImg;
    private ImageView girlImg;
    private Calendar mCalendar;
    private String sex;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void showLoading() {

    }

    @Override
    protected void onErrorViewClick(View v) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mCalendar = Calendar.getInstance();
        Date date = new Date();
        mCalendar.setTime(date);

        boyImg = findViewById(R.id.boy);
        girlImg = findViewById(R.id.girl);
        mNextButton = findViewById(R.id.next);

        mNextButton.setOnClickListener(this);
        boyImg.setOnClickListener(this);
        girlImg.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.set_baby_sex;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next:
                if (TextUtils.isEmpty(sex)) {
                    ToastUtil.showToast(this, getResources().getString(R.string.babay_sex));
                } else {
                    Intent intent = new Intent(this, SetBabyBirthdayActivity.class);
                    RequestUtils.getSharepreferenceEditor(this)
                            .putString(RequestUtils.BABYSEX, sex).commit();
                    startActivity(intent);
                }
                break;
            case R.id.boy:
                setSelect(boyImg);
                break;
            case R.id.girl:
                setSelect(girlImg);
                break;
            default:
                break;
        }
    }

    public void setSelect(ImageView imageView) {
        Bitmap bitmapSelect = ((BitmapDrawable) getResources().getDrawable(R.mipmap.check_select2x)).getBitmap();
        Bitmap bitmapImage;
        if (imageView == boyImg) {
            sex = getResources().getString(R.string.princeling);
            bitmapImage = ((BitmapDrawable) getResources().getDrawable(R.mipmap.boy2x)).getBitmap();
            setBitmap(imageView, bitmapImage, bitmapSelect);
            girlImg.setImageResource(R.mipmap.girl2x);
        } else if (imageView == girlImg) {
            sex = getResources().getString(R.string.princess);;
            bitmapImage = ((BitmapDrawable) getResources().getDrawable(R.mipmap.girl2x)).getBitmap();
            setBitmap(imageView, bitmapImage, bitmapSelect);
            boyImg.setImageResource(R.mipmap.boy2x);
        }
    }

    public void setBitmap(ImageView imageView, Bitmap bitmapImage, Bitmap bitmapSelect) {
        Drawable[] array = new Drawable[2];
        array[0] = new BitmapDrawable(bitmapImage);
        array[1] = new BitmapDrawable(bitmapSelect);
        LayerDrawable la = new LayerDrawable(array);
        la.setLayerInset(0, 0, 0, 0, 0);
        la.setLayerInset(1, 75, 75, 0, 0);
        imageView.setImageDrawable(la);
    }

}
