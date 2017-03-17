package com.example.jonat.rajawaliwithfragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ChatFragment extends Fragment {

    public static final String TAG = "FragmentTwo";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = View.inflate(getActivity(), R.layout.fragment_chat, null);
        return v;
    }


}
