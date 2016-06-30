package com.cashback.ui.featured;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.rest.event.CouponsEvent;
import com.cashback.ui.LaunchActivity;
import ui.MainActivity;
import com.cashback.ui.StoreActivity;
import com.cashback.ui.login.LoginActivity;
import com.cashback.ui.web.BrowserDealsActivity;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class HotDealsTabFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private FragmentUi fragmentUi;

    public static HotDealsTabFragment newInstance() {
        return new HotDealsTabFragment();
    }

    public HotDealsTabFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270) {
            view = inflater.inflate(R.layout.layout_featured_tab_0_land, container, false);
        } else {
            view = inflater.inflate(R.layout.layout_featured_tab_0, container, false);
        }
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
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
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
        fragmentUi.hotDealsAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        fragmentUi.hotDealsAdapter.changeCursor(null);
    }

    public void onEvent(CouponsEvent event) {
        if (event.isSuccess) {
            getLoaderManager().restartLoader(MainActivity.COUPONS_LOADER, null, this);
        } else {
            Utilities.showFailNotification(event.message, getContext());
        }
    }

    public class FragmentUi {
        private HotDealsRecyclerAdapter hotDealsAdapter;
        @Bind(R.id.hot_deals_recycler_view)
        RecyclerView hotDealsRecyclerView;

        public FragmentUi(HotDealsTabFragment fragment, View view) {
            ButterKnife.bind(this, view);
            initListAdapter();
        }

        private void initListAdapter() {
            hotDealsAdapter = new HotDealsRecyclerAdapter(getActivity());
            hotDealsRecyclerView.setHasFixedSize(true);
            hotDealsRecyclerView.setAdapter(hotDealsAdapter);
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }
    }

    public static class HotDealsRecyclerAdapter extends RecyclerView.Adapter<HotDealsRecyclerAdapter.HotDealsViewHolder> {
        final private Context context;
        private HotDealsCursorAdapter cursorAdapter;
        private Picasso picasso;
        private Cursor cursor;

        public class HotDealsViewHolder extends RecyclerView.ViewHolder {
            public ImageView vhStoreLogo;
            public TextView vhRestrictions;
            public TextView vhCashBack;
            public TextView vhBtnShopNow;
            public TextView vhExpireDate;
            public TextView vhCouponCode;
            public AppCompatImageView vhShareButton;

            public HotDealsViewHolder(View itemView) {
                super(itemView);
                vhStoreLogo = (ImageView) itemView.findViewById(R.id.storeLogo);
                vhRestrictions = (TextView) itemView.findViewById(R.id.restrictions);
                vhCashBack = (TextView) itemView.findViewById(R.id.cashBack);
                vhBtnShopNow = (TextView) itemView.findViewById(R.id.btnShopNow);
                vhExpireDate = (TextView) itemView.findViewById(R.id.expireDate);
                vhCouponCode = (TextView) itemView.findViewById(R.id.couponCode);
                vhShareButton = (AppCompatImageView) itemView.findViewById(R.id.shareButton);
                vhShareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        cursor.moveToPosition(position);
                        LaunchActivity.shareLink(context, cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_AFFILIATE_URL)),
                                cursor.getLong(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_ID)));
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
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        Cursor cursor = getCursor();
                        cursor.moveToPosition(position);
                        Intent intent = new Intent(context, StoreActivity.class);
                        intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_AFFILIATE_URL)));
                        intent.putExtra("vendor_logo_url", cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_LOGO_URL)));
                        intent.putExtra("vendor_commission", cursor.getFloat(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_COMMISSION)));
                        intent.putExtra("vendor_id", cursor.getLong(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_ID)));
                        context.startActivity(intent);
                    }
                });
            }
        }

        public HotDealsRecyclerAdapter(Context context) {
            this.context = context;
            cursorAdapter = new HotDealsCursorAdapter(context, null);
            picasso = Picasso.with(context);
        }

        @Override
        public HotDealsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (parent instanceof RecyclerView) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hot_deal, parent, false);
                view.setFocusable(true);
                return new HotDealsViewHolder(view);
            } else {
                throw new RuntimeException("Not bound to RecyclerView");
            }
        }

        @Override
        public void onBindViewHolder(HotDealsViewHolder holder, int position) {
            cursor = getCursor();
            cursor.moveToPosition(position);
            final String logoUrl = cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_LOGO_URL));
            String label = cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_LABEL));
            String cashBack = cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_COMMISSION));
            String date = cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_EXPIRATION_DATE));
            String expire = context.getString(R.string.prefix_expire) + " " + date.substring(5, 7) + "/" + date.substring(8, 10) + "/" + date.substring(0, 4);
            String couponCode = cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_COUPON_CODE));
            picasso.load(logoUrl).into(holder.vhStoreLogo);
            holder.vhRestrictions.setText(label);
            holder.vhCashBack.setText(cashBack);
            holder.vhExpireDate.setText(expire);
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

        private class HotDealsCursorAdapter extends CursorAdapter {

            public HotDealsCursorAdapter(Context context, Cursor cursor) {
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
}
