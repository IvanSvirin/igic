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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.model.Product;
import com.cashback.ui.LaunchActivity;
import com.cashback.ui.StoreActivity;
import com.cashback.ui.components.NestedListView;
import com.cashback.ui.login.LoginActivity;
import com.cashback.ui.web.BrowserActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by I.Svirin on 4/15/2016.
 */
public class ProductsTabFragment extends Fragment {
    private FragmentUi fragmentUi;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_featured_tab1, container, false);  // layout_all_results_tab_grid1 doesn't work
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
        private boolean isGridLayout;
        private ProductsAdapter productsAdapter;
        private NestedListView nestedListView;
        private GridView gridView;

        public FragmentUi(ProductsTabFragment fragment, View view) {
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
            productsAdapter = new ProductsAdapter(getActivity(), isGridLayout);
            productsAdapter.setOnSaleClickListener(new ProductsAdapter.OnSaleClickListener() {
                @Override
                public void onSaleClick(long id) {
                    if (Utilities.isLoggedIn(context)) {
                        Intent intent = new Intent(context, BrowserActivity.class);
                        intent.putExtra("vendor_id", id);
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                }
            });
            productsAdapter.setOnShareClickListener(new ProductsAdapter.OnShareClickListener() {
                @Override
                public void onShareClick(long shareId) {
                    Uri uri = Uri.withAppendedPath(DataContract.URI_COUPONS, String.valueOf(shareId));
                    Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();
                    LaunchActivity.createLink(context, cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_AFFILIATE_URL)),
                            cursor.getLong(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_ID)));
                }
            });
            productsAdapter.setOnCardClickListener(new ProductsAdapter.OnCardClickListener() {
                @Override
                public void onCardClick(long cardId) {
                    Intent intent = new Intent(context, StoreActivity.class);
                    intent.putExtra("vendor_id", cardId);
                    context.startActivity(intent);
                }
            });
            if (isGridLayout) {
                gridView.setAdapter(productsAdapter);
            } else {
                nestedListView.setAdapter(productsAdapter);
            }
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }
    }

    public static class ProductsAdapter extends BaseAdapter {
        private final boolean GRID_TYPE_FLAG;
        private Context context;
        private ArrayList<Product> productsArray;
        private OnSaleClickListener onSaleClickListener;
        private OnShareClickListener onShareClickListener;
        private OnCardClickListener onCardClickListener;
        private Picasso picasso;

        public ProductsAdapter(Context context, boolean gridType) {
            GRID_TYPE_FLAG = gridType;
            this.context = context;
            productsArray = AllResultsActivity.productsArray;
            picasso = Picasso.with(context);
        }

        @Override
        public int getCount() {
            return productsArray.size();
        }

        @Override
        public Object getItem(int position) {
            return productsArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_product_list_card, parent, false);
                if (GRID_TYPE_FLAG) {
                    GridViewHolder holder = new GridViewHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    ViewHolder holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                }
            }
            final long vendorId = productsArray.get(position).getVendorId();
            String logoUrl = productsArray.get(position).getVendorLogoUrl();
            String productImageUrl = productsArray.get(position).getImageUrl();
            String name = productsArray.get(position).getTitle();
            float price = productsArray.get(position).getPrice();
            float commission = productsArray.get(position).getVendorCommission();
            if (GRID_TYPE_FLAG) {
                final GridViewHolder holder = (GridViewHolder) convertView.getTag();
                picasso.load(logoUrl).into(holder.vhStoreLogo);
                picasso.load(productImageUrl).into(holder.vhProductImage);
                holder.vhProductName.setText(name);
                holder.vhPrice.setText("$" + String.valueOf(price) + " ($" + String.format("%.2f", (price * commission / 100)) + ")");
                holder.vhYourPriceValue.setText("$" + String.format("%.2f", (price * (100 - commission) / 100)));
                holder.vhCashBack.setText(String.valueOf(commission));
                holder.vhBtnShopNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSaleClickListener.onSaleClick(vendorId);
                    }
                });
                holder.vhShareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onShareClickListener.onShareClick(vendorId);
                    }
                });
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCardClickListener.onCardClick(vendorId);
                    }
                });
            } else {
                ViewHolder holder = (ViewHolder) convertView.getTag();
                picasso.load(logoUrl).into(holder.vhStoreLogo);
                picasso.load(productImageUrl).into(holder.vhProductImage);
                holder.vhProductName.setText(name);
                holder.vhPrice.setText("$" + String.valueOf(price) + " ($" + String.format("%.2f", (price * commission / 100)) + ")");
                holder.vhYourPriceValue.setText(" $" + String.format("%.2f", (price * (100 - commission) / 100)));
                holder.vhCashBack.setText(String.valueOf(commission));
                holder.vhBtnShopNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSaleClickListener.onSaleClick(vendorId);
                    }
                });
                holder.vhShareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onShareClickListener.onShareClick(vendorId);
                    }
                });
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCardClickListener.onCardClick(vendorId);
                    }
                });
            }
            return convertView;
        }

        public void setOnCardClickListener(OnCardClickListener listener) {
            onCardClickListener = listener;
        }

        public interface OnCardClickListener {
            void onCardClick(long cardId);
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
            @Bind(R.id.card_view)
            FrameLayout cardView;
            @Bind(R.id.storeLogo)
            ImageView vhStoreLogo;
            @Bind(R.id.productImage)
            ImageView vhProductImage;
            @Bind(R.id.cashBack)
            TextView vhCashBack;
            @Bind(R.id.btnShopNow)
            TextView vhBtnShopNow;
            @Bind(R.id.shareButton)
            AppCompatImageView vhShareButton;
            @Bind(R.id.productName)
            TextView vhProductName;
            @Bind(R.id.price)
            TextView vhPrice;
            @Bind(R.id.yourPriceValue)
            TextView vhYourPriceValue;

            public ViewHolder(View convertView) {
                ButterKnife.bind(this, convertView);
            }
        }

        public static class GridViewHolder {
            @Bind(R.id.card_view)
            FrameLayout cardView;
            @Bind(R.id.storeLogo)
            ImageView vhStoreLogo;
            @Bind(R.id.productImage)
            ImageView vhProductImage;
            @Bind(R.id.cashBack)
            TextView vhCashBack;
            @Bind(R.id.btnShopNow)
            TextView vhBtnShopNow;
            @Bind(R.id.shareButton)
            AppCompatImageView vhShareButton;
            @Bind(R.id.productName)
            TextView vhProductName;
            @Bind(R.id.price)
            TextView vhPrice;
            @Bind(R.id.yourPriceValue)
            TextView vhYourPriceValue;

            public GridViewHolder(View convertView) {
                ButterKnife.bind(this, convertView);
            }
        }
    }
}
