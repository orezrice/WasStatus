package com.whatstatus;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.InputMismatchException;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private NfcAdapter mNfcAdapter;

    private ListView inHouse;
    private ListView outHouse;

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

        ((FloatingActionButton)findViewById(R.id.fab)).setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                fabClick(view);
                return true;
            }
        });

        if (!mNfcAdapter.isEnabled()) {
            Intent NfcDisabledIntent = new Intent(this, NfcDisabledActivity.class);
            startActivity(NfcDisabledIntent);
        }

        handleIntent(getIntent());
        loadList();
    }

    public String convertImageToSring(int imageId) {

        Bitmap icon = BitmapFactory.decodeResource(this.getResources(), imageId);
        return encodeToBase64(icon, Bitmap.CompressFormat.JPEG, 100);

    }

    public void loadList(){
        inHouse = (ListView) findViewById(R.id.inhouselist);
        outHouse = (ListView) findViewById(R.id.outhouselist);
        final ListItem item = new ListItem("דונלד טראמפ",
                convertImageToSring(R.drawable.exampleperson));

        ListAdapter inHouseAdapter = new ListAdapter(this,
                new LinkedList<ListItem>(){{add(item);}});
        ListAdapter outHouseAdapter = new ListAdapter(this,
                new LinkedList<ListItem>(){{add(item); add(item);}});

        inHouse.setAdapter(inHouseAdapter);
        outHouse.setAdapter(outHouseAdapter);
    }

    public String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.whatstatus.R.menu.app_menu, menu);
        return true;
    }

    public void fabClick(View v) {

        final EditText inputNumber = new EditText(MainActivity.this);
        inputNumber.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        inputNumber.setLayoutParams(lp);

        final AlertDialog alert = new AlertDialog.Builder(this)
                .setTitle("הוסף לנוכחים")
                .setMessage("הקלד מספר אישי של חייל נוכח")
                .setView(inputNumber)
                .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            int typedNumber =
                                    Integer.parseInt(validateInput(inputNumber.getText().toString()));
                            dialogInterface.dismiss();
                            inputTyped(typedNumber);
                        } catch (InputMismatchException e) {
                            Toast.makeText(getApplicationContext(), "מספר החוגר לא תקין", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).create();

        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alert.show();
    }

    public String validateInput(String baseInput) throws InputMismatchException {
        final int DIGITS_IN_HOGER = 7;
        try{
            if(baseInput == null || baseInput.isEmpty() || baseInput.length() != DIGITS_IN_HOGER
                    || baseInput.contains("-"))
                throw new InputMismatchException();
            return baseInput;
        } catch (NumberFormatException e) {
            throw new InputMismatchException();
        }
    }

    public void inputTyped(int numberTyped) {
        Toast.makeText(MainActivity.this,"מספר שהוקלד" + numberTyped, Toast.LENGTH_LONG).show();
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