package com.cashback.ui.featured;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.rest.event.CouponsEvent;
import com.cashback.ui.LaunchActivity;
import com.cashback.ui.MainActivity;
import com.cashback.ui.StoreActivity;
import com.cashback.ui.components.NestedListView;
import com.cashback.ui.login.LoginActivity;
import com.cashback.ui.web.BrowserDealsActivity;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by I.Svirin on 4/7/2016.
 */
public class HotDealsTabFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private FragmentUi fragmentUi;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_common_hot_deals, container, false);
        fragmentUi = new FragmentUi(this, view);
        if (!Utilities.isActiveConnection(getActivity())) {
            Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), R.string.alert_about_connection, Snackbar.LENGTH_SHORT).show();
        }
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MainActivity.COUPONS_LOADER, null, this);
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
        if (id == MainActivity.COUPONS_LOADER) {
            loader = new CursorLoader(getActivity());
            loader.setUri(DataContract.URI_COUPONS);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        fragmentUi.featuredAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        fragmentUi.featuredAdapter.changeCursor(null);
    }

    public void onEvent(CouponsEvent event) {
        if (event.isSuccess) {
            getLoaderManager().restartLoader(MainActivity.COUPONS_LOADER, null, this);
        }
    }

    public class FragmentUi {
        private boolean isGridLayout;
        private FeaturedAdapter featuredAdapter;
        private NestedListView nestedListView;
        private GridView gridView;

        public FragmentUi(HotDealsTabFragment fragment, View view) {
            ButterKnife.bind(this, view);
            isGridLayout = ButterKnife.findById(view, R.id.checking_element) != null;
            if (isGridLayout) {
                gridView = ButterKnife.findById(view, R.id.common_list);
            } else {
                nestedListView = ButterKnife.findById(view, R.id.common_list);
            }
            initListAdapter(fragment.getContext());
        }

        private void initListAdapter(final Context context) {
            featuredAdapter = new FeaturedAdapter(getActivity(), null, 0, isGridLayout);
            featuredAdapter.setOnSaleClickListener(new FeaturedAdapter.OnSaleClickListener() {
                @Override
                public void onSaleClick(long id) {
                    if (Utilities.isLoggedIn(context)) {
                        Intent intent = new Intent(context, BrowserDealsActivity.class);
                        Uri uri = Uri.withAppendedPath(DataContract.URI_COUPONS, String.valueOf(id));
                        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        intent.putExtra("vendor_id", cursor.getLong(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_ID)));
                        intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_AFFILIATE_URL)));
                        intent.putExtra("vendor_commission", cursor.getFloat(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_COMMISSION)));
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                }
            });
            featuredAdapter.setOnShareClickListener(new FeaturedAdapter.OnShareClickListener() {
                @Override
                public void onShareClick(long shareId) {
                    Uri uri = Uri.withAppendedPath(DataContract.URI_COUPONS, String.valueOf(shareId));
                    Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();
                    LaunchActivity.createLink(context, cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_AFFILIATE_URL)),
                            cursor.getLong(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_ID)));
                }
            });
            AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = featuredAdapter.getCursor();
                    cursor.moveToPosition(position);
                    Intent intent = new Intent(context, StoreActivity.class);
                    intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_AFFILIATE_URL)));
                    intent.putExtra("vendor_logo_url", cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_LOGO_URL)));
                    intent.putExtra("vendor_commission", cursor.getFloat(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_COMMISSION)));
                    intent.putExtra("vendor_id", cursor.getLong(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_ID)));
                    context.startActivity(intent);
                }
            };
            if (isGridLayout) {
                gridView.setOnItemClickListener(listener);
                gridView.setAdapter(featuredAdapter);
            } else {
                nestedListView.setOnItemClickListener(listener);
                nestedListView.setAdapter(featuredAdapter);
            }
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }
    }

    public static class FeaturedAdapter extends CursorAdapter {
        private final boolean GRID_TYPE_FLAG;
        private Context context;
        private OnSaleClickListener onSaleClickListener;
        private OnShareClickListener onShareClickListener;
        private Picasso picasso;

        public FeaturedAdapter(Context context, Cursor c, int flags, boolean gridType) {
            super(context, c, flags);
            GRID_TYPE_FLAG = gridType;
            this.context = context;
            picasso = Picasso.with(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.item_store_list_big_card_hot_deal, parent, false);
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
            final long couponId = cursor.getLong(cursor.getColumnIndex(DataContract.Coupons.COLUMN_COUPON_ID));
            final String logoUrl = cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_LOGO_URL));
            String restrictions = cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_RESTRICTIONS));
            String cashBack = cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_COMMISSION));
            String date = cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_EXPIRATION_DATE));
            String expire = context.getString(R.string.prefix_expire) + " " + date.substring(5, 7) + "/" + date.substring(8, 10) + "/" + date.substring(0, 4);
            String couponCode = cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_COUPON_CODE));
            if (GRID_TYPE_FLAG) {
                final GridViewHolder holder = (GridViewHolder) view.getTag();
                picasso.load(logoUrl).into(holder.vhStoreLogo);
                holder.vhRestrictions.setText(restrictions.trim());
                holder.vhCashBack.setText(cashBack);
                holder.vhExpireDate.setText(expire);
                if (couponCode.length() < 4) {
                    holder.vhCouponCode.setVisibility(View.INVISIBLE);
                } else {
                    holder.vhCouponCode.setText(couponCode);
                }
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
                picasso.load(logoUrl).into(holder.vhStoreLogo);
                holder.vhRestrictions.setText(restrictions.trim());
                holder.vhCashBack.setText(cashBack);
                holder.vhExpireDate.setText(expire);
                if (couponCode.length() < 4) {
                    holder.vhCouponCode.setVisibility(View.INVISIBLE);
                } else {
                    holder.vhCouponCode.setText(couponCode);
                }
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
            void onSaleClick(long saleId);
        }

        public void setOnShareClickListener(OnShareClickListener listener) {
            onShareClickListener = listener;
        }

        public interface OnShareClickListener {
            void onShareClick(long shareId);
        }

        public static class ViewHolder {
            @Bind(R.id.storeLogo)
            ImageView vhStoreLogo;
            @Bind(R.id.restrictions)
            TextView vhRestrictions;
            @Bind(R.id.cashBack)
            TextView vhCashBack;
            @Bind(R.id.btnShopNow)
            TextView vhBtnShopNow;
            @Bind(R.id.expireDate)
            TextView vhExpireDate;
            @Bind(R.id.couponCode)
            TextView vhCouponCode;
            @Bind(R.id.shareButton)
            AppCompatImageView vhShareButton;

            public ViewHolder(View convertView) {
                ButterKnife.bind(this, convertView);
            }
        }

        public static class GridViewHolder {
            @Bind(R.id.storeLogo)
            ImageView vhStoreLogo;
            @Bind(R.id.restrictions)
            TextView vhRestrictions;
            @Bind(R.id.cashBack)
            TextView vhCashBack;
            @Bind(R.id.btnShopNow)
            TextView vhBtnShopNow;
            @Bind(R.id.expireDate)
            TextView vhExpireDate;
            @Bind(R.id.couponCode)
            TextView vhCouponCode;
            @Bind(R.id.shareButton)
            AppCompatImageView vhShareButton;

            public GridViewHolder(View convertView) {
                ButterKnife.bind(this, convertView);
            }
        }
    }
}
