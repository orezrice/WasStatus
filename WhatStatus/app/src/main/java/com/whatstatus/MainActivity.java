package com.whatstatus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.whatstatus.DAL.PeopleDAL;
import com.whatstatus.Models.Generals;
import com.whatstatus.Models.People;

import java.nio.ByteBuffer;
import java.util.HashMap;
import android.Manifest;

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

        startActivity(new Intent(this, ExpandbleListViewExample.class));

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

        ((FloatingActionButton)findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.add_by_id_dialog,null);
                final EditText edit = (EditText)dialogView.findViewById(R.id.input);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("מספר אישי:")
                        .setView(dialogView)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String cardNumber = edit.getText().toString();

                                HashMap<String, String> reqData = new HashMap<String, String>();

                                reqData.put("cardNumber", cardNumber);
                                reqData.put("token", FirebaseInstanceId.getInstance().getToken());

                                new HttpRequest("updateListByNumber", reqData, "http://socialchat.16mb.com/api.php").execute();

                                PeopleDAL.getInstance(getApplicationContext()).moveToPresent(cardNumber, false);

                                Utils.loadList();

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                //return false;
            }
        });

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
        Intent authenticationIntent = new Intent(this, AuthenticationActivity.class);

        // Handle item selection
        switch (item.getItemId()) {
            case com.whatstatus.R.id.refresh:
                Utils.initializePeopleData();

                return true;
            case com.whatstatus.R.id.clear:
                authenticationIntent.putExtra(Generals.REQUEST_TYPE, Generals.CLEAR_ACTION);
                startActivityForResult(authenticationIntent, Generals.CLEAR_ACTION);

                return true;
            case com.whatstatus.R.id.message:
                authenticationIntent.putExtra(Generals.REQUEST_TYPE, Generals.SEND_MESSAGE_ACTION);
                startActivityForResult(authenticationIntent, Generals.SEND_MESSAGE_ACTION);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case Generals.CLEAR_ACTION:
                    PeopleDAL.getInstance(this).deleteAll();
                    new HttpRequest("resetPresentsList", null).execute(new HttpRequest.TaskListener() {
                        @Override
                        public void onFinished(String result) {
                            Utils.initializePeopleData();
                        }
                    });

                    Toast.makeText(this, "הנתונים התאפסו!", Toast.LENGTH_SHORT).show();

                    break;

                case Generals.SEND_MESSAGE_ACTION:
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.SEND_SMS)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.SEND_SMS)) {
                        } else {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.SEND_SMS},
                                    0);
                        }
                    }
                    break;
            }
        } else {
            Toast.makeText(this, "בעיה בהתחברות!", Toast.LENGTH_SHORT).show();
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

            HashMap<String, String> reqData = new HashMap<String, String>();

            reqData.put("cardNumber", tagNumber + "");
            reqData.put("token", FirebaseInstanceId.getInstance().getToken());

            new HttpRequest("updateListByNumber", reqData, "http://socialchat.16mb.com/api.php").execute();

            PeopleDAL.getInstance(getApplicationContext()).moveToPresent(tagNumber + "", true);

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
    protected void onStart() {
        Utils.initializePeopleData();

        super.onStart();
    }

    @Override
    protected void onPause() {

        mNfcAdapter.disableForegroundDispatch(this);

        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager sms = SmsManager.getDefault();

                    for (People people : PeopleDAL.getInstance(getApplicationContext()).getAll()) {
                        sms.sendTextMessage(people.getPhoneNumber(),
                                null,
                                "הכל בסדר? אתה נמצא במחרב מוגן?", null, null);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }
}