package com.cashback.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
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
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.cashback.R;
import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.rest.event.MerchantsEvent;
import com.cashback.ui.components.NestedListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by I.Svirin on 4/11/2016.
 */
public class AllStoresFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG_ALL_STORES_FRAGMENT = "I_all_stores_fragment";
    private static final String SEARCH_KEY = "keyword_store";
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
        EventBus.getDefault().register(this);
        getLoaderManager().initLoader(MainActivity.MERCHANTS_LOADER, null, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        fragmentUi.allStoresList.clearTextFilter();
        fragmentUi.getAdapter().getFilter().filter(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        fragmentUi.unbind();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String searchFilter = null;
        if (args != null)
            searchFilter = args.getString(SEARCH_KEY);

        CursorLoader loader = null;
        if (id == MainActivity.MERCHANTS_LOADER) {
            loader = new CursorLoader(getActivity());
            loader.setUri(DataContract.URI_MERCHANTS);
            String projection[] = new String[]{
                    DataContract.Merchants._ID,
                    DataContract.Merchants.COLUMN_VENDOR_ID,
                    DataContract.Merchants.COLUMN_NAME,
                    DataContract.Merchants.COLUMN_COMMISSION,
            };
            loader.setProjection(projection);
            if (searchFilter != null) {
                loader.setSelection(DataContract.Merchants.COLUMN_NAME + " LIKE '%" + searchFilter + "%'");
            }
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        fragmentUi.getAdapter().swapCursor(data);
        if (data == null || data.getCount() == 0) {
            // TODO: 4/19/2016 TEST - will be deleted
            createExampleData();
//            new MerchantsRequest(getActivity()).fetchData();
        }
    }

    // TODO: 4/19/2016 TEST - will be deleted
    private void createExampleData() {
        List<ContentValues> listMerchantsValues = new ArrayList<>();
        ContentValues values;

        for (int i = 0; i < 20; i++) {
            values = new ContentValues();
            values.put(DataContract.Merchants.COLUMN_VENDOR_ID, String.valueOf(new Random().nextInt(1000)));
            char ch1 = (char) (new Random().nextInt(26) + 65);
            char ch2 = (char) (new Random().nextInt(26) + 97);
            values.put(DataContract.Merchants.COLUMN_NAME, String.valueOf(ch1) + String.valueOf(ch2) + "Store");
            values.put(DataContract.Merchants.COLUMN_COMMISSION, "5.0 %");
            listMerchantsValues.add(values);
        }
        DataInsertHandler handler = new DataInsertHandler(getContext(), getContext().getContentResolver());
        if (!DataInsertHandler.IS_FILLING_MERCHANT_TABLE) {
            handler.startBulkInsert(DataInsertHandler.MERCHANTS_TOKEN, false, DataContract.URI_MERCHANTS,
                    listMerchantsValues.toArray(new ContentValues[listMerchantsValues.size()]));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        fragmentUi.getAdapter().swapCursor(null);
    }

    public void onEvent(MerchantsEvent event) {
        if (event.isSuccess) {
            getLoaderManager().restartLoader(MainActivity.MERCHANTS_LOADER, null, this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu_filter, menu);
        MenuItem item = menu.findItem(R.id.action_filter);
        SearchView search = (SearchView) MenuItemCompat.getActionView(item);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    fragmentUi.allStoresList.clearTextFilter();
                    fragmentUi.getAdapter().getFilter().filter("");
                } else {
                    fragmentUi.getAdapter().getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    fragmentUi.allStoresList.clearTextFilter();
                    fragmentUi.getAdapter().getFilter().filter("");
                } else {
                    fragmentUi.getAdapter().getFilter().filter(newText);
                }
                return false;
            }
        });
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
            allStoresList.setTextFilterEnabled(false);

            initListAdapter();
            initListHandler();
        }

        private void initListAdapter() {
            adapter = new AllStoresAdapter(getActivity(), null, 0);
            adapter.setFilterQueryProvider(new FilterQueryProvider() {
                @Override
                public Cursor runQuery(CharSequence constraint) {
                    Bundle bundle = new Bundle();
                    bundle.putString(SEARCH_KEY, constraint.toString());
                    getLoaderManager().restartLoader(MainActivity.MERCHANTS_LOADER, bundle, AllStoresFragment.this);
                    return null;
                }
            });
            allStoresList.setAdapter(adapter);
        }

        private void initListHandler() {
            allStoresList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // if (!uAdapter.isSection(position)) {
                    //   int correctPosition = uAdapter.getCursorPositionWithoutSections(position);
                    Cursor cursor = adapter.getCursor();
                    cursor.moveToPosition(position); //cursor.moveToPosition(correctPosition);
                    int merchantId = cursor.getInt(cursor.getColumnIndex(DataContract.Merchants.COLUMN_VENDOR_ID));
                    String merchantName = cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_NAME));
                    Intent intent = new Intent(parent.getContext(), StoreActivity.class);
                    intent.putExtra(StoreActivity.STORE_ID, merchantId);
                    intent.putExtra(StoreActivity.STORE_NAME, merchantName);
//                    startActivity(intent);
                    // }
                }
            });
        }

        private AllStoresAdapter getAdapter() {
            return adapter;
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }

        private Toolbar getToolbar() {
            return toolbar;
        }
    }

    public static class AllStoresAdapter extends CursorAdapter implements SectionIndexer {
        protected SortedMap<Integer, String> sections = new TreeMap<>();
        ArrayList<Integer> sectionList = new ArrayList<>();
        private LayoutInflater layoutInflater;

        public AllStoresAdapter(Context context, Cursor c, boolean autoRequiry) {
            super(context, c, autoRequiry);
            init(context, null);
        }

        public AllStoresAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            init(context, null);
        }

        protected AllStoresAdapter(Context context, Cursor c, boolean autoRequiry, SortedMap<Integer, String> sections) {
            super(context, c, autoRequiry);
            init(context, sections);
        }

        private void init(Context context, SortedMap<Integer, String> sections) {
            layoutInflater = LayoutInflater.from(context);
            if (sections != null) {
                this.sections = sections;
            } else {
                buildSections();
            }
        }

        protected LayoutInflater getLayoutInflater() {
            return layoutInflater;
        }

        private void buildSections() {
            if (isOpenCursor()) {
                Cursor cursor = getCursor();
                sections = buildSections(cursor);
                if (sections == null) {
                    sections = new TreeMap<>();
                }
            }
        }

        protected SortedMap<Integer, String> buildSections(Cursor cursor) {
            TreeMap<Integer, String> sections = new TreeMap<>();
            int columnIndex = cursor.getColumnIndex(DataContract.Merchants.COLUMN_NAME);
            cursor.moveToFirst();
            do {
                String name = cursor.getString(columnIndex);
                String section = name.toUpperCase().substring(0, 1);
                if (section.matches("\\d")) {
                    section = "#";
                }
                if (!sections.containsValue(section)) {
                    sections.put(cursor.getPosition(), section);
                }
            } while (cursor.moveToNext() && isOpenCursor());
            return sections;
        }

        @Override
        public void notifyDataSetChanged() {
            if (isOpenCursor()) {
                buildSections();
                sectionList.clear();
            }
            super.notifyDataSetChanged();
        }

        /**
         * Clears out all section data before rebuilding it.
         */
        @Override
        public void notifyDataSetInvalidated() {
            if (isOpenCursor()) {
                buildSections();
                sectionList.clear();
            }
            super.notifyDataSetInvalidated();
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.item_store_list_extend, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            return convertView;
        }

        @Override
        public void bindView(View convertView, Context context, final Cursor cursor) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            int position = cursor.getPosition();
            if (sections.containsKey(position)) {
                String sort = sections.get(position);
                holder.sortColumn.setText(sort);
                if (position > 0)
                    holder.sortDivider.setBackgroundResource(R.color.primaryLight);
            } else {
                holder.sortColumn.setText("");
                holder.sortDivider.setBackgroundResource(android.R.color.transparent);
            }
            String name = cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_NAME));
            holder.shopName.setText(name.trim());
            String commission = cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_COMMISSION));
            holder.shopCommission.setText(commission.trim());
        }

        protected boolean isOpenCursor() {
            Cursor cursor = getCursor();
            if (cursor == null || cursor.isClosed() || cursor.getCount() < 1) {
                swapCursor(null);
                return false;
            }
            return true;
        }

        @Override
        public Object[] getSections() {
            Collection<String> sectionsCollection = sections.values();
            String[] objects = sectionsCollection.toArray(new String[sectionsCollection.size()]);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                for (int i = 0; i < objects.length; i++) {
                    if (objects[i].length() >= 3) {
                        objects[i] = objects[i].substring(0, 3);
                    }
                }
            }
            return objects;
        }

        @Override
        public int getPositionForSection(int sectionIndex) {
            if (sectionList.size() == 0) {
                for (Integer key : sections.keySet()) {
                    sectionList.add(key);
                }
            }
            return sectionIndex < sectionList.size() ? sectionList.get(sectionIndex) : mCursor.getCount();
        }

        @Override
        public int getSectionForPosition(int listPosition) {
            String[] secs = (String[]) getSections();
            boolean isAim = false;
            int sectionIndex = 0;
            for (int sectionPosition : sections.keySet()) {
                if (listPosition > sectionPosition) {
                    sectionIndex++;
                } else if (listPosition == sectionPosition) {
                    isAim = true;
                } else {
                    break;
                }
            }
            sectionIndex = isAim ? sectionIndex : Math.max(sectionIndex - 1, 0);
            return sectionIndex < secs.length ? sectionIndex : 0;
        }

        public static class ViewHolder {
            @Bind(R.id.sortDivider)
            TextView sortDivider;
            @Bind(R.id.sortColumn)
            TextView sortColumn;
            @Bind(R.id.shopName)
            TextView shopName;
            @Bind(R.id.shopCommission)
            TextView shopCommission;

            public ViewHolder(View convertView) {
                ButterKnife.bind(this, convertView);
            }
        }
    }
}
