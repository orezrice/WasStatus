package com.whatstatus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.whatstatus.Models.People;

import java.util.List;

/**
 * Created by Or Shachar on 09/03/2017.
 */
public class ListAdapter extends ArrayAdapter<People> {
    private final Context context;
    private final List<People> values;

    public ListAdapter(Context context, List<People> values) {
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
        textView.setText(values.get(position).getFirstName() + " " + values.get(position).getLastName());


        // load decoded image
        byte[] decodedString = Base64.decode(values.get(position).getPhoto(), Base64.DEFAULT);

        Bitmap mbitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageView.setImageBitmap(Utils.makeImageRounded(mbitmap));
/*
        Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
        Canvas canvas = new Canvas(imageRounded);
        Paint mpaint = new Paint();
        mpaint.setAntiAlias(true);
        mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), mbitmap.getWidth(), mbitmap.getHeight(), mpaint);// Round Image Corner 100 100 100 100
        imageView.setImageBitmap(imageRounded);*/
        return rowView;
    }

    @Override
    public People getItem(int position) {
        return values.get(position);
    }
}
