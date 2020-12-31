package mom.com.utils;

import android.app.Activity;
import android.app.ProgressDialog;

public class MyProgressDialog {
    static Activity context;
   static ProgressDialog progressDialog;
    public static void  getInstance(Activity context){
        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
    }


    public static ProgressDialog  getInstance2(Activity context){
        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
       return progressDialog;
    }

    public static void  setDismiss(){
        progressDialog.dismiss();
    }
}
