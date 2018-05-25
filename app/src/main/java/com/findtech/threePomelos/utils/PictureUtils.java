package com.findtech.threePomelos.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.findtech.threePomelos.R;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_CANCELED;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/05/05
 */

public class PictureUtils {

    /**
     * 拍照的RequestCode
     */
    public static final int CODE_GALLERY_REQUEST = 0xa0;
    /**
     * 选择照的RequestCode
     */
    public static final int CODE_CAMERA_REQUEST = 0xa1;
    /**
     * 裁剪的RequestCode
     */
    public static final int CODE_RESULT_REQUEST = 0xa2;
    private PictureSuccessCallBack callBack;

    public void setCallBack(PictureSuccessCallBack callBack) {
        this.callBack = callBack;
    }

    private Activity activity;
    private Fragment fragment;
    private Context context;

    /**
     * 设置Activity调用
     * @param activity
     * @param context
     */
    public void setActivity(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    /**
     * 设置Fragment调用
     * @param fragment
     */
    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
        context = fragment.getContext();
    }

    /**
     * 创建Dialog
     * @return
     */
    public Dialog createDialog() {
        final Dialog mPicChooserDialog;
        View viewDialog = LayoutInflater.from(context).inflate(R.layout.dialog_pic_chooser, null);
        mPicChooserDialog = new Dialog(context, R.style.MyDialogStyleBottom);
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

        TextView btnCamera = viewDialog.findViewById(R.id.btn_take_photo);
        TextView btnGallery = viewDialog.findViewById(R.id.btn_pick_photo);
        TextView btnCancel = viewDialog.findViewById(R.id.btn_cancel);

        //拍照
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mIntent.putExtra(MediaStore.EXTRA_OUTPUT, PicOperator.SOURCE_IMAGE_URI);
                if (activity != null) {
                    activity.startActivityForResult(mIntent, CODE_CAMERA_REQUEST);
                } else if (fragment != null) {
                    fragment.startActivityForResult(mIntent, CODE_CAMERA_REQUEST);
                }
                mPicChooserDialog.dismiss();
            }
        });
        //相册选取
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                intent.setType("image/*");
                if (activity != null) {
                    activity.startActivityForResult(intent, CODE_GALLERY_REQUEST);
                } else if (fragment != null) {
                    fragment.startActivityForResult(intent, CODE_GALLERY_REQUEST);
                }
                mPicChooserDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPicChooserDialog.dismiss();
            }
        });
        return mPicChooserDialog;
    }

    /**
     * 处理回调
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void activityResult(int requestCode, int resultCode,
                               Intent intent) {
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(context, context.getResources().getString(R.string.cancle), Toast.LENGTH_LONG).show();
            return;
        }
        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                String path = FileUtils.getPath(context, intent.getData());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    File file = new File(path);
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    PicOperator.SOURCE_IMAGE_URI = FileProvider.getUriForFile(context, PicOperator.IMAGE_AUTHORITY, file);
                } else {
                    PicOperator.SOURCE_IMAGE_URI = Uri.parse("file://" + path);
                }
                if (activity != null) {
                    PicOperator.toCropImageActivity(activity, PicOperator.SOURCE_IMAGE_URI,
                            PicOperator.OUTPUT_IMAGE_URI, 300, 300,
                            CODE_RESULT_REQUEST);
                } else if (fragment != null) {
                    PicOperator.toCropImageFragment(fragment, PicOperator.SOURCE_IMAGE_URI,
                            PicOperator.OUTPUT_IMAGE_URI, 300, 300,
                            CODE_RESULT_REQUEST);
                }
                break;
            case CODE_CAMERA_REQUEST:
                if (activity != null) {
                    PicOperator.toCropImageActivity(activity, PicOperator.SOURCE_IMAGE_URI,
                            PicOperator.OUTPUT_IMAGE_URI, 300, 300,
                            CODE_RESULT_REQUEST);
                } else if (fragment != null) {
                    PicOperator.toCropImageFragment(fragment, PicOperator.SOURCE_IMAGE_URI,
                            PicOperator.OUTPUT_IMAGE_URI, 300, 300,
                            CODE_RESULT_REQUEST);
                }
                break;
            case CODE_RESULT_REQUEST:
                if (PicOperator.OUTPUT_IMAGE_URI != null) {
                    final Bitmap bitmap = PicOperator.decodeUriAsBitmap(context, PicOperator.OUTPUT_IMAGE_URI);
                    if (bitmap == null) {
                        return;
                    }
                    if (callBack != null) {
                        callBack.pictureSuccess(bitmap);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 图片输出接口
     */
    public interface PictureSuccessCallBack {

        /**
         * 获取头像成功回调
         *
         * @param bitmap
         */
        void pictureSuccess(Bitmap bitmap);
    }
}
