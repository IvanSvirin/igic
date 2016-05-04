package com.cashback.ui.account;

import android.content.ContentValues;
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

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.rest.event.PaymentsEvent;
import com.cashback.rest.event.ShoppingTripsEvent;
import com.cashback.rest.request.ShoppingTripsRequest;
import com.cashback.ui.MainActivity;
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
 * Created by I.Svirin on 4/12/2016.
 */
public class ShoppingTripsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private UiActivity uiActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_shopping_tripss);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.shopping_trips);

        uiActivity = new UiActivity(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(MainActivity.SHOPPING_TRIPS_LOADER, null, this);
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
        if (id == MainActivity.SHOPPING_TRIPS_LOADER) {
            loader = new CursorLoader(this);
            loader.setUri(DataContract.URI_SHOPPING_TRIPS);
            String projection[] = new String[]{
                    DataContract.ShoppingTrips._ID,
                    DataContract.ShoppingTrips.COLUMN_CONFIRMATION_NUMBER,
                    DataContract.ShoppingTrips.COLUMN_TRIP_DATE,
                    DataContract.ShoppingTrips.COLUMN_VENDOR_NAME,
            };
            loader.setProjection(projection);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        uiActivity.getAdapter().swapCursor(data);
        if (data == null || data.getCount() == 0) {
            // TODO: 4/19/2016 TEST - will be deleted
            createExampleData();
//            new ShoppingTripsRequest(this).fetchData();
        }
    }

    // TODO: 4/19/2016 TEST - will be deleted
    private void createExampleData() {
        List<ContentValues> listShoppingTripsValues = new ArrayList<>();
        ContentValues values;

        for (int i = 0; i < 20; i++) {
            values = new ContentValues();
            int month = new Random().nextInt(12) + 1;
            String sMonth = month > 9 ? String.valueOf(month) : "0" + String.valueOf(month);
            int day = new Random().nextInt(30) + 1;
            String sDay = day > 9 ? String.valueOf(day) : "0" + String.valueOf(day);
            int hour = new Random().nextInt(23);
            String sHour = day > 9 ? String.valueOf(hour) : "0" + String.valueOf(hour);
            int min = new Random().nextInt(59);
            String sMin = day > 9 ? String.valueOf(min) : "0" + String.valueOf(min);
            values.put(DataContract.ShoppingTrips.COLUMN_TRIP_DATE, "201" + String.valueOf((new Random().nextInt(6)) + "-" + sMonth + "-" + sDay + "-" + sHour + "-" + sMin));
            values.put(DataContract.ShoppingTrips.COLUMN_CONFIRMATION_NUMBER, new Random().nextInt(100000));
            values.put(DataContract.ShoppingTrips.COLUMN_VENDOR_NAME, "Vendor" + i);
            listShoppingTripsValues.add(values);
        }
        DataInsertHandler handler = new DataInsertHandler(this, getContentResolver());
//        if (!DataInsertHandler.IS_FILLING_MERCHANT_TABLE) {
        handler.startBulkInsert(DataInsertHandler.SHOPPING_TRIPS_TOKEN, false, DataContract.URI_SHOPPING_TRIPS,
                listShoppingTripsValues.toArray(new ContentValues[listShoppingTripsValues.size()]));
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        uiActivity.getAdapter().swapCursor(null);
    }

    public void onEvent(ShoppingTripsEvent event) {
        if (event.isSuccess) {
            getSupportLoaderManager().restartLoader(MainActivity.SHOPPING_TRIPS_LOADER, null, this);
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
        private ShoppingTripsAdapter adapter;
        @Bind(R.id.shopping_trips_list)
        NestedListView shoppingTripsList;

        public UiActivity(ShoppingTripsActivity shoppingTripsActivity) {
            this.context = shoppingTripsActivity;
            ButterKnife.bind(this, shoppingTripsActivity);
            initListAdapter();
        }

        private void initListAdapter() {
            adapter = new ShoppingTripsAdapter(context, null, 0);
            shoppingTripsList.setAdapter(adapter);
        }

        private ShoppingTripsAdapter getAdapter() {
            return adapter;
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }
    }

    public static class ShoppingTripsAdapter extends CursorAdapter implements SectionIndexer {
        protected SortedMap<Integer, String> sections = new TreeMap<>();
        ArrayList<Integer> sectionList = new ArrayList<>();
        private LayoutInflater layoutInflater;

        public ShoppingTripsAdapter(Context context, Cursor c, int flags) {
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
            int columnIndex = cursor.getColumnIndex(DataContract.ShoppingTrips.COLUMN_TRIP_DATE);
            cursor.moveToFirst();
            do {
                String name = cursor.getString(columnIndex);
                String section = name.toUpperCase().substring(0, 7);
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
            View convertView = LayoutInflater.from(context).inflate(R.layout.item_shopping_trips_listt, parent, false);
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
                if (position > 0)
                    holder.sortDivider.setBackgroundResource(R.color.primary);
            } else {
                holder.monthYear.setText("");
                holder.sortDivider.setBackgroundResource(android.R.color.transparent);
            }
            String date = cursor.getString(cursor.getColumnIndex(DataContract.ShoppingTrips.COLUMN_TRIP_DATE));
            String monthDay = Utilities.getMonth(date.substring(5, 7)) + " " + date.substring(8, 10);
            holder.monthDay.setText(monthDay);
            String name = cursor.getString(cursor.getColumnIndex(DataContract.ShoppingTrips.COLUMN_VENDOR_NAME));
            holder.vendorName.setText(name.trim());
            String number = cursor.getString(cursor.getColumnIndex(DataContract.ShoppingTrips.COLUMN_CONFIRMATION_NUMBER));
            holder.shoppingTripsNumber.setText(" " + number.trim());
            String dateValue = date.substring(5, 7) + "/" + date.substring(8, 10) + "/" + date.substring(0, 4);
            holder.dateValue.setText(" " + dateValue + " ");
            String timeValueu = date.substring(5, 7) + "/" + date.substring(8, 10) + "/" + date.substring(0, 4);
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
            @Bind(R.id.vendorName)
            TextView vendorName;
            @Bind(R.id.shoppingTripsNumber)
            TextView shoppingTripsNumber;
            @Bind(R.id.dateValue)
            TextView dateValue;
            @Bind(R.id.timeValue)
            TextView timeValue;

            public ViewHolder(View convertView) {
                ButterKnife.bind(this, convertView);
            }
        }
    }
}
