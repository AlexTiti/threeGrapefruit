package com.findtech.threePomelos.user.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.findtech.threePomelos.R;
import com.findtech.threePomelos.adapter.BabyInfoAdapter;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.sdk.MyApplication;
import com.findtech.threePomelos.sdk.base.mvp.BaseActivityMvp;
import com.findtech.threePomelos.sdk.manger.RxHelper;
import com.findtech.threePomelos.user.bean.UserInfo;
import com.findtech.threePomelos.user.present.BabyInfoPresent;
import com.findtech.threePomelos.user.view.BabyInfoView;
import com.findtech.threePomelos.utils.MyCalendar;
import com.findtech.threePomelos.utils.PicOperator;
import com.findtech.threePomelos.utils.PictureUtils;
import com.findtech.threePomelos.utils.PictureUtils.PictureSuccessCallBack;
import com.findtech.threePomelos.utils.RequestUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.utils.Tools;
import com.findtech.threePomelos.view.datepicker.DatepickerDialog;
import com.findtech.threePomelos.view.datepicker.NativePickerDialog;
import com.findtech.threePomelos.view.dialog.CustomDialog;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;


/**
 * @author Administrator
 */
public class BabyInfoActivity extends BaseActivityMvp<BabyInfoActivity, BabyInfoPresent> implements View.OnClickListener,
        RequestUtils.MyItemClickListener, PictureSuccessCallBack, BabyInfoView<String> {

    @BindView(R.id.include)
    View toolbar_layout;
    private RecyclerView mRecyclerView;
    @BindView(R.id.image_baby_info)
    ImageView mBabyImage;
    @BindView(R.id.ivChangeName)
    ImageView ivChangeName;
    @BindView(R.id.baby_name)
    TextView babyNameView;

    private EditText edittext;
    private BabyInfoAdapter mAdapter;
    private DatepickerDialog mChangeBirthDialog = null;

    private Map<String, String> listItem = new HashMap<>();
    private String[] title = {"性别", "生日", "年龄", "身高", "体重", "籍贯"};
    private int[] title_id = {R.string.sex_baby, R.string.birth_baby, R.string.age_bany, R.string.height_baby, R.string.weight_baby, R.string.address};
    private BabyInfoEntity babyInfoEntity;


    private Bitmap bitmap;
    private Context mContext = BabyInfoActivity.this;
    private final static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 0;
    private NativePickerDialog mNativePickerDialog = null;
    Intent intent_0;

    public static final String BABYINFO = "babyInfo";

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        int position = intent.getIntExtra(BABYINFO, 0);
        babyInfoEntity = UserInfo.getInstance().getBabyInfoEntities().get(position);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        initToolBar(toolbar_layout, getString(R.string.babyinfo));
        for (int i = 0; i < title.length; i++) {
            title[i] = getString(title_id[i]);
        }

        babyNameView.setOnClickListener(this);
        mBabyImage.setOnClickListener(this);
        ivChangeName.setOnClickListener(this);
        mAdapter = new BabyInfoAdapter(this);
        mAdapter.setTitle(title);
        mRecyclerView = findViewById(R.id.baby_info_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        PicOperator.initFilePath(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        if (babyInfoEntity.getBabyName() != null) {
            babyNameView.setText(babyInfoEntity.getBabyName());
        }

        bitmap = PicOperator.toRoundBitmap(PicOperator.getIconFromPath(this,
                MyApplication.getInstance().getBabyHeadIconPath(babyInfoEntity.getBabyInfoObjectId())));
        if (bitmap != null) {
            mBabyImage.setImageBitmap(bitmap);
        } else {
            mBabyImage.setImageResource(R.mipmap.personal_head_bg2_nor2x);
        }

        final String currentDate = Tools.getSystemTimeInChina("yyyy-MM-dd");
        try {
            if (babyInfoEntity.getBirthday() != null && !babyInfoEntity.getBirthday().equals(getString(R.string.input_birth_baby))) {
                String birthday = babyInfoEntity.getBirthday().replace("年", "-").replace("月", "-").replace("日", "");
                MyCalendar myCalendar = new MyCalendar(birthday, currentDate, this);
                listItem.put(title[2], myCalendar.getDate());
            } else {
                listItem.put(title[2], getString(R.string.input_birth_notice));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        listItem.put(title[0], babyInfoEntity.getBabySex());
        listItem.put(title[1], babyInfoEntity.getBirthday());

        listItem.put(title[3], getResources().getString(R.string.xliff_height_num, "0"));
        listItem.put(title[4], getResources().getString(R.string.xliff_weight_num, "0"));
        listItem.put(title[5], babyInfoEntity.getBabyNative());
        mAdapter.setListItem(listItem);

        intent_0 = new Intent();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_baby_info;
    }


    @Override
    public void onItemClick(View view, int position) {

        switch (position) {
            case 0:
                showInputBabySexDialog();
                break;
            case 1:
                showInputBabyBirthdayDataPicker();
                break;
            case 2:
                ToastUtil.showToast(this, getString(R.string.age_info_notice));
                break;
            case 3:
                ToastUtil.showToast(this, getString(R.string.height_ino_notice));
                break;
            case 4:
                ToastUtil.showToast(this, getString(R.string.weight_ino_notice));
                break;
            case 5:
                showInputBabyNativeDialog();
                break;
            default:
                break;
        }
    }

    private void showInputBabyNativeDialog() {
        if (mNativePickerDialog != null && mNativePickerDialog.isShowing()) {
            return;
        }
        mNativePickerDialog = new NativePickerDialog(this, 2);
        NativePickerDialog.setWindowSize(mNativePickerDialog);
        mNativePickerDialog.show();
        mNativePickerDialog.setNativeListener(new NativePickerDialog.OnNativeListener() {
            @Override
            public void onClick(String province, String city, String county) {
                final String nativeStr = province + "," + city;

                if (babyInfoEntity.getBabyInfoObjectId() != null) {
                    present.saveBabyNative(nativeStr, babyInfoEntity.getBabyInfoObjectId(), BabyInfoActivity.this, babyInfoEntity);
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                }
                mNativePickerDialog = null;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == babyNameView || view == ivChangeName) {
            showInputBabyNameDialog();
        } else if (view == mBabyImage) {
            PackageManager pm = getPackageManager();
            boolean permission = (PackageManager.PERMISSION_GRANTED ==
                    pm.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", this.getPackageName()));
            if (permission) {
                showPicChooserDialog();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            }
        }
    }

    private void showInputBabyNameDialog() {
        final CustomDialog customDialog;
        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.edit_nikename))
                .setShowEditView(true)
                .setShowButton(true)
                .setShowSelectSex(false)
                .setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final String babyName = edittext.getText().toString().trim();
                        if (!TextUtils.isEmpty(babyName)) {
                            String oldName = babyInfoEntity.getBabyName();
                            if (!(babyName).equals(oldName)) {
                                if (babyInfoEntity.getBabyInfoObjectId() != null) {
                                    present.saveBabyName(babyName, babyInfoEntity.getBabyInfoObjectId(), BabyInfoActivity.this, babyInfoEntity);
                                } else {
                                    Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancle),
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        customDialog = builder.create();
        edittext = customDialog.findViewById(R.id.input_data);
        customDialog.show();

    }

    private void showInputBabySexDialog() {
        final CustomDialog customDialog;
        final CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setTitle(getString(R.string.hint_sex))
                .setShowEditView(false)
                .setShowButton(false)
                .setShowSelectSex(true);
        customDialog = builder.create();

        RadioGroup group = customDialog.findViewById(R.id.radioGroup);
        RadioButton radioMale = customDialog.findViewById(R.id.radioMale);
        RadioButton radioFemale = customDialog.findViewById(R.id.radioFemale);
        String babySex = babyInfoEntity.getBabySex();

        if (getResources().getString(R.string.princeling).equals(babySex)) {
            radioMale.setChecked(true);
        } else if (getResources().getString(R.string.princess).equals(babySex)) {
            radioFemale.setChecked(true);
        }

        radioMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmissDialog(customDialog);
            }
        });
        radioFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dissmissDialog(customDialog);
            }
        });

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            String male = getResources().getString(R.string.princeling);
            String female = getResources().getString(R.string.princess);

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                if (radioButtonId == R.id.radioMale) {
                    if (babyInfoEntity.getBabyInfoObjectId() != null) {
                        present.saveBabySex(male, babyInfoEntity.getBabyInfoObjectId(), BabyInfoActivity.this, babyInfoEntity);
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                    }
                } else if (radioButtonId == R.id.radioFemale) {

                    if (babyInfoEntity.getBabyInfoObjectId() != null) {
                        present.saveBabySex(female, babyInfoEntity.getBabyInfoObjectId(), BabyInfoActivity.this, babyInfoEntity);
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        customDialog.show();
    }

    private void dissmissDialog(final CustomDialog dialog) {
        Observable.intervalRange(0, 1, 300, 0, TimeUnit.MILLISECONDS)
                .compose(RxHelper.<Long>rxSchedulerHelper())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        dialog.dismiss();
                    }
                });
    }

    private void showInputBabyBirthdayDataPicker() {
        if (mChangeBirthDialog != null && mChangeBirthDialog.isShowing()) {
            return;
        }
        mChangeBirthDialog = new DatepickerDialog(mContext);
        DatepickerDialog.setWindowSize(mChangeBirthDialog);
        mChangeBirthDialog.show();
        mChangeBirthDialog.setBirthdayListener(new DatepickerDialog.OnBirthListener() {
            @Override
            public void onClick(String year, String month, String day) {
                final String birthdayStrFormat = year + "-" + month + "-" + day;
                if (babyInfoEntity.getBabyInfoObjectId() != null) {
                    present.saveBabyBirth(birthdayStrFormat, babyInfoEntity.getBabyInfoObjectId(), BabyInfoActivity.this, babyInfoEntity);
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
                }
                mChangeBirthDialog = null;
            }
        });
    }

    private PictureUtils mPictureUtils;

    private void showPicChooserDialog() {
        mPictureUtils = new PictureUtils();
        mPictureUtils.setActivity(this, this);
        mPictureUtils.setCallBack(this);
        final Dialog mPicChooserDialog = mPictureUtils.createDialog();
        mPicChooserDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        mPictureUtils.activityResult(requestCode, resultCode, intent);
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void initDataFromServer() {

    }

    @Override
    protected BabyInfoPresent createPresent() {
        return new BabyInfoPresent();
    }


    @Override
    protected void showLoading() {

    }

    @Override
    protected void onErrorViewClick(View v) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showPicChooserDialog();
            } else {
                Toast.makeText(this, getResources().getString(R.string.text_toast_no_storage_permission), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void pictureSuccess(final Bitmap bitmap) {
        if (babyInfoEntity.getBabyInfoObjectId() != null) {
            present.saveHeadImage(bitmap, babyInfoEntity.getBabyInfoObjectId(), BabyInfoActivity.this);
        } else {
            Toast.makeText(mContext, getResources().getString(R.string.save_data_failed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void saveNameSuccess(String name) {
        babyNameView.setText(name);
    }

    @Override
    public void saveNameFailed(String message) {

    }

    @Override
    public void saveHeadViewSuccess(Bitmap bitmap) {
        mBabyImage.setImageBitmap(PicOperator.toRoundBitmap(bitmap));

        intent_0.putExtra("intent", "IMAGE_CHANGE");
        setResult(10, intent_0);
    }

    @Override
    public void saveHeadViewFailed(String message) {

    }

    @Override
    public void saveSexSuccess(String sex) {
        listItem.put(title[0], sex);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void saveSexFailed(String message) {

    }

    @Override
    public void saveBirthdaySuccess(String birthDay) {
        final String currentDate = Tools.getSystemTimeInChina("yyyy-MM-dd");
        listItem.put(title[1], birthDay);

        try {
            MyCalendar myCalendar = new MyCalendar(birthDay, currentDate, BabyInfoActivity.this);
            listItem.put(title[2], myCalendar.getDate());

        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void saveBirthdayFailed(String message) {

    }

    @Override
    public void loadSuccess(String s) {
        listItem.put(title[5], s);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadFailed(String s) {

    }
}
