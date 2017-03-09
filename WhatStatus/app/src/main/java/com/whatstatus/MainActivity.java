package com.whatstatus;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "NfcCard";

    private NfcAdapter mNfcAdapter;
    private Tag detectedTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.whatstatus.R.layout.activity_main);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            Intent NfcDisabledIntent = new Intent(this, NfcDisabledActivity.class);
            startActivity(NfcDisabledIntent);
        }

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.whatstatus.R.menu.app_menu, menu);
        return true;
    }


    /**
     * define events for each menu click
     * @param item the menu item clicked
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case com.whatstatus.R.id.refresh:
                Toast.makeText(this,"Refreshing", Toast.LENGTH_LONG).show();
                return true;
            case com.whatstatus.R.id.clear:
                Toast.makeText(this,"clearing", Toast.LENGTH_LONG).show();
                return true;
            case com.whatstatus.R.id.message:
                Toast.makeText(this,"sending message", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);

        super.onNewIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if(intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED) ||
                intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)){
            Tag test = intent.getParcelableExtra(mNfcAdapter.EXTRA_TAG);
            byte[] tagId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            int tagNumber = ByteBuffer.wrap(tagId).getInt();
            Toast.makeText(getApplicationContext(), tagNumber + "", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);

        Intent intent = getIntent();

        handleIntent(intent);
    }

    @Override
    protected void onPause() {

        mNfcAdapter.disableForegroundDispatch(this);

        super.onPause();
    }
}