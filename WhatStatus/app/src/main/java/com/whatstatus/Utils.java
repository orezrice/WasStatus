package com.whatstatus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ListView;

import com.whatstatus.DAL.PeopleDAL;
import com.whatstatus.Models.People;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

/**
 * Created by User on 12/03/2017.
 */
public class Utils {
    public static ListAdapter inHouseAdapter = new ListAdapter(MainActivity.getInstance().getApplicationContext(), new LinkedList<ListItem>());
    public static ListAdapter outHouseAdapter = new ListAdapter(MainActivity.getInstance().getApplicationContext(), new LinkedList<ListItem>());

    public static void initializePeopleData() {
        PeopleDAL pDal = PeopleDAL.getInstance(MainActivity.getInstance().getApplicationContext());
        String peopleJSON = "";

        // Try to get people list from the server api
        try {
            peopleJSON = new HttpRequest("getAbsenceList",
                    null, "http://socialchat.16mb.com/api.php").execute().get();
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

        MainActivity.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.loadList();
            }
        });
    }

    public static String convertImageToSring(int imageId) {

        Bitmap icon = BitmapFactory.decodeResource(MainActivity.getInstance().getResources(), imageId);
        return encodeToBase64(icon, Bitmap.CompressFormat.JPEG, 100);

    }

    public static void loadList(){
        ArrayList<People> peopleArr = PeopleDAL.getInstance(MainActivity.getInstance().getApplicationContext()).getAll();
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

        inHouseAdapter = new ListAdapter(MainActivity.getInstance().getApplicationContext(), new LinkedList<ListItem>());
        outHouseAdapter = new ListAdapter(MainActivity.getInstance().getApplicationContext(), new LinkedList<ListItem>());

        inHouseAdapter.addAll(inHouseList);
        outHouseAdapter.addAll(outHouseList);

        MainActivity.getInstance().inHouse.setAdapter(inHouseAdapter);
        MainActivity.getInstance().outHouse.setAdapter(outHouseAdapter);

        inHouseAdapter.notifyDataSetChanged();
        outHouseAdapter.notifyDataSetChanged();
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
}
