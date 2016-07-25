package com.cashback.ui.allresults;

import android.app.ProgressDialog;
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

import com.cashback.model.Merchant;
import com.cashback.rest.event.FavoritesEvent;
import com.cashback.rest.request.FavoritesRequest;
import com.cashback.ui.LaunchActivity;
import com.cashback.ui.StoreActivity;
import com.cashback.ui.web.BrowserDealsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class StoresTabFragment extends Fragment {
    private FragmentUi fragmentUi;
    private static ProgressDialog progressDialog;

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
        fragmentUi = new FragmentUi(view);
        if (!Utilities.isActiveConnection(getActivity())) {
            Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), R.string.alert_about_connection, Snackbar.LENGTH_SHORT).show();
        }
        return view;
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

    public void onEvent(FavoritesEvent event) {
        progressDialog.dismiss();
        if (event.isSuccess) {
            fragmentUi.storesAdapter.notifyDataSetChanged();
        } else {
            Utilities.showFailNotification(event.message, getContext());
        }
    }

    public class FragmentUi {
        private StoresRecyclerAdapter storesAdapter;
        @Bind(R.id.hot_deals_recycler_view)
        RecyclerView hotDealsRecyclerView;

        public FragmentUi(View view) {
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
                            if (cursor != null) {
                                cursor.moveToFirst();
                                LaunchActivity.shareMerchantLink(context, cursor.getString(cursor.getColumnIndex(
                                        DataContract.Merchants.COLUMN_AFFILIATE_URL)), storesArray.get(position).getVendorId(), storesArray.get(position).getLogoUrl());
                                cursor.close();
                            }
                        } else {
                            Bundle loginBundle = new Bundle();
                            loginBundle.putString(Utilities.CALLING_ACTIVITY, "AllResultsActivity");
                            Utilities.needLoginDialog(context, loginBundle);
                        }
                    }
                });
                vhFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utilities.isLoggedIn(context)) {
                            int position = getAdapterPosition();
                            Uri uri = Uri.withAppendedPath(DataContract.URI_FAVORITES, String.valueOf(storesArray.get(position).getVendorId()));
                            Cursor c = context.getContentResolver().query(uri, null, null, null, null);
                            int count = 0;
                            if (c != null) {
                                count = c.getCount();
                                c.close();
                            }
                            if (count == 0) {
                                new FavoritesRequest(context).addMerchant(storesArray.get(position).getVendorId());
                            } else {
                                new FavoritesRequest(context).deleteMerchant(storesArray.get(position).getVendorId());
                            }
                            progressDialog = Utilities.onCreateProgressDialog(context);
                            progressDialog.show();
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
                        Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(storesArray.get(position).getVendorId()));
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
                        Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(storesArray.get(position).getVendorId()));
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
            float commission = storesArray.get(position).getCommission();
            picasso.load(logoUrl).into(holder.vhStoreLogo);
            boolean benefit = storesArray.get(position).isOwnersBenefit();
            if (commission != 0) {
                holder.vhCashBack.setText("+ " + String.valueOf(commission) + "% " + context.getString(R.string.cash_back));
            } else {
                if (benefit) {
                    holder.vhCashBack.setText("OWNERS BENEFIT");
                } else {
                    holder.vhCashBack.setText("SPECIAL RATE");
                }
            }

            Uri uri = Uri.withAppendedPath(DataContract.URI_FAVORITES, String.valueOf(storesArray.get(position).getVendorId()));
            Cursor c = context.getContentResolver().query(uri, null, null, null, null);
            int count = 0;
            if (c != null) {
                count = c.getCount();
                c.close();
            }
            if (count == 0) {
                holder.vhFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favoriteoff));
            } else {
                holder.vhFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite));
            }
        }

        @Override
        public int getItemCount() {
            return storesArray.size();
        }
    }
}
