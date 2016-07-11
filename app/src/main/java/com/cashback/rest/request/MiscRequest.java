package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.db.DataInsertHandler;
import com.cashback.model.Merchant;
import com.cashback.model.Misc;
import com.cashback.rest.event.CategoryMerchantsEvent;
import com.cashback.rest.event.MiscEvent;
import com.cashback.rest.event.SignInEvent;
import com.cashback.rest.event.SignUpEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import db.DataContract;
import de.greenrobot.event.EventBus;

public class MiscRequest {
    private Context context;

    public MiscRequest(Context ctx) {
        this.context = ctx;
    }

    public void fetchData() {
        new MiscRequestTask().execute();
    }

    private class MiscRequestTask extends AsyncTask<Void, Void, Void> {
        private String jsonString = "";
        JSONArray jsonArray;
        private JSONObject jObj = null;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = new URL("https://beta1.iconsumer.com/rest/icon/api/v1/misc/");
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
                jObj = new JSONObject(jsonString);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (jObj != null) {
                    ContentValues values = new ContentValues();
                    String shareDealText = jObj.getString("share_deal_text");
                    Utilities.saveShareDealText(context, shareDealText);
                    values.put(DataContract.Misc.COLUMN_SHARE_DEAL_TEXT, shareDealText);
                    values.put(DataContract.Misc.COLUMN_TELL_A_FRIEND_TEXT, jObj.getString("tell_a_frined_text"));
                    values.put(DataContract.Misc.COLUMN_TELL_A_FRIEND_BANNER_URL, jObj.getString("tell_a_frined_banner_url"));
                    DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                    handler.startInsert(DataInsertHandler.MISC_TOKEN, null, DataContract.URI_MISC, values);
                    EventBus.getDefault().post(new MiscEvent(true, null));
                } else {
                    EventBus.getDefault().post(new MiscEvent(false, null));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
