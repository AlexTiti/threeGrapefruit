package com.findtech.threePomelos.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.findtech.threePomelos.R;

/**
 * @author Administrator
 */
public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CustomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {
        private Context context;
        private String title;
        private int color;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private boolean isShowEditView;
        private String bindInfoStr;
        private boolean isShowButton = false;
        private boolean isShowSelectSex = false;
        private EditText editText;
        private TextView textView;
        private DialogInterface.OnClickListener positiveButtonClickListener;
        private DialogInterface.OnClickListener negativeButtonClickListener;

        private String notifyStr;
        private TextView notifyTextView;
        private boolean isCanceledOnTouchOutside = false;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @return Builder
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return Builder
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return Builder
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTextColor(int color) {
            this.color = color;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * 是否显示EditView，输入宝贝昵称Dialog界面
         * @param isShow
         * @return Builder
         */
        public Builder setShowEditView(boolean isShow) {
            this.isShowEditView = isShow;
            return this;
        }

        /**
         * 是否显示TextView，解除绑定Dialog界面
         *
         * @param bindStr
         * @return Builder
         */
        public Builder setShowBindInfo(String bindStr) {
            this.bindInfoStr = bindStr;
            return this;
        }

        public Builder setShowButton(boolean isShow) {
            this.isShowButton = isShow;
            return this;
        }

        public Builder setShowSelectSex(boolean isShow) {
            this.isShowSelectSex = isShow;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         * @param positiveButtonText
         * @return Builder
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * 设置提示信息的内容
         *
         * @param notifyStr
         * @return Builder
         */
        public Builder setNotifyInfo(String notifyStr) {
            this.notifyStr = notifyStr;
            return this;
        }

        public CustomDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final CustomDialog dialog = new CustomDialog(context, R.style.Dialog);
            dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
            View layout = inflater.inflate(R.layout.input_name_dialog_layout, null);
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            dialog.addContentView(layout, lp);
            setWindowSize(dialog);
            ((TextView) layout.findViewById(R.id.title)).setText(title);

            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    layout.findViewById(R.id.positiveButton)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    layout.findViewById(R.id.negativeButton)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    negativeButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {

                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }
            editText = layout.findViewById(R.id.input_data);
            if (isShowEditView) {
                editText.setVisibility(View.VISIBLE);
            } else {
                editText.setVisibility(View.GONE);
            }

            textView =  layout.findViewById(R.id.show_bind);

            if (!TextUtils.isEmpty(bindInfoStr)) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(bindInfoStr);
            } else {
                textView.setVisibility(View.GONE);
            }

            notifyTextView =  layout.findViewById(R.id.show_notify);
            if (!TextUtils.isEmpty(notifyStr)) {
                notifyTextView.setVisibility(View.VISIBLE);
                notifyTextView.setText(notifyStr);
            } else {
                notifyTextView.setVisibility(View.GONE);
            }

            LinearLayout buttonLayout = layout.findViewById(R.id.button_layout);
            if (isShowButton) {
                buttonLayout.setVisibility(View.VISIBLE);
            } else {
                buttonLayout.setVisibility(View.GONE);
            }
            LinearLayout selectSexLayout =  layout.findViewById(R.id.select_sex_dialog);
            if (isShowSelectSex) {
                selectSexLayout.setVisibility(View.VISIBLE);
            } else {
                selectSexLayout.setVisibility(View.GONE);
            }
            dialog.setContentView(layout);
            return dialog;
        }

        public void setCanceledOnTouchOutside(boolean isCancel) {
            isCanceledOnTouchOutside = isCancel;
        }

        private void setWindowSize(Dialog dialog) {
            DisplayMetrics dm = new DisplayMetrics();
            Window dialogWindow = dialog.getWindow();
            WindowManager m = dialogWindow.getWindowManager();
            m.getDefaultDisplay().getMetrics(dm);
            // 为获取屏幕宽、高
            WindowManager.LayoutParams p = dialogWindow.getAttributes();
//            p.height = (int) (dm.heightPixels * 0.25);
            p.width = (int) (dm.widthPixels * 0.86);
            p.alpha = 1.0f;
            p.dimAmount = 0.6f;
            dialogWindow.setAttributes(p);
        }

        public void showKeyboard() {
            if (editText != null) {
                //设置可获得焦点
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                //请求获得焦点
                editText.requestFocus();
                //调用系统输入法
                InputMethodManager inputManager = (InputMethodManager) editText
                        .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }
    }

}
