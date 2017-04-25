package com.example.jonat.rajawaliwithfragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.alicebot.ab.Timer;
import org.rajawali3d.renderer.ISurfaceRenderer;
import org.rajawali3d.view.IDisplay;
import org.rajawali3d.view.ISurface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

import AsyncTask.AsyncTaskMary;
import Pojo.ChatMessage;
import Visemes.Visemes;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements IDisplay, AsyncTaskMary.MoveCompleteListener{

    public static final String TAG = "MainFragment";

    protected FrameLayout frameLayout;
    protected ISurface iSurface;
    protected ISurfaceRenderer iSurfaceRenderer;
    private MainRenderer mainRenderer;

    private TextView tvMyMessage;
    private TextView tvComputerMessage;
    private FloatingActionButton fabMessageRecognition;
    private FloatingActionButton fabChat;
    private Button btnPlay;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    private ArrayList<ChatMessage> chatMessages;
    private String response;

    public static final String OUTPUT_TYPE_AUDIO = "AUDIO";
    public static final String OUTPUT_TYPE_ACOUSTPARAMS = "ACOUSTPARAMS";

//    private final String BASE_URL = "http://192.168.0.113:59125/";
    private final String BASE_URL = "http://mary.dfki.de:59125/";
    private final String INPUT_TYPE = "TEXT";
    private final String LOCALE = "en_US";
    private  String inputText = "how are you mother?";
    private final String VOICE = "cmu-bdl";
    private final String AUDIO = "WAVE_FILE";

    private AsyncTaskMary asyncTaskMary;

    public Bot bot;
    public static Chat chat;

    MessagesList messagesList;

    public MainActivityFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            messagesList = (MessagesList) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement TextClicked");
        }

        chatMessages = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        chatBot();

        mainRenderer = new MainRenderer(getContext());
        frameLayout = (FrameLayout) inflater.inflate(getLayoutID(), container, false);

        frameLayout.findViewById(R.id.relative_layout_loader_container);

        tvMyMessage = (TextView) frameLayout.findViewById(R.id.tv_my_message);
        tvComputerMessage = (TextView) frameLayout.findViewById(R.id.tv_computer_message);
        fabMessageRecognition = (FloatingActionButton) frameLayout.findViewById(R.id.fab_voiceRecognition);
        fabChat = (FloatingActionButton) frameLayout.findViewById(R.id.btnChangeFragment);
        btnPlay = (Button) frameLayout.findViewById(R.id.btnPlay);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainRenderer.play();
            }
        });

        fabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                ChatFragment chatFragment = (ChatFragment) fm.findFragmentByTag(ChatFragment.TAG);
                if (chatFragment == null){
                    chatFragment = new ChatFragment();
                }
                ft.replace(R.id.content_frame, chatFragment, ChatFragment.TAG);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        fabMessageRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection())
                    promptSpeechInput();
            }
        });

        if (chatMessages.size() >= 2){
            tvMyMessage.setText(chatMessages.get(chatMessages.size()-2).getContent());
            tvMyMessage.setVisibility(View.VISIBLE);
            tvComputerMessage.setText(chatMessages.get(chatMessages.size()-1).getContent());
            tvComputerMessage.setVisibility(View.VISIBLE);
        }

        iSurface = (ISurface) frameLayout.findViewById(R.id.rajwali_surface);
        iSurfaceRenderer = createRenderer();
        iSurface.setSurfaceRenderer(iSurfaceRenderer);
        return frameLayout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        messagesList.sendMessages(chatMessages);
    }

    @Override
    public ISurfaceRenderer createRenderer() {
        return mainRenderer;
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
        /*intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);*/
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.UK.toString());
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
                    String mySentence = result.get(0);
                    chatMessages.add(new ChatMessage(result.get(0),true, false));
                    tvMyMessage.setText(result.get(0));
                    if (tvMyMessage.getVisibility() == View.INVISIBLE)
                        tvMyMessage.setVisibility(View.VISIBLE);

                    response = chat.multisentenceRespond(mySentence);
//                    response = "Hi it's great to see you!";
//                    response = "Hi! I can really feel your smile today!";
//                    response = "Hi it's delightful to see you!";
                    chatMessages.add(new ChatMessage(response,false,false));
                    tvComputerMessage.setText(response);
                    if (tvComputerMessage.getVisibility() == View.INVISIBLE)
                        tvComputerMessage.setVisibility(View.VISIBLE);

                    if (checkInternetConnection()) {
                        inputText = response;
                        String url = getURL(inputText, OUTPUT_TYPE_AUDIO);
                        asyncTaskMary = new AsyncTaskMary(getActivity(), MainActivityFragment.this, OUTPUT_TYPE_AUDIO);
                        asyncTaskMary.execute(url);

                        url = getURL(inputText, OUTPUT_TYPE_ACOUSTPARAMS);
                        asyncTaskMary = new AsyncTaskMary(getActivity(), MainActivityFragment.this, OUTPUT_TYPE_ACOUSTPARAMS);
                        asyncTaskMary.execute(url);
                    }
                }
                break;
            }
        }
    }

    public String getURL(String inputText, String outputType){
        StringBuilder getURL = new StringBuilder(BASE_URL);

        getURL.append("process?INPUT_TYPE=" + INPUT_TYPE);
        getURL.append("&OUTPUT_TYPE=" + outputType);
        getURL.append("&LOCALE=" + LOCALE);
        getURL.append("&INPUT_TEXT=");
        try {
            getURL.append(URLEncoder.encode(inputText, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        getURL.append("&VOICE=" + VOICE);
        getURL.append("&AUDIO=" + AUDIO);

        return getURL.toString();
    }

    public void chatBot(){
        //checking SD card availablility
        boolean a = isSDCARDAvailable();
        //receiving the assets from the app directory
        AssetManager assets = getResources().getAssets();
        File jayDir = new File(Environment.getExternalStorageDirectory().toString() + "/hari/bots/Hari");
        boolean b = jayDir.mkdirs();
        if (jayDir.exists()) {
            //Reading the file
            try {
                for (String dir : assets.list("Hari")) {
                    File subdir = new File(jayDir.getPath() + "/" + dir);
                    boolean subdir_check = subdir.mkdirs();
                    for (String file : assets.list("Hari/" + dir)) {
                        File f = new File(jayDir.getPath() + "/" + dir + "/" + file);
                        if (f.exists()) {
                            continue;
                        }
                        InputStream in = null;
                        OutputStream out = null;
                        in = assets.open("Hari/" + dir + "/" + file);
                        out = new FileOutputStream(jayDir.getPath() + "/" + dir + "/" + file);
                        //copy file from assets to the mobile's SD card or any secondary memory
                        copyFile(in, out);
                        in.close();
                        in = null;
                        out.flush();
                        out.close();
                        out = null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //get the working directory
        MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/hari";
        System.out.println("Working Directory = " + MagicStrings.root_path);
        AIMLProcessor.extension =  new PCAIMLProcessorExtension();
        //Assign the AIML files to bot for processing
        bot = new Bot("Hari", MagicStrings.root_path, "chat");
        chat = new Chat(bot);
        String[] args = null;
        mainFunction(args);
    }

    //check SD card availability
    public static boolean isSDCARDAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)? true :false;
    }
    //copying the file
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    //Request and response of user and the bot
    public static void mainFunction (String[] args) {
        MagicBooleans.trace_mode = false;
        System.out.println("trace mode = " + MagicBooleans.trace_mode);
        Graphmaster.enableShortCuts = true;
        Timer timer = new Timer();
        String request = "Hello.";
        String response = chat.multisentenceRespond(request);

        System.out.println("Human: "+request);
        System.out.println("Robot: " + response);
    }

    public boolean checkInternetConnection(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null){
            Toast.makeText(getContext(), "No Internet connection", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void OnTaskComplete(String aResult, String outputType) {
        Toast.makeText(getContext(), aResult, Toast.LENGTH_LONG).show();

        if (outputType == OUTPUT_TYPE_AUDIO) {
            File cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "Mary/proba.mp3");
            MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), Uri.fromFile(cacheDir));
            mediaPlayer.start();
        }

        if (outputType == OUTPUT_TYPE_ACOUSTPARAMS) {
            Visemes visemes = new Visemes(getContext(), mainRenderer.fapUtil, response);
        }

        /*Thread thread1 = new Thread(new PlayAudio(getContext(), outputType));
        thread1.start();*/
    }

    @Override
    public void onError(String aError) {
        Toast.makeText(getContext(), aError, Toast.LENGTH_LONG).show();
    }

    public interface MessagesList {
        public void sendMessages(ArrayList<ChatMessage> messages);
    }
}
