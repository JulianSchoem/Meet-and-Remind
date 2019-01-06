package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ContactDetail extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static String BlueID = "BTID";

    private FloatingActionButton fbtn_AddReminder;

    private ListView listview_reminderList;

    ImageView img_delete;
    FrameLayout fl_touch_area;

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
                intent.putExtra("ERSTELLEN", "Erinnerung erstellen");
                startActivity(intent);
            }
        });

        fbtn_AddReminder = findViewById(R.id.fabReminder);
        listview_reminderList = findViewById(R.id.listview_reminderlist);
        listview_reminderList.setAdapter(new AdapterReminderList(this, new String[]{"Essen",
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
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

        fl_touch_area = view.findViewById(R.id.touch_area_delete);
        fl_touch_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 AlertDialog.Builder dialog = new AlertDialog.Builder(ContactDetail.this);
                 dialog.setCancelable(false);
                 dialog.setTitle("Erinnerung löschen?");
                 dialog.setMessage("Bist du Dir sicher, dass Du die Erinnerung endgültig löschen möchtest? " +
                 "Du wirst keine Benachrichtigung mehr zu dieser Erinnerung erhalten. " );
                 dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                //Action for "cancel".
                }

                })
                 .setNegativeButton("Delete ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO Action for "Delete".
                }
                });

                 final AlertDialog alert = dialog.create();
                 alert.show();

            }
        });
        Intent activityIntent = new Intent(ContactDetail.this, ReminderDetail.class);
        String reminderName = listview_reminderList.getAdapter().getItem(position).toString();
        activityIntent.putExtra("RNAME", reminderName);
        startActivity(activityIntent);
    }
}
