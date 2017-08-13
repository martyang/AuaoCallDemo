package com.example.niezhenzhen.toolbardemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "AutoCall";
    private final int REQUEST_CODE = 100;
    Button add_number;
    Button start_test;
    Button stop_test;
    EditText number;
    EditText test_count;
    TextView test_number;
    TelephonyManager mTelephonyManager;
    int count;
    int success = 0;
    int fail = 0;
    ArrayList<String> numbers;
    DrawerLayout drawerLayout;
    boolean isRunning = false;
    SharedPreferences sp;
    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == 1){
                Log.i(TAG,"测试完成");
                start_test.setEnabled(true);
                stop_test.setEnabled(false);
                isRunning = false;
            }
            return true;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate执行");
        setContentView(R.layout.drawerlayout);
        initView();
        numbers =new ArrayList<>();
        if(savedInstanceState!=null){
            Log.i(TAG,"恢复按键状态");
            isRunning = savedInstanceState.getBoolean("isRunning");
        }
        /*设置toolbar*/
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("自动拨号测试工具");
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        start_test.setOnClickListener(listener);
        add_number.setOnClickListener(listener);
        stop_test.setOnClickListener(listener);
        /*设置导航按钮*/
//        ActionBar actionBar = getSupportActionBar();
//        if(actionBar != null){
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher);
//        }

    }

    private void initView(){
        add_number = (Button) findViewById(R.id.add);
        start_test = (Button) findViewById(R.id.start_test);
        stop_test = (Button) findViewById(R.id.stop);
        stop_test.setEnabled(false);
        number = (EditText) findViewById(R.id.phone_number);
        test_count = (EditText) findViewById(R.id.test_count);
        test_number = (TextView) findViewById(R.id.show_number);
    }

    private void startTest(String number){
        if(mTelephonyManager.getSimState()!=TelephonyManager.SIM_STATE_READY){
            Toast.makeText(MainActivity.this,"SIM卡不可用，请检查SIM卡状态！",Toast.LENGTH_SHORT).show();
        }else if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
            String[] permissions = new String[]{Manifest.permission.CALL_PHONE,Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(MainActivity.this,permissions,REQUEST_CODE);
        }else{
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri uri = Uri.parse("tel:"+number);
            intent.setData(uri);
            startActivity(intent);
        }
    }
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.add:
                    Log.i(TAG,"Add number");
                    String phone_number = number.getText().toString();
                    if("".equals(phone_number)){
                        Toast.makeText(MainActivity.this,"号码不能为空，请重新输入！",Toast.LENGTH_SHORT).show();
                    }else {
                        if(numbers.size()<10){
                            numbers.add(phone_number);
                            test_number.append("\n");
                            test_number.append(phone_number);
                        }else {
                            Toast.makeText(MainActivity.this,"号码已经够多了！",Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.start_test:
                    count = Integer.parseInt(test_count.getText().toString());
                    Log.i(TAG,"测试次数："+count);
                   if(count<1){
                        Toast.makeText(MainActivity.this,"没有设置正确的测试次数",Toast.LENGTH_SHORT).show();
                    }else {
                       Log.i(TAG,"The test number count:"+numbers.size());
                       start_test.setEnabled(false);
                       stop_test.setEnabled(true);
                       isRunning = true;
                       Log.i(TAG,"start test");
                       new Thread(new Runnable() {
                           @Override
                           public void run() {
                               for (int i = 0; i < count; i++) {
                                   if(isRunning){
                                       String number;
                                       Log.i(TAG, "拨打电话:" + i);
                                       saveReult("自动拨电话测试结果：\n");
                                       if(numbers.size()==0){
                                           number = "10086";
                                       }else {
                                           number = numbers.get(i%numbers.size());
                                           saveReult(getCurrentTime()+":start call "+number+"\n");
                                           Log.i(TAG,"Test number:"+number);
                                       }
                                       startTest(number);
                                       try {
                                           Log.i(TAG, "delay 10 seconds");
                                           Thread.sleep(10000);
                                           if (endCall()) {
                                               Log.i(TAG, getCurrentTime()+"Result Success");
                                               success +=1;
                                               saveReult(getCurrentTime()+":end call\n");
                                               saveReult(getCurrentTime()+":Pass\n");
                                           }else{
                                               Log.i(TAG,"Result Failed");
                                               fail +=1;
                                               saveReult(getCurrentTime()+":Failed\n");
                                           }
                                           Thread.sleep(10000);
                                       } catch (InterruptedException e) {
                                           e.printStackTrace();
                                       }
                                   }else {
                                       break;
                                   }
                               }
                               Message message = Message.obtain();
                               message.what = 1;
                               mHandler.sendMessage(message);
                               Log.i(TAG, "Test end");
                           }
                       }).start();

                     }
                    break;
                case R.id.stop:
                    Log.i(TAG,"isRunning:"+isRunning);
                    if(isRunning){
                        Log.i(TAG,"Stop Test");
                        isRunning = false;
                        start_test.setEnabled(true);
                        stop_test.setEnabled(false);
                    }
                    break;
            }
        }
    };

    private String getCurrentTime(){
        String current_time = "";
//        DateFormat format = SimpleDateFormat.getDateInstance(SimpleDateFormat.DATE_FIELD);
        SimpleDateFormat format = new SimpleDateFormat("MM-dd hh:mm:ss");
        current_time = format.format(new Date());
        return current_time;
    }
    private void saveReult(String data){
        File result = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/AutoCall");
        if(!result.exists()){
            result.mkdirs();
        }
        File result_txt = new File(result.getAbsolutePath(),"result.txt");
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(result_txt,true);
            fo.write(data.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }finally {
            if(fo!=null){
                try {
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 使用反射方式调用TelephonyManager的getITelephony.endCall方法来挂断电话
     */
    private boolean endCall(){
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
            }else{
                saveReult(getCurrentTime()+":call has dropped\n");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return isEnd;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==REQUEST_CODE&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
//            startTest();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.test_report:
                Intent intent = new Intent(MainActivity.this,ReportActivity.class);
                intent.putExtra("Total",count);
                intent.putExtra("Pass",success);
                intent.putExtra("Fail",fail);
                startActivity(intent);
                break;
            case R.id.send_email:
                //send test report by email
                break;
            case R.id.note:
                //test note
                break;

            case R.id.author:

                break;
            case R.id.about:

                break;
            case R.mipmap.ic_launcher:
                drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }
}
