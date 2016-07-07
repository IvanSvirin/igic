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
import com.cashback.model.Merchant;
import com.cashback.rest.request.FavoritesRequest;
import com.cashback.ui.LaunchActivity;
import com.cashback.ui.StoreActivity;
import com.cashback.ui.web.BrowserDealsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StoresTabFragment extends Fragment {
    private FragmentUi fragmentUi;

    public static StoresTabFragment newInstance() {
        return new StoresTabFragment();
    }

    public StoresTabFragment() {
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
    public void onDestroyView() {
        super.onDestroyView();
        fragmentUi.unbind();
    }

    public class FragmentUi {
        private StoresRecyclerAdapter storesAdapter;
        @Bind(R.id.hot_deals_recycler_view)
        RecyclerView hotDealsRecyclerView;

        public FragmentUi(StoresTabFragment fragment, View view) {
            ButterKnife.bind(this, view);
            initListAdapter();
        }

        private void initListAdapter() {
            storesAdapter = new StoresRecyclerAdapter(getActivity());
            hotDealsRecyclerView.setHasFixedSize(true);
            hotDealsRecyclerView.setAdapter(storesAdapter);
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }
    }

    public static class StoresRecyclerAdapter extends RecyclerView.Adapter<StoresRecyclerAdapter.StoresViewHolder> {
        final private Context context;
        private ArrayList<Merchant> storesArray;
        private Picasso picasso;

        public class StoresViewHolder extends RecyclerView.ViewHolder {
            public ImageView vhStoreLogo;
            public TextView vhCashBack;
            public TextView vhBtnShopNow;
            public AppCompatImageView vhShareButton;
            public ImageView vhFavorite;

            public StoresViewHolder(View itemView) {
                super(itemView);
                vhStoreLogo = (ImageView) itemView.findViewById(R.id.storeLogo);
                vhCashBack = (TextView) itemView.findViewById(R.id.cashBack);
                vhBtnShopNow = (TextView) itemView.findViewById(R.id.btnShopNow);
                vhShareButton = (AppCompatImageView) itemView.findViewById(R.id.shareButton);
                vhFavorite = (ImageView) itemView.findViewById(R.id.favorite);
                vhShareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utilities.isLoggedIn(context)) {
                            int position = getAdapterPosition();
                            Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(
                                    storesArray.get(position).getVendorId()));
                            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                            cursor.moveToFirst();
                            LaunchActivity.shareLink(context, cursor.getString(cursor.getColumnIndex(
                                    DataContract.Merchants.COLUMN_AFFILIATE_URL)), storesArray.get(position).getVendorId());
                            cursor.close();
                        } else {
                            Utilities.needLoginDialog(context);
                        }
                    }
                });
                vhFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utilities.isLoggedIn(context)) {
                            int position = getAdapterPosition();
                            Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(storesArray.get(position).getVendorId()));
                            Cursor c = context.getContentResolver().query(uri, null, null, null, null);
                            int count = c.getCount();
                            c.close();
                            if (count == 0) {
                                new FavoritesRequest(context).addMerchant(storesArray.get(position).getVendorId());
                            } else {
                                new FavoritesRequest(context).deleteMerchant(storesArray.get(position).getVendorId());
                            }
                            notifyDataSetChanged();
                        } else {
                            Utilities.needLoginDialog(context);
                        }
                    }
                });

                vhBtnShopNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utilities.isLoggedIn(context)) {
                            int position = getAdapterPosition();
                            Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(storesArray.get(position).getVendorId()));
                            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                            cursor.moveToFirst();
                            Intent intent = new Intent(context, BrowserDealsActivity.class);
                            intent.putExtra("vendor_id", cursor.getLong(cursor.getColumnIndex(DataContract.Merchants.COLUMN_VENDOR_ID)));
                            intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_AFFILIATE_URL)));
                            intent.putExtra("vendor_commission", cursor.getFloat(cursor.getColumnIndex(DataContract.Merchants.COLUMN_COMMISSION)));
                            context.startActivity(intent);
                        } else {
                            Utilities.needLoginDialog(context);
                        }
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(storesArray.get(position).getVendorId()));
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

        public StoresRecyclerAdapter(Context context) {
            this.context = context;
            storesArray = AllResultsActivity.storesArray;
            picasso = Picasso.with(context);
        }

        @Override
        public StoresViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (parent instanceof RecyclerView) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
                view.setFocusable(true);
                return new StoresViewHolder(view);
            } else {
                throw new RuntimeException("Not bound to RecyclerView");
            }
        }

        @Override
        public void onBindViewHolder(StoresViewHolder holder, int position) {
            String logoUrl = storesArray.get(position).getLogoUrl();
            String commission = String.valueOf(storesArray.get(position).getCommission());
            boolean isFavorite = storesArray.get(position).isFavorite();
            picasso.load(logoUrl).into(holder.vhStoreLogo);
            holder.vhCashBack.setText(commission);
            if (isFavorite) {
                holder.vhFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite));
            } else {
                holder.vhFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favoriteoff));
            }
        }

        @Override
        public int getItemCount() {
            return storesArray.size();
        }
    }
}
