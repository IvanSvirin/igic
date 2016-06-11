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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.model.Merchant;
import com.cashback.ui.LaunchActivity;
import com.cashback.ui.StoreActivity;
import com.cashback.ui.login.LoginActivity;
import com.cashback.ui.web.BrowserDealsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StoresTabFragmentTest extends Fragment {
    private FragmentUi fragmentUi;

    public static StoresTabFragmentTest newInstance() {
        return new StoresTabFragmentTest();
    }

    public StoresTabFragmentTest() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270) {
            view = inflater.inflate(R.layout.layout_featured_tab_new0_land, container, false);
        } else {
            view = inflater.inflate(R.layout.layout_featured_tab_new0, container, false);
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
        private HotDealsRecyclerAdapter hotDealsAdapter;
        @Bind(R.id.hot_deals_recycler_view)
        RecyclerView hotDealsRecyclerView;

        public FragmentUi(StoresTabFragmentTest fragment, View view) {
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
        private ArrayList<Merchant> storesArray;
        private Picasso picasso;

        public class HotDealsViewHolder extends RecyclerView.ViewHolder {
            public FrameLayout cardView;
            public ImageView vhStoreLogo;
            public TextView vhCashBack;
            public TextView vhBtnShopNow;
            public AppCompatImageView vhShareButton;
            public ImageView vhFavorite;

            public HotDealsViewHolder(View itemView) {
                super(itemView);
                cardView = (FrameLayout) itemView.findViewById(R.id.card_view);
                vhStoreLogo = (ImageView) itemView.findViewById(R.id.storeLogo);
                vhCashBack = (TextView) itemView.findViewById(R.id.cashBack);
                vhBtnShopNow = (TextView) itemView.findViewById(R.id.btnShopNow);
                vhShareButton = (AppCompatImageView) itemView.findViewById(R.id.shareButton);
                vhShareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(
                                storesArray.get(position).getVendorId()));
                        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        LaunchActivity.shareLink(context, cursor.getString(cursor.getColumnIndex(
                                DataContract.Merchants.COLUMN_AFFILIATE_URL)), storesArray.get(position).getVendorId());
                        cursor.close();
                    }
                });
                vhBtnShopNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utilities.isLoggedIn(context)) {
                            int position = getAdapterPosition();
                            Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(
                                    storesArray.get(position).getVendorId()));
                            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
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
                                storesArray.get(position).getVendorId()));
                        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
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

        public HotDealsRecyclerAdapter(Context context) {
            this.context = context;
            storesArray = AllResultsActivity.storesArray;
            picasso = Picasso.with(context);
        }

        @Override
        public HotDealsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (parent instanceof RecyclerView) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_common_favorite, parent, false);
                view.setFocusable(true);
                return new HotDealsViewHolder(view);
            } else {
                throw new RuntimeException("Not bound to RecyclerView");
            }
        }

        @Override
        public void onBindViewHolder(HotDealsViewHolder holder, int position) {
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
