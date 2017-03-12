package com.whatstatus;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.OnNmeaMessageListener;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.whatstatus.DAL.PeopleDAL;
import com.whatstatus.DAL.StatusHelper;
import com.whatstatus.FCM.DataManagementService;
import com.whatstatus.FCM.TokenManagementService;
import com.whatstatus.Models.People;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static MainActivity m_instance = null;
    private NfcAdapter mNfcAdapter;

    public ListView inHouse;
    public ListView outHouse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_instance = this;

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

        inHouse = (ListView) findViewById(R.id.inhouselist);
        outHouse = (ListView) findViewById(R.id.outhouselist);

        Utils.initializePeopleData();

        handleIntent(getIntent());
    }

    public static MainActivity getInstance() {
        return m_instance;
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
            byte[] tagId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            int tagNumber = ByteBuffer.wrap(tagId).getInt();
            Toast.makeText(getApplicationContext(), tagNumber + "", Toast.LENGTH_LONG).show();

            HashMap<String, String> reqData = new HashMap<String, String>();

            reqData.put("cardId", tagNumber + "");
            reqData.put("token", FirebaseInstanceId.getInstance().getToken());

            HttpRequest updateRequest = new HttpRequest("updateListById", reqData, "http://socialchat.16mb.com/api.php");

            PeopleDAL.getInstance(getApplicationContext()).moveToPresent(tagNumber + "");

            Utils.loadList();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0);
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