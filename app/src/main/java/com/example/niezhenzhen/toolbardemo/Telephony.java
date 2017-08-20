package com.example.niezhenzhen.toolbardemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.lang.reflect.Method;

/**
 * Created by niezhenzhen on 2017-8-19.
 */

public class Telephony {
    private TelephonyManager mTelephonyManager;
    private Context context;

    public Telephony(Context context) {
        this.context = context;
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

    }

    public void call(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri uri = Uri.parse("tel:" + number);
        intent.setData(uri);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context,"没有拨打电话权限",Toast.LENGTH_SHORT).show();
        }else {
            if(mTelephonyManager.getCallState()==TelephonyManager.CALL_STATE_IDLE){
                context.startActivity(intent);
            }
        }
    }

    /**
     * 使用反射方式调用TelephonyManager的getITelephony.endCall方法来挂断电话
     */
    public boolean endCall(){
        Class<TelephonyManager> ct = TelephonyManager.class;
        boolean isEnd = false;
        try {
            Method getITelephony = ct.getDeclaredMethod("getITelephony",(Class[])null);
            getITelephony.setAccessible(true);
            final Object iTelephony =  getITelephony.invoke(mTelephonyManager,(Object[])null);
            Method endCall = iTelephony.getClass().getDeclaredMethod("endCall",(Class[])null);
            endCall.setAccessible(true);
            if(mTelephonyManager.getCallState()==TelephonyManager.CALL_STATE_OFFHOOK){
                isEnd = (boolean) endCall.invoke(iTelephony,(Object[])null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return isEnd;
    }
}
