package me.dennisg.ath1charger;

import android.nfc.tech.MifareUltralight;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Scanner;

public class OasaTicket {
    private HashMap<Integer, String> data;
    private final MifareUltralight mfu;
    private String pages;
    private static final String TAG = MifareUltralight.class.getSimpleName();

    public boolean write(int page, String hex, byte[] key) {
        try {
            mfu.connect();

            byte[] b = new byte[] {key[0], key[1], key[2], key[3]};
            if (!auth(b)) return false;

            mfu.writePage(page, hexStringToByteArray(hex));
            mfu.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    public boolean writeAll(HashMap<Integer, String> data, String key) {
        try {
            mfu.connect();
            byte[] pwd = hexStringToByteArray(key);
            if (!auth(new byte[] {pwd[0], pwd[1], pwd[2], pwd[3]})) return false;

            for (int i = 4; i <= 35; i++) {
                mfu.writePage(i, hexStringToByteArray(data.get(i)));
            }
            mfu.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private boolean auth(byte[] key) {
        assert key.length == 4;
        try {
            byte[] response = mfu.transceive(new byte[]{
                    (byte) 0x1b,
                    key[0], key[1], key[2], key[3]
            });
            if (response != null && response.length >= 2) {
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public OasaTicket(MifareUltralight mfu)  {
        this.mfu = mfu;

    }

    public boolean read() {
        try {
            this.data = mapData();
            this.pages = parse();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String readPage(int page) {
        assert page >= 4;
        assert page <= 35;
        return data.get(page);
    }

    private String parse() {
        StringBuilder builder = new StringBuilder();
        for (int i = 4; i <= 35; i++) {
            builder.append(this.readPage(i));
            builder.append("\n");
        }
        return builder.toString();
    }

    public String pages() {
        return this.pages;
    }

    private HashMap<Integer, String> mapData() {
        try {
            mfu.connect();

            HashMap<Integer, String> map = new HashMap<Integer, String>();
            int index = 4;

            for (int i = 0; i < 8; i++) {
                byte[] payload = mfu.readPages(index);
                String hex = byteArrayToHex(payload);

                String s0 = hex.substring(0, 8);
                String s1 = hex.substring(8, 16);
                String s2 = hex.substring(16, 24);
                String s3 = hex.substring(24, 32);

                map.put(index, s0);
                map.put(index+1, s1);
                map.put(index+2, s2);
                map.put(index+3, s3);
                index += 4;
            }

            return map;
        } catch (IOException ignored) {

        } finally {
            if (mfu != null) {
                try {
                    mfu.close();
                }
                catch (IOException ignored) {

                }
            }
        }
        return null;
    }

    /*
    Borrowed By Athena Ticket Scanner Application For Android
    Thanks :)
     */
    public static String byteArrayToHex(byte[] var0) {
        StringBuilder var1 = new StringBuilder(var0.length * 2);
        int var2 = var0.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            var1.append(String.format("%02x", var0[var3]));
        }

        return var1.toString();
    }
}
