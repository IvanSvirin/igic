package com.cashback.ui.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseIntArray;
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
import com.cashback.db.DataContract;
import com.cashback.rest.event.CouponsEvent;
import com.cashback.ui.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by I.Svirin on 4/12/2016.
 */
public class BrowserActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ActivityUi ui;
    private MenuItem menuItem;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        setContentView(R.layout.layout_browser);

        ui = new ActivityUi(this);
        ui.setNavigationButtonState();
        ui.webView.setWebChromeClient(new MyWebChromeClient());
        ui.webView.setWebViewClient(new MyWebViewClient());
        ui.setWebSettings(false);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(MainActivity.COUPONS_LOADER, null, this);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if (id == MainActivity.COUPONS_LOADER) {
            loader = new CursorLoader(this);
            loader.setUri(DataContract.URI_COUPONS);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ui.cursorPagerAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ui.cursorPagerAdapter.changeCursor(null);
    }

    public void onEvent(CouponsEvent event) {
        if (event.isSuccess) {
            getSupportLoaderManager().restartLoader(MainActivity.COUPONS_LOADER, null, this);
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
            if (!FIT_SCALE) {
                FIT_SCALE = true;
                webView.setInitialScale(1);
            } else {
                FIT_SCALE = false;
                webView.setInitialScale(0);
            }
            setPageNumber();
        }

        @OnClick(R.id.forwardButton)
        public void onNext() {
            pager.setCurrentItem(pager.getCurrentItem() + 1);
            setPageNumber();
        }

        @OnClick(R.id.backButton)
        public void onPrev() {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
            setPageNumber();
        }

        @OnClick(R.id.lastPageButton)
        public void onLast() {
            pager.setCurrentItem(pager.getBottom());
            setPageNumber();
        }

        @OnClick(R.id.firstPageButton)
        public void onFirst() {
            pager.setCurrentItem(0);
            setPageNumber();
        }

        @OnClick(R.id.collapseButton)
        public void onCollapse() {
            collapseLayout.setVisibility(View.INVISIBLE);
            pagerNavigator.setVisibility(View.INVISIBLE);
            navigationPanel.setVisibility(View.VISIBLE);
            pager.setAdapter(null);
        }

        public ActivityUi(BrowserActivity browserActivity) {
            context = browserActivity;
            ButterKnife.bind(this, browserActivity);
            initToolbar(browserActivity);
            cursorPagerAdapter = new CursorPagerAdapter(getSupportFragmentManager(),
                    new String[]{DataContract.OfferEntry.COLUMN_DESCRIPTION, DataContract.OfferEntry.COLUMN_EXPIRE, DataContract.OfferEntry.COLUMN_CODE}, null);
            pagerNavigator.setVisibility(View.INVISIBLE);
            collapseLayout.setVisibility(View.INVISIBLE);
        }

        private void initToolbar(Activity activity) {
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
            bar = ((AppCompatActivity) activity).getSupportActionBar();
            if (bar != null) {
                bar.setDisplayHomeAsUpEnabled(true);
                bar.setDefaultDisplayHomeAsUpEnabled(true);
                // TODO: 4/19/2016 TEST - will be deleted
                intent = getIntent();
                bar.setTitle("STORE");
                bar.setSubtitle(intent.getStringExtra("vendor_commission"));
                loadContent("https://www.iconsumer.com/");
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

        private void setTitle(String title) {
            bar.setDisplayShowTitleEnabled(true);
            bar.setTitle(title);
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
        private final String[] projection;
        private Cursor cursor;
        protected boolean dataValid;
        protected int rowIDColumn;
        protected SparseIntArray itemPositions;

        public CursorPagerAdapter(FragmentManager fm, String[] projection, Cursor cursor) {
            super(fm);
            this.projection = projection;
            this.cursor = cursor;
            this.dataValid = cursor != null;
            this.rowIDColumn = dataValid ? cursor.getColumnIndexOrThrow("_id") : -1;
        }

        @Override
        public PageFragment getItem(int position) {
            if (cursor == null) // shouldn't happen
                return null;

            cursor.moveToPosition(position);
            PageFragment frag;
            try {
                frag = PageFragment.newInstance();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            Bundle args = new Bundle();
            for (int i = 0; i < projection.length; ++i) {
                args.putString(projection[i], cursor.getString(cursor.getColumnIndex(projection[i])));
            }
            frag.setArguments(args);
            return frag;
        }

        @Override
        public int getCount() {
            if (cursor == null)
                return 0;
            else
                return cursor.getCount();
        }

        public Cursor getCursor() {
            return cursor;
        }

        public void changeCursor(Cursor cursor) {
            Cursor old = swapCursor(cursor);
            if (old != null) {
                old.close();
            }
        }

        public Cursor swapCursor(Cursor newCursor) {
            if (newCursor == cursor) {
                return null;
            }
            Cursor oldCursor = cursor;
            cursor = newCursor;
            if (newCursor != null) {
                rowIDColumn = newCursor.getColumnIndexOrThrow("_id");
                dataValid = true;
            } else {
                rowIDColumn = -1;
                dataValid = false;
            }
            setItemPositions();
            notifyDataSetChanged();
            return oldCursor;
        }

        public void setItemPositions() {
            itemPositions = null;

            if (dataValid) {
                int count = cursor.getCount();
                itemPositions = new SparseIntArray(count);
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    int rowId = cursor.getInt(rowIDColumn);
                    int cursorPos = cursor.getPosition();
                    itemPositions.append(rowId, cursorPos);
                }
            }
        }
    }

    private void showDescriptionDialog(String message) {
        InfoDialog dialog = InfoDialog.newInstance(message);
        dialog.setCancelable(true);
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        dialog.show(getSupportFragmentManager(), "TAG_DIALOG");
    }

    public static class InfoDialog extends DialogFragment {

        private static String description;

        static InfoDialog newInstance(String message) {
            description = message;
            return new InfoDialog();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.view_dialog, container, false);
            TextView tv = (TextView) v.findViewById(R.id.description);
            tv.setText(description);
            return v;
        }
    }
}
