package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class ReminderDetail extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Toolbar mToolbar;
    Button btn_info;

    ListView listview_labelList;

    FloatingActionButton fabCheck;
    boolean oneIsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_detail);

        mToolbar = findViewById(R.id.toolbarDetailR);
        mToolbar.setTitle("Erinnerung bearbeiten");
        mToolbar.setTitleTextColor(0xFFFFFFFF);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_info = findViewById(R.id.btn_info);
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ReminderDetail.this);
                dialog.setCancelable(false);
                dialog.setTitle("Thema einer Erinnerung");
                dialog.setMessage("Du kannst Erinnerungen ein Thema zuweisen. " +
                        "Mit diesen Themen kann dir das System Themenvorschläge liefern, " +
                        "die dir im Gespräch weiterhelfen können. " +
                        "Der Themenvorschlag erscheint als blaues Symbol bei der Kontaktliste" );
                dialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Action for "cancel".
                    }
                });

                final AlertDialog alert = dialog.create();
                alert.show();
            }
        });

        fabCheck = findViewById(R.id.fabCheck);
        fabCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO ERINNERUNG SPEICHERN
                onBackPressed();
            }
        });

        listview_labelList = findViewById(R.id.listview_labelList);

        listview_labelList.setAdapter(new AdapterLabelList(this, new String[]{"Food Trends",
                "Politik", "Sport", "Klima", "Charts", "Schule", "Studium", "Beruf", "Tiere", "Wissenschaft"}));


        listview_labelList.setOnItemClickListener(ReminderDetail.this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ImageView img_check = view.findViewById(R.id.img_check);

        if (img_check.getVisibility() == View.VISIBLE) {
            if ( oneIsChecked == true) {
                img_check.setVisibility(View.INVISIBLE);
                oneIsChecked = false;
            }
        } else {
            if ( oneIsChecked == false) {
                img_check.setVisibility(View.VISIBLE);
                oneIsChecked = true;
            }
        }

        //TODO Label für Themenvorschlag beim Speichern übergeben
        // TODO BUG: wenn Beschreibung länger, in Liste 2 Häkchen...

    }
}
