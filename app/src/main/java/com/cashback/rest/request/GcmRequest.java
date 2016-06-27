package com.cashback.rest.request;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.rest.event.FavoritesEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import de.greenrobot.event.EventBus;

public class GcmRequest {
    private Context context;

    public GcmRequest(Context ctx) {
        this.context = ctx;
    }

    public void onGcm(String gcmToken) {
        new OnGcmTask(gcmToken).execute();
    }

    public void offGcm(String gcmToken) {
        new OffGcmTask(gcmToken).execute();
    }

    private class OnGcmTask extends AsyncTask<Void, Void, Void> {
        private String gcmToken;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;

        public OnGcmTask(String gcmToken) {
            this.gcmToken = gcmToken;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = new URL(context.getString(R.string.gcm_path));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("IDFA", Utilities.retrieveIdfa(context));
                urlConnection.setRequestProperty("token", Utilities.retrieveUserToken(context));
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                String postParameters;
                postParameters = "token_gcm=" + gcmToken;
                urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(postParameters);
                out.close();

                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class OffGcmTask extends AsyncTask<Void, Void, Void> {
        private String gcmToken;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;

        public OffGcmTask(String gcmToken) {
            this.gcmToken = gcmToken;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = new URL(context.getString(R.string.gcm_path));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("IDFA", Utilities.retrieveIdfa(context));
                urlConnection.setRequestProperty("token", Utilities.retrieveUserToken(context));
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("DELETE");
                String postParameters;
                postParameters = "token_gcm=" + gcmToken;
                urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(postParameters);
                out.close();

                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
