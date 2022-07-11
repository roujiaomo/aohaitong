package com.aohaitong.utils.dialog;

import android.app.Activity;
import android.os.Handler;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class MyQmuiDialog {
    public interface onDismiss {
        void onDismiss();
    }

    public static void showSuccessDialog(Activity context, String s, onDismiss listener) {
        try {
            context.runOnUiThread(() -> {
                QMUITipDialog dialog = new QMUITipDialog.Builder(context)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                        .setTipWord(s)
                        .create();
                if (context != null && dialog != null && !context.isDestroyed())
                    dialog.show();
                dialog.setOnDismissListener(dialog1 -> listener.onDismiss());
                new Handler().postDelayed(() -> {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }, 1500);
            });
        } catch (Exception ignore) {
        }
    }

    public static void showSuccessDialog(Activity context, String s) {
        try {
            context.runOnUiThread(() -> {
                QMUITipDialog dialog = new QMUITipDialog.Builder(context)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                        .setTipWord(s)
                        .create();
                if (context != null && dialog != null && !context.isDestroyed())
                    dialog.show();
                new Handler().postDelayed(() -> {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }, 1500);
            });
        } catch (Exception ignore) {
        }
    }

    public static void showErrorDialog(Activity context, String s) {
        try {
            context.runOnUiThread(() -> {
                QMUITipDialog dialog = new QMUITipDialog.Builder(context)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_FAIL)
                        .setTipWord(s)
                        .create();
                if (dialog != null && !context.isDestroyed())
                    dialog.show();
                new Handler().postDelayed(() -> {
                    if (dialog != null && !context.isFinishing()) {
                        dialog.dismiss();
                    }
                }, 1500);
            });
        } catch (Exception ignore) {
        }
    }
}
