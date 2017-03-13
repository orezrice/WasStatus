package com.whatstatus;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
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

    CheckBox checkLevel1;
    CheckBox checkLevel2;

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

        checkLevel1 = (CheckBox) findViewById(R.id.statusHogerNumber);
        checkLevel2 = (CheckBox) findViewById(R.id.statusPass);

        iptLevel2 = (EditText) findViewById(R.id.iptPass);
        iptLevel2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkLevel2.setChecked(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkLevel2.setChecked(true);
            }
        });

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

            openLevel2();
        }
    }

    public void openLevel2() {
        level2Layout.setVisibility(View.VISIBLE);

        confirmButton.setEnabled(true);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmClick();
            }
        });

        checkLevel1.setChecked(true);
        iptLevel2.setEnabled(true);
    }

    public void confirmClick() {
        password = iptLevel2.getText().toString();
        checkLevel2.setChecked(true);
        Intent returnIntent = new Intent();

        if(Utils.loginCommander(hogerNumber + "", password)) {
            setResult(Activity.RESULT_OK, returnIntent);
        } else {
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }

        finish();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);

        super.onBackPressed();
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