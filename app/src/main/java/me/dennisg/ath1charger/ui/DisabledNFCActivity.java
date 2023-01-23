package me.dennisg.ath1charger.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;

import me.dennisg.ath1charger.NFCUtils;
import me.dennisg.ath1charger.R;

public class DisabledNFCActivity extends AppCompatActivity {
    private NFCUtils nfcUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disabled_nfc);

        NfcManager manager = (NfcManager) getApplicationContext().getSystemService(Context.NFC_SERVICE);
        this.nfcUtils = new NFCUtils(manager.getDefaultAdapter());

        final Button button = (Button) findViewById(R.id.enfc_btn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Please Enable NFC", Toast.LENGTH_LONG).show();
                Intent intent;
                intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
            }
        });
        registerReceiver(nfcListener, new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED));
    }

    private final BroadcastReceiver nfcListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (Objects.equals(action, NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)) {
                if (nfcUtils.isEnabled()) {
                    finish();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nfcListener);
    }

}