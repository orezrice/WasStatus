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


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Utils.loadList();
    }
}
