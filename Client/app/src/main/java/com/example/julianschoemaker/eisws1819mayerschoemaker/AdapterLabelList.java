package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Label List Adapter for Listviews of Topics with attributes "name" and ImageView "check"
 */
public class AdapterLabelList extends BaseAdapter {

    Context context;
    String[] data;
    private static LayoutInflater inflater = null;

    public AdapterLabelList(Context context, String[] data) {
        // Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // Auto-generated method stub
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        // Auto-generated method stub
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        // Auto-generated method stub
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.label_item, null);
        if (position % 2 == 1) {
            vi.setBackgroundColor(Color.WHITE);
        } else {
            vi.setBackgroundResource(R.color.colorListGrey);
        }
        TextView txt_labelName = vi.findViewById(R.id.txt_labelName);
        txt_labelName.setText(data[position]);

        return vi;
    }

}
