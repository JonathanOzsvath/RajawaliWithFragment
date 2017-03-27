package AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.jonat.rajawaliwithfragment.MainActivityFragment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jonat on 2017. 03. 20..
 */

public class AsyncTaskMary extends AsyncTask<String, Void, String> {

    public interface MoveCompleteListener {
        public void OnTaskComplete(String aResult, String outputType);

        public void onError(String aError);
    }

    private Context context;
    private ProgressDialog pd = null;
    private MoveCompleteListener listener;
    private String error = null;
    private String progress = null;
    private String outputType;

    public AsyncTaskMary(Context context, MoveCompleteListener listener, String outputType) {
        this.context = context;
        this.listener = listener;
        this.outputType = outputType;
    }

    @Override
    protected void onPreExecute() {
        pd = new ProgressDialog(context);
        progress = "Please wait!";
        pd.setMessage(progress);
        pd.show();
    }

    @Override
    protected String doInBackground(String... params) {
        File cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), "Mary");
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        if (outputType == MainActivityFragment.OUTPUT_TYPE_AUDIO) {
            try {
                File f = new File(cacheDir, "proba.mp3");
                URL url = new URL(params[0]);

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(f);

                byte data[] = new byte[1024];
                long total = 0;
                int count = 0;
                while ((count = input.read(data)) != -1) {
                    total++;
                    Log.e("while", "A" + total);

                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                error = "Mary server not found!";
                e.printStackTrace();
            }
        }else {
            File f = new File(cacheDir, "proba.txt");
            try {
                URL url = new URL(params[0]);

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(f);
                StringBuilder sb = new StringBuilder();
                int ch;
                while ((ch = input.read()) != -1){
                    sb.append((char)ch);
                    output.write((char)ch);
                }

                System.out.println(sb.toString());
                output.flush();
                output.close();
                input.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                error = "Mary server not found!";
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        progress += values[0];
        pd.setMessage(progress);
    }

    @Override
    protected void onPostExecute(String s) {
        pd.dismiss();
        if (error != null){
            listener.onError(error);
        }else {
            listener.OnTaskComplete("Ready", outputType);
        }
    }
}
