package com.practice.wpsactivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


/**
 * 이 클래스는 서버와 통신할 용도의 모듈이 된다.
 * 앞의 adapter자바 클래스나 sub1클래스에서 intent 를 통해서 이쪽으로 넘어온다.
 * **/
public class subactivity1_1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subactivity11);
    }
}