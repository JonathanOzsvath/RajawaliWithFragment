package com.example.jonat.rajawaliwithfragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import Adapter.ChatMessageAdapter;
import Pojo.ChatMessage;


public class ChatFragment extends Fragment {

    public static final String TAG = "ChatFragment";

    private ListView mListView;
    private ChatMessageAdapter mAdapter;
    private ArrayList<ChatMessage> chatMessages;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = View.inflate(getActivity(), R.layout.fragment_chat, null);

        mListView = (ListView) v.findViewById(R.id.listView);
        mAdapter = new ChatMessageAdapter(getActivity(), chatMessages);
        mListView.setAdapter(mAdapter);
        return v;
    }

    private void myMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);
    }

    private void computerMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }

    public void getMessagesList(ArrayList<ChatMessage> messages){
        chatMessages = messages;
    }
}
