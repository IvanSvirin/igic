package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.model.AuthObject;
import com.cashback.model.CharityAccount;
import com.cashback.model.ErrorResponse;
import com.cashback.model.Merchant;
import com.cashback.model.WarningResponse;
import com.cashback.rest.ErrorRestException;
import com.cashback.rest.IAuthorization;
import com.cashback.rest.IMerchants;
import com.cashback.rest.ServiceGenerator;
import com.cashback.rest.WarningRestException;
import com.cashback.rest.adapter.CharityAccountDeserializer;
import com.cashback.rest.event.LoginEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by I.Svirin on 5/12/2016.
 */
public class SignInRequest extends ServiceGenerator<IAuthorization> {
    private Call<CharityAccount> call;
    private Gson gson1;
    private AuthObject authObject;
    private Context context;
    private String idfa;

    {
        Type type = new TypeToken<CharityAccount>() {
        }.getType();
        gson1 = new GsonBuilder().registerTypeAdapter(type, new CharityAccountDeserializer()).create();
    }


    public SignInRequest(Context context, AuthObject authObject) {
        super(IAuthorization.class);
        this.context = context;
        this.authObject = authObject;
        this.idfa = Utilities.retrieveIdfa(context);
    }

    public void fetchData() {
        call = createService(gson1).logIn(idfa, authObject);
        call.enqueue(new Callback<CharityAccount>() {
            @Override
            public void onResponse(Call<CharityAccount> call, Response<CharityAccount> response) {
                if (response.isSuccess()) {
                    CharityAccount account = response.body();
                    EventBus.getDefault().post(new LoginEvent(true, null));
                    pushAccount(account);
                    Utilities.saveUserToken(context, account.getToken());
                } else {
                    int statusCode = response.code();
                    String answer = "Code " + statusCode + " . ";
                    ResponseBody errorBody = response.errorBody();
                    try {
                        answer += errorBody.string();
                        errorBody.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    EventBus.getDefault().post(new LoginEvent(false, answer));
                }
            }

            @Override
            public void onFailure(Call<CharityAccount> call, Throwable t) {
                if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_ERROR)) {
                    ErrorResponse err = ((ErrorRestException) t).getBody();
                    EventBus.getDefault().post(new LoginEvent(false, err.getMessage()));
                } else if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_WARNING)) {
                    WarningResponse warn = ((WarningRestException) t).getBody();
                    EventBus.getDefault().post(new LoginEvent(false, warn.getMessage()));
                } else {
                    EventBus.getDefault().post(new LoginEvent(false, t.getMessage()));
                }
            }

            private void pushAccount(CharityAccount account) {
                ContentValues values = new ContentValues();
                values.put(DataContract.CharityAccounts.COLUMN_TOKEN, account.getToken());
                values.put(DataContract.CharityAccounts.COLUMN_FIRST_NAME, account.getFirstName());
                values.put(DataContract.CharityAccounts.COLUMN_LAST_NAME, account.getLastName());
                values.put(DataContract.CharityAccounts.COLUMN_EMAIL, account.getEmail());
                values.put(DataContract.CharityAccounts.COLUMN_MEMBER_DATE, account.getMemberDate());
                values.put(DataContract.CharityAccounts.COLUMN_NEXT_CHECK_AMOUNT, account.getNextCheckAmount());
                values.put(DataContract.CharityAccounts.COLUMN_PENDING_AMOUNT, account.getPendingAmount());
                values.put(DataContract.CharityAccounts.COLUMN_TOTAL_PAID_AMOUNT, account.getTotalPaidAmount());
                values.put(DataContract.CharityAccounts.COLUMN_TOTAL_PAID_DATE, account.getTotalPaidDate());
                values.put(DataContract.CharityAccounts.COLUMN_TOTAL_RAISED, account.getTotalRaised());
                values.put(DataContract.CharityAccounts.COLUMN_CAUSE_DASHBOARD_URL, account.getCauseDashboardUrl());
                values.put(DataContract.CharityAccounts.COLUMN_SELECT_CAUSE_URL, account.getSelectCauseUrl());
                DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                handler.startInsert(DataInsertHandler.ACCOUNT_TOKEN, null, DataContract.URI_CHARITY_ACCOUNTS, values);
            }
        });
    }

    // TODO: 4/19/2016 TEST - will be deleted
    public class Task extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String jsonString = "";
            JSONObject jObj = null;
            URL url;
            InputStream inputStream = null;
            HttpURLConnection urlConnection = null;
            AuthObject authObject = new AuthObject();
            authObject.setAuthType("email");
            authObject.setEmail("sandi_schleicher@hotmail.com");
            authObject.setPassword("igive");
            try {
                url = new URL("http://beta1.igive.com/rest/iGive/api/v1/merchants");
//                url = new URL("http://beta1.igive.com/rest/iGive/api/v1/authorization/login");
                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("POST");
//                urlConnection.addRequestProperty("auth_type", "email");
//                urlConnection.addRequestProperty("email", "sandi_schleicher@hotmail.com");
//                urlConnection.addRequestProperty("password", "igive");
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
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
    }
}
