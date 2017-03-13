package com.whatstatus;

import android.os.AsyncTask;
import android.util.Log;

import com.whatstatus.DAL.PeopleDAL;
import com.whatstatus.Models.People;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class initializeAsync extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        PeopleDAL pDal = PeopleDAL.getInstance(MainActivity.getInstance().getApplicationContext());
        String peopleJSON = "";

        // Try to get people list from the server api
        try {
            Log.d("Testt", "dsadsa");
            peopleJSON = new HttpRequest("getAbsenceList",
                    null, "http://socialchat.16mb.com/api.php").execute().get();

            JSONArray peopleArr = new JSONArray(peopleJSON);

            // Remove all the absent people from the local db
            pDal.deleteAbsence();

            // Add all the people to the local database
            for (int pIndex = 0; pIndex < peopleArr.length(); pIndex++) {
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
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Utils.loadList();
    }
}
