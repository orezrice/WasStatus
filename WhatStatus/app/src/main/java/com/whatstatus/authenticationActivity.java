package com.whatstatus;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.whatstatus.DAL.PeopleDAL;
import com.whatstatus.Models.Generals;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class AuthenticationActivity extends AppCompatActivity {

    ProgressBar progress;

    CheckBox checkLevel1;
    CheckBox checkLevel2;

    TextView txtLevel1;
    EditText iptLevel2;

    View level2Layout;

    Button confirmButton;

    int hogerNumber;
    String password;

    int requestCode;

    private NfcAdapter mNfcAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        progress = (ProgressBar) findViewById(R.id.progress);

        checkLevel1 = (CheckBox) findViewById(R.id.statusHogerNumber);
        checkLevel2 = (CheckBox) findViewById(R.id.statusPass);

        txtLevel1 = (TextView) findViewById(R.id.txtHogerNumber);
        iptLevel2 = (EditText) findViewById(R.id.iptPass);

        confirmButton = (Button) findViewById(R.id.btnConfirm);

        level2Layout = findViewById(R.id.layoutLevelTwo);

        requestCode = getIntent().getIntExtra(Generals.REQUEST_TYPE, -1);

        if(requestCode == -1) {
            startActivity(new Intent(this, MainActivity.class));
        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleNFCRead(intent);

        super.onNewIntent(intent);
    }

    private void handleNFCRead(Intent intent) {
        if(intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED) ||
                intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)){
            byte[] tagId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            hogerNumber = ByteBuffer.wrap(tagId).getInt();

            Toast.makeText(getApplicationContext(), hogerNumber + "", Toast.LENGTH_LONG).show();

            openLevel2();
        }
    }

    public void openLevel2() {
        level2Layout.setVisibility(View.VISIBLE);
        progress.setProgress(1);
        confirmButton.setEnabled(true);
        checkLevel1.setChecked(true);
        txtLevel1.setText(hogerNumber + "");
        txtLevel1.setVisibility(View.VISIBLE);
        iptLevel2.setEnabled(true);
    }

    public void confirmClick(View v) {
        password = iptLevel2.getText().toString();
        checkLevel2.setChecked(true);
        if(confirmed()) {

            switch (requestCode) {
                case Generals.CLEAR_ACTION:
                    clear();
                    break;
                case Generals.SEND_MESSAGE_ACTION:
                    sendMessage();
                    break;
            }

            new CountDownTimer(1000, 100) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }
            }.start();
        } else {
            Toast.makeText(this,
                    "אנחנו כמעט שם!\nרק צריך לוודא שם משתמש וסיסמא",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public boolean confirmed() {
        return Math.random() >= 0.5d;
    }

    public void sendMessage() {

        Toast.makeText(this, "שולח הודעות", Toast.LENGTH_SHORT).show();
    }

    public void clear() {
        Toast.makeText(this, "מאפס", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0);
        mNfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    @Override
    protected void onPause() {

        mNfcAdapter.disableForegroundDispatch(this);

        super.onPause();
    }
}