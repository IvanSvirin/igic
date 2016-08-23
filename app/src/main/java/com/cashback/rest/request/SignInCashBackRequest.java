package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cashback.R;
import com.cashback.Utilities;

import db.DataContract;

import com.cashback.db.DataInsertHandler;
import com.cashback.model.AuthObject;
import com.cashback.rest.event.SignInEvent;
import com.cashback.rest.event.SignUpEvent;

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

public class SignInCashBackRequest {
    private AuthObject authObject;
    private Context context;
    private String pathEnd;
    private boolean isRegistered = false;

    public SignInCashBackRequest(Context context, AuthObject authObject, String pathEnd) {
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
                        if (pathEnd.equals("signup")) {
                            postParameters = "email=" + authObject.getEmail() + "&password=" + authObject.getPassword() + "&auth_type=" + authObject.getAuthType() +
                                    "&incoming_referrer_id=" + authObject.getReferrerEmail();
                        } else {
                            postParameters = "email=" + authObject.getEmail() + "&password=" + authObject.getPassword() + "&auth_type=" + authObject.getAuthType();
                        }
                        break;
                    case "1":
                    case "2":
                        postParameters = "token=" + authObject.getToken() + "&first_name=" + authObject.getFirstName() + "&last_name=" + authObject.getLastName() +
                                "&email=" + authObject.getEmail() + "&auth_type=" + authObject.getAuthType() + "&user_id=" + authObject.getUserId();
                        break;
                    default:
                        if (pathEnd.equals("signup")) {
                            postParameters = "email=" + authObject.getEmail() + "&password=" + authObject.getPassword() + "&auth_type=" + authObject.getAuthType() +
                                    "incoming_referrer_id" + authObject.getReferrerEmail();
                        } else {
                            postParameters = "email=" + authObject.getEmail() + "&password=" + authObject.getPassword() + "&auth_type=" + authObject.getAuthType();
                        }
                }
                urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(postParameters);
                out.close();

                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                isRegistered = true;
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
                    values.put(DataContract.CashbackAccounts.COLUMN_CASH_PENDING_AMOUNT, jObj.getDouble("cash_pending_amount"));
                    values.put(DataContract.CashbackAccounts.COLUMN_PAYMENTS_TOTAL_AMOUNT, jObj.getDouble("payments_total_amount"));
                    values.put(DataContract.CashbackAccounts.COLUMN_NEXT_CHECK_AMOUNT, jObj.getDouble("next_check_amount"));
                    values.put(DataContract.CashbackAccounts.COLUMN_TOTAL_EARNED, jObj.getDouble("total_earned"));
                    values.put(DataContract.CashbackAccounts.COLUMN_LAST_NAME, jObj.getString("last_name"));
                    String email = jObj.getString("email");
                    Utilities.saveEmail(context, email);
                    values.put(DataContract.CashbackAccounts.COLUMN_EMAIL, email);
                    values.put(DataContract.CashbackAccounts.COLUMN_FIRST_NAME, jObj.getString("first_name"));
                    String token = jObj.getString("token");
                    Utilities.saveUserToken(context, token);
                    values.put(DataContract.CashbackAccounts.COLUMN_TOKEN, token);
                    values.put(DataContract.CashbackAccounts.COLUMN_MEMBER_DATE, jObj.getString("member_date"));
                    values.put(DataContract.CashbackAccounts.COLUMN_NEXT_PAYMENT_DATE, jObj.getString("next_payment_date"));
                    values.put(DataContract.CashbackAccounts.COLUMN_REFERRER_ID, jObj.getString("referrer_id"));

                    DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                    handler.startInsert(DataInsertHandler.ACCOUNT_TOKEN, null, DataContract.URI_CASH_BACK_ACCOUNT, values);
                    if (pathEnd.equals("login")) {
                        EventBus.getDefault().post(new SignInEvent(true, token));
                    } else {
                        EventBus.getDefault().post(new SignUpEvent(true, token));
                    }
                } else {
                    if (pathEnd.equals("login")) {
                        EventBus.getDefault().post(new SignInEvent(false, "Check your internet connection or authorization data"));
                    } else {
                        if (isRegistered) {
                            EventBus.getDefault().post(new SignUpEvent(false, "That email address is already registered.  Please use the Login screen instead."));
                        } else {
                            EventBus.getDefault().post(new SignUpEvent(false, "Check your internet connection or authorization data"));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
