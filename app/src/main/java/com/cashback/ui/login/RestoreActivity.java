package com.cashback.ui.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;

import com.cashback.App;
import com.cashback.R;
import com.cashback.model.AuthObject;
import com.cashback.rest.request.ResetRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RestoreActivity extends AppCompatActivity {
    @Bind(R.id.email)
    EditText email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_restore);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.request_password_reset);
        //Google Analytics
        App app = (App) getApplication();
        Tracker tracker = app.getDefaultTracker();
        tracker.setScreenName("Password Reset");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @OnClick(R.id.restoreButton)
    public void onSend() {
        AuthObject authObject = new AuthObject();
        authObject.setEmail(String.valueOf(email.getText()));
        new ResetRequest(this, authObject).doReset();
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
