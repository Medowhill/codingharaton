package com.haraton.salad.codingharaton.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.haraton.salad.codingharaton.R;
import com.haraton.salad.codingharaton.utils.UbyteConverter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpTask extends AsyncTask<Byte, Void, Byte> {

    public static final int TASK_CLIENT = 0, TASK_SERVER = 1, TASK_CMD_GET = 2, TASK_CMD_POST = 3;
    private static final int[] ID_URL =
            new int[] { R.string.url_client, R.string.url_server, R.string.url_command_get, R.string.url_command_post };

    private Context mContext;
    private Callback mCallback;
    private int mTask;

    public abstract static class Callback {
        public abstract void onResult(byte result);
    }

    public HttpTask(Context context, int task, Callback callback) {
        mContext = context;
        mTask = task;
        mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Byte doInBackground(Byte... bytes) {
        HttpURLConnection connection = null;
        String urlStr = mContext.getString(ID_URL[mTask]), method;
        URL url;

        try {
            switch (mTask) {
                case TASK_CLIENT:
                    method = "GET";
                    if (bytes.length == 1 && bytes[0] >= 0)
                        url = new URL(String.format(urlStr, bytes[0]));
                    else
                        return -1;
                    break;
                case TASK_SERVER:
                    method = "GET";
                    if (bytes.length == 1)
                        url = new URL(String.format(urlStr, UbyteConverter.ubyteToInt(bytes[0])));
                    else
                        return -1;
                    break;
                case TASK_CMD_GET:
                    method = "GET";
                    if (bytes.length == 1 && bytes[0] >= 0)
                        url = new URL(String.format(urlStr, bytes[0]));
                    else
                        return -1;
                    break;
                case TASK_CMD_POST:
                    method = "POST";
                    if (bytes.length == 2 && 0 <= bytes[0] && 0 <= bytes[1] && bytes[1] <= 3)
                        url = new URL(String.format(urlStr, bytes[0], bytes[1]));
                    else
                        return -1;
                    break;
                default:
                    return -1;
            }

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoInput(true);
            connection.setDoOutput(false);

            BufferedInputStream stream = new BufferedInputStream(connection.getInputStream());
            byte[] buf = new byte[1];
            stream.read(buf);

            stream.close();
            return buf[0];
        } catch (MalformedURLException e) {
            Log.i("test-http", "mue", e);
        } catch (IOException e) {
            Log.i("test-http", "ioe", e);
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return -1;
    }

    @Override
    protected void onPostExecute(Byte aByte) {
        if (mCallback != null) mCallback.onResult(aByte);
    }
}
