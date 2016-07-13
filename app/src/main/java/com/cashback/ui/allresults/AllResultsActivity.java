package com.cashback.ui.allresults;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cashback.App;
import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.model.Coupon;
import com.cashback.model.Merchant;
import com.cashback.model.Product;
import com.cashback.rest.event.SearchEvent;
import com.cashback.rest.request.SearchRequest;
import com.cashback.ui.components.FixedNestedScrollView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class AllResultsActivity extends AppCompatActivity {
    private UiActivity uiActivity;
    private static String searchingWord = "";
    public static ArrayList<Merchant> storesArray;
    public static ArrayList<Product> productsArray;
    public static ArrayList<Coupon> dealsArray;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_all_results_common);

        Intent intent = getIntent();
        String newWord = intent.getStringExtra("searching_word");
        if (!searchingWord.equals(newWord)) {
            searchingWord = newWord;
            storesArray = new ArrayList<>();
            productsArray = new ArrayList<>();
            dealsArray = new ArrayList<>();
            new SearchRequest(this, searchingWord, storesArray, productsArray, dealsArray).fetchData();
            progressDialog = Utilities.onCreateProgressDialog(this);
            progressDialog.show();
        } else {
            uiActivity = new UiActivity(this);
        }
        //Google Analytics
        App app = (App) getApplication();
        Tracker tracker = app.getDefaultTracker();
        tracker.setScreenName("All Search Results");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(SearchEvent event) {
        progressDialog.dismiss();
        uiActivity = new UiActivity(this);
        if (!event.isSuccess) {
            Utilities.showFailNotification(event.message, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView search = (SearchView) MenuItemCompat.getActionView(item);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                } else {
                    finish();
                    Intent intent = new Intent(AllResultsActivity.this, AllResultsActivity.class);
                    intent.putExtra("searching_word", query);
                    startActivity(intent);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                } else {
                }
                return false;
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class UiActivity {
        private Context context;
        private TabsPagerAdapter tabsPagerAdapter;
        @Bind(R.id.tab_header)
        TabLayout tabLayout;
        @Bind(R.id.nested_scroll)
        FixedNestedScrollView nestedScrollView;
        @Bind(R.id.tab_content)
        ViewPager tabViewPager;
        @Bind(R.id.toolbar)
        Toolbar toolbar;

        public UiActivity(AllResultsActivity activity) {
            this.context = activity;
            ButterKnife.bind(this, activity);
            setupTabsView(getSupportFragmentManager());

            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            setTitle(getResources().getString(R.string.all_results) + " \"" + searchingWord + "\"");
        }

        private void setupTabsView(FragmentManager mng) {
            TabsPagerAdapter tabsAdapter = new TabsPagerAdapter(mng);
            tabsAdapter.addTab(StoresTabFragment.newInstance(), getString(R.string.tab_stores).toUpperCase());
            tabsAdapter.addTab(ProductsTabFragment.newInstance(), getString(R.string.tab_products).toUpperCase());
            tabsAdapter.addTab(CouponsTabFragment.newInstance(), getString(R.string.tab_coupons).toUpperCase());
            tabViewPager.setAdapter(tabsAdapter);
            tabLayout.setupWithViewPager(tabViewPager);
        }
    }

    private class TabsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> titleList = new ArrayList<>();

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addTab(Fragment fragment, String title) {
            fragmentList.add(fragment);
            titleList.add(title);
        }
    }
}
