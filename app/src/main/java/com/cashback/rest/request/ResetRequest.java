package com.cashback.rest.request;

import android.content.Context;
import android.os.AsyncTask;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.model.AuthObject;
import com.cashback.rest.IAuthorization;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by I.Svirin on 6/24/2016.
 */
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
            }
            return null;
        }
    }
}
