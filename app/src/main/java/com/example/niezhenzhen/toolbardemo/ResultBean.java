package com.example.niezhenzhen.toolbardemo;

/**
 * Created by niezhenzhen on 2017-8-19.
 */

public class ResultBean  {
    private int passCount;
    private int failCount;

    public ResultBean(int pass,int fail){
        this.passCount = pass;
        this.failCount = fail;
    }
    public int getPassCount(){
        return this.passCount;
    }
    public int getFailCount(){
        return this.failCount;
    }

    public int getSumCount(){
        return passCount+failCount;
    }
}
