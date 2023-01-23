package me.dennisg.ath1charger;

import android.nfc.NfcAdapter;


public class NFCUtils {
    private final NfcAdapter adapter;
    private boolean en;

    public NFCUtils(NfcAdapter adapter) {
        this.adapter = adapter;
        if (assertNFC() == 1) {
            en = true;
        }
    }

    public boolean isEnabled() {
        assertNFC();
        return this.en;
    }

    public int assertNFC() {
        if (adapter == null) {
            return -1;
        } else if (!(adapter.isEnabled())) {
            return 0;
        } else {
            en = true;
            return 1;
        }
    }
}
