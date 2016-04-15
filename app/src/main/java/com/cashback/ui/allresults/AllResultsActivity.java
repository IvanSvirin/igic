package com.cashback.ui.allresults;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cashback.R;
import com.cashback.ui.components.FixedNestedScrollView;
import com.cashback.ui.components.WrapContentHeightViewPager;
import com.cashback.ui.featured.ExtraTabFragment;
import com.cashback.ui.featured.FavoritesTabFragment;
import com.cashback.ui.featured.HotDealsTabFragment;
import com.daimajia.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by I.Svirin on 4/14/2016.
 */
public class AllResultsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private TabsPagerAdapter tabsPagerAdapter;
    @Bind(R.id.tab_header)
    TabLayout tabLayout;
    @Bind(R.id.nested_scroll)
    FixedNestedScrollView nestedScrollView;
    @Bind(R.id.tab_content)
    WrapContentHeightViewPager tabViewPager;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_all_results);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.all_results);

        setupTabsView(getSupportFragmentManager());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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

    private void setupTabsView(FragmentManager mng) {
        TabsPagerAdapter tabsAdapter = new TabsPagerAdapter(mng);
        tabsAdapter.addTab(new HotDealsTabFragment(), getString(R.string.tab_stores).toUpperCase());
        tabsAdapter.addTab(new FavoritesTabFragment(), getString(R.string.tab_products).toUpperCase());
        tabsAdapter.addTab(new ExtraTabFragment(), getString(R.string.tab_coupons).toUpperCase());
        tabViewPager.setAdapter(tabsAdapter);
        tabLayout.setupWithViewPager(tabViewPager);
    }

    private class TabsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> aFragmentList = new ArrayList<>();
        private final List<String> aTitleList = new ArrayList<>();

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return aFragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return aTitleList.get(position);
        }

        @Override
        public int getCount() {
            return aFragmentList.size();
        }

        public void addTab(Fragment fragment, String title) {
            aFragmentList.add(fragment);
            aTitleList.add(title);
        }
    }
}
