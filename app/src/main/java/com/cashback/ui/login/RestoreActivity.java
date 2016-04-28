package com.cashback.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.cashback.R;
import com.cashback.ui.MainActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by I.Svirin on 4/28/2016.
 */
public class RestoreActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_restore);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.request_password_reset);
    }

    @OnClick(R.id.restoreButton)
    public void onSend() {
        // TODO: 4/19/2016 TEST - will be deleted
        startActivity(new Intent(this, MainActivity.class));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
