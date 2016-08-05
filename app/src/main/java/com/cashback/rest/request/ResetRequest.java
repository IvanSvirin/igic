package com.cashback.rest.request;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.model.AuthObject;
import com.cashback.rest.event.RestoreEvent;
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

public class ResetRequest {
    private AuthObject authObject;
    private Context context;

    public ResetRequest(Context context, AuthObject authObject) {
        this.context = context;
        this.authObject = authObject;
    }

    public void doReset() {
        new ResetRequestTask(authObject).execute();
    }

    private class ResetRequestTask extends AsyncTask<Void, Void, Void> {
        private AuthObject authObject;
        private URL url;
        private String jsonString = "";
        private JSONObject jObj = null;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;

        public ResetRequestTask(AuthObject authObject) {
            this.authObject = authObject;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                url = new URL(context.getString(R.string.reset_path));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("IDFA", Utilities.retrieveIdfa(context));
                String postParameters;
                postParameters = "email=" + authObject.getEmail();
                urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(postParameters);
                out.close();

                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new RestoreEvent(false, ""));
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                inputStream.close();
                urlConnection.disconnect();
                jsonString = sb.toString();
                while (jsonString.charAt(0) != '{' && jsonString.charAt(0) != '[') {
                    jsonString = jsonString.substring(1);
                }
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
                EventBus.getDefault().post(new RestoreEvent(false, ""));
            }
            try {
                jObj = new JSONObject(jsonString);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
                EventBus.getDefault().post(new RestoreEvent(false, ""));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (jObj != null) {
                    int status = jObj.getInt("status");
                    if (status == 1) {
                        EventBus.getDefault().post(new RestoreEvent(true, ""));
                    } else {
                        EventBus.getDefault().post(new RestoreEvent(false, ""));
                    }
                } else {
                    EventBus.getDefault().post(new RestoreEvent(false, ""));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new RestoreEvent(false, ""));
            }
        }
    }
}
