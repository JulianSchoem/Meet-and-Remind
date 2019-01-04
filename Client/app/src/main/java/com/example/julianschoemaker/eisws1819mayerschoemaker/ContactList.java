package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

public class ContactList extends AppCompatActivity {

    private FloatingActionButton fbtn_AddContact;

    private ArrayList<ContactItem> contacts = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar1);
        mToolbar.setTitle(getString(R.string.app_name));
        fbtn_AddContact = findViewById(R.id.fab);
        fbtn_AddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactList.this, AddContact.class);
                startActivity(intent);
            }
        });

    }

}
