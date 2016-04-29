package com.cashback.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cashback.R;
import com.cashback.db.DataContract;
import com.cashback.rest.event.CouponsEvent;
import com.cashback.ui.components.NestedListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by I.Svirin on 4/11/2016.
 */
public class AllStoresFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG_ALL_STORES_FRAGMENT = "I_all_stores_fragment";
    private FragmentUi fragmentUi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_all_stores, container, false);
        fragmentUi = new FragmentUi(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = fragmentUi.getToolbar();
        ((MainActivity) getActivity()).setAssociateToolbar(toolbar);
        getActivity().setTitle(R.string.title_all_stores_fragment);

        // TODO: 4/19/2016 TEST - will be deleted
        getLoaderManager().initLoader(MainActivity.COUPONS_LOADER, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
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
        // TODO: 4/19/2016 TEST - will be deleted
        if (id == MainActivity.COUPONS_LOADER) {
            loader = new CursorLoader(getActivity());
            loader.setUri(DataContract.URI_COUPONS);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        fragmentUi.adapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        fragmentUi.adapter.changeCursor(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
    }

    // TODO: 4/19/2016 TEST - will be deleted
    public void onEvent(CouponsEvent event) {
        if (event.isSuccess) {
            getLoaderManager().restartLoader(MainActivity.COUPONS_LOADER, null, this);
        }
    }

    public class FragmentUi {
        private Context context;
        private AllStoresAdapter adapter;
        @Bind(R.id.toolbar)
        Toolbar toolbar;
        @Bind(R.id.all_stores_list)
        NestedListView allStoresList;

        public FragmentUi(AllStoresFragment fragment, View view) {
            this.context = fragment.getContext();
            ButterKnife.bind(this, view);
            initListAdapter();
        }

        private void initListAdapter() {
            adapter = new AllStoresAdapter(getActivity(), null, 0);
            adapter.setOnStoreClickListener(new AllStoresAdapter.OnStoreClickListener() {
                @Override
                public void onStoreClick(String categoryName) {
                }
            });
            allStoresList.setAdapter(adapter);
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }

        private Toolbar getToolbar() {
            return toolbar;
        }
    }

    public static class AllStoresAdapter extends CursorAdapter {

        private OnStoreClickListener listener;

        public AllStoresAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.item_store_list, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            return convertView;
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();

            // TODO: 4/19/2016 TEST - will be deleted
            final String name = "Store name";
            final String commission = "5.0 %";

            holder.storeName.setText(name);
            holder.storeCommission.setText(commission);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onStoreClick(name);
                }
            });
        }

        public void setOnStoreClickListener(OnStoreClickListener listener) {
            this.listener = listener;
        }

        public interface OnStoreClickListener {
            void onStoreClick(String categoryName);
        }

        public static class ViewHolder {
            @Bind(R.id.storeName)
            TextView storeName;
            @Bind(R.id.storeCommission)
            TextView storeCommission;

            public ViewHolder(View convertView) {
                ButterKnife.bind(this, convertView);
            }
        }
    }
}
