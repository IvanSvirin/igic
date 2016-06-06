package com.cashback.ui.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.model.Coupon;
import com.cashback.rest.event.MerchantCouponsEvent;
import com.cashback.rest.request.CouponsByMerchantIdRequest;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;
import de.greenrobot.event.EventBus;

/**
 * Created by I.Svirin on 4/12/2016.
 */
public class BrowserDealsActivity extends AppCompatActivity {
    private ActivityUi ui;
    private MenuItem menuItem;
    private Intent intent;
    private ArrayList<Coupon> coupons;
    private String exceptionInfo;
    private String description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        setContentView(R.layout.layout_browser_deals);

        coupons = new ArrayList<>();
        intent = getIntent();
        new CouponsByMerchantIdRequest(this, intent.getLongExtra("vendor_id", 1), coupons).fetchData();

        ui = new ActivityUi(this);
        ui.setNavigationButtonState();
        ui.webView.setWebChromeClient(new MyWebChromeClient());
        ui.webView.setWebViewClient(new MyWebViewClient());
        ui.setWebSettings(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu, menu);
        menuItem = menu.findItem(R.id.action_info);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_info) {
            showDescriptionDialog("");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    public void onEvent(MerchantCouponsEvent event) {
        if (event.isSuccess) {
            ui.cursorPagerAdapter = new CursorPagerAdapter(getSupportFragmentManager(),coupons);
            ui.dealsButton.setText(String.valueOf(coupons.size()) + " DEALS");
            ui.dealsButton.setVisibility(View.VISIBLE);
        }
    }

    class ActivityUi {
        private boolean FIT_SCALE;
        private Context context;
        private ActionBar bar;
        private String logo;
        CursorPagerAdapter cursorPagerAdapter;

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
        @Bind(R.id.dealsButton)
        TextView dealsButton;
        @Bind(R.id.navigation_panel)
        LinearLayout navigationPanel;
        @Bind(R.id.pager)
        ViewPager pager;
        @Bind(R.id.pagerNavigator)
        RelativeLayout pagerNavigator;
        @Bind(R.id.forwardButton)
        ImageButton forwardButton;
        @Bind(R.id.backButton)
        ImageButton backButton;
        @Bind(R.id.lastPageButton)
        ImageButton lastPageButton;
        @Bind(R.id.firstPageButton)
        ImageButton firstPageButton;
        @Bind(R.id.pageNumber)
        TextView pageNumber;
        @Bind(R.id.collapseLayout)
        LinearLayout collapseLayout;
        @Bind(R.id.collapseButton)
        ImageButton collapseButton;

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

        @OnClick(R.id.dealsButton)
        public void onShowDeals() {
            pager.setAdapter(cursorPagerAdapter);
            collapseLayout.setVisibility(View.VISIBLE);
            pagerNavigator.setVisibility(View.VISIBLE);
            navigationPanel.setVisibility(View.INVISIBLE);
            setPageNumber();
        }

        @OnClick(R.id.forwardButton)
        public void onNext() {
            pager.setCurrentItem(pager.getCurrentItem() + 1);
        }

        @OnClick(R.id.backButton)
        public void onPrev() {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }

        @OnClick(R.id.lastPageButton)
        public void onLast() {
            pager.setCurrentItem(pager.getBottom());
        }

        @OnClick(R.id.firstPageButton)
        public void onFirst() {
            pager.setCurrentItem(0);
        }

        @OnClick(R.id.collapseButton)
        public void onCollapse() {
            collapseLayout.setVisibility(View.INVISIBLE);
            pagerNavigator.setVisibility(View.INVISIBLE);
            navigationPanel.setVisibility(View.VISIBLE);
            pager.setAdapter(null);
        }

        @OnPageChange(R.id.pager)
        public void onSwipe() {
            setPageNumber();
        }

        public ActivityUi(BrowserDealsActivity browserDealsActivity) {
            context = browserDealsActivity;
            ButterKnife.bind(this, browserDealsActivity);
            initToolbar(browserDealsActivity);
            pagerNavigator.setVisibility(View.INVISIBLE);
            collapseLayout.setVisibility(View.INVISIBLE);
            dealsButton.setVisibility(View.INVISIBLE);
        }

        private void initToolbar(Activity activity) {
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
            bar = ((AppCompatActivity) activity).getSupportActionBar();
            if (bar != null) {
                bar.setDisplayHomeAsUpEnabled(true);
                bar.setDefaultDisplayHomeAsUpEnabled(true);

                Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(intent.getLongExtra("vendor_id", 1)));
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst();
                String name = cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_NAME));
                exceptionInfo = cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_EXCEPTION_INFO));
                description = cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_DESCRIPTION));
                bar.setTitle(name);
                bar.setSubtitle(String.valueOf(cursor.getFloat(cursor.getColumnIndex(DataContract.Merchants.COLUMN_COMMISSION))) + " " + getResources().getString(R.string.cash_back_percent));
                loadContent(cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_AFFILIATE_URL)) + "&token=" + Utilities.retrieveUserToken(context));
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

        private void setPageNumber() {
            pageNumber.setText(String.valueOf(pager.getCurrentItem() + 1) + " of " + String.valueOf(cursorPagerAdapter.getCount()));
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

    public class CursorPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<Coupon> coupons;

        public CursorPagerAdapter(FragmentManager fm, ArrayList<Coupon> coupons) {
            super(fm);
            this.coupons = coupons;
        }

        @Override
        public PageFragment getItem(int position) {
            PageFragment frag;
            try {
                frag = PageFragment.newInstance();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            Bundle args = new Bundle();
            args.putString(PageFragment.COUPON_CODE, coupons.get(position).getCouponCode());
            args.putString(PageFragment.EXPIRATION_DATE, coupons.get(position).getExpirationDate());
            args.putString(PageFragment.RESTRICTIONS, coupons.get(position).getRestrictions());
            args.putLong(PageFragment.VENDOR_ID, coupons.get(position).getVendorId());
            frag.setArguments(args);
            return frag;
        }

        @Override
        public int getCount() {
            return coupons.size();
        }
    }

    private void showDescriptionDialog(String message) {
        InfoDialog dialog = InfoDialog.newInstance(description, exceptionInfo);
        dialog.setCancelable(true);
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        dialog.show(getSupportFragmentManager(), "TAG_DIALOG");
    }

    public static class InfoDialog extends DialogFragment {
        private static String description;
        private static String exceptionInfo;

        static InfoDialog newInstance(String d, String e) {
            description = d;
            exceptionInfo = e;
            return new InfoDialog();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.view_dialog, container, false);
            TextView tvDescription = (TextView) v.findViewById(R.id.description);
            TextView tvExceptions = (TextView) v.findViewById(R.id.exceptions);
            tvDescription.setText(description);
            tvExceptions.setText(exceptionInfo);
            return v;
        }
    }
}
