package me.dennisg.ath1charger.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import me.dennisg.ath1charger.R;

public class ViewDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("View Data");
    }
}