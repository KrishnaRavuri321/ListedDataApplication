package com.assignment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CoustomDialog extends Dialog {
    public Context c;
    public Dialog dailog;
   public static  RelativeLayout gallery;
    public static  RelativeLayout camera;
    @SuppressLint("StaticFieldLeak")
    public static TextView cancel,save;
    public static  EditText recording_name;


    public CoustomDialog(@NonNull Context context) {
        super(context);
        this.c=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout);

        dailog=new Dialog(c);


        gallery=findViewById(R.id.Rl5);
        camera=findViewById(R.id.Rl6);


    }


   /* @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Cancel:
                dismiss();
                break;
            case R.id.save_button:

                dailog.dismiss();

            default:
                break;
        }
        dismiss();
    }
*/

}


