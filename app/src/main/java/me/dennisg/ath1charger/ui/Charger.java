package me.dennisg.ath1charger.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import me.dennisg.ath1charger.MainActivity;
import me.dennisg.ath1charger.OasaTicket;
import me.dennisg.ath1charger.R;

public class Charger extends AppCompatActivity {
    private PendingIntent pendingIntent;
    private String[][] techListsArray;
    private IntentFilter[] intentFiltersArray = new IntentFilter[] {};
    private OasaTicket ticket;
    private static final String TAG = ReloadActivity.class.getSimpleName();
    private String key;
    NfcManager manager;
    NfcAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charger);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Charging...");

        Bundle bundle = getIntent().getExtras();

        this.key = bundle.getString("pwd");
        this.manager = (NfcManager) getApplicationContext().getSystemService(Context.NFC_SERVICE);
        this.adapter = manager.getDefaultAdapter();
        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");    /* Handles all MIME based dispatches.
                                       You should specify only the ones that you need. */
        }
        catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[] {ndef, };
        techListsArray = new String[][] { new String[] { MifareUltralight.class.getName() } };
    }

    public void onPause() {
        super.onPause();
        adapter.disableForegroundDispatch(this);
    }

    public void onResume() {
        super.onResume();

        adapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        MifareUltralight mfu = MifareUltralight.get(tag);

        this.ticket = new OasaTicket(mfu);
        if (!(ticket.read())) {
            ticket = null;
            Toast.makeText(getApplicationContext(), "Error, Please Try Again!", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<Integer, String> map = new HashMap<>();
        FileInputStream fis = null;

        try {
            fis = openFileInput(MainActivity.DATA_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String text;

            int i = 4;
            while ((text = br.readLine()) != null) {
                map.put(i, text);
                i++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (map.isEmpty()) {
            Log.e(TAG, "Empty Map!");
            finish();
        }

        if (ticket.writeAll(map, this.key)) {
            Log.i(TAG, "Written!");
            Intent i = new Intent(Charger.this, CompleteCharging.class);
            startActivity(i);
            finish();
        } else {
            Log.wtf(TAG, "Writing Failed!");
            Intent i = new Intent(Charger.this, FailedCharging.class);
            startActivity(i);
            finish();
        }
    }
}