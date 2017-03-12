package com.whatstatus;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.whatstatus.Models.Generals;

import java.nio.ByteBuffer;

public class authenticationActivity extends AppCompatActivity {

    ProgressBar progress;

    CheckBox checkLevel1;
    CheckBox checkLevel2;

    TextView txtLevel1;
    EditText iptLevel2;

    Button confirmButton;

    int hogerNumber;
    String password;

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
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED) ||
                intent.getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)) {
            handleNFCRead(intent);
        }

        super.onNewIntent(intent);
    }

    private void handleNFCRead(Intent intent) {
        byte[] tagId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        hogerNumber = ByteBuffer.wrap(tagId).getInt();

        progress.setProgress(1);
        confirmButton.setEnabled(true);
        txtLevel1.setText(hogerNumber + "");
        txtLevel1.setVisibility(View.VISIBLE);
        iptLevel2.setEnabled(true);
    }

    public void confirmClick(View v) {
        password = iptLevel2.getText().toString();
        checkLevel2.setChecked(true);
        if(confirmed()) {

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
}