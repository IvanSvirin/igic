package com.cashback.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.model.Coupon;
import com.cashback.rest.event.MerchantCouponsEvent;
import com.cashback.rest.request.CouponsByMerchantIdRequest;
import com.cashback.rest.request.FavoritesRequest;
import com.cashback.ui.components.NestedListView;
import com.cashback.ui.login.LoginActivity;
import com.cashback.ui.web.BrowserActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by I.Svirin on 4/20/2016.
 */
public class StoreActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ArrayList<Coupon> coupons;
    private Intent intent;
    private UiActivity uiActivity;
    private MenuItem menuItem;
    private Handler handler;
    private long vendorId;
    private String vendorName;
    private String affiliateUrl;
    private String exceptionInfo;
    private String description;
    private String logoUrl;
    private float commission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        setContentView(R.layout.layout_store);

        handler = new Handler();
        coupons = new ArrayList<>();
        intent = getIntent();
        vendorId = intent.getLongExtra("vendor_id", 1);
        new CouponsByMerchantIdRequest(this, vendorId, coupons).fetchData();
        Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(vendorId));
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        vendorName = cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_NAME));
        exceptionInfo = cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_EXCEPTION_INFO));
        description = cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_DESCRIPTION));

        commission = intent.getFloatExtra("vendor_commission", 1);
        logoUrl = intent.getStringExtra("vendor_logo_url");
        affiliateUrl = intent.getStringExtra("affiliate_url");

        getSupportLoaderManager().initLoader(MainActivity.FAVORITES_LOADER, null, this);

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
            uiActivity.initListAdapter(this);
        }
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
                new FavoritesRequest(this).deleteMerchant(vendorId);
            } else {
                item.setChecked(true);
                item.setIcon(R.drawable.ic_favorite_added_white);
                new FavoritesRequest(this).addMerchant(vendorId);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if (id == MainActivity.FAVORITES_LOADER) {
            loader = new CursorLoader(this);
            loader.setUri(DataContract.URI_FAVORITES);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        do {
            int id = data.getInt(data.getColumnIndex(DataContract.Favorites.COLUMN_VENDOR_ID));
            if (id == vendorId) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        menuItem.setChecked(true);
                        menuItem.setIcon(R.drawable.ic_favorite_added_white);
                    }
                }, 500);
                return;
            }
        } while (data.moveToNext());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
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
            initToolbar(activity, vendorName);
            initClicks();
            setData(logoUrl);
        }

        private void initListAdapter(final Context context) {
            adapter = new CursorCouponsAdapter(context, coupons, isGridLayout);
            adapter.setOnSaleClickListener(new CursorCouponsAdapter.OnSaleClickListener() {
                @Override
                public void onSaleClick(long id) {
                    if (Utilities.isLoggedIn(context)) {
                        Intent intent = new Intent(context, BrowserActivity.class);
                        intent.putExtra("vendor_id", vendorId);
                        intent.putExtra("vendor_commission", commission);
                        intent.putExtra("affiliate_url", affiliateUrl);
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                }
            });
            adapter.setOnShareClickListener(new CursorCouponsAdapter.OnShareClickListener() {
                @Override
                public void onShareClick(long shareId) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, String.valueOf(shareId));
                    startActivity(Intent.createChooser(share, "Share Text"));
                }
            });
            AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(context, BrowserActivity.class);
                    intent.putExtra("vendor_id", vendorId);
                    intent.putExtra("vendor_commission", commission);
                    intent.putExtra("affiliate_url", affiliateUrl);
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
                actionBar.setTitle("");
            }
            collapsingBar.setTitleEnabled(false);
        }

        private void initClicks() {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utilities.isLoggedIn(context)) {
                        Intent intent = new Intent(context, BrowserActivity.class);
                        intent.putExtra("vendor_id", vendorId);
                        intent.putExtra("vendor_commission", commission);
                        intent.putExtra("affiliate_url", affiliateUrl);
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                }
            };
//            storeLogo.setOnClickListener(listener);
            buttonShop.setOnClickListener(listener);

            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDescriptionDialog("");
                }
            });
        }

        private void setData(final String url) {
            cashBack.setText(String.valueOf(commission));
            storeName.setText(vendorName);

            Picasso.with(context).load(url).into(storeLogo, new Callback() {
                @Override
                public void onSuccess() {
                    if (storeLogo.getDrawable() != null) {
                        Bitmap bitmap = ((BitmapDrawable) storeLogo.getDrawable()).getBitmap();
                        Palette.Builder pb = new Palette.Builder(bitmap);
                        Palette palette = pb.generate();
                        Palette.Swatch swatch = palette.getVibrantSwatch();
                        @ColorInt
                        int color = swatch != null ? swatch.getRgb() : -7292864;
                        toolbar.setBackgroundColor(color);
                        bigRelativeLayout.setBackgroundColor(color);
                        storeName.setBackgroundColor(1157627903);
                    }
                }

                @Override
                public void onError() {

                }
            });
        }

        private void showDescriptionDialog(String message) {
            InfoDialog dialog = InfoDialog.newInstance(description, exceptionInfo);
            dialog.setCancelable(true);
            dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            dialog.show(getSupportFragmentManager(), "TAG_DIALOG");
        }
    }

    public static class CursorCouponsAdapter extends BaseAdapter {
        private final boolean GRID_TYPE_FLAG;
        private Context context;
        private ArrayList<Coupon> coupons;
        private OnSaleClickListener onSaleClickListener;
        private OnShareClickListener onShareClickListener;

        public CursorCouponsAdapter(Context context, ArrayList<Coupon> coupons, boolean gridType) {
            GRID_TYPE_FLAG = gridType;
            this.context = context;
            this.coupons = coupons;
        }

        @Override
        public int getCount() {
            return coupons.size();
        }

        @Override
        public Object getItem(int position) {
            return coupons.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_coupons_list, parent, false);
                if (GRID_TYPE_FLAG) {
                    GridViewHolder holder = new GridViewHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    ViewHolder holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                }
            }
            final long couponId = coupons.get(position).getCouponId();
            String restrictions = coupons.get(position).getRestrictions();
            String date = coupons.get(position).getExpirationDate();
            String expire = context.getString(R.string.prefix_expire) + " " + date.substring(5, 7) + "/" + date.substring(8, 10) + "/" + date.substring(0, 4);
            if (GRID_TYPE_FLAG) {
                final GridViewHolder holder = (GridViewHolder) convertView.getTag();
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
                ViewHolder holder = (ViewHolder) convertView.getTag();
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
            return convertView;
        }

        public void setOnSaleClickListener(OnSaleClickListener listener) {
            onSaleClickListener = listener;
        }

        public interface OnSaleClickListener {
            void onSaleClick(long saleId);
        }

        public void setOnShareClickListener(OnShareClickListener listener) {
            onShareClickListener = listener;
        }

        public interface OnShareClickListener {
            void onShareClick(long shareId);
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
        private static String exceptionInfo;

        static InfoDialog newInstance(String d, String e) {
            description = d;
            exceptionInfo = e;
            return new InfoDialog();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.view_dialog, container, false);
            TextView tvDescription = (TextView) v.findViewById(R.id.description);
            TextView tvExceptions = (TextView) v.findViewById(R.id.exceptions);
            tvDescription.setText(description);
            tvExceptions.setText(exceptionInfo);
            return v;
        }
    }
}
