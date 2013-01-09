package com.marakana.android.labs.concurrency.svc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
public class PostingService extends Service {
    private static final String TAG = "POST_SVC";

    Poster.PostHandler postHandler;
    private Thread looper;

    @Override
    public void onCreate() {
        super.onCreate();
        looper = new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                postHandler = new Poster.PostHandler();
                Looper.loop();
            }
        };
        looper.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "bound");
        return new Poster(postHandler);
    }
}
