package com.whatstatus;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class ReportShelterActivity extends AppCompatActivity {

    EditText reportEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_shelter);

        reportEditText = (EditText) findViewById(R.id.logisticReport);
    }

    public void sendButtonClicked(View v) {
        sendReportToServer(reportEditText.getText().toString());
        finish();
    }

    public void sendReportToServer(String report) {
        setResult(Activity.RESULT_OK, new Intent());
        Log.d("check", "sendReportToServer");
        Toast.makeText(this, "Sent Report: " + report, Toast.LENGTH_SHORT).show();

        HashMap<String, String> reqData = new HashMap<String, String>();

        reqData.put("token", FirebaseInstanceId.getInstance().getToken());
        reqData.put("reportType", "2");
        reqData.put("reportInfo", report);

        new HttpRequest("addReport", reqData, "http://socialchat.16mb.com/api.php").execute();

    }


    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);

        super.onBackPressed();
    }
}
