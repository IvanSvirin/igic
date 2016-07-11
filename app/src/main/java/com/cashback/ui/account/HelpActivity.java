package com.cashback.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.cashback.App;
import com.cashback.R;
import com.cashback.ui.web.BrowserActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import butterknife.ButterKnife;
import butterknife.OnClick;

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
        //Google Analytics
        App app = (App) getApplication();
        Tracker tracker = app.getDefaultTracker();
        tracker.setScreenName("Help");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.howItWorksFrame, R.id.sendAppFeedbackFrame, R.id.paymentQuestionsFrame, R.id.howToUseFrame, R.id.whereIsMineFrame, R.id.privacySecurityFrame, R.id.termsConditionsFrame})
    public void onClicks(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.howItWorksFrame:
                intent = new Intent(this, TourActivity.class);
                startActivity(intent);
                break;
            case R.id.sendAppFeedbackFrame:
                intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("title", getApplication().getResources().getString(R.string.send_app_feedback));
                intent.putExtra("url", getString(R.string.send_app_feedback_path));
                startActivity(intent);
                break;
            case R.id.paymentQuestionsFrame:
                intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("title", getApplication().getResources().getString(R.string.payment_questions));
                intent.putExtra("url", getString(R.string.payment_questions_path));
                startActivity(intent);
                break;
            case R.id.howToUseFrame:
                intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("title", getApplication().getResources().getString(R.string.how_to_use));
                intent.putExtra("url", getString(R.string.how_to_use_path));
                startActivity(intent);
                break;
            case R.id.whereIsMineFrame:
                intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("title", getApplication().getResources().getString(R.string.where_is_mine));
                intent.putExtra("url", getString(R.string.where_is_my_donation_path));
                startActivity(intent);
                break;
            case R.id.privacySecurityFrame:
                intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("title", getApplication().getResources().getString(R.string.privacy_security));
                intent.putExtra("url", getString(R.string.privacy_and_security_path));
                startActivity(intent);
                break;
            case R.id.termsConditionsFrame:
                intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("title", getApplication().getResources().getString(R.string.terms_conditions));
                intent.putExtra("url", getString(R.string.terms_and_conditions_path));
                startActivity(intent);
                break;
        }
    }
}
