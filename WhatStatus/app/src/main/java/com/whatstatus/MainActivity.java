package com.whatstatus;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.whatstatus.DAL.PeopleDAL;
import com.whatstatus.DAL.StatusHelper;
import com.whatstatus.Models.People;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

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

        if (!mNfcAdapter.isEnabled()) {
            Intent NfcDisabledIntent = new Intent(this, NfcDisabledActivity.class);
            startActivity(NfcDisabledIntent);
        }

        initializePeopleData();

        loadList();

        handleIntent(getIntent());
    }

    private void initializePeopleData() {
        PeopleDAL pDal = PeopleDAL.getInstance(getApplicationContext());
        String peopleJSON = "";

        // Try to get people list from the server api
        try {
            peopleJSON = new HttpRequest("getAbsenceList",
                    null,
                    "http://socialchat.16mb.com/api.php").execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // Parse the string to json objects
        try {
            JSONArray peopleArr = new JSONArray(peopleJSON);

            // Remove all the absent people from the local db
            pDal.deleteAbsence();

            // Add all the people to the local database
            for (int pIndex = 0; pIndex < peopleArr.length(); pIndex++) {
                pDal.addPeople(new People(
                        peopleArr.getJSONObject(pIndex).getString("cardId"),
                        peopleArr.getJSONObject(pIndex).getString("cardNumber"),
                        peopleArr.getJSONObject(pIndex).getString("cardNumber"),
                        peopleArr.getJSONObject(pIndex).getString("firstName"),
                        peopleArr.getJSONObject(pIndex).getString("lastName"),
                        peopleArr.getJSONObject(pIndex).getString("phoneNumber"),
                        peopleArr.getJSONObject(pIndex).getString("rank"),
                        peopleArr.getJSONObject(pIndex).getInt("isPresentAndSafe"),
                        peopleArr.getJSONObject(pIndex).getInt("isPresentGlobaly")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String convertImageToSring(int imageId) {

        Bitmap icon = BitmapFactory.decodeResource(this.getResources(), imageId);
        return encodeToBase64(icon, Bitmap.CompressFormat.JPEG, 100);

    }

    public void loadList(){
        ArrayList<People> peopleArr = PeopleDAL.getInstance(getApplicationContext()).getAll();
        LinkedList<ListItem> inHouseList = new LinkedList<ListItem>();
        LinkedList<ListItem> outHouseList = new LinkedList<ListItem>();

        for (People people : peopleArr) {
            if (people.getIsPresentAndSafe() == 0) {
                outHouseList.add(new ListItem(
                        people.getFirstName() + " " + people.getLastName(),
                        convertImageToSring(R.drawable.exampleperson)
                ));
            } else {
                inHouseList.add(new ListItem(
                        people.getFirstName() + " " + people.getLastName(),
                        convertImageToSring(R.drawable.exampleperson)
                ));
            }
        }

        inHouse = (ListView) findViewById(R.id.inhouselist);
        outHouse = (ListView) findViewById(R.id.outhouselist);

        ListAdapter inHouseAdapter = new ListAdapter(this, inHouseList);
        ListAdapter outHouseAdapter = new ListAdapter(this, outHouseList);

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