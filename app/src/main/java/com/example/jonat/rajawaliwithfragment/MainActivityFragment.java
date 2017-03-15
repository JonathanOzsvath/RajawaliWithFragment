package com.example.jonat.rajawaliwithfragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.rajawali3d.renderer.ISurfaceRenderer;
import org.rajawali3d.view.IDisplay;
import org.rajawali3d.view.ISurface;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements IDisplay{

    public static final String TAG = "Fragment";

    protected FrameLayout frameLayout;
    protected ISurface iSurface;
    protected ISurfaceRenderer iSurfaceRenderer;

    private TextView tvMessage;
    private FloatingActionButton fabMessageRecognition;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        frameLayout = (FrameLayout) inflater.inflate(getLayoutID(), container, false);

        frameLayout.findViewById(R.id.relative_layout_loader_container);

        tvMessage = (TextView) frameLayout.findViewById(R.id.tv_message);
        fabMessageRecognition = (FloatingActionButton) frameLayout.findViewById(R.id.fab_voiceRecognition);

        fabMessageRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        iSurface = (ISurface) frameLayout.findViewById(R.id.rajwali_surface);
        iSurfaceRenderer = createRenderer();
        iSurface.setSurfaceRenderer(iSurfaceRenderer);
        return frameLayout;
    }

    @Override
    public ISurfaceRenderer createRenderer() {
        return new MainRenderer(getActivity());
    }

    /*Fragmenthez tartozó xml*/
    @Override
    public int getLayoutID() {
        return R.layout.fragment_main;
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvMessage.setText(result.get(0));
                    tvMessage.setVisibility(View.VISIBLE);
                }
                break;
            }

        }
    }
}
