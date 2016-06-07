package com.cashback.ui.account;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.cashback.R;
import com.viewpagerindicator.CirclePageIndicator;

import butterknife.ButterKnife;

public class TourActivity extends AppCompatActivity {
    private static final int TOTAL_PAGE_COUNT = 4;
    private static final int SCORE_RETAINED_FRAGMENT = 2;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tour);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.how_it_works);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        ButterKnife.bind(this);


        viewPager = (ViewPager) findViewById(R.id.tour_pager);
        CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.tour_pager_indicator);
        FragmentPagerAdapter adapter = new TourFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(SCORE_RETAINED_FRAGMENT);
        indicator.setViewPager(viewPager);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class TourFragmentPagerAdapter extends FragmentPagerAdapter {
        public TourFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return StepFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return TOTAL_PAGE_COUNT;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }
}
