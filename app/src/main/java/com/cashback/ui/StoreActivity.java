package com.cashback.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.rest.event.MerchantCouponsEvent;
import com.cashback.ui.components.AutofitRecyclerView;
import com.cashback.ui.login.LoginActivity;
import com.cashback.ui.web.BrowserActivity;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by I.Svirin on 4/20/2016.
 */
public class StoreActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String STORE_ID = "store_id";
    public static final String STORE_NAME = "store_name";

    private UiActivity uiActivity;
    private int merchantId;
    private String merchantName;
    private MenuItem menuItem;
    private Handler handler;

    private String logoUrl;
    private String commission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        setContentView(R.layout.layout_storeee);
        handler = new Handler();

        Intent intent = getIntent();
        if (savedInstanceState == null) {
            merchantId = intent.getIntExtra(STORE_ID, 0);
            merchantName = intent.getStringExtra(STORE_NAME);
        } else {
            merchantId = savedInstanceState.getInt(STORE_ID);
            merchantName = savedInstanceState.getString(STORE_NAME);
        }

        // TODO: 4/19/2016 TEST - will be deleted
        logoUrl = intent.getStringExtra("vendor_logo_url");
        commission = intent.getStringExtra("vendor_commission");

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
    }

    public void onEvent(MerchantCouponsEvent event) {
        if (event.isSuccess) {
            // TODO: 4/19/2016 TEST - will be deleted
            getSupportLoaderManager().restartLoader(MainActivity.COUPONS_LOADER, null, this);
//            getSupportLoaderManager().restartLoader(MainActivity.MERCHANT_COUPONS_LOADER, null, this);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // TODO: 4/19/2016 TEST - will be deleted
        getSupportLoaderManager().initLoader(MainActivity.COUPONS_LOADER, null, this);
//        getSupportLoaderManager().initLoader(MainActivity.MERCHANT_COUPONS_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
// TODO: 4/19/2016 TEST - will be deleted
        CursorLoader loader = null;
        if (id == MainActivity.COUPONS_LOADER) {
            loader = new CursorLoader(this);
            loader.setUri(DataContract.URI_COUPONS);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        uiActivity.adapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        uiActivity.adapter.changeCursor(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STORE_ID, merchantId);
        outState.putString(STORE_NAME, merchantName);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        merchantId = savedInstanceState.getInt(STORE_ID);
        merchantName = savedInstanceState.getString(STORE_NAME);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorite, menu);
        menuItem = menu.findItem(R.id.action_favorite);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_favorite) {
            boolean state = item.isChecked();
            if (state) {
                item.setChecked(false);
                item.setIcon(R.drawable.ic_favorite_removed_white);
                DataInsertHandler handler = new DataInsertHandler(this, getContentResolver());
//                Uri deleteRequest = Uri.withAppendedPath(DataContract.URI_FAVORITE_REMAP, String.valueOf(merchantId));
//                handler.startDelete(DataInsertHandler.FAVORITE_TOKEN, merchantId, deleteRequest, null, null);
            } else {
                item.setChecked(true);
                item.setIcon(R.drawable.ic_favorite_added_white);
                DataInsertHandler handler = new DataInsertHandler(this, getContentResolver());
//                Uri insertRequest = Uri.withAppendedPath(DataContract.URI_FAVORITE_REMAP, String.valueOf(merchantId));
//                handler.startInsert(DataInsertHandler.FAVORITE_TOKEN, merchantId, insertRequest, null);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class UiActivity {
        private Context context;
        private ActionBar actionBar;
        private CursorCouponsAdapter adapter;

        @Bind(R.id.appbar_layout)
        AppBarLayout appBarLayout;
        @Bind(R.id.toolbar)
        Toolbar toolbar;
        @Bind(R.id.collapsing_toolbar)
        CollapsingToolbarLayout collapsingBar;
        @Bind(R.id.cashBack)
        TextView cashBack;
        @Bind(R.id.coupons_list)
        AutofitRecyclerView couponsList;
        @Bind(R.id.storeLogo)
        ImageView storeLogo;
        @Bind(R.id.buttonShop)
        TextView buttonShop;
        @Bind(R.id.info)
        ImageView info;

        public UiActivity(StoreActivity activity) {
            this.context = activity;
            ButterKnife.bind(this, activity);
            initToolbar(activity, merchantName);
            initRecycleView();
            initClicks();

            // TODO: 4/19/2016 TEST - will be deleted
            setLogo(logoUrl);
        }

        private void initToolbar(Activity activity, String categoryName) {
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
            actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDefaultDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setTitle(categoryName);
            }
            collapsingBar.setTitleEnabled(false);
        }

        private void initRecycleView() {
            couponsList.setHasFixedSize(true);
            adapter = new CursorCouponsAdapter(context, null);
            adapter.setOnSaleClickListener(new CursorCouponsAdapter.OnSaleClickListener() {
                @Override
                public void onSaleClick(int saleId) {
                    if (Utilities.isLoggedIn(context)) {
//                        Intent intent = new Intent(context, BrowserActivity.class);
//                        intent.putExtra(BrowserActivity.FLAG_MERCHANT_ID, merchantId);
//                        intent.putExtra(BrowserActivity.FLAG_SALE_ID, saleId);
//                        intent.putExtra(BrowserActivity.FLAG_EVENT_TYPE, BrowserActivity.EVENT_TYPE_SALE);
//                        context.startActivity(intent);
                    } else {
                        // TODO: 4/19/2016 TEST - will be deleted
                        Intent intent = new Intent(context, BrowserActivity.class);
//                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                }
            });
            couponsList.setAdapter(adapter);
        }

        private void initClicks() {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utilities.isLoggedIn(context)) {
//                        Intent intent = new Intent(context, BrowserActivity.class);
//                        intent.putExtra(BrowserActivity.FLAG_MERCHANT_ID, merchantId);
//                        intent.putExtra(BrowserActivity.FLAG_SALE_ID, -1);
//                        intent.putExtra(BrowserActivity.FLAG_EVENT_TYPE, BrowserActivity.EVENT_TYPE_STORE);
//                        context.startActivity(intent);
                    } else {
                        // TODO: 4/19/2016 TEST - will be deleted
                        Intent intent = new Intent(context, BrowserActivity.class);
//                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                }
            };
            storeLogo.setOnClickListener(listener);
            buttonShop.setOnClickListener(listener);

            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDescriptionDialog("");
                }
            });
        }

        private void setLogo(final String url) {
//            if (url != null) {
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Picasso.with(context).load(url).into(storeLogo);
//                    }
//                }, 100);
//            }
            Picasso.with(context).load(url).into(storeLogo);

            cashBack.setText(commission);


            Bitmap bitmap = ((BitmapDrawable) storeLogo.getDrawable()).getBitmap();
            Palette.Builder pb = new Palette.Builder(bitmap);
            Palette palette = pb.generate();
            Palette.Swatch swatch = palette.getVibrantSwatch();
            int color = swatch != null ? swatch.getRgb() : -7292864;
            appBarLayout.setBackgroundColor(color);
        }

        private void showDescriptionDialog(String message) {
            InfoDialog dialog = InfoDialog.newInstance(message);
            dialog.setCancelable(true);
            dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            dialog.show(getSupportFragmentManager(), "TAG_DIALOG");
        }
    }

    public static class CursorCouponsAdapter extends RecyclerView.Adapter<CursorCouponsAdapter.ViewHolder> {
        private OnSaleClickListener listener;
        private Context context;
        protected Cursor c;
        protected boolean dataValid;
        protected int rowIDColumn;

        public CursorCouponsAdapter(Context context, Cursor c) {
            this.context = context;
            this.c = c;
        }

        @Override
        public CursorCouponsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_merchant_coupons, parent, false);
            return new CursorCouponsAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(CursorCouponsAdapter.ViewHolder holder, int position) {
            c.moveToPosition(position);
            // TODO: 4/19/2016 TEST - will be deleted
            final int couponId = c.getInt(c.getColumnIndex(DataContract.OfferEntry.COLUMN_ID));
            final String logoUrl = c.getString(c.getColumnIndex(DataContract.OfferEntry.COLUMN_LOGO));
            String restrictions = c.getString(c.getColumnIndex(DataContract.OfferEntry.COLUMN_DESCRIPTION));
            String cashBack = c.getString(c.getColumnIndex(DataContract.OfferEntry.COLUMN_MSG));
            String expire = context.getString(R.string.prefix_expire) + c.getString(c.getColumnIndex(DataContract.OfferEntry.COLUMN_EXPIRE));
            String couponCode = c.getString(c.getColumnIndex(DataContract.OfferEntry.COLUMN_CODE));
            holder.vhRestrictions.setText(restrictions.trim());
            holder.vhRestrictions.setText(restrictions);
            holder.vhExpireDate.setText(expire);
        }

        @Override
        public int getItemCount() {
            if (dataValid && c != null) {
                return c.getCount();
            } else {
                return 0;
            }
        }

        public Cursor getCursor() {
            return c;
        }

        @Override
        public long getItemId(int position) {
            if (hasStableIds() && dataValid && c != null) {
                if (c.moveToPosition(position)) {
                    return c.getLong(rowIDColumn);
                } else {
                    return RecyclerView.NO_ID;
                }
            } else {
                return RecyclerView.NO_ID;
            }
        }

        public void changeCursor(Cursor cursor) {
            Cursor oldCursor = swapCursor(cursor);
            if (oldCursor != null) oldCursor.close();
        }

        public Cursor swapCursor(Cursor cursor) {
            Cursor oldCursor = c;
            c = cursor;
            if (cursor != null) {
                // TODO: 4/19/2016 TEST - will be deleted
                rowIDColumn = cursor.getColumnIndexOrThrow(DataContract.Coupons._ID);
                dataValid = true;
                notifyDataSetChanged();
            } else {
                rowIDColumn = -1;
                dataValid = false;
                notifyItemRangeRemoved(0, getItemCount());
            }
            return oldCursor;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.restrictions)
            TextView vhRestrictions;
            @Bind(R.id.expireDate)
            TextView vhExpireDate;
            @Bind(R.id.btnShopNow)
            TextView vhBtnShopNow;
            @Bind(R.id.shareButton)
            AppCompatImageView vhShareButton;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        public void setOnSaleClickListener(OnSaleClickListener listener) {
            this.listener = listener;
        }

        interface OnSaleClickListener {
            void onSaleClick(int saleId);
        }
    }

    public static class InfoDialog extends DialogFragment {

        private static String description;

        static InfoDialog newInstance(String message) {
            description = message;
            return new InfoDialog();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.view_dialog, container, false);
            TextView tv = (TextView) v.findViewById(R.id.description);
            tv.setText(description);
            return v;
        }
    }
}
