package ui.account;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.cashback.App;
import com.cashback.R;
import com.cashback.Utilities;
import db.DataContract;

import com.cashback.rest.event.OrdersEvent;
import com.cashback.rest.request.CashBackOrdersRequest;
import com.cashback.ui.components.NestedListView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import ui.MainActivity;

public class ShoppingReportActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private UiActivity uiActivity;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        setContentView(R.layout.layout_your_order_histoy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.shopping_report);

        uiActivity = new UiActivity(this);
        //Google Analytics
        App app = (App) getApplication();
        Tracker tracker = app.getDefaultTracker();
        tracker.setScreenName("Shopping Report");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(MainActivity.ORDERS_LOADER, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        uiActivity.unbind();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if (id == MainActivity.ORDERS_LOADER) {
            loader = new CursorLoader(this);
            loader.setUri(DataContract.URI_ORDERS);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        uiActivity.getAdapter().swapCursor(data);
        if (data == null || data.getCount() == 0) {
            new CashBackOrdersRequest(this).fetchData();
            progressDialog = Utilities.onCreateProgressDialog(this);
            progressDialog.show();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        uiActivity.getAdapter().swapCursor(null);
    }

    public void onEvent(OrdersEvent event) {
        progressDialog.dismiss();
        if (event.isSuccess) {
            getSupportLoaderManager().restartLoader(MainActivity.ORDERS_LOADER, null, this);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class UiActivity {
        private Context context;
        private OrderHistoryAdapter adapter;
        @Bind(R.id.your_order_history_list)
        NestedListView ordersList;

        public UiActivity(ShoppingReportActivity yourOrderHistoryActivity) {
            this.context = yourOrderHistoryActivity;
            ButterKnife.bind(this, yourOrderHistoryActivity);
            initListAdapter();
        }

        private void initListAdapter() {
            View view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.shopping_report_footer, null, false);
            ordersList.addFooterView(view);
            adapter = new OrderHistoryAdapter(context, null, 0);
            ordersList.setAdapter(adapter);
        }

        private OrderHistoryAdapter getAdapter() {
            return adapter;
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }
    }

    public static class OrderHistoryAdapter extends CursorAdapter implements SectionIndexer {
        protected SortedMap<Integer, String> sections = new TreeMap<>();
        ArrayList<Integer> sectionList = new ArrayList<>();
        private LayoutInflater layoutInflater;

        public OrderHistoryAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
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
            int columnIndex = cursor.getColumnIndex(DataContract.CashBackOrders.COLUMN_ORDER_DATE);
            cursor.moveToFirst();
            do {
                String date = cursor.getString(columnIndex);
                String section = date.substring(0, 7);
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
            View convertView = LayoutInflater.from(context).inflate(R.layout.item_your_order_history_list, parent, false);
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
                holder.monthYear.setText(Utilities.getFullMonth(sort.substring(5, 7)) + " " + sort.substring(0, 4));
                holder.monthYear.setPadding(0, 0, 0, 20);
                if (position > 0)
                    holder.sortDivider.setBackgroundResource(R.color.primary);
            } else {
                holder.monthYear.setText("");
                holder.monthYear.setHeight(1);
                holder.sortDivider.setBackgroundResource(android.R.color.transparent);
            }
            String date = cursor.getString(cursor.getColumnIndex(DataContract.CashBackOrders.COLUMN_ORDER_DATE));
            String monthDay = Utilities.getMonth(date.substring(5, 7)) + " " + date.substring(8, 10);
            holder.monthDay.setText(monthDay);
            String name = cursor.getString(cursor.getColumnIndex(DataContract.CashBackOrders.COLUMN_VENDOR_NAME));
            holder.storeName.setText(name.trim());
            String number = cursor.getString(cursor.getColumnIndex(DataContract.CashBackOrders.COLUMN_CONFIRMATION_NUMBER));
            holder.orderNumber.setText(" " + number.trim());
            String stock = String.format("%.2f", cursor.getFloat(cursor.getColumnIndex(DataContract.CashBackOrders.COLUMN_SHARED_STOCK_AMOUNT)));
            holder.pendingStockValue.setText(" " + stock);
            String cashBack = String.format("%.2f", cursor.getFloat(cursor.getColumnIndex(DataContract.CashBackOrders.COLUMN_CASH_BACK)));
            holder.storeCommission.setText("$" + cashBack);
            String total = String.format("%.2f", cursor.getFloat(cursor.getColumnIndex(DataContract.CashBackOrders.COLUMN_PURCHASE_TOTAL)));
            holder.total.setText("$" + total);
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
            @Bind(R.id.monthYear)
            TextView monthYear;
            @Bind(R.id.monthDay)
            TextView monthDay;
            @Bind(R.id.storeName)
            TextView storeName;
            @Bind(R.id.storeCommission)
            TextView storeCommission;
            @Bind(R.id.orderNumber)
            TextView orderNumber;
            @Bind(R.id.total)
            TextView total;
            @Bind(R.id.pendingStockValue)
            TextView pendingStockValue;

            public ViewHolder(View convertView) {
                ButterKnife.bind(this, convertView);
            }
        }
    }
}

