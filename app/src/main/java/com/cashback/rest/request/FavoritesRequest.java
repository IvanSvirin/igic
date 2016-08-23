package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cashback.R;
import com.cashback.Utilities;

import db.DataContract;

import com.cashback.db.DataInsertHandler;
import com.cashback.rest.event.FavoritesEvent;

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
import java.util.List;

import de.greenrobot.event.EventBus;

public class FavoritesRequest {
    private Context context;

    public FavoritesRequest(Context ctx) {
        this.context = ctx;
    }

    public void fetchData() {
        new FavoritesRequestTask().execute();
    }

    public void addMerchant(long id) {
        new AddMerchantTask(id).execute();
    }

    public void deleteMerchant(long id) {
        new DeleteMerchantTask(id).execute();
    }

    private class FavoritesRequestTask extends AsyncTask<Void, Void, Void> {
        private String jsonString = "";
        private JSONArray jsonArray;
        private JSONObject jObj = null;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = new URL(context.getString(R.string.favorites_path));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("IDFA", Utilities.retrieveIdfa(context));
                urlConnection.setRequestProperty("token", Utilities.retrieveUserToken(context));

                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new FavoritesEvent(false, "No merchants featured data"));
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
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
                EventBus.getDefault().post(new FavoritesEvent(false, "No merchants featured data"));
            }
            try {
                jsonArray = new JSONArray(jsonString);
            } catch (JSONException e) {
                EventBus.getDefault().post(new FavoritesEvent(false, "No merchants featured data"));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (jsonArray != null) {
                List<ContentValues> listValues = new ArrayList<>(jsonArray.length());
                ContentValues values;
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jObj = jsonArray.getJSONObject(i);
                        values = new ContentValues();
                        values.put(DataContract.Favorites.COLUMN_COMMISSION, jObj.getDouble("commission"));
                        values.put(DataContract.Favorites.COLUMN_AFFILIATE_URL, jObj.getString("affiliate_url"));
                        values.put(DataContract.Favorites.COLUMN_IS_FAVORITE, jObj.getInt("is_favorite"));
                        values.put(DataContract.Favorites.COLUMN_DESCRIPTION, jObj.getString("description"));
                        values.put(DataContract.Favorites.COLUMN_LOGO_URL, jObj.getString("logo_url"));
                        values.put(DataContract.Favorites.COLUMN_GIFT_CARD, jObj.getInt("gift_card"));
                        values.put(DataContract.Favorites.COLUMN_EXCEPTION_INFO, jObj.getString("exception_info"));
                        values.put(DataContract.Favorites.COLUMN_VENDOR_ID, jObj.getLong("vendor_id"));
                        values.put(DataContract.Favorites.COLUMN_NAME, jObj.getString("name"));
                        if (jObj.has("owners_benefit")) {
                            if (jObj.getBoolean("owners_benefit")) {
                                values.put(DataContract.Favorites.COLUMN_OWNERS_BENEFIT, 1);
                            }
                        } else {
                            values.put(DataContract.Favorites.COLUMN_OWNERS_BENEFIT, 0);
                        }
                        listValues.add(values);
                    }
                    DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                    handler.startBulkInsert(DataInsertHandler.FAVORITES_TOKEN, false, DataContract.URI_FAVORITES, listValues.toArray(new ContentValues[listValues.size()]));
                    EventBus.getDefault().post(new FavoritesEvent(true, "OK"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new FavoritesEvent(false, "No merchants featured data"));
                }
            } else {
                DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                handler.startDelete(DataInsertHandler.FAVORITES_TOKEN, false, DataContract.URI_FAVORITES, null, null);
                EventBus.getDefault().post(new FavoritesEvent(true, "OK"));
            }
        }
    }

    private class AddMerchantTask extends AsyncTask<Void, Void, Void> {
        private long id;
        private String jsonString = "";
        private JSONObject jObj = null;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;

        public AddMerchantTask(long id) {
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = new URL(context.getString(R.string.favorites_path) + String.valueOf(id));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("IDFA", Utilities.retrieveIdfa(context));
                urlConnection.setRequestProperty("token", Utilities.retrieveUserToken(context));
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");

                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
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
                jObj = (JSONObject) new JSONArray(jsonString).get(0);
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
                    long gottenId = jObj.getLong("vendor_id");
                    long isFavorite = jObj.getInt("is_favorite");
                    if (gottenId == id && isFavorite == 1) fetchData();
                } else {
                    EventBus.getDefault().post(new FavoritesEvent(false, "Check your internet connection or authorization data"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class DeleteMerchantTask extends AsyncTask<Void, Void, Void> {
        private long id;
        private String jsonString = "";
        private JSONObject jObj = null;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;

        public DeleteMerchantTask(long id) {
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = new URL(context.getString(R.string.favorites_path) + String.valueOf(id));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("IDFA", Utilities.retrieveIdfa(context));
                urlConnection.setRequestProperty("token", Utilities.retrieveUserToken(context));
                urlConnection.setRequestMethod("DELETE");

                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
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
                jObj = (JSONObject) new JSONArray(jsonString).get(0);
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
                    long gottenId = jObj.getLong("vendor_id");
                    long isFavorite = jObj.getInt("is_favorite");
                    if (gottenId == id && isFavorite == 0) fetchData();
                } else {
                    EventBus.getDefault().post(new FavoritesEvent(false, "Check your internet connection or authorization data"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

