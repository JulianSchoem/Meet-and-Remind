package com.example.julianschoemaker.eisws1819mayerschoemaker;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

//Eventuell ändern zu BroadcastReceiver, bei mehreren Activities...
@SuppressLint("ParcelCreator")
public class MessageReceiver extends ResultReceiver {
    private ContactList.Message message;

    public MessageReceiver(ContactList.Message message) {
        super(new Handler());
        this.message = message;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        message.displayMessage(resultCode, resultData);
    }
}
