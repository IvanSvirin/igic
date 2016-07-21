package com.cashback.rest.request;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cashback.Utilities;
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

    public void changeData(boolean isNewNotifyOn, boolean isPurchaseNotifyOn) {
        new ChangeCharitySettingsRequestTask(isNewNotifyOn, isPurchaseNotifyOn).execute();
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
                    Utilities.setPurchaseNotify(context, jObj.getBoolean("purchase_notify"));
                    Utilities.setWeeklyNewsNotify(context, jObj.getBoolean("weekly_news_notify"));
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
        private String jsonString = "";
        private JSONArray jsonArray;
        private JSONObject jObj = null;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;
        private boolean isNewsNotifyOn;
        private boolean isPurchaseNotifyOn;

        public ChangeCharitySettingsRequestTask(boolean isNewsNotifyOn, boolean isPurchaseNotifyOn) {
            this.isNewsNotifyOn = isNewsNotifyOn;
            this.isPurchaseNotifyOn = isPurchaseNotifyOn;
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

                String postParameters = "purchase_notify=" + isPurchaseNotifyOn + "&weekly_news_notify=" + isNewsNotifyOn;
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
                    Utilities.setPurchaseNotify(context, jObj.getBoolean("purchase_notify"));
                    Utilities.setWeeklyNewsNotify(context, jObj.getBoolean("weekly_news_notify"));
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
