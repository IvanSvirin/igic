package com.cashback.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.rest.event.MerchantCouponsEvent;
import com.cashback.ui.components.NestedListView;
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
        setContentView(R.layout.layout_store);
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
        private boolean isGridLayout;
        private NestedListView nestedListView;
        private GridView gridView;
        @Bind(R.id.appbar_layout)
        AppBarLayout appBarLayout;
        @Bind(R.id.bigRelativeLayout)
        RelativeLayout bigRelativeLayout;
        @Bind(R.id.toolbar)
        Toolbar toolbar;
        @Bind(R.id.collapsing_toolbar)
        CollapsingToolbarLayout collapsingBar;
        @Bind(R.id.cashBack)
        TextView cashBack;
        @Bind(R.id.storeLogo)
        ImageView storeLogo;
        @Bind(R.id.storeName)
        TextView storeName;
        @Bind(R.id.buttonShop)
        TextView buttonShop;
        @Bind(R.id.info)
        ImageView info;

        public UiActivity(StoreActivity activity) {
            this.context = activity;
            ButterKnife.bind(this, activity);
            isGridLayout = ButterKnife.findById(activity, R.id.checking_element) != null;
            if (isGridLayout) {
                gridView = ButterKnife.findById(activity, R.id.common_list);
            } else {
                nestedListView = ButterKnife.findById(activity, R.id.common_list);
            }
            initToolbar(activity, merchantName);
            initListAdapter(activity);
            initClicks();
            // TODO: 4/19/2016 TEST - will be deleted
            setLogo(logoUrl);
        }

        private void initListAdapter(final Context context) {
            adapter = new CursorCouponsAdapter(context, null, 0, isGridLayout);
            adapter.setOnSaleClickListener(new CursorCouponsAdapter.OnSaleClickListener() {
                @Override
                public void onSaleClick(int id) {
                    if (Utilities.isLoggedIn(context)) {
                        Intent intent = new Intent(context, BrowserActivity.class);
//                        intent.putExtra(BrowserActivity.FLAG_SALE_ID, id);
//                        intent.putExtra(BrowserActivity.FLAG_EVENT_TYPE, BrowserActivity.EVENT_TYPE_SALE);
                        context.startActivity(intent);
                    } else {
                        // TODO: 4/19/2016 TEST - will be deleted
                        Intent intent = new Intent(context, BrowserActivity.class);
                        Cursor cursor = adapter.getCursor();
                        intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.OfferEntry.COLUMN_URL)));
                        intent.putExtra("vendor_commission", cursor.getString(cursor.getColumnIndex(DataContract.OfferEntry.COLUMN_MSG)));
//                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                }
            });
            adapter.setOnShareClickListener(new CursorCouponsAdapter.OnShareClickListener() {
                @Override
                public void onShareClick(int shareId) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, String.valueOf(shareId));
                    startActivity(Intent.createChooser(share, "Share Text"));
                }
            });
            AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = adapter.getCursor();
                    cursor.moveToPosition(position);
                    Intent intent = new Intent(context, StoreActivity.class);
                    // TODO: 4/19/2016 TEST - will be deleted
                    intent.putExtra("restriction", cursor.getString(cursor.getColumnIndex(DataContract.OfferEntry.COLUMN_DESCRIPTION)));
                    intent.putExtra("expiration_date", cursor.getString(cursor.getColumnIndex(DataContract.OfferEntry.COLUMN_EXPIRE)));
                    intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.OfferEntry.COLUMN_URL)));
                    intent.putExtra("vendor_logo_url", cursor.getString(cursor.getColumnIndex(DataContract.OfferEntry.COLUMN_LOGO)));
                    intent.putExtra("vendor_commission", cursor.getString(cursor.getColumnIndex(DataContract.OfferEntry.COLUMN_MSG)));
//                    intent.putExtra("vendor_id", c.getString(c.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_ID)));
                    context.startActivity(intent);
                }
            };
            if (isGridLayout) {
                gridView.setOnItemClickListener(listener);
                gridView.setAdapter(adapter);
            } else {
                nestedListView.setOnItemClickListener(listener);
                nestedListView.setAdapter(adapter);
            }
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
                        intent.putExtra("vendor_commission", commission);
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
            @ColorInt
            int color = swatch != null ? swatch.getRgb() : -7292864;
//            appBarLayout.setBackgroundColor(color);
            bigRelativeLayout.setBackgroundColor(color);
            storeName.setBackgroundColor(1157627903);
        }

        private void showDescriptionDialog(String message) {
            InfoDialog dialog = InfoDialog.newInstance(message);
            dialog.setCancelable(true);
            dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            dialog.show(getSupportFragmentManager(), "TAG_DIALOG");
        }
    }

    public static class CursorCouponsAdapter extends CursorAdapter {
        private final boolean GRID_TYPE_FLAG;
        private Context context;
        private OnSaleClickListener onSaleClickListener;
        private OnShareClickListener onShareClickListener;

        public CursorCouponsAdapter(Context context, Cursor c, int flags, boolean gridType) {
            super(context, c, flags);
            GRID_TYPE_FLAG = gridType;
            this.context = context;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.item_coupons_list, parent, false);
            if (GRID_TYPE_FLAG) {
                GridViewHolder holder = new GridViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                ViewHolder holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            return convertView;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // TODO: 4/19/2016 TEST - will be deleted
            final int couponId = cursor.getInt(cursor.getColumnIndex(DataContract.OfferEntry.COLUMN_ID));
            final String logoUrl = cursor.getString(cursor.getColumnIndex(DataContract.OfferEntry.COLUMN_LOGO));
            String restrictions = cursor.getString(cursor.getColumnIndex(DataContract.OfferEntry.COLUMN_DESCRIPTION));
            String cashBack = cursor.getString(cursor.getColumnIndex(DataContract.OfferEntry.COLUMN_MSG));
            String expire = context.getString(R.string.prefix_expire) + cursor.getString(cursor.getColumnIndex(DataContract.OfferEntry.COLUMN_EXPIRE));
            String couponCode = cursor.getString(cursor.getColumnIndex(DataContract.OfferEntry.COLUMN_CODE));

//            final int couponId = c.getInt(c.getColumnIndex(DataContract.Coupons.COLUMN_COUPON_ID));
//            final String logoUrl = c.getString(c.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_LOGO_URL));
//            String restrictions = c.getString(c.getColumnIndex(DataContract.Coupons.COLUMN_RESTRICTIONS));
//            String cashBack = c.getString(c.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_COMMISSION));
//            String expire = context.getString(R.string.prefix_expire) + c.getString(c.getColumnIndex(DataContract.Coupons.COLUMN_EXPIRATION_DATE));
//            String couponCode = c.getString(c.getColumnIndex(DataContract.Coupons.COLUMN_COUPON_CODE));
            if (GRID_TYPE_FLAG) {
                final GridViewHolder holder = (GridViewHolder) view.getTag();
                holder.vhRestrictions.setText(restrictions.trim());
                holder.vhExpireDate.setText(expire);
                holder.vhBtnShopNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSaleClickListener.onSaleClick(couponId);
                    }
                });
                holder.vhShareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onShareClickListener.onShareClick(couponId);
                    }
                });
            } else {
                ViewHolder holder = (ViewHolder) view.getTag();
                holder.vhRestrictions.setText(restrictions.trim());
                holder.vhExpireDate.setText(expire);
                holder.vhBtnShopNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSaleClickListener.onSaleClick(couponId);
                    }
                });
                holder.vhShareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onShareClickListener.onShareClick(couponId);
                    }
                });
            }
        }

        public void setOnSaleClickListener(OnSaleClickListener listener) {
            onSaleClickListener = listener;
        }

        public interface OnSaleClickListener {
            void onSaleClick(int saleId);
        }

        public void setOnShareClickListener(OnShareClickListener listener) {
            onShareClickListener = listener;
        }

        public interface OnShareClickListener {
            void onShareClick(int shareId);
        }

        public static class ViewHolder {
            @Bind(R.id.restrictions)
            TextView vhRestrictions;
            @Bind(R.id.btnShopNow)
            TextView vhBtnShopNow;
            @Bind(R.id.expireDate)
            TextView vhExpireDate;
            @Bind(R.id.shareButton)
            AppCompatImageView vhShareButton;

            public ViewHolder(View convertView) {
                ButterKnife.bind(this, convertView);
            }
        }

        public static class GridViewHolder {
            @Bind(R.id.restrictions)
            TextView vhRestrictions;
            @Bind(R.id.btnShopNow)
            TextView vhBtnShopNow;
            @Bind(R.id.expireDate)
            TextView vhExpireDate;
            @Bind(R.id.shareButton)
            AppCompatImageView vhShareButton;

            public GridViewHolder(View convertView) {
                ButterKnife.bind(this, convertView);
            }
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
