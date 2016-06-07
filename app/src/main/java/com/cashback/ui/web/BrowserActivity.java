package com.cashback.ui.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cashback.R;
import com.cashback.db.DataContract;
import com.cashback.model.Coupon;
import com.cashback.rest.request.CouponsByMerchantIdRequest;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BrowserActivity extends AppCompatActivity {
    private ActivityUi ui;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        setContentView(R.layout.layout_browser);

        intent = getIntent();

        ui = new ActivityUi(this);
        ui.setNavigationButtonState();
        ui.webView.setWebChromeClient(new MyWebChromeClient());
        ui.webView.setWebViewClient(new MyWebViewClient());
        ui.setWebSettings(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    class ActivityUi {
        private boolean FIT_SCALE;
        private Context context;
        private ActionBar bar;

        @Bind(R.id.toolbar)
        Toolbar toolbar;
        @Bind(R.id.progress_bar)
        ContentLoadingProgressBar progressBar;
        @Bind(R.id.browser_web_view)
        WebView webView;
        @Bind(R.id.back_btn)
        ImageButton btnBack;
        @Bind(R.id.forward_btn)
        ImageButton btnForward;
        @Bind(R.id.refresh_btn)
        ImageButton btnRefresh;

        @OnClick(R.id.forward_btn)
        public void onForward() {
            webView.goForward();
        }

        @OnClick(R.id.back_btn)
        public void onBack() {
            webView.goBack();
        }

        @OnClick(R.id.refresh_btn)
        public void onRefresh() {
            webView.reload();
        }

        public ActivityUi(BrowserActivity browserActivity) {
            context = browserActivity;
            ButterKnife.bind(this, browserActivity);
            initToolbar(browserActivity);
        }

        private void initToolbar(Activity activity) {
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
            bar = ((AppCompatActivity) activity).getSupportActionBar();
            if (bar != null) {
                bar.setDisplayHomeAsUpEnabled(true);
                bar.setDefaultDisplayHomeAsUpEnabled(true);

                bar.setTitle(intent.getStringExtra("title"));
                loadContent(intent.getStringExtra("url"));
            }
        }

        private void setNavigationButtonState() {
            if (webView.canGoForward()) {
                btnForward.setEnabled(true);
            } else {
                btnForward.setEnabled(false);
            }
            if (webView.canGoBack()) {
                btnBack.setEnabled(true);
            } else {
                btnBack.setEnabled(false);
            }
        }

        public void setWebSettings(boolean fitSize) {
            FIT_SCALE = fitSize;
            WebSettings settings = webView.getSettings();
            webView.setPadding(0, 0, 0, 0);
            if (FIT_SCALE)
                webView.setInitialScale(1);
            else
                webView.setInitialScale(0);

            settings.setJavaScriptEnabled(true);
            settings.setSupportZoom(true);
            settings.setAppCacheEnabled(true);
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
            settings.setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(false);
        }

        private void loadContent(String url) {
            webView.loadUrl(url);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            ui.progressBar.setProgress(newProgress);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            ui.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            ui.progressBar.setVisibility(View.GONE);
            ui.setNavigationButtonState();
        }
    }
}
