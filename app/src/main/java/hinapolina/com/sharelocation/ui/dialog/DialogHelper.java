package hinapolina.com.sharelocation.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import hinapolina.com.sharelocation.R;

import static android.os.Build.VERSION_CODES.O;

/**
 * Created by hinaikhan on 10/28/17.
 */

public class DialogHelper {

    public static DialogHelper getInstance(){
        return InnerDialogHelper.INSTANCE;
    }

    public void showDefaultErrorDialog(final Context context, String title, String errorMsg, boolean isCancelable,
                                       DialogInterface.OnClickListener okOnClick){
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(errorMsg);
        alertDialog.setCancelable(isCancelable);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", okOnClick);
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(R.color.colorPrimary);
            }
        });
        alertDialog.show();
    }

    private static class InnerDialogHelper{
        private static final DialogHelper INSTANCE = new DialogHelper();
    }
}
