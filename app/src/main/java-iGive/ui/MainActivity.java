package ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.rest.event.AccountEvent;
import com.cashback.ui.AllStoresFragment;
import com.cashback.ui.CategoriesFragment;
import com.cashback.ui.HelpFragment;
import com.cashback.ui.LaunchActivity;
import com.cashback.ui.TellAFriendFragment;
import com.cashback.ui.allresults.SearchActivity;
import com.cashback.ui.featured.FeaturedFragment;
import com.cashback.ui.login.LoginActivity;
import com.facebook.appevents.AppEventsLogger;

import bolts.AppLinks;
import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import db.DataContract;
import de.greenrobot.event.EventBus;
import ui.account.AccountFragment;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String SELECTED_ITEM_ID = "SELECTED_ITEM_ID";
    private final String CONTENT_KEY = "IAM_CONTENT";
    /* MAIN FRAGMENTS */
    public static final int FRAGMENT_FEATURED = 0;
    public static final int FRAGMENT_ALL_STORES = 1;
    public static final int FRAGMENT_CATEGORIES = 2;
    public static final int FRAGMENT_TELL_A_FRIEND = 3;
    public static final int FRAGMENT_ACCOUNT = 4;
    public static final int FRAGMENT_HELP = 5;

    /* LOADERS */
    public static final int ACCOUNT_LOADER = 0;
    public static final int COUPONS_LOADER = 1;
    public static final int FAVORITES_LOADER = 2;
    public static final int EXTRAS_LOADER = 3;
    public static final int CATEGORIES_LOADER = 4;
    public static final int MERCHANTS_LOADER = 5;
    public static final int PAYMENTS_LOADER = 6;
    public static final int SHOPPING_TRIPS_LOADER = 7;
    public static final int ORDERS_LOADER = 8;

    private DrawerUi drawerUi;
    private int currentItemId = 0;
    private Handler handler;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            Log.d(LaunchActivity.DB_TAG_LOG, "App Link Target URL: " + targetUrl.toString());
        }

        handler = new Handler();
        fragmentManager = getSupportFragmentManager();
        drawerUi = new DrawerUi(this);
        drawerUi.init(savedInstanceState);

        if (Utilities.isLoggedIn(this)) {
            getSupportLoaderManager().initLoader(ACCOUNT_LOADER, null, this);
        } else {
            Utilities.saveUserToken(this, "unauthorized");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawerUi.initDrawerHeaderFree();
                }
            }, 500);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        AppEventsLogger.activateApp(this);
        if (Utilities.isLoggedIn(this)) {
            getSupportLoaderManager().initLoader(ACCOUNT_LOADER, null, this);
            drawerUi.navigator.getMenu().findItem(R.id.item_account).setVisible(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if (id == MainActivity.ACCOUNT_LOADER) {
            loader = new CursorLoader(this);
            loader.setUri(DataContract.URI_CHARITY_ACCOUNT);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final String name, email, cashBack;
        if (data != null && data.getCount() > 0 && Utilities.isLoggedIn(this)) {
            data.moveToFirst();
            name = data.getString(data.getColumnIndex(DataContract.CharityAccounts.COLUMN_FIRST_NAME)) + " "
                    + data.getString(data.getColumnIndex(DataContract.CharityAccounts.COLUMN_LAST_NAME));
            email = data.getString(data.getColumnIndex(DataContract.CharityAccounts.COLUMN_EMAIL));
            cashBack = "$ " + String.valueOf(data.getFloat(data.getColumnIndex(DataContract.CharityAccounts.COLUMN_TOTAL_RAISED)));
        } else {
            name = "";
            email = "";
            cashBack = "";
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawerUi.initDrawerHeader(cashBack, name, email);
            }
        }, 500);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void onEvent(AccountEvent event) {
        if (event.isSuccess) {
            getSupportLoaderManager().restartLoader(ACCOUNT_LOADER, null, this);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerUi.isBackPressed()) {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_ITEM_ID, currentItemId);
        if (fragmentManager != null) {
            fragmentManager.putFragment(outState, CONTENT_KEY, drawerUi.getCurrentContent());
        }
    }

    public void setAssociateToolbar(Toolbar toolbar) {
        drawerUi.associateToolbarDrawer(toolbar);
    }

    class DrawerUi implements NavigationView.OnNavigationItemSelectedListener {
        private static final long DRAWER_CLOSE_DELAY_MS = 200;
        private MainActivity activity;
        private Fragment lastFragment;
        @Bind(R.id.drawer_layout)
        DrawerLayout drawer;
        @Bind(R.id.drawer_navigator)
        NavigationView navigator;
        @BindString(R.string.format_donation_amount)
        String stringFormat;

        public DrawerUi(final MainActivity activity) {
            this.activity = activity;
            ButterKnife.bind(this, activity);
            navigator.setSaveEnabled(true);
            navigator.setNavigationItemSelectedListener(this);
            drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(drawerView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                }

                @Override
                public void onDrawerStateChanged(int newState) {
                }
            });
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

        public void associateToolbarDrawer(Toolbar toolbar) {
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }

        @Override
        public boolean onNavigationItemSelected(final MenuItem item) {
            item.setChecked(true);
            int selectedItem = item.getItemId();

            if (selectedItem == currentItemId) {
                drawer.closeDrawer(GravityCompat.START);
                return true;
            } else {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        switch (item.getItemId()) {
                            case (R.id.item_featured):
                                currentItemId = FRAGMENT_FEATURED;
                                fragmentManager.beginTransaction()
                                        .replace(R.id.content_frame, new FeaturedFragment(), FeaturedFragment.TAG_FEATURED_FRAGMENT)
                                        .commit();
                                break;
                            case (R.id.item_all_stores):
                                currentItemId = FRAGMENT_ALL_STORES;
                                fragmentManager.beginTransaction()
                                        .replace(R.id.content_frame, new AllStoresFragment(), AllStoresFragment.TAG_ALL_STORES_FRAGMENT)
                                        .commit();
                                break;
                            case (R.id.item_categories):
                                currentItemId = FRAGMENT_CATEGORIES;
                                fragmentManager.beginTransaction()
                                        .replace(R.id.content_frame, new CategoriesFragment(), CategoriesFragment.TAG_CATEGORIES_FRAGMENT)
                                        .commit();
                                break;
                            case (R.id.item_tell_a_friend):
                                currentItemId = FRAGMENT_TELL_A_FRIEND;
                                fragmentManager.beginTransaction()
                                        .replace(R.id.content_frame, new TellAFriendFragment(), TellAFriendFragment.TAG_TELL_A_FRIEND_FRAGMENT)
                                        .commit();
                                break;
                            case (R.id.item_account):
                                currentItemId = FRAGMENT_ACCOUNT;
                                fragmentManager.beginTransaction()
                                        .replace(R.id.content_frame, new AccountFragment(), AccountFragment.TAG_ACCOUNT_FRAGMENT)
                                        .commit();
                                break;
                            case (R.id.item_help):
                                currentItemId = FRAGMENT_HELP;
                                fragmentManager.beginTransaction()
                                        .replace(R.id.content_frame, new HelpFragment(), HelpFragment.TAG_HELP_FRAGMENT)
                                        .commit();
                                break;
                            case (R.id.item_search):
                                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                                startActivity(intent);
                                break;
                        }
                    }
                }, DRAWER_CLOSE_DELAY_MS);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        }

        private void initDrawerHeader(String cashBack, String name, String email) {
            Button button = ButterKnife.findById(navigator, R.id.loginButton);
            button.setVisibility(View.INVISIBLE);
            TextView viewCashBack = ButterKnife.findById(navigator, R.id.totalEarned);
            viewCashBack.setText(String.format(stringFormat, cashBack));
            ((TextView) ButterKnife.findById(navigator, R.id.userName)).setText(name);
            ((TextView) ButterKnife.findById(navigator, R.id.userEmail)).setText(email);
        }

        private void initDrawerHeaderFree() {
            Button button = ButterKnife.findById(navigator, R.id.loginButton);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle loginBundle = new Bundle();
                    loginBundle.putString(Utilities.CALLING_ACTIVITY, "MainActivity");
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra(Utilities.LOGIN_BUNDLE, loginBundle);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
            });
        }

        private boolean isBackPressed() {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
            return true;
        }

        private Fragment getCurrentContent() {
            return fragmentManager.findFragmentById(R.id.content_frame);
        }
    }
}
