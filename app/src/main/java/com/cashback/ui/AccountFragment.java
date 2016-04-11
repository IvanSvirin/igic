package com.cashback.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cashback.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by I.Svirin on 4/11/2016.
 */
public class AccountFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG_ACCOUNT_FRAGMENT = "I_account_fragment";
    private FragmentUi fragmentUi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_account, container, false);
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
        getActivity().setTitle(R.string.item_account);
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
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.options_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_rate_the_app:
                break;
            case R.id.action_logout:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class FragmentUi {
        private Context context;
        @Bind(R.id.toolbar)
        Toolbar toolbar;

        public FragmentUi(AccountFragment fragment, View view) {
            this.context = fragment.getContext();
            ButterKnife.bind(this, view);
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }

        private Toolbar getToolbar() {
            return toolbar;
        }
    }
}
