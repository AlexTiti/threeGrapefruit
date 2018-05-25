package com.findtech.threePomelos.music.view;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.findtech.threePomelos.R;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/04/11
 */

public  class PopDialogUtils {
    private Context context;

    public PopDialogUtils(Context context) {
        this.context = context;
    }
    public Dialog createDialog(View view,int gravity){
        Dialog dialog = new Dialog(context, R.style.MyDialogStyleBottom);
        dialog.setContentView(view,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Window window = dialog.getWindow();
        WindowManager manager = window.getWindowManager();
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        WindowManager.LayoutParams layoutParams= window.getAttributes();
        layoutParams.width = displayMetrics.widthPixels;
        layoutParams.alpha = 1.0f;
        layoutParams.dimAmount = 0.6f;
        layoutParams.gravity = gravity;
        window.setAttributes(layoutParams);
        return dialog;
    }

    public void showdialog(int resLayout,int gravity){
        View view = LayoutInflater.from(context).inflate(resLayout,null);
        Dialog dialog = createDialog(view,gravity);
        dialog.show();
    }
}
