package com.cashback.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cashback.App;
import com.cashback.R;
import com.cashback.Utilities;

import db.DataContract;

import com.cashback.model.Coupon;
import com.cashback.rest.event.FavoritesEvent;
import com.cashback.rest.event.MerchantCouponsEvent;
import com.cashback.rest.request.AllUsedCouponsRequest;
import com.cashback.rest.request.FavoritesRequest;
import com.cashback.ui.web.BrowserDealsActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import ui.MainActivity;

public class StoreActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
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
    private int benefit;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270) {
            setContentView(R.layout.layout_store_land_common);
        } else {
            setContentView(R.layout.layout_store);
        }
        handler = new Handler();
        Intent intent = getIntent();
        vendorId = intent.getLongExtra("vendor_id", 1);

        EventBus.getDefault().register(this);
        Set<String> set = Utilities.retrieveVendorIdSet(this);
        boolean marker = true;
        if (set != null) {
            for (String s : set) {
                if (s.equals(String.valueOf(vendorId))) marker = false;
            }
        } else {
            set = new HashSet<>();
        }
        if (marker) {
            set.add(String.valueOf(vendorId));
            Utilities.saveVendorIdSet(this, set);
            progressDialog = Utilities.onCreateProgressDialog(this);
            progressDialog.show();
            new AllUsedCouponsRequest(this).fetchData();
        } else {
            getSupportLoaderManager().initLoader(MainActivity.COUPONS_LOADER, null, this);
        }

        Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(vendorId));
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            vendorName = cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_NAME));
            exceptionInfo = cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_EXCEPTION_INFO));
            description = cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_DESCRIPTION));
            commission = cursor.getFloat(cursor.getColumnIndex(DataContract.Merchants.COLUMN_COMMISSION));
            benefit = cursor.getInt(cursor.getColumnIndex(DataContract.Merchants.COLUMN_OWNERS_BENEFIT));
            logoUrl = cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_LOGO_URL));
            affiliateUrl = cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_AFFILIATE_URL));
            cursor.close();
        }

        getSupportLoaderManager().initLoader(MainActivity.FAVORITES_LOADER, null, this);

        uiActivity = new UiActivity(this);
        //Google Analytics
        App app = (App) getApplication();
        Tracker tracker = app.getDefaultTracker();
        tracker.setScreenName("Store");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(MerchantCouponsEvent event) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (event.isSuccess) {
            getSupportLoaderManager().restartLoader(MainActivity.COUPONS_LOADER, null, this);
        } else {
            Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), R.string.no_coupons, Snackbar.LENGTH_SHORT).show();
        }
    }

    public void onEvent(FavoritesEvent event) {
        if (progressDialog != null) {
            progressDialog.dismiss();
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
            if (Utilities.isLoggedIn(this)) {
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
                progressDialog = Utilities.onCreateProgressDialog(this);
                progressDialog.show();
                return true;
            } else {
                Bundle loginBundle = new Bundle();
                loginBundle.putString(Utilities.CALLING_ACTIVITY, "StoreActivity");
                loginBundle.putLong(Utilities.VENDOR_ID, vendorId);
                Utilities.needLoginDialog(this, loginBundle);
            }
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
        if (id == MainActivity.COUPONS_LOADER) {
            loader = new CursorLoader(this);
            loader.setUri(DataContract.URI_COUPONS);
            loader.setSelection(DataContract.Coupons.COLUMN_VENDOR_ID + "=" + String.valueOf(vendorId));
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == MainActivity.FAVORITES_LOADER) {
            int count = data.getCount();
            if (count > 0) {
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
        }
        if (loader.getId() == MainActivity.COUPONS_LOADER) {
            if (data.getCount() == 0) {
                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), R.string.no_coupons, Snackbar.LENGTH_SHORT).show();
            }
            uiActivity.initListAdapter(this);
            uiActivity.adapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        uiActivity.adapter.changeCursor(null);
    }

    class UiActivity {
        private Context context;
        private ActionBar actionBar;
        private CouponsRecyclerAdapter adapter;
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
        @Bind(R.id.noImage)
        TextView noImage;
        @Bind(R.id.store_recycler_view)
        RecyclerView storeRecyclerView;

        public UiActivity(StoreActivity activity) {
            this.context = activity;
            ButterKnife.bind(this, activity);
            initToolbar(activity);
            initClicks();
            setData(logoUrl);
        }

        private void initListAdapter(final Context context) {
            adapter = new CouponsRecyclerAdapter(context);
            storeRecyclerView.setHasFixedSize(true);
            storeRecyclerView.setAdapter(adapter);
        }

        private void initToolbar(Activity activity) {
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
                        Intent intent = new Intent(context, BrowserDealsActivity.class);
                        intent.putExtra("vendor_id", vendorId);
                        intent.putExtra("vendor_commission", commission);
                        intent.putExtra("affiliate_url", affiliateUrl);
                        context.startActivity(intent);
                    } else {
                        Bundle loginBundle = new Bundle();
                        loginBundle.putString(Utilities.CALLING_ACTIVITY, "BrowserDealsActivity");
                        loginBundle.putLong(Utilities.VENDOR_ID, vendorId);
                        loginBundle.putString(Utilities.AFFILIATE_URL, affiliateUrl);
                        loginBundle.putFloat(Utilities.VENDOR_COMMISSION, commission);
                        Utilities.needLoginDialog(context, loginBundle);
                    }
                }
            };
            buttonShop.setOnClickListener(listener);
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDescriptionDialog();
                }
            });
        }

        private void setData(final String url) {
            if (commission != 0) {
                cashBack.setText("+ " + String.valueOf(commission) + "% " + context.getString(R.string.cash_back));
            } else {
                if (benefit == 1) {
                    cashBack.setText("OWNERS BENEFIT");
                } else {
                    cashBack.setText("SPECIAL RATE");
                }
            }

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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            String hexColor = Utilities.decToHex(color);
//                            char red = (char) (hexColor.charAt(2) - 1);
//                            char green = (char) (hexColor.charAt(4) - 1);
//                            char blue = (char) (hexColor.charAt(6) - 1);
//                            StringBuilder sb = new StringBuilder();
//                            sb.append(hexColor.charAt(0)).append(hexColor.charAt(1)).append(red).append(hexColor.charAt(3))
//                                    .append(green).append(hexColor.charAt(5)).append(blue).append(hexColor.charAt(7));
//                            int darkColor = Utilities.hexToDec(sb.toString());
//                            getWindow().setStatusBarColor(darkColor);
                            if (color < -394758) {
                                getWindow().setStatusBarColor(color + 394758);
                            } else {
                                getWindow().setStatusBarColor(color);
                            }
                        }
                    }
                }

                @Override
                public void onError() {
                    @ColorInt
                    int color = -7292864;
                    toolbar.setBackgroundColor(color);
                    bigRelativeLayout.setBackgroundColor(color);
                    storeName.setBackgroundColor(1157627903);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        String hexColor = Utilities.decToHex(color);
//                        char red = (char) (hexColor.charAt(3) - 1);
//                        char green = (char) (hexColor.charAt(5) - 1);
//                        char blue = (char) (hexColor.charAt(7) - 1);
//                        StringBuilder sb = new StringBuilder();
//                        sb.append(hexColor.charAt(1)).append(hexColor.charAt(2)).append(red).append(hexColor.charAt(4))
//                                .append(green).append(hexColor.charAt(6)).append(blue).append(hexColor.charAt(8));
//                        getWindow().setStatusBarColor(Utilities.hexToDec(sb.toString()));
                        getWindow().setStatusBarColor(color + 394758);
                    }
                    noImage.setVisibility(View.VISIBLE);
                }
            });
        }

        private void showDescriptionDialog() {
            InfoDialog dialog = InfoDialog.newInstance(description, exceptionInfo);
            dialog.setCancelable(true);
            dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            dialog.show(getSupportFragmentManager(), "TAG_DIALOG");
        }
    }

    public static class CouponsRecyclerAdapter extends RecyclerView.Adapter<CouponsRecyclerAdapter.CouponsViewHolder> {
        final private Context context;
        private CouponsCursorAdapter cursorAdapter;
        private Cursor cursor;
        private Calendar calendar;

        public class CouponsViewHolder extends RecyclerView.ViewHolder {
            public TextView vhRestrictions;
            public TextView vhBtnShopNow;
            public TextView vhExpireDate;
            public TextView vhCouponCode;
            public AppCompatImageView vhShareButton;

            public CouponsViewHolder(View itemView) {
                super(itemView);
                vhRestrictions = (TextView) itemView.findViewById(R.id.restrictions);
                vhBtnShopNow = (TextView) itemView.findViewById(R.id.btnShopNow);
                vhExpireDate = (TextView) itemView.findViewById(R.id.expireDate);
                vhCouponCode = (TextView) itemView.findViewById(R.id.couponCode);
                vhShareButton = (AppCompatImageView) itemView.findViewById(R.id.shareButton);
                vhShareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utilities.isLoggedIn(context)) {
                            int position = getAdapterPosition();
                            cursor.moveToPosition(position);
                            LaunchActivity.shareDealLink(context, cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_AFFILIATE_URL)),
                                    cursor.getLong(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_ID)), cursor.getLong(cursor.getColumnIndex(DataContract.Coupons.COLUMN_COUPON_ID)),
                                    cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_LOGO_URL)));
                        } else {
                            Bundle loginBundle = new Bundle();
                            loginBundle.putString(Utilities.CALLING_ACTIVITY, "MainActivity");
                            Utilities.needLoginDialog(context, loginBundle);
                        }
                    }
                });
                vhBtnShopNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utilities.isLoggedIn(context)) {
                            int position = getAdapterPosition();
                            cursor.moveToPosition(position);
                            Intent intent = new Intent(context, BrowserDealsActivity.class);
                            intent.putExtra("vendor_id", cursor.getLong(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_ID)));
                            intent.putExtra("coupon_id", cursor.getLong(cursor.getColumnIndex(DataContract.Coupons.COLUMN_COUPON_ID)));
                            intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_AFFILIATE_URL)));
                            intent.putExtra("vendor_commission", cursor.getFloat(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_COMMISSION)));
                            context.startActivity(intent);
                        } else {
                            if (cursor != null) {
                                int position = getAdapterPosition();
                                cursor.moveToPosition(position);
                                Bundle loginBundle = new Bundle();
                                loginBundle.putString(Utilities.CALLING_ACTIVITY, "BrowserDealsActivity");
                                loginBundle.putLong(Utilities.VENDOR_ID, cursor.getLong(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_ID)));
                                loginBundle.putLong(Utilities.COUPON_ID, cursor.getLong(cursor.getColumnIndex(DataContract.Coupons.COLUMN_COUPON_ID)));
                                loginBundle.putString(Utilities.AFFILIATE_URL, cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_AFFILIATE_URL)));
                                loginBundle.putFloat(Utilities.VENDOR_COMMISSION, cursor.getFloat(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_COMMISSION)));
                                Utilities.needLoginDialog(context, loginBundle);
                            }
                        }
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utilities.isLoggedIn(context)) {
                            int position = getAdapterPosition();
                            cursor.moveToPosition(position);
                            Intent intent = new Intent(context, BrowserDealsActivity.class);
                            intent.putExtra("vendor_id", cursor.getLong(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_ID)));
                            intent.putExtra("coupon_id", cursor.getLong(cursor.getColumnIndex(DataContract.Coupons.COLUMN_COUPON_ID)));
                            intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_AFFILIATE_URL)));
                            intent.putExtra("vendor_commission", cursor.getFloat(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_COMMISSION)));
                            context.startActivity(intent);
                        } else {
                            if (cursor != null) {
                                int position = getAdapterPosition();
                                cursor.moveToPosition(position);
                                Bundle loginBundle = new Bundle();
                                loginBundle.putString(Utilities.CALLING_ACTIVITY, "BrowserDealsActivity");
                                loginBundle.putLong(Utilities.VENDOR_ID, cursor.getLong(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_ID)));
                                loginBundle.putLong(Utilities.COUPON_ID, cursor.getLong(cursor.getColumnIndex(DataContract.Coupons.COLUMN_COUPON_ID)));
                                loginBundle.putString(Utilities.AFFILIATE_URL, cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_AFFILIATE_URL)));
                                loginBundle.putFloat(Utilities.VENDOR_COMMISSION, cursor.getFloat(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_COMMISSION)));
                                Utilities.needLoginDialog(context, loginBundle);
                            }
                        }
                    }
                });
            }
        }

        public CouponsRecyclerAdapter(Context context) {
            this.context = context;
            cursorAdapter = new CouponsCursorAdapter(context, null);
            calendar = Calendar.getInstance();
        }

        @Override
        public CouponsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (parent instanceof RecyclerView) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupons, parent, false);
                view.setFocusable(true);
                return new CouponsViewHolder(view);
            } else {
                throw new RuntimeException("Not bound to RecyclerView");
            }
        }

        @Override
        public void onBindViewHolder(CouponsViewHolder holder, int position) {
            cursor = getCursor();
            cursor.moveToPosition(position);
            String label = cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_LABEL));
            String date = cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_EXPIRATION_DATE));
            String expire = context.getString(R.string.prefix_expire) + " " + date.substring(5, 7) + "/" + date.substring(8, 10) + "/" + date.substring(0, 4);
            String couponCode = cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_COUPON_CODE));
            holder.vhRestrictions.setText(label);
            calendar.set(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)), Integer.parseInt(date.substring(8, 10)));
            long timeDifference = calendar.getTimeInMillis();
            long timeCurrent = System.currentTimeMillis();
            timeDifference = timeDifference - timeCurrent;
            if (timeDifference > 31536000000L) {
                holder.vhExpireDate.setText("Ongoing");
            } else {
                holder.vhExpireDate.setText(expire);
            }
            if (couponCode.length() < 4) {
                holder.vhCouponCode.setVisibility(View.INVISIBLE);
            } else if (couponCode.length() > 12) {
                holder.vhCouponCode.setText(couponCode.substring(0, 12));
                holder.vhCouponCode.setVisibility(View.VISIBLE);
            } else {
                holder.vhCouponCode.setText(couponCode);
                holder.vhCouponCode.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return cursorAdapter.getCount();
        }

        public void changeCursor(Cursor cursor) {
            cursorAdapter.changeCursor(cursor);
            notifyDataSetChanged();
        }

        public Cursor getCursor() {
            return cursorAdapter.getCursor();
        }

        private class CouponsCursorAdapter extends CursorAdapter {

            public CouponsCursorAdapter(Context context, Cursor cursor) {
                super(context, cursor);
            }

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return null;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
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
            TextView tvClose = (TextView) v.findViewById(R.id.closeButton);
            tvDescription.setText(description);
            tvExceptions.setText(exceptionInfo);
            tvClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InfoDialog.super.dismiss();
                }
            });
            return v;
        }
    }
}
