package com.whatstatus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Or Shachar on 09/03/2017.
 */
public class ListAdapter extends ArrayAdapter<ListItem> {
    private final Context context;
    private final List<ListItem> values;

    public ListAdapter(Context context, List<ListItem> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Load list item
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listviewitem, parent, false);

        // load image and textview
        TextView textView = (TextView) rowView.findViewById(R.id.personname);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.personimage);
        textView.setText(values.get(position).name);

        // load decoded image
        byte[] decodedString = Base64.decode(values.get(position).base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageView.setImageBitmap(decodedByte);


        return rowView;
    }
}
