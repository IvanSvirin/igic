package com.cashback.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.cashback.R;
import com.cashback.ui.web.BrowserActivity;

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
                intent.putExtra("title", getApplication().getResources().getString(R.string.send_app_feedback));
                intent.putExtra("url", "http://support.igive.com/Main/frmNewTicket.aspx");
                startActivity(intent);
                break;
            case R.id.sendAppFeedbackFrame:
                intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("title", getApplication().getResources().getString(R.string.send_app_feedback));
                intent.putExtra("url", "http://support.igive.com/Main/frmNewTicket.aspx");
                startActivity(intent);
                break;
            case R.id.paymentQuestionsFrame:
                intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("title", getApplication().getResources().getString(R.string.payment_questions));
                intent.putExtra("url", "http://support.igive.com/kb/a3/when-will-my-cause-get-a-check.aspx");
                startActivity(intent);
                break;
            case R.id.howToUseFrame:
                intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("title", getApplication().getResources().getString(R.string.how_to_use));
                intent.putExtra("url", "http://support.igive.com/kb/a115/how-igive-works.aspx");
                startActivity(intent);
                break;
            case R.id.whereIsMineFrame:
                intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("title", getApplication().getResources().getString(R.string.where_is_mine));
                intent.putExtra("url", "http://support.igive.com/kb/a2/tracking-your-donation.aspx");
                startActivity(intent);
                break;
            case R.id.privacySecurityFrame:
                intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("title", getApplication().getResources().getString(R.string.privacy_security));
                intent.putExtra("url", "http://www.igive.com/isearch/privacy.cfm");
                startActivity(intent);
                break;
            case R.id.termsConditionsFrame:
                intent = new Intent(this, BrowserActivity.class);
                intent.putExtra("title", getApplication().getResources().getString(R.string.terms_conditions));
                intent.putExtra("url", "http://www.igive.com/isearch/tos.cfm");
                startActivity(intent);
                break;
        }
    }
}
