package com.whatstatus.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiConfiguration;
import android.util.Log;

import com.whatstatus.Models.People;

import java.util.ArrayList;

/**
 * Created by User on 10/03/2017.
 */
public class PeopleDAL {

    // Data Members
    public static PeopleDAL m_instance = null;
    private StatusHelper dbHelper;

    // C'tor
    private PeopleDAL(Context context) {
        dbHelper = StatusHelper.getInstance(context);
    }

    // Other Methods

    public static PeopleDAL getInstance(Context context) {
        if (m_instance == null) {
            m_instance = new PeopleDAL(context);
        }
        return m_instance;
    }

    public People getById(String cardId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                "cardId",
                "cardNumber",
                "firstName",
                "lastName",
                "phoneNumber",
                "photo",
                "rank",
                "isPresentAndSafe",
                "isPresentGlobaly",
        };

        // Create a cursor to iterate over the results
        Cursor cursor = db.query(
                "t_people",              // The table to query
                projection,              // The columns to return
                "cardId = ?",            // The columns for the WHERE clause
                new String[] { cardId }, // The values for the WHERE clause
                null,                    // don't group the rows
                null,                    // don't filter by row groups
                null                     // The sort order
        );

        cursor.moveToNext();

        return new People(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6),
                cursor.getInt(7),
                cursor.getInt(8)
        );
    }

    public ArrayList<People> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                "cardId",
                "cardNumber",
                "firstName",
                "lastName",
                "phoneNumber",
                "photo",
                "rank",
                "isPresentAndSafe",
                "isPresentGlobaly",
        };

        // Create a cursor to iterate over the results
        Cursor cursor = db.query(
            "t_people",              // The table to query
            projection,              // The columns to return
            null,                    // The columns for the WHERE clause
            null,                    // The values for the WHERE clause
            null,                    // don't group the rows
            null,                    // don't filter by row groups
            null                     // The sort order
        );

        // Initialize new people array
        ArrayList<People> peopleArr = new ArrayList<People>();

        // Add the results to the array
        while(cursor.moveToNext()) {
            peopleArr.add(new People(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getInt(7),
                    cursor.getInt(8)
            ));
        }

        // Close connections
        cursor.close();
        db.close();

        return peopleArr;
    }

    public void addPeople(People peopleToAdd) {
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put("cardId", peopleToAdd.getCardId());
        values.put("cardNumber", peopleToAdd.getCardNumber());
        values.put("firstName", peopleToAdd.getFirstName());
        values.put("lastName", peopleToAdd.getLastName());
        values.put("phoneNumber", peopleToAdd.getPhoneNumber());
        values.put("photo", peopleToAdd.getPhoto());
        values.put("rank", peopleToAdd.getRank());
        values.put("isPresentAndSafe", peopleToAdd.getIsPresentAndSafe());
        values.put("isPresentGlobaly", peopleToAdd.getIsPresentGlobaly());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert("t_people", null, values);

        // Close the db connection
        db.close();
    }

    public void deleteAbsence() {
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = "isPresentAndSafe = ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { "0" };

        // Issue SQL statement.
        db.delete("t_people", selection, selectionArgs);

        // Close the db connection
        db.close();
    }

    public void moveToPresent(String cardId) {
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("isPresentAndSafe", 1);

        // Define 'where' part of query.
        String selection = "cardId = ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { cardId };

        // Issue SQL statement.
        db.update("t_people", //table
                values, // column/value
                selection, // selections
                selectionArgs);

        // Close the db connection
        db.close();
    }

    public void deleteById(String cardId) {
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = "cardId = ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { cardId };

        // Issue SQL statement.
        db.delete("t_people", selection, selectionArgs);

        // Close the db connection
        db.close();

        Log.d("DBTest", "Deleted");
    }

    public void deleteAll() {
        // Gets the data repository in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Issue SQL statement.
        db.delete("t_people", null, null);

        // Close the db connection
        db.close();
    }
}
