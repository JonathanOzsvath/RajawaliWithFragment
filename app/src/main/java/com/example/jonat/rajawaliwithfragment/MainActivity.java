package com.example.jonat.rajawaliwithfragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

import Pojo.ChatMessage;

public class MainActivity extends FragmentActivity implements MainActivityFragment.MessagesList {

    private MainActivityFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (fragment == null){
            fragment = new MainActivityFragment();
        }

        transaction.replace(R.id.content_frame, fragment, fragment.TAG);
        transaction.commit();
    }

    @Override
    public void sendMessages(ArrayList<ChatMessage> messages) {
        ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(ChatFragment.TAG);
        chatFragment.getMessagesList(messages);
    }
}
