package com.example.niezhenzhen.toolbardemo;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by niezhenzhen on 2017-8-19.
 */

public class Utils {

    /**
     * 获取yyyy MM-dd hh:mm:ss格式的时间
     * @return yyyy MM-dd hh:mm:ss格式时间字符
     */
    public static String getCurrentTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy MM-dd hh:mm:ss", Locale.CHINA);
        return format.format(new Date());
    }

    /**
     * 将数据以txt保存到存储中
     * @param folder 保存数据的文件加
     * @param data 需要保存的数据
     */
    public static void saveReult(String folder,String data){
        File result = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+folder);
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
     * 删除指定文件名称下的文件
     * @param folder 制定删除的文件名
     */
    public static boolean clearData(String folder){
        File result = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+folder);
        return result.exists()&&deleFiles(result);
    }

    /**
     * 删除文件及文件夹下的所有文件
     * @param file 需要删除的文件
     */
    public static boolean deleFiles(File file){
        if(file.isDirectory()){
            File[] fileList = file.listFiles();
            for(File f:fileList){
                if(f.isFile()){
                    f.delete();
                }else{
                    deleFiles(f);
                }
            }
        }
        return file.delete();
    }

    /**
     * 读取文件folder中的测试结果
     * @param folder
     */
    public static ResultBean getReult(String folder) {
        String pass = "Pass";
        String fail = "Failed";
        int passCount = 0;
        int failCount = 0;
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+folder);
        if(file.exists()){
            File result_txt = new File(file.getAbsolutePath(),"result.txt");
            FileInputStream fin = null;
            BufferedReader reader = null;
            try {
                fin = new FileInputStream(result_txt);
                reader = new BufferedReader(new InputStreamReader(fin));
                String line = "";
                while ((line=reader.readLine())!=null){
                    if(line.contains(pass)){
                        passCount++;
                    }else if(line.contains(fail)){
                        failCount++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
               if(reader!=null){
                   try {
                       reader.close();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
               if(fin!=null){
                   try {
                       fin.close();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
            }
        }
        return new ResultBean(passCount,failCount);
    }
}
