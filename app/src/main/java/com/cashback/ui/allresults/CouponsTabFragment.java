package com.cashback.ui.allresults;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.cashback.model.Coupon;
import com.cashback.ui.LaunchActivity;
import com.cashback.ui.StoreActivity;
import com.cashback.ui.login.LoginActivity;
import com.cashback.ui.web.BrowserDealsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CouponsTabFragment extends Fragment {
    private FragmentUi fragmentUi;

    public static CouponsTabFragment newInstance() {
        return new CouponsTabFragment();
    }

    public CouponsTabFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270) {
            view = inflater.inflate(R.layout.layout_featured_tab_2_land, container, false);
        } else {
            view = inflater.inflate(R.layout.layout_featured_tab_2, container, false);
        }
        fragmentUi = new FragmentUi(this, view);
        if (!Utilities.isActiveConnection(getActivity())) {
            Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), R.string.alert_about_connection, Snackbar.LENGTH_SHORT).show();
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentUi.unbind();
    }

    public class FragmentUi {
        private CouponsRecyclerAdapter couponsAdapter;
        @Bind(R.id.extra_recycler_view)
        RecyclerView extraRecyclerView;

        public FragmentUi(CouponsTabFragment fragment, View view) {
            ButterKnife.bind(this, view);
            initListAdapter();
        }

        private void initListAdapter() {
            couponsAdapter = new CouponsRecyclerAdapter(getActivity());
            extraRecyclerView.setHasFixedSize(true);
            extraRecyclerView.setAdapter(couponsAdapter);
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }
    }

    public static class CouponsRecyclerAdapter extends RecyclerView.Adapter<CouponsRecyclerAdapter.CouponsViewHolder> {
        final private Context context;
        private ArrayList<Coupon> couponsArray;
        private Picasso picasso;

        public class CouponsViewHolder extends RecyclerView.ViewHolder {
            public ImageView vhStoreLogo;
            public TextView vhRestrictions;
            public TextView vhExpireDate;
            public TextView vhCashBack;
            public TextView vhCouponCode;
            public TextView vhBtnShopNow;
            public AppCompatImageView vhShareButton;

            public CouponsViewHolder(View itemView) {
                super(itemView);
                vhStoreLogo = (ImageView) itemView.findViewById(R.id.storeLogo);
                vhRestrictions = (TextView) itemView.findViewById(R.id.restrictions);
                vhExpireDate = (TextView) itemView.findViewById(R.id.expireDate);
                vhCashBack = (TextView) itemView.findViewById(R.id.cashBack);
                vhCouponCode = (TextView) itemView.findViewById(R.id.couponCode);
                vhBtnShopNow = (TextView) itemView.findViewById(R.id.btnShopNow);
                vhShareButton = (AppCompatImageView) itemView.findViewById(R.id.shareButton);
                vhShareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(
                                couponsArray.get(position).getVendorId()));
                        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        LaunchActivity.shareLink(context, cursor.getString(cursor.getColumnIndex(
                                DataContract.Merchants.COLUMN_AFFILIATE_URL)), couponsArray.get(position).getVendorId());
                        cursor.close();
                    }
                });
                vhBtnShopNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utilities.isLoggedIn(context)) {
                            int position = getAdapterPosition();
                            Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(
                                    couponsArray.get(position).getVendorId()));
                            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                            cursor.moveToFirst();
                            Intent intent = new Intent(context, BrowserDealsActivity.class);
                            intent.putExtra("vendor_id", cursor.getLong(cursor.getColumnIndex(DataContract.Merchants.COLUMN_VENDOR_ID)));
                            intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_AFFILIATE_URL)));
                            intent.putExtra("vendor_commission", cursor.getFloat(cursor.getColumnIndex(DataContract.Merchants.COLUMN_COMMISSION)));
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
                        Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(
                                couponsArray.get(position).getVendorId()));
                        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        Intent intent = new Intent(context, StoreActivity.class);
                        intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_AFFILIATE_URL)));
                        intent.putExtra("vendor_logo_url", cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_LOGO_URL)));
                        intent.putExtra("vendor_commission", cursor.getFloat(cursor.getColumnIndex(DataContract.Merchants.COLUMN_COMMISSION)));
                        intent.putExtra("vendor_id", cursor.getLong(cursor.getColumnIndex(DataContract.Merchants.COLUMN_VENDOR_ID)));
                        context.startActivity(intent);
                    }
                });
            }
        }

        public CouponsRecyclerAdapter(Context context) {
            this.context = context;
            couponsArray = AllResultsActivity.dealsArray;
            picasso = Picasso.with(context);
        }

        @Override
        public CouponsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (parent instanceof RecyclerView) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hot_deal, parent, false);
                view.setFocusable(true);
                return new CouponsViewHolder(view);
            } else {
                throw new RuntimeException("Not bound to RecyclerView");
            }
        }

        @Override
        public void onBindViewHolder(CouponsViewHolder holder, int position) {
            String logoUrl = couponsArray.get(position).getVendorLogoUrl();
            String label = couponsArray.get(position).getLabel();
            float commission = couponsArray.get(position).getVendorCommission();
            // TODO: 5/31/2016 not forever
            String expire = "Exp. 12/31/2050";
            String code = couponsArray.get(position).getCouponCode();
            picasso.load(logoUrl).into(holder.vhStoreLogo);
            holder.vhRestrictions.setText(label);
            holder.vhCashBack.setText(String.valueOf(commission));
            holder.vhExpireDate.setText(expire);
            if (code.length() < 4) {
                holder.vhCouponCode.setVisibility(View.INVISIBLE);
            } else {
                holder.vhCouponCode.setText(code);
            }
        }

        @Override
        public int getItemCount() {
            return couponsArray.size();
        }
    }
}
