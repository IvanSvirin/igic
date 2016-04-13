package com.cashback.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cashback.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by I.Svirin on 4/12/2016.
 */
public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.help);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.howItWorksFrame})
    public void onClicks(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.howItWorksFrame:
                intent = new Intent(this, TourActivity.class);
                startActivity(intent);
                break;
        }
    }
}
