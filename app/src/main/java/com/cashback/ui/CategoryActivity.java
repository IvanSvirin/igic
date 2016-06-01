package com.cashback.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.cashback.R;
import com.cashback.db.DataContract;
import com.cashback.model.Merchant;
import com.cashback.rest.event.CategoryMerchantsEvent;
import com.cashback.rest.event.MerchantsEvent;
import com.cashback.rest.request.MerchantsByCategoryRequest;
import com.cashback.rest.request.MerchantsRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by I.Svirin on 4/14/2016.
 */
public class CategoryActivity extends AppCompatActivity {
    private static final String SEARCH_KEY = "keyword_store";
    private UiActivity uiActivity;
    private ArrayList<Merchant> merchants = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        setContentView(R.layout.layout_categoryy);

        Intent intent = getIntent();
        long id = intent.getLongExtra("category_id", 1);
        new MerchantsByCategoryRequest(this, merchants, id).fetchData();

        uiActivity = new UiActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
        uiActivity.allStoresList.clearTextFilter();
        uiActivity.getAdapter().getFilter().filter(null);
    }

    public void onEvent(CategoryMerchantsEvent event) {
        if (event.isSuccess) {
            uiActivity.initListAdapter();
            uiActivity.initListHandler();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_filter, menu);
        MenuItem item = menu.findItem(R.id.action_filter);
        SearchView search = (SearchView) MenuItemCompat.getActionView(item);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    uiActivity.allStoresList.clearTextFilter();
                    uiActivity.getAdapter().getFilter().filter("");
                } else {
                    uiActivity.getAdapter().getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    uiActivity.allStoresList.clearTextFilter();
                    uiActivity.getAdapter().getFilter().filter("");
                } else {
                    uiActivity.getAdapter().getFilter().filter(newText);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    class UiActivity {
        private Context context;
        private AllStoresAdapter adapter;
        @Bind(R.id.toolbar)
        Toolbar toolbar;
        @Bind(R.id.all_stores_list)
        ListViewCompat allStoresList;

        public UiActivity(CategoryActivity categoryActivity) {
            this.context = categoryActivity;
            ButterKnife.bind(this, categoryActivity);

            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(getIntent().getStringExtra("category_name"));

            allStoresList.setTextFilterEnabled(true);
//            initListAdapter();
//            initListHandler();
        }

        private void initListAdapter() {
            adapter = new AllStoresAdapter(context, merchants);
            allStoresList.setAdapter(adapter);
        }

        private void initListHandler() {
            allStoresList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    long merchantId = merchants.get(position).getVendorId();
                    Intent intent = new Intent(parent.getContext(), StoreActivity.class);
                    intent.putExtra("vendor_id", merchantId);
                    startActivity(intent);
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

    public static class AllStoresAdapter extends BaseAdapter implements SectionIndexer, Filterable {
        private Context context;
        private ArrayList<Merchant> merchants;
        private ArrayList<Merchant> merchantsFiltered;
        protected SortedMap<Integer, String> sections = new TreeMap<>();
        ArrayList<Integer> sectionList = new ArrayList<>();
        private LayoutInflater layoutInflater;

        public AllStoresAdapter(Context context, ArrayList<Merchant> merchants) {
            this.context = context;
            this.merchants = merchants;
            Collections.sort(merchants, new Comparator<Merchant>() {
                @Override
                public int compare(Merchant lhs, Merchant rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
            init(context, null);
        }

        private void init(Context context, SortedMap<Integer, String> sections) {
            layoutInflater = LayoutInflater.from(context);
            if (sections != null) {
                this.sections = sections;
            } else {
                buildSections();
            }
        }

        @Override
        public int getCount() {
            return merchantsFiltered == null ? merchants.size() : merchantsFiltered.size();
        }

        @Override
        public Object getItem(int position) {
            return merchantsFiltered == null ? merchants.get(position) : merchantsFiltered.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        protected LayoutInflater getLayoutInflater() {
            return layoutInflater;
        }

        private void buildSections() {
            sections = build();
            if (sections == null) {
                sections = new TreeMap<>();
            }
        }

        protected SortedMap<Integer, String> build() {
            TreeMap<Integer, String> sections = new TreeMap<>();
            for (int i = 0; i < merchants.size(); i++) {
                String name = merchants.get(i).getName();
                String section = name.toUpperCase().substring(0, 1);
                if (section.matches("\\d")) {
                    section = "#";
                }
                if (!sections.containsValue(section)) {
                    sections.put(i, section);
                }
            }
            return sections;
        }

        @Override
        public void notifyDataSetChanged() {
            buildSections();
            sectionList.clear();
            super.notifyDataSetChanged();
        }

        /**
         * Clears out all section data before rebuilding it.
         */
        @Override
        public void notifyDataSetInvalidated() {
            buildSections();
            sectionList.clear();
            super.notifyDataSetInvalidated();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_store_list_extend, parent, false);
                ViewHolder holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            if (sections.containsKey(position)) {
                String sort = sections.get(position);
                holder.sortColumn.setText(sort);
                if (position > 0)
                    holder.sortDivider.setBackgroundResource(R.color.primaryLight);
            } else {
                holder.sortColumn.setText("");
                holder.sortDivider.setBackgroundResource(android.R.color.transparent);
            }
            String name = merchantsFiltered == null ? merchants.get(position).getName() : merchantsFiltered.get(position).getName();
            holder.shopName.setText(name);
            float commission = merchantsFiltered == null ? merchants.get(position).getCommission() : merchantsFiltered.get(position).getCommission();
            holder.shopCommission.setText(String.valueOf(commission + "%"));
            return convertView;
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
            return sectionIndex < sectionList.size() ? sectionList.get(sectionIndex) : getCount();
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

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    if (constraint == null || constraint.length() == 0) {
                        //no search, so just return all the data
                        results.count = merchants.size();
                        results.values = merchants;
                    } else {//do the search
                        ArrayList<Merchant> resultsData = new ArrayList<>();
                        String searchStr = constraint.toString().toUpperCase();
                        for (Merchant m : merchants)
                            if (m.getName().toUpperCase().contains(searchStr)) resultsData.add(m);
                        results.count = resultsData.size();
                        results.values = resultsData;
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    merchantsFiltered = (ArrayList<Merchant>) results.values;
                    notifyDataSetChanged();
                }
            };
        }
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

