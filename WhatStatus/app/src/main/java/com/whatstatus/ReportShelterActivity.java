package com.whatstatus;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ReportShelterActivity extends AppCompatActivity {

    EditText reportEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_shelter);

        reportEditText = (EditText) findViewById(R.id.logisticReport);
        Button sendReport = (Button)findViewById(R.id.sendReport);
        sendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReportToServer(reportEditText.getText().toString());
                finish();
            }
        });
    }

    public void sendReportToServer(String report) {
        setResult(Activity.RESULT_OK, new Intent());
        Log.d("check", "sendReportToServer");
        Toast.makeText(this, "דיווח נשלח", Toast.LENGTH_SHORT).show();
        String currentDateandTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
        HashMap<String, String> reqData = new HashMap<String, String>();

        reqData.put("token", FirebaseInstanceId.getInstance().getToken());
        reqData.put("reportType", "2");
        reqData.put("reportInfo", report);
        reqData.put("timeStamp", currentDateandTime);

        new HttpRequest("addReport", reqData, "http://socialchat.16mb.com/api.php").execute();

    }


    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);

        super.onBackPressed();
    }
}
