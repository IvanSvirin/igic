package com.cashback.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.ui.StoreActivity;
import com.cashback.ui.allresults.AllResultsActivity;
import com.cashback.ui.web.BrowserDealsActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import ui.MainActivity;
import ui.login.SignInFragment;
import ui.login.SignUpFragment;

public class LoginActivity extends AppCompatActivity {
    @Bind(R.id.container)
    ViewPager viewPager;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tabs)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Bundle loginBundle = getIntent().getBundleExtra(Utilities.LOGIN_BUNDLE);
            Intent intent;
            switch (loginBundle.getString(Utilities.CALLING_ACTIVITY)) {
                case "MainActivity":
                    intent = new Intent(this, MainActivity.class);
                    break;
                case "AllResultsActivity":
                    intent = new Intent(this, AllResultsActivity.class);
                    break;
                case "StoreActivity":
                    intent = new Intent(this, StoreActivity.class);
                    intent.putExtra(Utilities.VENDOR_ID, loginBundle.getLong(Utilities.VENDOR_ID));
                    break;
                case "BrowserDealsActivity":
                    intent = new Intent(this, MainActivity.class);
                    break;
                default:
                    intent = new Intent(this, MainActivity.class);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 1) {
                return new SignUpFragment();
            } else {
                return new SignInFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.login_second_page_name);
                case 1:
                    return getString(R.string.login_first_page_name);
            }
            return null;
        }
    }
}
