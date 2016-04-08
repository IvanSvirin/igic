package com.cashback.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.cashback.R;
import com.cashback.Utilities;
import com.facebook.appevents.AppEventsLogger;

import bolts.AppLinks;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by I.Svirin on 4/6/2016.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private final String SELECTED_ITEM_ID = "SELECTED_ITEM_ID";
    private final String CONTENT_KEY = "IAM_CONTENT";
    /* MAIN FRAGMENTS */
    public static final int FRAGMENT_FEATURED = 0;
    public static final int FRAGMENT_ALL_STORES = 1;
    public static final int FRAGMENT_CATEGORIES = 2;
    public static final int FRAGMENT_TELL_A_FRIEND = 3;
    public static final int FRAGMENT_ACCOUNT = 4;

    /* LOADERS */
    public static final int ACCOUNT_LOADER = 0;
    public static final int CATEGORY_LOADER = 1;
    public static final int ALL_STORES_LOADER = 2;
    public static final int STORES_IN_CATEGORY_LOADER = 3;
//    public static final int DEPLOYED_ABOUT_STORE = 4;
//    public static final int SALE_LOADER = 5;
    public static final int FAVORITE_LOADER = 6;
//    public static final int TYPE_LOADER = 7;
//    public static final int STORES_BY_SALE_CATEGORIES_LOADER = 8;
    public static final int FAVORITE_STORES_LOADER = 9;
    public static final int FEATURED_LOADER = 10;
//    public static final int IMAGE_LOADER = 11;

    private DrawerUi drawerUi;
    private int currentItemId = 0;
    private Handler handler;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        RestUtilities.syncDistantData(getApplicationContext(), RestUtilities.TOKEN_ACCOUNT);
        setContentView(R.layout.layout_main);

        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            Log.d(LaunchActivity.DB_TAG_LOG, "App Link Target URL: " + targetUrl.toString());
        }

        handler = new Handler();
        fragmentManager = getSupportFragmentManager();
        drawerUi = new DrawerUi(this);
        drawerUi.init(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(ACCOUNT_LOADER, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
//        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // TODO: 4/7/2016  
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // TODO: 4/7/2016
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // TODO: 4/7/2016
    }

    class DrawerUi implements NavigationView.OnNavigationItemSelectedListener{
        private static final long DRAWER_CLOSE_DELAY_MS = 200;
        private MainActivity activity;
        private Fragment lastFragment;

        @Bind(R.id.drawer_layout)
        DrawerLayout drawer;
        @Bind(R.id.drawer_navigator)
        NavigationView navigator;
        @BindString(R.string.format_donation_amount)
        String stringFormat;

        public DrawerUi(MainActivity activity) {
            this.activity = activity;
            ButterKnife.bind(this, activity);
            navigator.setSaveEnabled(true);
            navigator.setNavigationItemSelectedListener(this);
        }

        private void init(Bundle savedInstanceState) {
            Menu menu = navigator.getMenu();
            if (!Utilities.isLoggedIn(activity)) {
                menu.findItem(R.id.item_account).setVisible(false);
            }
            if (savedInstanceState != null) {
                currentItemId = savedInstanceState.getInt(SELECTED_ITEM_ID);
                lastFragment = fragmentManager.getFragment(savedInstanceState, CONTENT_KEY);
                menu.findItem(getCurrentSelectedMenuItemId(currentItemId)).setChecked(true);
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, lastFragment, lastFragment.getTag())
                        .commit();
            } else {
                currentItemId = FRAGMENT_FEATURED;
                onNavigationItemSelected(menu.findItem(R.id.item_featured));
            }
        }

        private int getCurrentSelectedMenuItemId(int drawerMenuPosition) {
            switch (drawerMenuPosition) {
                case FRAGMENT_FEATURED:
                    return R.id.item_featured;
                case FRAGMENT_ALL_STORES:
                    return R.id.item_all_stores;
                case FRAGMENT_CATEGORIES:
                    return R.id.item_categories;
                case FRAGMENT_TELL_A_FRIEND:
                    return R.id.item_tell_a_friend;
                case FRAGMENT_ACCOUNT:
                    return R.id.item_account;
            }
            return 0;
        }

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            return false;
        }
    }
}
