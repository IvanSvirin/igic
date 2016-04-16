package com.cashback.ui.featured;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cashback.R;
import com.cashback.rest.RestUtilities;
import com.cashback.ui.MainActivity;
import com.cashback.ui.allresults.AllResultsActivity;
import com.cashback.ui.components.FixedNestedScrollView;
import com.cashback.ui.components.WrapContentHeightViewPager;
import com.daimajia.slider.library.SliderLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by I.Svirin on 4/7/2016.
 */
public class FeaturedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG_FEATURED_FRAGMENT = "I_featured_fragment";
    private FragmentUi fragmentUi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        RestUtilities.syncDistantData(this.getContext(), RestUtilities.TOKEN_ACCOUNT);
        RestUtilities.syncDistantData(this.getContext(), RestUtilities.TOKEN_HOT_DEALS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_featuredd, container, false);
        fragmentUi = new FragmentUi(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        getLoaderManager().initLoader(MainActivity.IMAGE_LOADER, null, this);
        Toolbar toolbar = fragmentUi.getToolbar();
        ((MainActivity) getActivity()).setAssociateToolbar(toolbar);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.title_featured_fragment);
//        EventBus.getDefault().register(this);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView search = (SearchView) MenuItemCompat.getActionView(item);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getContext(), AllResultsActivity.class);
                startActivity(intent);
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
    }

    public class FragmentUi {
        private Context context;
        @Bind(R.id.tab_header)
        TabLayout tabLayout;
        @Bind(R.id.nested_scroll)
        FixedNestedScrollView nestedScrollView;
        @Bind(R.id.tab_content)
        WrapContentHeightViewPager tabViewPager;
        @Bind(R.id.toolbar)
        Toolbar toolbar;
        @Bind(R.id.img_carousel)
        SliderLayout sliderLayout;

        public FragmentUi(FeaturedFragment fragment, View view) {
            this.context = fragment.getContext();
            ButterKnife.bind(this, view);
            setupTabsView(fragment.getChildFragmentManager());
            initImageSlider();
        }

        private void setupTabsView(FragmentManager mng) {
            TabsPagerAdapter tabsAdapter = new TabsPagerAdapter(mng);
            tabsAdapter.addTab(new HotDealsTabFragment(), getString(R.string.tab_hot_deals).toUpperCase());
            tabsAdapter.addTab(new FavoritesTabFragment(), getString(R.string.tab_favorites).toUpperCase());
            tabsAdapter.addTab(new ExtraTabFragment(), getString(R.string.tab_extra).toUpperCase());
            tabViewPager.setAdapter(tabsAdapter);
            tabLayout.setupWithViewPager(tabViewPager);
        }

        private void initImageSlider() {
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }

        private Toolbar getToolbar() {
            return toolbar;
        }
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
