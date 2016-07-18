package com.cashback.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cashback.App;
import com.cashback.R;
import db.DataContract;
import rest.RestUtilities;
import com.cashback.rest.event.CategoriesEvent;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import ui.MainActivity;

public class CategoriesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG_CATEGORIES_FRAGMENT = "I_categories_fragment";
    private FragmentUi fragmentUi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestUtilities.syncDistantData(this.getContext(), RestUtilities.TOKEN_CATEGORIES);
        //Google Analytics
        App app = (App) getActivity().getApplication();
        Tracker tracker = app.getDefaultTracker();
        tracker.setScreenName("Categories");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_categories, container, false);
        fragmentUi = new FragmentUi(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = fragmentUi.getToolbar();
        ((MainActivity) getActivity()).setAssociateToolbar(toolbar);
        getActivity().setTitle(R.string.title_categories_fragment);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MainActivity.CATEGORIES_LOADER, null, this);
    }

        @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentUi.unbind();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if (id == MainActivity.CATEGORIES_LOADER) {
            loader = new CursorLoader(getActivity());
            loader.setUri(DataContract.URI_CATEGORIES);
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

    public void onEvent(CategoriesEvent event) {
        if (event.isSuccess) {
            getLoaderManager().restartLoader(MainActivity.CATEGORIES_LOADER, null, this);
        }
    }

    public class FragmentUi {
        private CategoriesAdapter adapter;
        @Bind(R.id.toolbar)
        Toolbar toolbar;
        @Bind(R.id.categories_list)
        ListViewCompat categoriesList;

        public FragmentUi(View view) {
            ButterKnife.bind(this, view);
            initListAdapter();
        }

        private void initListAdapter() {
            adapter = new CategoriesAdapter(getActivity(), null, 0);
            adapter.setOnCategoryClickListener(new CategoriesAdapter.OnCategoryClickListener() {
                @Override
                public void onCategoryClick(String categoryName, long id) {
                    Intent intent = new Intent(getContext(), CategoryActivity.class);
                    intent.putExtra("category_name", categoryName);
                    intent.putExtra("category_id", id);
                    startActivity(intent);
                }
            });
            categoriesList.setAdapter(adapter);
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }

        private Toolbar getToolbar() {
            return toolbar;
        }

        @OnClick({R.id.allStoresFrame})
        public void onClicks(View view) {
            switch (view.getId()) {
                case R.id.allStoresFrame:
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new AllStoresFragment(), AllStoresFragment.TAG_ALL_STORES_FRAGMENT)
                            .commit();
                    break;
            }
        }
    }

    public static class CategoriesAdapter extends CursorAdapter {

        private OnCategoryClickListener listener;

        public CategoriesAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.item_category_list, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            return convertView;
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            final String name = cursor.getString(cursor.getColumnIndex(DataContract.Categories.COLUMN_NAME));
            final long id = cursor.getLong(cursor.getColumnIndex(DataContract.Categories.COLUMN_CATEGORY_ID));
            holder.categoryName.setText(name);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCategoryClick(name, id);
                }
            });
        }

        public void setOnCategoryClickListener(OnCategoryClickListener listener) {
            this.listener = listener;
        }

        public interface OnCategoryClickListener {
            void onCategoryClick(String categoryName, long id);
        }

        public static class ViewHolder {
            @Bind(R.id.categoryName)
            TextView categoryName;

            public ViewHolder(View convertView) {
                ButterKnife.bind(this, convertView);
            }
        }
    }
}
