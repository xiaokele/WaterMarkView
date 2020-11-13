package com.kele.watermarkviewdemo;

import android.os.Bundle;

import com.kele.watermarkviewdemo.watermark.WaterMarkView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WaterMarkView wmv = findViewById(R.id.wmv);
        wmv.setParams("我是小可乐");
    }
}