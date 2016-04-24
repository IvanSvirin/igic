package com.cashback.ui.featured;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
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
import com.cashback.Utilities;
import com.cashback.rest.RestUtilities;
import com.cashback.rest.event.ImageEvent;
import com.cashback.ui.MainActivity;
import com.cashback.ui.allresults.AllResultsActivity;
import com.cashback.ui.components.FixedNestedScrollView;
import com.cashback.ui.components.WrapContentHeightViewPager;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

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
//        RestUtilities.syncDistantData(this.getContext(), RestUtilities.TOKEN_MERCHANTS);
        RestUtilities.syncDistantData(this.getContext(), RestUtilities.TOKEN_COUPONS);
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
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            fragmentUi.changeImageSlider();
            fragmentUi.bindImgSlider();
        }
    }

    @Override
    public void onStop() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            fragmentUi.unbindImgSlider();
        }
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentUi.unbind();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
//        if (id == MainActivity.IMAGE_LOADER) {
//            loader = new CursorLoader(getActivity());
//            loader.setUri(DataContract.URI_IMAGES);
//        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == MainActivity.IMAGE_LOADER) {
//            fragmentUi.changeImageSlider(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        fragmentUi.changeImageSlider(null);
    }

    public void onEvent(ImageEvent event) {
        if (event.isSuccess) {
            getLoaderManager().restartLoader(MainActivity.IMAGE_LOADER, null, this);
        }
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
        private final int DURATION_SLIDER_TRANSITION = 250;
        private final long DURATION_DISPLAY_SLIDER = 3500;

        private Context context;
        @Bind(R.id.tab_header)
        TabLayout tabLayout;
        @Bind(R.id.nested_scroll)
        FixedNestedScrollView nestedScrollView;
        @Bind(R.id.tab_content)
        WrapContentHeightViewPager tabViewPager;
        @Bind(R.id.toolbar)
        Toolbar toolbar;
        //        @Bind(R.id.img_carousel)
//        SliderLayout sliderLayout;
        SliderLayout sliderLayout;

        public FragmentUi(FeaturedFragment fragment, View view) {
            this.context = fragment.getContext();
            ButterKnife.bind(this, view);
            setupTabsView(fragment.getChildFragmentManager());
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                sliderLayout = (SliderLayout) view.findViewById(R.id.img_carousel);
                initImageSlider();
            }
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
            sliderLayout.setPresetTransformer(SliderLayout.Transformer.ZoomOutSlide);
            sliderLayout.setSliderTransformDuration(DURATION_SLIDER_TRANSITION, new LinearOutSlowInInterpolator());
            sliderLayout.setDuration(DURATION_DISPLAY_SLIDER);
            sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        }

        private void changeImageSlider() {
            if (sliderLayout != null) {
                sliderLayout.stopAutoCycle();
                sliderLayout.removeAllSliders();
                sliderLayout.setVisibility(View.INVISIBLE);
            }
            DefaultSliderView itemSlider;

            itemSlider = new DefaultSliderView(context);
            itemSlider.errorDisappear(true);
            itemSlider.image(R.drawable.discover);
            itemSlider.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    if (Utilities.isLoggedIn(context)) {
                    }
                }
            });
            sliderLayout.addSlider(itemSlider);

            itemSlider = new DefaultSliderView(context);
            itemSlider.errorDisappear(true);
            itemSlider.image(R.drawable.great);
            itemSlider.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    if (Utilities.isLoggedIn(context)) {
                    }
                }
            });
            sliderLayout.addSlider(itemSlider);

            itemSlider = new DefaultSliderView(context);
            itemSlider.errorDisappear(true);
            itemSlider.image(R.drawable.save);
            itemSlider.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    if (Utilities.isLoggedIn(context)) {
                    }
                }
            });
            sliderLayout.addSlider(itemSlider);

            itemSlider = new DefaultSliderView(context);
            itemSlider.errorDisappear(true);
            itemSlider.image(R.drawable.travel);
            itemSlider.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    if (Utilities.isLoggedIn(context)) {
                    }
                }
            });
            sliderLayout.addSlider(itemSlider);

            sliderLayout.startAutoCycle();
            sliderLayout.setVisibility(View.VISIBLE);
        }

        private void bindImgSlider() {
            if (sliderLayout != null)
                sliderLayout.startAutoCycle();
        }

        private void unbindImgSlider() {
            if (sliderLayout != null)
                sliderLayout.stopAutoCycle();
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

