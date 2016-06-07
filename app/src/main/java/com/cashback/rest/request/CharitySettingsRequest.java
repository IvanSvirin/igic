package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.model.AuthObject;
import com.cashback.model.CharitySettings;
import com.cashback.rest.event.LoginEvent;
import com.cashback.rest.event.SettingsEvent;

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

public class CharitySettingsRequest {
    private Context context;

    public CharitySettingsRequest(Context ctx) {
        this.context = ctx;
    }

    public void fetchData() {
        new CharitySettingsRequestTask().execute();
    }

    public void changeData(boolean isDonationNotifyOn, boolean isDealsNotifyOn) {
        new ChangeCharitySettingsRequestTask(isDonationNotifyOn, isDealsNotifyOn).execute();
    }

    private class CharitySettingsRequestTask extends AsyncTask<Void, Void, Void> {
        private String jsonString = "";
        private JSONArray jsonArray;
        private JSONObject jObj = null;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = new URL("https://beta1.igive.com/rest/iGive/api/v1/account/settings/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("IDFA", Utilities.retrieveIdfa(context));
                urlConnection.setRequestProperty("token", Utilities.retrieveUserToken(context));

                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                inputStream.close();
                urlConnection.disconnect();
                jsonString = sb.toString();
                while (jsonString.charAt(0) != '{' && jsonString.charAt(0) != '[') {
                    jsonString = jsonString.substring(1);
                }
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
            try {
                jsonArray = new JSONArray(jsonString);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (jsonArray != null) {
                    jObj = jsonArray.getJSONObject(0);
                    Utilities.setDonationNotify(context, jObj.getBoolean("donation_notify"));
                    Utilities.setDealsNotify(context, jObj.getBoolean("deals_notify"));
                    EventBus.getDefault().post(new SettingsEvent(true, null));
                } else {
                    EventBus.getDefault().post(new SettingsEvent(false, "Check your internet connection or authorization data"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ChangeCharitySettingsRequestTask extends AsyncTask<Void, Void, Void> {
        private AuthObject authObject;
        private String jsonString = "";
        private JSONArray jsonArray;
        private JSONObject jObj = null;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;
        private boolean isDonationNotifyOn;
        private boolean isDealsNotifyOn;

        public ChangeCharitySettingsRequestTask(boolean isDonationNotifyOn, boolean isDealsNotifyOn) {
            this.isDealsNotifyOn = isDealsNotifyOn;
            this.isDonationNotifyOn = isDonationNotifyOn;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = new URL("https://beta1.igive.com/rest/iGive/api/v1/account/settings/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("IDFA", Utilities.retrieveIdfa(context));
                urlConnection.setRequestProperty("token", Utilities.retrieveUserToken(context));

                String postParameters = "donation_notify=" + isDonationNotifyOn + "&deals_notify=" + isDealsNotifyOn;
                urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(postParameters);
                out.close();

                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                inputStream.close();
                urlConnection.disconnect();
                jsonString = sb.toString();
                while (jsonString.charAt(0) != '{' && jsonString.charAt(0) != '[') {
                    jsonString = jsonString.substring(1);
                }
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
            try {
                jsonArray = new JSONArray(jsonString);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (jsonArray != null) {
                    jObj = jsonArray.getJSONObject(0);
                    Utilities.setDonationNotify(context, jObj.getBoolean("donation_notify"));
                    Utilities.setDealsNotify(context, jObj.getBoolean("deals_notify"));
                    EventBus.getDefault().post(new SettingsEvent(true, null));
                } else {
                    EventBus.getDefault().post(new SettingsEvent(false, "Check your internet connection or authorization data"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
