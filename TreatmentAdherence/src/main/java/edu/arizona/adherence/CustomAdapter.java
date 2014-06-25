package edu.arizona.adherence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sibelius on 6/24/14.
 */
public class CustomAdapter extends ArrayAdapter<ImageText> {

    private LayoutInflater mInflater;
    private ArrayList<ImageText> data;

    public CustomAdapter(
            Context context,
            int resourceId,
            ArrayList<ImageText> objects) {
        super(context, resourceId, objects);

        this.data = objects;

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // This funtion called for each row ( Called data.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = mInflater.inflate(R.layout.spinner_rows, parent, false);

        ImageText tempValues = data.get(position);

        ImageView img = (ImageView) row.findViewById(R.id.image);
        TextView description = (TextView) row.findViewById(R.id.description);

        img.setImageResource(tempValues.getImageId());
        description.setText(tempValues.getDescription());

        return row;
    }
}
