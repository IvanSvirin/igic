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

import db.DataContract;

import com.cashback.model.Product;
import com.cashback.ui.LaunchActivity;
import com.cashback.ui.StoreActivity;
import com.cashback.ui.web.BrowserDealsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductsTabFragment extends Fragment {
    private FragmentUi fragmentUi;

    public static ProductsTabFragment newInstance() {
        return new ProductsTabFragment();
    }

    public ProductsTabFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270) {
            view = inflater.inflate(R.layout.layout_featured_tab_1_land_common, container, false);
        } else {
            view = inflater.inflate(R.layout.layout_featured_tab_1, container, false);
        }
        fragmentUi = new FragmentUi(view);
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
        private ProductsRecyclerAdapter productsAdapter;
        @Bind(R.id.favorites_recycler_view)
        RecyclerView favoritesRecyclerView;

        public FragmentUi(View view) {
            ButterKnife.bind(this, view);
            initListAdapter();
        }

        private void initListAdapter() {
            productsAdapter = new ProductsRecyclerAdapter(getActivity());
            favoritesRecyclerView.setHasFixedSize(true);
            favoritesRecyclerView.setAdapter(productsAdapter);
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }
    }

    public static class ProductsRecyclerAdapter extends RecyclerView.Adapter<ProductsRecyclerAdapter.ProductsViewHolder> {
        final private Context context;
        private ArrayList<Product> productsArray;
        private Picasso picasso;

        public class ProductsViewHolder extends RecyclerView.ViewHolder {
            public ImageView vhStoreLogo;
            public ImageView vhProductImage;
            public TextView vhCashBack;
            public TextView vhBtnShopNow;
            public AppCompatImageView vhShareButton;
            public TextView vhProductName;
            public TextView vhPrice;
            public TextView vhYourPriceValue;

            public ProductsViewHolder(View itemView) {
                super(itemView);
                vhStoreLogo = (ImageView) itemView.findViewById(R.id.storeLogo);
                vhProductImage = (ImageView) itemView.findViewById(R.id.productImage);
                vhCashBack = (TextView) itemView.findViewById(R.id.cashBack);
                vhBtnShopNow = (TextView) itemView.findViewById(R.id.btnShopNow);
                vhShareButton = (AppCompatImageView) itemView.findViewById(R.id.shareButton);
                vhProductName = (TextView) itemView.findViewById(R.id.productName);
                vhPrice = (TextView) itemView.findViewById(R.id.price);
                vhYourPriceValue = (TextView) itemView.findViewById(R.id.yourPriceValue);
                vhShareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utilities.isLoggedIn(context)) {
                            int position = getAdapterPosition();
                            Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(
                                    productsArray.get(position).getVendorId()));
                            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                LaunchActivity.shareMerchantLink(context, cursor.getString(cursor.getColumnIndex(
                                        DataContract.Merchants.COLUMN_AFFILIATE_URL)), productsArray.get(position).getVendorId(), productsArray.get(position).getVendorLogoUrl());
                                cursor.close();
                            }
                        } else {
                            Bundle loginBundle = new Bundle();
                            loginBundle.putString(Utilities.CALLING_ACTIVITY, "AllResultsActivity");
                            Utilities.needLoginDialog(context, loginBundle);
                        }
                    }
                });
                vhBtnShopNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(
                                productsArray.get(position).getVendorId()));
                        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                        if (Utilities.isLoggedIn(context)) {
                            if (cursor != null) {
                                cursor.moveToFirst();
                                Intent intent = new Intent(context, BrowserDealsActivity.class);
                                intent.putExtra("vendor_id", cursor.getLong(cursor.getColumnIndex(DataContract.Merchants.COLUMN_VENDOR_ID)));
                                intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_AFFILIATE_URL)));
                                intent.putExtra("vendor_commission", cursor.getFloat(cursor.getColumnIndex(DataContract.Merchants.COLUMN_COMMISSION)));
                                context.startActivity(intent);
                                cursor.close();
                            }
                        } else {
                            if (cursor != null) {
                                cursor.moveToFirst();
                                Bundle loginBundle = new Bundle();
                                loginBundle.putString(Utilities.CALLING_ACTIVITY, "BrowserDealsActivity");
                                loginBundle.putLong(Utilities.VENDOR_ID, cursor.getLong(cursor.getColumnIndex(DataContract.Merchants.COLUMN_VENDOR_ID)));
                                loginBundle.putString(Utilities.AFFILIATE_URL, cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_AFFILIATE_URL)));
                                loginBundle.putFloat(Utilities.VENDOR_COMMISSION, cursor.getFloat(cursor.getColumnIndex(DataContract.Merchants.COLUMN_COMMISSION)));
                                Utilities.needLoginDialog(context, loginBundle);
                                cursor.close();
                            }
                        }
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(productsArray.get(position).getVendorId()));
                        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            Intent intent = new Intent(context, StoreActivity.class);
                            intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_AFFILIATE_URL)));
                            intent.putExtra("vendor_logo_url", cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_LOGO_URL)));
                            intent.putExtra("vendor_commission", cursor.getFloat(cursor.getColumnIndex(DataContract.Merchants.COLUMN_COMMISSION)));
                            intent.putExtra("vendor_id", cursor.getLong(cursor.getColumnIndex(DataContract.Merchants.COLUMN_VENDOR_ID)));
                            context.startActivity(intent);
                            cursor.close();
                        }
                    }
                });
            }
        }

        public ProductsRecyclerAdapter(Context context) {
            this.context = context;
            productsArray = AllResultsActivity.productsArray;
            picasso = Picasso.with(context);
        }

        @Override
        public ProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (parent instanceof RecyclerView) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
                view.setFocusable(true);
                return new ProductsViewHolder(view);
            } else {
                throw new RuntimeException("Not bound to RecyclerView");
            }
        }

        @Override
        public void onBindViewHolder(ProductsViewHolder holder, int position) {
            Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(productsArray.get(position).getVendorId()));
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int benefit = cursor.getInt(cursor.getColumnIndex(DataContract.Merchants.COLUMN_OWNERS_BENEFIT));

            String logoUrl = productsArray.get(position).getVendorLogoUrl();
            String productImageUrl = productsArray.get(position).getImageUrl();
            String name = productsArray.get(position).getTitle();
            float price = productsArray.get(position).getPrice();
            float commission = productsArray.get(position).getVendorCommission();
            picasso.load(logoUrl).into(holder.vhStoreLogo);
            picasso.load(productImageUrl).into(holder.vhProductImage);
            holder.vhProductName.setText(name);
            holder.vhPrice.setText("$" + String.valueOf(price) + " ($" + String.format("%.2f", (price * commission / 100)) + ")");
            holder.vhYourPriceValue.setText("$" + String.format("%.2f", (price * (100 - commission) / 100)));
            if (commission != 0) {
                holder.vhCashBack.setText("+ " + String.valueOf(commission) + "% " + context.getString(R.string.cash_back));
            } else {
                if (benefit == 1) {
                    holder.vhCashBack.setText("OWNERS BENEFIT");
                } else {
                    holder.vhCashBack.setText("SPECIAL RATE");
                }
            }
        }

        @Override
        public int getItemCount() {
            return productsArray.size();
        }
    }
}
