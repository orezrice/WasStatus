package com.whatstatus;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
    }

    public void sendReportToServer(String report) {
        //TODO: Add some real implemtation to send report to server
        Toast.makeText(this, "Sent Report: " + report, Toast.LENGTH_SHORT).show();
        goBackToMainPage();
    }

    public void goBackToMainPage() {

        //TODO: boolean indication report actually added to database
        boolean successAddingReport = true;
        Intent returnIntent = new Intent();
        if(successAddingReport) {
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
}
