package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class ReminderDetail extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Toolbar mToolbar;

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

    }
}
