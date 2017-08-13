package com.example.niezhenzhen.toolbardemo;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class ReportActivity extends AppCompatActivity {

    TextView report;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        report = (TextView) findViewById(R.id.report_text);
        Toolbar report_toolbar = (Toolbar) findViewById(R.id.report_toolbar);
        report_toolbar.setTitle("测试报告");
        report_toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(report_toolbar);

        Intent data = getIntent();
        int total = data.getIntExtra("Total",0);
        int pass = data.getIntExtra("Pass",0);
        int fail = data.getIntExtra("Fail",0);
        report.setText("自动拨打电话测试报告：\n");
        report.append("测试总数："+total+"\n");
        report.append("测试通过："+pass+"\n");
        report.append("测试失败："+fail+"\n");
    }
}
