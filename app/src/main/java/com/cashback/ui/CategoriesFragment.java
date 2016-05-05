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

import com.cashback.R;
import com.cashback.db.DataContract;
import com.cashback.model.Category;
import com.cashback.rest.RestUtilities;
import com.cashback.rest.event.CategoriesEvent;
import com.cashback.rest.event.CouponsEvent;
import com.cashback.ui.components.NestedListView;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by I.Svirin on 4/11/2016.
 */
public class CategoriesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG_CATEGORIES_FRAGMENT = "I_categories_fragment";
    private FragmentUi fragmentUi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        RestUtilities.syncDistantData(this.getContext(), RestUtilities.TOKEN_CATEGORIES);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_categories, container, false);
        fragmentUi = new FragmentUi(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = fragmentUi.getToolbar();
        ((MainActivity) getActivity()).setAssociateToolbar(toolbar);
        getActivity().setTitle(R.string.title_categories_fragment);

        // TODO: 4/19/2016 TEST - will be deleted
        getLoaderManager().initLoader(MainActivity.COUPONS_LOADER, null, this);
//        getLoaderManager().initLoader(MainActivity.CATEGORIES_LOADER, null, this);
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
//        if (id == MainActivity.CATEGORIES_LOADER) {
//            loader = new CursorLoader(getActivity());
//            loader.setUri(DataContract.URI_CATEGORIES);
//        }
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

    // TODO: 4/19/2016 TEST - will be deleted
    public void onEvent(CouponsEvent event) {
        if (event.isSuccess) {
            getLoaderManager().restartLoader(MainActivity.COUPONS_LOADER, null, this);
        }
    }
//    public void onEvent(CategoriesEvent event) {
//        if (event.isSuccess) {
//            getLoaderManager().restartLoader(MainActivity.CATEGORIES_LOADER, null, this);
//        }
//    }

    public class FragmentUi {
        private Context context;
        private CategoriesAdapter adapter;
        @Bind(R.id.toolbar)
        Toolbar toolbar;
        @Bind(R.id.categories_list)
        NestedListView categoriesList;

        public FragmentUi(CategoriesFragment fragment, View view) {
            this.context = fragment.getContext();
            ButterKnife.bind(this, view);
            initListAdapter();
        }

        private void initListAdapter() {
            adapter = new CategoriesAdapter(getActivity(), null, 0);
            adapter.setOnCategoryClickListener(new CategoriesAdapter.OnCategoryClickListener() {
                @Override
                public void onCategoryClick(String categoryName) {
                    Intent intent = new Intent(getContext(), CategoryActivity.class);
                    intent.putExtra("category_name", categoryName);
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
            // TODO: 4/19/2016 TEST - will be deleted
            final String name = "Category" + String.valueOf(new Random().nextInt(20));
//            final String name = cursor.getString(cursor.getColumnIndex(DataContract.Categories.COLUMN_NAME));
            holder.categoryName.setText(name);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCategoryClick(name);
                }
            });
        }

        public void setOnCategoryClickListener(OnCategoryClickListener listener) {
            this.listener = listener;
        }

        public interface OnCategoryClickListener {
            void onCategoryClick(String categoryName);
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
