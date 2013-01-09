package com.marakana.android.labs.concurrency.svc;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.marakana.android.labs.concurrency.MainActivity;
import com.marakana.android.labs.concurrency.client.NetworkClient;


/**
 *
 * @version $Revision: $
 * @author <a href="mailto:blake.meike@gmail.com">G. Blake Meike</a>
 */
class Poster extends IPostingService.Stub {
    private static final String TAG = "POSTER";

    private static final int POST_MESSAGE_ID = 42;

    static class PostHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
            case POST_MESSAGE_ID:
                Log.d(TAG, "post message");
                try {
                    PostArgs args = (PostArgs) message.obj;
                    args.hdlr.postCompleted((NetworkClient.getNetClient().post(args.text))
                            ? MainActivity.STATUS_SUCCESS
                            : MainActivity.STATUS_FAIL);
                }
                catch (RemoteException e) {
                    Log.d(TAG, "failed delivering response");
                }
                break;
            }
        }
    };

    private static class PostArgs {
        public final String text;
        public final IPostCompletionHandler hdlr;
        public PostArgs(String text, IPostCompletionHandler hdlr) {
            this.text = text;
            this.hdlr = hdlr;
        }
    }

    private PostHandler postHandler;

    public Poster(PostHandler postHandler) { this.postHandler = postHandler; }

    @Override
    public void post(final String text, final IPostCompletionHandler hdlr) {
        Log.d(TAG, "posting: " + text);
        postHandler.sendMessage(postHandler.obtainMessage(
                POST_MESSAGE_ID,
                new PostArgs(text, hdlr)));
   }
}