package com.whatstatus;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.ListView;

import com.whatstatus.DAL.PeopleDAL;
import com.whatstatus.Models.People;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

/**
 * Created by User on 12/03/2017.
 */
public class Utils {
    public static ListAdapter inHouseAdapter = new ListAdapter(MainActivity.getInstance().getApplicationContext(), new LinkedList<ListItem>());
    public static ListAdapter outHouseAdapter = new ListAdapter(MainActivity.getInstance().getApplicationContext(), new LinkedList<ListItem>());

    public static void initializePeopleData() {
        new HttpRequest("getAbsenceList",
                null).execute(new HttpRequest.TaskListener() {
            @Override
            public void onFinished(String result) {
                PeopleDAL pDal = PeopleDAL.getInstance(MainActivity.getInstance().getApplicationContext());

                JSONArray peopleArr = null;
                try {
                    peopleArr = new JSONArray(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Remove all the absent people from the local db
                pDal.deleteAbsence();

                // Add all the people to the local database
                for (int pIndex = 0; peopleArr != null && pIndex < peopleArr.length(); pIndex++) {
                    try {
                        pDal.addPeople(new People(
                                peopleArr.getJSONObject(pIndex).getString("cardId"),
                                peopleArr.getJSONObject(pIndex).getString("cardNumber"),
                                peopleArr.getJSONObject(pIndex).getString("firstName"),
                                peopleArr.getJSONObject(pIndex).getString("lastName"),
                                peopleArr.getJSONObject(pIndex).getString("phoneNumber"),
                                peopleArr.getJSONObject(pIndex).getString("photo"),
                                peopleArr.getJSONObject(pIndex).getString("rank"),
                                peopleArr.getJSONObject(pIndex).getInt("isPresentAndSafe"),
                                peopleArr.getJSONObject(pIndex).getInt("isPresentGlobaly")
                        ));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

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
                        convertImageToSring(R.drawable.noimage)
                ));
            } else {
                inHouseList.add(new ListItem(
                        people.getFirstName() + " " + people.getLastName(),
                        convertImageToSring(R.drawable.noimage)
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

    public static boolean loginCommander(String cardId, String password) {
        HashMap<String, String> data = new HashMap<String, String>();

        data.put("cardId", cardId);
        data.put("password", password);

        try {
            String responseCode = new HttpRequest("loginCommander", data).execute().get();

            JSONObject responseObject = new JSONObject(responseCode);

            if (responseObject.getInt("loginCommander") == 200) {
                return (true);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return (false);
    }
}


