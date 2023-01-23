package me.dennisg.ath1charger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;

import me.dennisg.ath1charger.ui.DisabledNFCActivity;
import me.dennisg.ath1charger.ui.InfoActivity;
import me.dennisg.ath1charger.ui.NoNFCActivity;
import me.dennisg.ath1charger.ui.ReloadActivity;
import me.dennisg.ath1charger.ui.ScanActivity;
import me.dennisg.ath1charger.ui.SettingsActivity;
import me.dennisg.ath1charger.ui.ViewDataActivity;

public class MainActivity extends AppCompatActivity {
    private NFCUtils nfcUtils;
    public static final String DATA_NAME = "full_data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NfcManager manager = (NfcManager) getApplicationContext().getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();

        this.nfcUtils = new NFCUtils(adapter);
        checkNFC();
        registerReceiver(nfcListener, new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED));

        registerButtons();
    }

    private void registerButtons() {
        final Button scanBtn = findViewById(R.id.scan_btn);
        scanBtn.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, ScanActivity.class);
            startActivity(i);
        });
        final Button reloadBtn = findViewById(R.id.reload_btn);
        reloadBtn.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, ReloadActivity.class);
            startActivity(i);
        });
    }


    private void checkNFC() {
        Intent i;
        switch (nfcUtils.assertNFC()) {
            case -1:
                i = new Intent(MainActivity.this, NoNFCActivity.class);
                startActivity(i);
                break;
            case 0:
                i = new Intent(MainActivity.this, DisabledNFCActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.view_itm:
                i = new Intent(MainActivity.this, ViewDataActivity.class);
                startActivity(i);
                return true;
            case R.id.settings_itm:
                i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.info_itm:
                i = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(i);
                return true;
            default:
                super.onOptionsItemSelected(item);
        }
        return false;
    }

    private final BroadcastReceiver nfcListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (Objects.equals(action, NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)) {
                checkNFC();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(nfcListener, new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(nfcListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nfcListener);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkNFC();
    }

}