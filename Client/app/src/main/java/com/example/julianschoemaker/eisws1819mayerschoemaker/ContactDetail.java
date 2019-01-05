package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class ContactDetail extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static String BlueID = "BTID";

    private FloatingActionButton fbtn_AddReminder;

    private ListView listview_reminderList;

    Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        mToolbar = (Toolbar) findViewById(R.id.toolbarDetailC);
        mToolbar.setTitle("Name");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fbtn_AddReminder = findViewById(R.id.fabReminder);
        fbtn_AddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactDetail.this, ReminderDetail.class);
                startActivity(intent);
            }
        });

        fbtn_AddReminder = findViewById(R.id.fabReminder);
        listview_reminderList = findViewById(R.id.listview_reminderlist);
        listview_reminderList.setAdapter(new AdapterContactList(this, new String[]{"Essen",
                "Geld", "Film gucken", "Film zurückgeben"}));

        listview_reminderList.setOnItemClickListener(ContactDetail.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BlueID = getIntent().getExtras().getString(BlueID);
        mToolbar.setTitle(BlueID);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent activityIntent = new Intent(ContactDetail.this, ReminderDetail.class);
        String reminderName = listview_reminderList.getAdapter().getItem(position).toString();
        activityIntent.putExtra("RNAME", reminderName);
        startActivity(activityIntent);
    }
}
