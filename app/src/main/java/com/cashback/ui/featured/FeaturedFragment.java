package com.cashback.ui.featured;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cashback.R;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_featuredd, container, false);
        fragmentUi = new FragmentUi(this, view);
        return view;
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

    public class FragmentUi {
        private Context context;
        @Bind(R.id.tab_header)
        TabLayout tabLayout;
        @Bind(R.id.nested_scroll)
        FixedNestedScrollView uNestedScroll;
        @Bind(R.id.tab_content)
        WrapContentHeightViewPager tabViewPager;
        @Bind(R.id.toolbar)
        Toolbar toolbar;
        @Bind(R.id.img_carousel)
        SliderLayout uImgSlider;

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
