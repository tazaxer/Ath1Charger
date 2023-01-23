package me.dennisg.ath1charger.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import me.dennisg.ath1charger.MainActivity;
import me.dennisg.ath1charger.OasaTicket;
import me.dennisg.ath1charger.R;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class ScanActivity extends AppCompatActivity {
    private PendingIntent pendingIntent;
    private String[][] techListsArray;
    private IntentFilter[] intentFiltersArray = new IntentFilter[] {};
    private TextView scannedDataTxt;
    private Button saveBtn;
    private GifImageView scanGif;
    private TextView tagRemovedTxt;
    private OasaTicket ticket;
    NfcManager manager;
    NfcAdapter adapter;
    private static final String TAG = ScanActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        this.tagRemovedTxt = (TextView) findViewById(R.id.tagRemoved_txt);
        this.scanGif = (GifImageView) findViewById(R.id.gif_img);
        this.saveBtn = (Button) findViewById(R.id.save_btn);
        this.scannedDataTxt = (TextView) findViewById(R.id.scanneddata_txt);
        scannedDataTxt.setMovementMethod(new ScrollingMovementMethod());

        this.manager = (NfcManager) getApplicationContext().getSystemService(Context.NFC_SERVICE);
        this.adapter = manager.getDefaultAdapter();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Scan Full Ticket");

        registerSaveButton();

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

    private void registerSaveButton() {
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toStore = ticket.pages();
                FileOutputStream fos = null;

                try {
                    fos = openFileOutput(MainActivity.DATA_NAME, MODE_PRIVATE);
                    fos.write(toStore.getBytes());

                    Toast.makeText(getApplicationContext(), "Data Saved Successfully", Toast.LENGTH_LONG).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                            finish();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        MifareUltralight mfu = MifareUltralight.get(tag);
        this.ticket = new OasaTicket(mfu);
        if (!(ticket.read())) {
            tagRemovedTxt.setVisibility(View.VISIBLE);
            ticket = null;
            return;
        }

        tagRemovedTxt.setVisibility(View.INVISIBLE);
        scannedDataTxt.setText(ticket.pages());
        scannedDataTxt.setVisibility(View.VISIBLE);
        saveBtn.setVisibility(View.VISIBLE);
        scanGif.setVisibility(View.INVISIBLE);

    }
}