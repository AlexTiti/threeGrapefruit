package com.findtech.threePomelos.home.fragment;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.findtech.threePomelos.R;
import com.findtech.threePomelos.database.OperateDBUtils;
import com.findtech.threePomelos.entity.BabyInfoEntity;
import com.findtech.threePomelos.home.adapter.UserBabyAdapter;
import com.findtech.threePomelos.home.adapter.UserDeviceAdapter;
import com.findtech.threePomelos.home.presenter.UserFragmentPresent;
import com.findtech.threePomelos.home.view.UserViewMvp;
import com.findtech.threePomelos.music.activity.MusicBabyActivity;
import com.findtech.threePomelos.music.activity.MusicLocalActivity;
import com.findtech.threePomelos.music.utils.IConstants;
import com.findtech.threePomelos.music.utils.MusicUtils;
import com.findtech.threePomelos.music.view.AbstractAppBarStateChangeListener;
import com.findtech.threePomelos.musicserver.Nammu;
import com.findtech.threePomelos.mydevices.activity.BluetoothlinkActivity;
import com.findtech.threePomelos.mydevices.activity.DeviceDetailActivity;
import com.findtech.threePomelos.mydevices.activity.NewAutoDetailActivity;
import com.findtech.threePomelos.mydevices.bean.DeviceCarBean;
import com.findtech.threePomelos.sdk.base.mvp.BaseFragmentMvp;
import com.findtech.threePomelos.user.activity.BabyInfoActivity;
import com.findtech.threePomelos.user.activity.CommendProblemActivity;
import com.findtech.threePomelos.user.activity.FeedBack;
import com.findtech.threePomelos.user.activity.SettingActivity;
import com.findtech.threePomelos.user.bean.UserInfo;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.PicOperator;
import com.findtech.threePomelos.utils.PictureUtils;
import com.findtech.threePomelos.utils.SpUtils;
import com.findtech.threePomelos.utils.StatusBarUtils;
import com.findtech.threePomelos.utils.ToastUtil;
import com.findtech.threePomelos.view.dialog.CustomDialog;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * @author Administrator
 */
public class UserFragment extends BaseFragmentMvp<UserFragment, UserFragmentPresent> implements View.OnClickListener
        , PictureUtils.PictureSuccessCallBack, UserViewMvp<ArrayList<BabyInfoEntity>> {

    @BindView(R.id.userNickName)
    TextView userNickName;
    @BindView(R.id.imageHeadView)
    ImageView imageHeadView;

    @BindView(R.id.rvDevice)
    RecyclerView rvDevice;
    @BindView(R.id.rvBaby)
    RecyclerView rvBaby;

    @BindView(R.id.tvSet)
    TextView tvSet;
    @BindView(R.id.tvCollectMusic)
    TextView tvCollectMusic;
    @BindView(R.id.tvDownMusic)
    TextView tvDownMusic;
    @BindView(R.id.tvFeedBack)
    TextView tvFeedBack;
    @BindView(R.id.tvExplain)
    TextView tvExplain;
    @BindView(R.id.tv_toolbar)
    TextView tv_toolbar;
    @BindView(R.id.numberMusic)
    TextView numberMusic;
    @BindView(R.id.tvDownLoad)
    TextView tvDownLoad;


    @BindView(R.id.ivAddDevice)
    ImageView ivAddDevice;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvAddBaby)
    ViewStub tvAddBaby;
    @BindView(R.id.tvAddDevice)
    ViewStub tvAddDevice;

    private PictureUtils mPictureUtils;
    private DeviceCarBean deleteDeviceCarBean;
    private BluetoothAdapter bleAdapter;
    private UserDeviceAdapter userDeviceAdapter;
    private View viewDevice;


    public static UserFragment getInstance() {
        return new UserFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_user;
    }

    @Override
    public void initUI(View view, @Nullable Bundle savedInstanceState) {

        tvSet.setOnClickListener(this);
        tvCollectMusic.setOnClickListener(this);
        tvDownMusic.setOnClickListener(this);
        tvFeedBack.setOnClickListener(this);
        tvExplain.setOnClickListener(this);
        ivAddDevice.setOnClickListener(this);

        PicOperator.initFilePath(mActivity);
        rvBaby.setLayoutManager(new LinearLayoutManager(mActivity));
        rvDevice.setLayoutManager(new LinearLayoutManager(mActivity));
        tv_toolbar.setText("个人主页");
        StatusBarUtils.fixToolbar(toolbar, mActivity);
        appBarLayout.addOnOffsetChangedListener(new AbstractAppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, AbstractAppBarStateChangeListener.State state) {
                if (state == State.COLLAPSED) {
                    tv_toolbar.setAlpha(1f);
                } else if (state == State.EXPANDED) {
                    tv_toolbar.setAlpha(0f);
                }
            }

            @Override
            public void onScale(float f) {
                tv_toolbar.setAlpha(f);
            }
        });

        BluetoothManager manager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = manager.getAdapter();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

        if (UserInfo.getInstance().getNickName() == null){
            UserInfo.getInstance().setNickName(SpUtils.getString(mActivity,OperateDBUtils.NICKNAME,"昵称"));
        }
        userNickName.setText(UserInfo.getInstance().getNickName());

        userNickName.setOnClickListener(this);

        imageHeadView.setOnClickListener(this);
        Bitmap bitmap = PicOperator.toRoundBitmap(PicOperator.getIconFromData(mActivity));
        if (bitmap != null) {
            imageHeadView.setImageBitmap(bitmap);
        } else {
            imageHeadView.setImageResource(R.mipmap.personal_head_bg2_nor2x);
        }

        mPictureUtils = new PictureUtils();
        mPictureUtils.setFragment(this);
        mPictureUtils.setCallBack(this);

        UserFragmentPresent present = getPresent();
        present.getUserDevice();
        present.getBabyList();
        present.getCollectMusicCount(mActivity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Nammu.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Nammu.requestPermission(mActivity, Nammu.PERMISSIONS_STORAGE, 100, Manifest.permission.READ_EXTERNAL_STORAGE, getResources().getString(R.string.primess_notice));
        } else {
            tvDownLoad.setText(getResources().getString(R.string.number_music, MusicUtils.queryMusicCount(mActivity, IConstants.START_FROM_FOLDER)));
        }

    }

    EditText edittext;

    private void showInputBabyNameDialog() {
        final CustomDialog customDialog;
        final CustomDialog.Builder builder = new CustomDialog.Builder(mActivity);
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
                            UserInfo entity = UserInfo.getInstance();
                            String oldName = entity.getNickName();
                            if (!(babyName).equals(oldName)) {
                                getPresent().saveBabyName(babyName, mActivity);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.userNickName:
                showInputBabyNameDialog();
                break;
            case R.id.imageHeadView:
                mPictureUtils.createDialog().show();
                break;
            case R.id.tvSet:
                startNewActivity(SettingActivity.class);
                break;
            case R.id.tvCollectMusic:
                startNewActivity(MusicBabyActivity.class);
                break;
            case R.id.tvDownMusic:
                startNewActivity(MusicLocalActivity.class);
                break;
            case R.id.tvFeedBack:
                startNewActivity(FeedBack.class);
                break;
            case R.id.tvExplain:
                startNewActivity(CommendProblemActivity.class);
                break;
            case R.id.ivAddDevice:
                startActivityForResult(new Intent(mActivity, BluetoothlinkActivity.class), 111);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 110) {
            userDeviceAdapter.notifyDataSetChanged();
            return;
        }
        if (requestCode == 111) {
            refreshAdapter(IContent.getInstacne().addressList);
            return;
        }

        mPictureUtils.activityResult(requestCode, resultCode, data);
    }

    @Override
    public void pictureSuccess(Bitmap bitmap) {
        getPresent().saveHeadImage(bitmap, mActivity);
    }

    @Override
    public void getDeviceSuccess(ArrayList<DeviceCarBean> beanArrayList) {

        if (beanArrayList.size() == 0) {
            viewDevice = tvAddDevice.inflate();
            TextView textView = viewDevice.findViewById(R.id.tvNoData);
            textView.setText(getResources().getString(R.string.no_device));
            return;
        }
        refreshAdapter(beanArrayList);
    }

    private void refreshAdapter(ArrayList<DeviceCarBean> beanArrayList) {

        if (viewDevice != null) {
            viewDevice.setVisibility(View.GONE);
        }
        userDeviceAdapter = new UserDeviceAdapter(R.layout.user_recycler_item, beanArrayList);
        userDeviceAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                click(position);
            }
        });

        userDeviceAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                longClick(position);
                return true;
            }
        });
        rvDevice.setAdapter(userDeviceAdapter);
    }

    @Override
    public void getDeviceFailed(String message) {

    }

    @Override
    public void changeNickNameSuccess(String nickName) {
        SpUtils.putString(mActivity, OperateDBUtils.NICKNAME, nickName);
        userNickName.setText(nickName);
    }

    @Override
    public void changeNickNameFailed(String message) {

    }

    @Override
    public void changePictureSuccess(Bitmap bitmap) {
        imageHeadView.setImageBitmap(PicOperator.toRoundBitmap(bitmap));
    }

    @Override
    public void changePictureFailed(String message) {

    }

    @Override
    public void getCollectMusic(String s) {
        numberMusic.setText(getResources().getString(R.string.number_music, s));
    }

    @Override
    public void deleteDeviceSuccess() {
        ToastUtil.showToast(getActivity(), getResources().getString(R.string.unbound_success));
        IContent.getInstacne().addressList.remove(deleteDeviceCarBean);

        if (IContent.getInstacne().addressList.size() == 0) {
            viewDevice = tvAddDevice.inflate();
            TextView textView = viewDevice.findViewById(R.id.tvNoData);
            textView.setText(getResources().getString(R.string.no_device));
        }
        userDeviceAdapter.setNewData(IContent.getInstacne().addressList);
    }

    @Override
    public void deleteDeviceFailed(String message) {

    }

    @Override
    public void deleteBabySuccess() {

    }

    @Override
    public void deleteBabyFailed(String message) {

    }

    @Override
    public void loadSuccess(ArrayList<BabyInfoEntity> strings) {
        UserBabyAdapter userBabyAdapter = new UserBabyAdapter(R.layout.user_recycler_item, strings);
        userBabyAdapter.setEnableLoadMore(false);
        userBabyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putInt(BabyInfoActivity.BABYINFO,
                        position);
                startNewActivity(BabyInfoActivity.class, bundle);
            }
        });
        rvBaby.setAdapter(userBabyAdapter);

    }

    @Override
    public void loadFailed(String s) {

    }

    @Override
    protected UserFragmentPresent createPresent() {
        return new UserFragmentPresent();
    }

    public void click(int position) {
        if (!bleAdapter.isEnabled()) {
            mApplication.manager.isEnabled(getActivity());
            return;
        }
        IContent.isSended = false;
        DeviceCarBean deviceCarBean = IContent.getInstacne().addressList.get(position);

        if (mApplication.manager.cubicBLEDevice != null && deviceCarBean.getDeviceaAddress() != null
                && !deviceCarBean.getDeviceaAddress().equals(IContent.getInstacne().address)) {
            mApplication.manager.cubicBLEDevice.disconnectedDevice();
            mApplication.manager.cubicBLEDevice = null;
        }
        Intent intent;
        if ("2".equals(deviceCarBean.getFunctionType())) {
            intent = new Intent(mActivity, NewAutoDetailActivity.class);
        } else {
            intent = new Intent(mActivity, DeviceDetailActivity.class);
        }

        intent.putExtra(IContent.getInstacne().clickPositionAddress, deviceCarBean.getDeviceaAddress());
        intent.putExtra(IContent.getInstacne().clickPositionName, deviceCarBean.getDeviceName());
        intent.putExtra(IContent.getInstacne().clickPositionFunction, deviceCarBean.getFunctionType());
        IContent.getInstacne().clickPositionType = deviceCarBean.getDeviceType();
        IContent.getInstacne().functionType = deviceCarBean.getFunctionType();
        IContent.getInstacne().company = deviceCarBean.getCompany();
        startActivityForResult(intent, 110);
    }

    public void longClick(final int position) {
        final CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.notice));
        builder.setShowBindInfo(getResources().getString(R.string.unbound_message));
        builder.setShowButton(true);
        builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                deleteDeviceCarBean = IContent.getInstacne().addressList.get(position);
                final String deviceNumAddress = deleteDeviceCarBean.getDeviceaAddress();

                if (mApplication.manager.cubicBLEDevice != null && deviceNumAddress.equals(IContent.getInstacne().address)) {
                    mApplication.manager.cubicBLEDevice.disconnectedDevice();
                }
                getPresent().deleteDevice(deviceNumAddress);
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
        CustomDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    tvDownLoad.setText(getResources().getString(R.string.number_music, MusicUtils.queryMusicCount(mActivity, IConstants.START_FROM_FOLDER)));
                }
                break;
            default:
                break;
        }
    }


}
