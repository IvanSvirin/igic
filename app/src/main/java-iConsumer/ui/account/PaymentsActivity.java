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
 * Created by I.Svirin on 4/13/2016.
 */
public class PaymentsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private UiActivity uiActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        setContentView(R.layout.layout_payments);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.title_payments);

        uiActivity = new UiActivity(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(MainActivity.PAYMENTS_LOADER, null, this);
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
        if (id == MainActivity.PAYMENTS_LOADER) {
            loader = new CursorLoader(this);
            loader.setUri(DataContract.URI_PAYMENTS);
            String projection[] = new String[]{
                    DataContract.Payments._ID,
                    DataContract.Payments.COLUMN_PAYMENT_DATE,
                    DataContract.Payments.COLUMN_PAYMENT_AMOUNT,
                    DataContract.Payments.COLUMN_PAYMENT_ACCOUNT,
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
//            new PaymentsRequest(this).fetchData();
        }
    }

    // TODO: 4/19/2016 TEST - will be deleted
    private void createExampleData() {
        List<ContentValues> listPaymentsValues = new ArrayList<>();
        ContentValues values;

        for (int i = 0; i < 20; i++) {
            values = new ContentValues();
            int month = new Random().nextInt(12) + 1;
            String sMonth = month > 9 ? String.valueOf(month) : "0" + String.valueOf(month);
            int day = new Random().nextInt(30) + 1;
            String sDay = day > 9 ? String.valueOf(day) : "0" + String.valueOf(day);
            values.put(DataContract.Payments.COLUMN_PAYMENT_DATE, "201" + String.valueOf((new Random().nextInt(6)) + "-" +
                    sMonth + "-" + sDay));
            values.put(DataContract.Payments.COLUMN_PAYMENT_AMOUNT, new Random().nextFloat() * 100);
            values.put(DataContract.Payments.COLUMN_PAYMENT_ACCOUNT, "PayPal");
            listPaymentsValues.add(values);
        }
        DataInsertHandler handler = new DataInsertHandler(this, getContentResolver());
//        if (!DataInsertHandler.IS_FILLING_MERCHANT_TABLE) {
        handler.startBulkInsert(DataInsertHandler.PAYMENTS_TOKEN, false, DataContract.URI_PAYMENTS,
                listPaymentsValues.toArray(new ContentValues[listPaymentsValues.size()]));
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        uiActivity.getAdapter().swapCursor(null);
    }

    public void onEvent(PaymentsEvent event) {
        if (event.isSuccess) {
            getSupportLoaderManager().restartLoader(MainActivity.PAYMENTS_LOADER, null, this);
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
        private PaymentsAdapter adapter;
        @Bind(R.id.payments_list)
        NestedListView paymentsList;

        public UiActivity(PaymentsActivity paymentsActivity) {
            this.context = paymentsActivity;
            ButterKnife.bind(this, paymentsActivity);
            initListAdapter();
        }

        private void initListAdapter() {
            adapter = new PaymentsAdapter(context, null, 0);
            paymentsList.setAdapter(adapter);
        }

        private PaymentsAdapter getAdapter() {
            return adapter;
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }
    }

    public static class PaymentsAdapter extends CursorAdapter implements SectionIndexer {
        protected SortedMap<Integer, String> sections = new TreeMap<>();
        ArrayList<Integer> sectionList = new ArrayList<>();
        protected SortedMap<String, Float> sums = new TreeMap<>();
        private LayoutInflater layoutInflater;

        public PaymentsAdapter(Context context, Cursor c, int flags) {
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
            int columnIndex = cursor.getColumnIndex(DataContract.Payments.COLUMN_PAYMENT_DATE);
            int amountColumnIndex = cursor.getColumnIndex(DataContract.Payments.COLUMN_PAYMENT_AMOUNT);
            cursor.moveToFirst();
            do {
                String date = cursor.getString(columnIndex);
                Float amount = cursor.getFloat(amountColumnIndex);
                String section = date.substring(0, 4);
                if (!sections.containsValue(section)) {
                    sections.put(cursor.getPosition(), section);
                }
                if (!sums.containsKey(section)) {
                    sums.put(section, amount);
                } else {
                    sums.put(section, sums.get(section) + amount);
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
            View convertView = LayoutInflater.from(context).inflate(R.layout.item_payments_list, parent, false);
            ViewHolder holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            return convertView;
        }

        @Override
        public void bindView(View convertView, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            int position = cursor.getPosition();
            if (sections.containsKey(position)) {
                String sort = sections.get(position);
                holder.year.setText(sort);
                holder.yearPaymentsSum.setText("$" + String.format("%.2f", sums.get(sort)));
                holder.year.setPadding(0, 0, 0, 20);
                if (position > 0)
                    holder.sortDivider.setBackgroundResource(R.color.primary);
            } else {
                holder.year.setText("");
                holder.year.setHeight(1);
                holder.yearPaymentsSum.setText("");
                holder.yearPaymentsSum.setHeight(1);
                holder.sortDivider.setBackgroundResource(android.R.color.transparent);
            }
            String date = cursor.getString(cursor.getColumnIndex(DataContract.Payments.COLUMN_PAYMENT_DATE));
            String monthDay = Utilities.getMonth(date.substring(5, 7)) + " " + date.substring(8, 10);
            holder.monthDay.setText(monthDay);
            String account = cursor.getString(cursor.getColumnIndex(DataContract.Payments.COLUMN_PAYMENT_ACCOUNT));
            holder.account.setText(account);
            String payment = String.format("%.2f", cursor.getFloat(cursor.getColumnIndex(DataContract.Payments.COLUMN_PAYMENT_AMOUNT)));
            holder.payment.setText("$" + payment);
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
            @Bind(R.id.year)
            TextView year;
            @Bind(R.id.yearPaymentsSum)
            TextView yearPaymentsSum;
            @Bind(R.id.monthDay)
            TextView monthDay;
            @Bind(R.id.account)
            TextView account;
            @Bind(R.id.payment)
            TextView payment;

            public ViewHolder(View convertView) {
                ButterKnife.bind(this, convertView);
            }
        }
    }
}