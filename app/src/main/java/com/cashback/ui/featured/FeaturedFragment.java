package com.cashback.ui.featured;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cashback.R;
import com.cashback.rest.RestUtilities;
import com.cashback.ui.MainActivity;
import com.cashback.ui.allresults.SearchActivity;
import com.cashback.ui.components.FixedNestedScrollView;
import com.cashback.ui.components.WrapContentHeightViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FeaturedFragment extends Fragment {
    public static final String TAG_FEATURED_FRAGMENT = "I_featured_fragment";
    private FragmentUi fragmentUi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        RestUtilities.syncDistantData(this.getContext(), RestUtilities.TOKEN_ACCOUNT);
        RestUtilities.syncDistantData(this.getContext(), RestUtilities.TOKEN_MERCHANTS);
        RestUtilities.syncDistantData(this.getContext(), RestUtilities.TOKEN_EXTRAS);
        RestUtilities.syncDistantData(this.getContext(), RestUtilities.TOKEN_FAVORITES);
        RestUtilities.syncDistantData(this.getContext(), RestUtilities.TOKEN_COUPONS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_featured_common, container, false);
        fragmentUi = new FragmentUi(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = fragmentUi.getToolbar();
        ((MainActivity) getActivity()).setAssociateToolbar(toolbar);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.title_featured_fragment);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentUi.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_call_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class FragmentUi {
        private Context context;
        @Bind(R.id.tab_header)
        TabLayout tabLayout;
        @Bind(R.id.nested_scroll)
        FixedNestedScrollView nestedScrollView;
        @Bind(R.id.tab_content)
        ViewPager tabViewPager;
//        WrapContentHeightViewPager tabViewPager;
        @Bind(R.id.toolbar)
        Toolbar toolbar;

        public FragmentUi(FeaturedFragment fragment, View view) {
            this.context = fragment.getContext();
            ButterKnife.bind(this, view);
            setupTabsView(fragment.getChildFragmentManager());
        }

        private void setupTabsView(FragmentManager mng) {
            TabsPagerAdapter tabsAdapter = new TabsPagerAdapter(mng);
            tabsAdapter.addTab(HotDealsTabFragmentTest.newInstance(), getString(R.string.tab_hot_deals).toUpperCase());
            tabsAdapter.addTab(FavoritesTabFragmentTest.newInstance(), getString(R.string.tab_favorites).toUpperCase());
            tabsAdapter.addTab(ExtraTabFragmentTest.newInstance(), getString(R.string.tab_extra).toUpperCase());
            tabViewPager.setAdapter(tabsAdapter);
            tabLayout.setupWithViewPager(tabViewPager);
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }

        private Toolbar getToolbar() {
            return toolbar;
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

