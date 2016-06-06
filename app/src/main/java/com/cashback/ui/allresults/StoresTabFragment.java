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
import com.cashback.model.Merchant;
import com.cashback.ui.LaunchActivity;
import com.cashback.ui.StoreActivity;
import com.cashback.ui.components.NestedListView;
import com.cashback.ui.login.LoginActivity;
import com.cashback.ui.web.BrowserDealsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by I.Svirin on 4/15/2016.
 */
public class StoresTabFragment extends Fragment {
    private FragmentUi fragmentUi;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_common_stores, container, false);
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
        private StoresAdapter storesAdapter;
        private NestedListView nestedListView;
        private GridView gridView;

        public FragmentUi(StoresTabFragment fragment, View view) {
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
            storesAdapter = new StoresAdapter(getActivity(), isGridLayout);
            storesAdapter.setOnSaleClickListener(new StoresAdapter.OnSaleClickListener() {
                @Override
                public void onSaleClick(long id) {
                    if (Utilities.isLoggedIn(context)) {
                        Intent intent = new Intent(context, BrowserDealsActivity.class);
                        intent.putExtra("vendor_id", id);
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                }
            });
            storesAdapter.setOnShareClickListener(new StoresAdapter.OnShareClickListener() {
                @Override
                public void onShareClick(long shareId) {
                    Uri uri = Uri.withAppendedPath(DataContract.URI_COUPONS, String.valueOf(shareId));
                    Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();
                    LaunchActivity.createLink(context, cursor.getString(cursor.getColumnIndex(DataContract.Coupons.COLUMN_AFFILIATE_URL)),
                            cursor.getLong(cursor.getColumnIndex(DataContract.Coupons.COLUMN_VENDOR_ID)));
                }
            });
            storesAdapter.setOnCardClickListener(new StoresAdapter.OnCardClickListener() {
                @Override
                public void onCardClick(long cardId) {
                    Intent intent = new Intent(context, StoreActivity.class);
                    intent.putExtra("vendor_id", cardId);
                    context.startActivity(intent);
                }
            });
            if (isGridLayout) {
                gridView.setAdapter(storesAdapter);
            } else {
                nestedListView.setAdapter(storesAdapter);
            }
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }
    }

    public static class StoresAdapter extends BaseAdapter {
        private final boolean GRID_TYPE_FLAG;
        private Context context;
        private ArrayList<Merchant> storesArray;
        private OnSaleClickListener onSaleClickListener;
        private OnShareClickListener onShareClickListener;
        private OnCardClickListener onCardClickListener;
        private Picasso picasso;

        public StoresAdapter(Context context, boolean gridType) {
            GRID_TYPE_FLAG = gridType;
            this.context = context;
            storesArray = AllResultsActivity.storesArray;
            picasso = Picasso.with(context);
        }

        @Override
        public int getCount() {
            return storesArray.size();
        }

        @Override
        public Object getItem(int position) {
            return storesArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_store_list_small_card_favorite, parent, false);
                if (GRID_TYPE_FLAG) {
                    GridViewHolder holder = new GridViewHolder(convertView);
                    convertView.setTag(holder);
                } else {
                    ViewHolder holder = new ViewHolder(convertView);
                    convertView.setTag(holder);
                }
            }
            final long vendorId = storesArray.get(position).getVendorId();
            String logoUrl = storesArray.get(position).getLogoUrl();
            String commission = String.valueOf(storesArray.get(position).getCommission());
            boolean isFavorite = storesArray.get(position).isFavorite();
            if (GRID_TYPE_FLAG) {
                final GridViewHolder holder = (GridViewHolder) convertView.getTag();
                picasso.load(logoUrl).into(holder.vhStoreLogo);
                holder.vhCashBack.setText(commission);
                if (isFavorite) {
                    holder.vhFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favoritegrey));
                } else {
                    holder.vhFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favoriteoff));
                }
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
                holder.vhCashBack.setText(commission);
                if (isFavorite) {
                    holder.vhFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favoritegrey));
                } else {
                    holder.vhFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favoriteoff));
                }
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
            @Bind(R.id.cashBack)
            TextView vhCashBack;
            @Bind(R.id.btnShopNow)
            TextView vhBtnShopNow;
            @Bind(R.id.shareButton)
            AppCompatImageView vhShareButton;
            @Bind(R.id.favorite)
            ImageView vhFavorite;

            public ViewHolder(View convertView) {
                ButterKnife.bind(this, convertView);
            }
        }

        public static class GridViewHolder {
            @Bind(R.id.card_view)
            FrameLayout cardView;
            @Bind(R.id.storeLogo)
            ImageView vhStoreLogo;
            @Bind(R.id.cashBack)
            TextView vhCashBack;
            @Bind(R.id.btnShopNow)
            TextView vhBtnShopNow;
            @Bind(R.id.shareButton)
            AppCompatImageView vhShareButton;
            @Bind(R.id.favorite)
            ImageView vhFavorite;

            public GridViewHolder(View convertView) {
                ButterKnife.bind(this, convertView);
            }
        }
    }
}
