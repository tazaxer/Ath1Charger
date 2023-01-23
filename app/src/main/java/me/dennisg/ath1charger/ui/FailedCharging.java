package me.dennisg.ath1charger.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import me.dennisg.ath1charger.R;

public class FailedCharging extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failed_charging);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Failed");
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}