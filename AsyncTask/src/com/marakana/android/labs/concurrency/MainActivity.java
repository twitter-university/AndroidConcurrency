package com.marakana.android.labs.concurrency;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;
import android.graphics.Color;

import com.marakana.android.labs.concurrency.client.NetworkClient;


public class MainActivity extends Activity {
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_FAIL = -1;

    private static final String TAG = "MAIN";

    static class Poster extends AsyncTask<String, Void, Integer> {
        private final MainActivity ctxt;

        public Poster(MainActivity ctxt) { this.ctxt = ctxt; }

        @Override
        protected Integer doInBackground(String... args) {
            return Integer.valueOf((NetworkClient.getNetClient().post(args[0]))
                    ? STATUS_SUCCESS : STATUS_FAIL);
        }

        @Override
        protected void onCancelled() {
            Log.d(TAG, "posting cancelled");
            ctxt.postComplete(Integer.valueOf(STATUS_FAIL));
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.d(TAG, "posting finished");
            ctxt.postComplete(result);
        }
    }


    private View status;
    private EditText postText;
    private Poster poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postText = (EditText) findViewById(R.id.mainText);
        status = findViewById(R.id.mainStatus);

        findViewById(R.id.mainButton).setOnClickListener(
                new Button.OnClickListener() {
                    @Override public void onClick(View v) { post(); }
                } );

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != poster) { poster.cancel(true); }
    }

    void post() {
        if (null != poster) { return; }

        String text = postText.getText().toString();
        if (TextUtils.isEmpty(text)) { return; }

        postText.setText("");
        status.setBackgroundColor(Color.YELLOW);

        Log.d(TAG, "posting: " + text);
        poster = new Poster(this);
        poster.execute(text);
    }

    void postComplete(Integer ret) {
        Log.d(TAG, "post complete");
        status.setBackgroundColor((0 <= ret.intValue()) ? Color.GREEN : Color.RED);
        poster = null;

    }
}
