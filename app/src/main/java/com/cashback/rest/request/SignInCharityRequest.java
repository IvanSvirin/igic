package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.model.AuthObject;
import com.cashback.rest.IAuthorization;
import com.cashback.rest.ServiceGenerator;
import com.cashback.rest.event.AccountEvent;

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

public class SignInCharityRequest {
    private AuthObject authObject;
    private Context context;
    private String pathEnd;

    public SignInCharityRequest(Context context, AuthObject authObject,  String pathEnd) {
        this.context = context;
        this.authObject = authObject;
        this.pathEnd = pathEnd;
    }

    public void fetchData() {
        new SignInRequestTask(authObject, pathEnd).execute();
    }

    public class SignInRequestTask extends AsyncTask<Void, Void, Void> {
        private AuthObject authObject;
        private String jsonString = "";
        private JSONObject jObj = null;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;
        private String pathEnd;

        public SignInRequestTask(AuthObject authObject, String pathEnd) {
            this.authObject = authObject;
            this.pathEnd = pathEnd;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = new URL(context.getString(R.string.sign_in_path) + pathEnd);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("IDFA", Utilities.retrieveIdfa(context));
                String postParameters;
                switch (authObject.getAuthType()) {
                    case "0":
                        postParameters = "email=sandi_schleicher@hotmail.com&password=igive&auth_type=" + authObject.getAuthType();
//                postParameters = "email=" + authObject.getEmail() + "&password=" + authObject.getPassword() + "&auth_type=" + authObject.getAuthType();
                        break;
                    case "1":
                    case "2":
                        postParameters = "token=" + authObject.getToken() + "&first_name=" + authObject.getFirstName() + "&last_name=" + authObject.getLastName() +
                                "&email=" + authObject.getEmail() + "&auth_type=" + authObject.getAuthType();
                        break;
                    default:
                        postParameters = "email=sandi_schleicher@hotmail.com&password=igive&auth_type=" + authObject.getAuthType();
//                postParameters = "email=" + authObject.getEmail() + "&password=" + authObject.getPassword() + "&auth_type=" + authObject.getAuthType();
                }
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
                    values.put(DataContract.CharityAccounts.COLUMN_TOTAL_PAID_AMOUNT, jObj.getDouble("total_paid_amount"));
                    values.put(DataContract.CharityAccounts.COLUMN_NEXT_CHECK_AMOUNT, jObj.getDouble("next_check_amount"));
                    values.put(DataContract.CharityAccounts.COLUMN_CAUSE_DASHBOARD_URL, jObj.getString("cause_dashboard_url"));
                    values.put(DataContract.CharityAccounts.COLUMN_LAST_NAME, jObj.getString("last_name"));
                    values.put(DataContract.CharityAccounts.COLUMN_EMAIL, jObj.getString("email"));
                    values.put(DataContract.CharityAccounts.COLUMN_FIRST_NAME, jObj.getString("first_name"));
                    values.put(DataContract.CharityAccounts.COLUMN_TOTAL_RAISED, jObj.getDouble("total_raised"));
//                    values.put(DataContract.CharityAccounts.COLUMN_TOTAL_EARNED, jObj.getString("TOTALEARNED"));
                    values.put(DataContract.CharityAccounts.COLUMN_SELECT_CAUSE_URL, jObj.getString("select_cause_url"));
                    values.put(DataContract.CharityAccounts.COLUMN_PENDING_AMOUNT, jObj.getDouble("pending_amount"));
                    values.put(DataContract.CharityAccounts.COLUMN_TOKEN, jObj.getString("token"));
                    values.put(DataContract.CharityAccounts.COLUMN_MEMBER_DATE, jObj.getString("member_date"));
                    values.put(DataContract.CharityAccounts.COLUMN_REFERRER_ID, jObj.getString("referrer_id"));

                    DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                    handler.startInsert(DataInsertHandler.ACCOUNT_TOKEN, null, DataContract.URI_CHARITY_ACCOUNTS, values);
                    EventBus.getDefault().post(new AccountEvent(true, null));
                    Utilities.saveUserToken(context, jObj.getString("token"));
                } else {
                    EventBus.getDefault().post(new AccountEvent(false, "Check your internet connection or authorization data"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
