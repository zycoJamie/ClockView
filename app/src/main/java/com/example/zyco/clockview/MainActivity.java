package com.example.zyco.clockview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.zyco.clockview.widget.ClockView;

public class MainActivity extends AppCompatActivity {
    private ClockView mClockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mClockView=findViewById(R.id.cv_clock);
        mClockView.start();
    }
}
