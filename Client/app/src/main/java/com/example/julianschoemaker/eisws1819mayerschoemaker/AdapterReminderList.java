package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

class AdapterReminderList extends BaseAdapter {

    Context context;
    String[] data;
    String[] dataReminder;
    private static LayoutInflater inflater = null;

    public AdapterReminderList(Context context, String[] dataName, String[] dataReminder) {
        // Auto-generated constructor stub
        this.context = context;
        this.data = dataName;
        this.dataReminder = dataReminder;
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
            vi = inflater.inflate(R.layout.remind_item, null);
        TextView txt_contactName = (TextView) vi.findViewById(R.id.txt_contactName);
        TextView txt_contactReminder = vi.findViewById(R.id.txt_contactReminder);
        txt_contactName.setText(data[position]);
        txt_contactReminder.setText(dataReminder[position]);
        return vi;
    }
}
