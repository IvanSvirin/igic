package com.cashback.ui.featured;

import android.app.ProgressDialog;
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

import db.DataContract;

import com.cashback.rest.event.FavoritesEvent;
import com.cashback.rest.request.FavoritesRequest;

import ui.MainActivity;

import com.cashback.ui.LaunchActivity;
import com.cashback.ui.StoreActivity;
import com.cashback.ui.web.BrowserDealsActivity;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class FavoritesTabFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private FragmentUi fragmentUi;
    private static ProgressDialog progressDialog;

    public static FavoritesTabFragment newInstance() {
        return new FavoritesTabFragment();
    }

    public FavoritesTabFragment() {
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Utilities.isLoggedIn(getContext())) {
            getLoaderManager().initLoader(MainActivity.FAVORITES_LOADER, null, this);
        } else {
            if (fragmentUi.favoritesRecyclerView != null) {
                fragmentUi.favoritesRecyclerView.setAdapter(null);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        getLoaderManager().restartLoader(MainActivity.FAVORITES_LOADER, null, this);
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
        if (id == MainActivity.FAVORITES_LOADER) {
            loader = new CursorLoader(getActivity());
            loader.setUri(DataContract.URI_FAVORITES);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        fragmentUi.favoritesAdapter.changeCursor(data);
        if (data == null) {
            new FavoritesRequest(getContext()).fetchData();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        fragmentUi.favoritesAdapter.changeCursor(null);
    }

    public void onEvent(FavoritesEvent event) {
        progressDialog.dismiss();
        if (event.isSuccess) {
            fragmentUi.favoritesAdapter.notifyDataSetChanged();
            getLoaderManager().restartLoader(MainActivity.FAVORITES_LOADER, null, this);
        } else {
            Utilities.showFailNotification(event.message, getContext());
        }
    }

    public class FragmentUi {
        private FavoritesRecyclerAdapter favoritesAdapter;
        @Bind(R.id.favorites_recycler_view)
        RecyclerView favoritesRecyclerView;

        public FragmentUi(View view) {
            ButterKnife.bind(this, view);
            initListAdapter();
        }

        private void initListAdapter() {
            favoritesAdapter = new FavoritesRecyclerAdapter(getActivity());
            favoritesRecyclerView.setHasFixedSize(true);
            favoritesRecyclerView.setAdapter(favoritesAdapter);
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }
    }

    public static class FavoritesRecyclerAdapter extends RecyclerView.Adapter<FavoritesRecyclerAdapter.FavoritesViewHolder> {
        final private Context context;
        private FavoritesCursorAdapter cursorAdapter;
        private Picasso picasso;
        private Cursor cursor;

        public class FavoritesViewHolder extends RecyclerView.ViewHolder {
            public ImageView vhStoreLogo;
            public TextView vhCashBack;
            public TextView vhBtnShopNow;
            public AppCompatImageView vhShareButton;
            ImageView vhFavorite;

            public FavoritesViewHolder(View itemView) {
                super(itemView);
                vhStoreLogo = (ImageView) itemView.findViewById(R.id.storeLogo);
                vhFavorite = (ImageView) itemView.findViewById(R.id.favorite);
                vhCashBack = (TextView) itemView.findViewById(R.id.cashBack);
                vhBtnShopNow = (TextView) itemView.findViewById(R.id.btnShopNow);
                vhShareButton = (AppCompatImageView) itemView.findViewById(R.id.shareButton);
                vhShareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utilities.isLoggedIn(context)) {
                            int position = getAdapterPosition();
                            cursor.moveToPosition(position);
                            LaunchActivity.shareMerchantLink(context, cursor.getString(cursor.getColumnIndex(DataContract.Favorites.COLUMN_AFFILIATE_URL)),
                                    cursor.getLong(cursor.getColumnIndex(DataContract.Favorites.COLUMN_VENDOR_ID)), cursor.getString(cursor.getColumnIndex(DataContract.Favorites.COLUMN_LOGO_URL)));
                        } else {
                            Bundle loginBundle = new Bundle();
                            loginBundle.putString(Utilities.CALLING_ACTIVITY, "MainActivity");
                            Utilities.needLoginDialog(context, loginBundle);
                        }
                    }
                });
                vhFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utilities.isLoggedIn(context)) {
                            int position = getAdapterPosition();
                            cursor.moveToPosition(position);
                            new FavoritesRequest(context).deleteMerchant(cursor.getLong(cursor.getColumnIndex(DataContract.Favorites.COLUMN_VENDOR_ID)));
                            progressDialog = Utilities.onCreateProgressDialog(context);
                            progressDialog.show();
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
                            intent.putExtra("vendor_id", cursor.getLong(cursor.getColumnIndex(DataContract.Favorites.COLUMN_VENDOR_ID)));
                            intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.Favorites.COLUMN_AFFILIATE_URL)));
                            intent.putExtra("vendor_commission", cursor.getFloat(cursor.getColumnIndex(DataContract.Favorites.COLUMN_COMMISSION)));
                            context.startActivity(intent);
                        } else {
                            if (cursor != null) {
                                Bundle loginBundle = new Bundle();
                                loginBundle.putString(Utilities.CALLING_ACTIVITY, "BrowserDealsActivity");
                                loginBundle.putLong(Utilities.VENDOR_ID, cursor.getLong(cursor.getColumnIndex(DataContract.Favorites.COLUMN_VENDOR_ID)));
                                loginBundle.putString(Utilities.AFFILIATE_URL, cursor.getString(cursor.getColumnIndex(DataContract.Favorites.COLUMN_AFFILIATE_URL)));
                                loginBundle.putFloat(Utilities.VENDOR_COMMISSION, cursor.getFloat(cursor.getColumnIndex(DataContract.Favorites.COLUMN_COMMISSION)));
                                Utilities.needLoginDialog(context, loginBundle);
                                cursor.close();
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
                            intent.putExtra("vendor_id", cursor.getLong(cursor.getColumnIndex(DataContract.Favorites.COLUMN_VENDOR_ID)));
                            intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.Favorites.COLUMN_AFFILIATE_URL)));
                            intent.putExtra("vendor_commission", cursor.getFloat(cursor.getColumnIndex(DataContract.Favorites.COLUMN_COMMISSION)));
                            context.startActivity(intent);
                        } else {
                            if (cursor != null) {
                                Bundle loginBundle = new Bundle();
                                loginBundle.putString(Utilities.CALLING_ACTIVITY, "BrowserDealsActivity");
                                loginBundle.putLong(Utilities.VENDOR_ID, cursor.getLong(cursor.getColumnIndex(DataContract.Favorites.COLUMN_VENDOR_ID)));
                                loginBundle.putString(Utilities.AFFILIATE_URL, cursor.getString(cursor.getColumnIndex(DataContract.Favorites.COLUMN_AFFILIATE_URL)));
                                loginBundle.putFloat(Utilities.VENDOR_COMMISSION, cursor.getFloat(cursor.getColumnIndex(DataContract.Favorites.COLUMN_COMMISSION)));
                                Utilities.needLoginDialog(context, loginBundle);
                                cursor.close();
                            }
                        }
                    }
                });
                vhStoreLogo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        Cursor cursor = getCursor();
                        cursor.moveToPosition(position);
                        Intent intent = new Intent(context, StoreActivity.class);
                        intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.Favorites.COLUMN_AFFILIATE_URL)));
                        intent.putExtra("vendor_logo_url", cursor.getString(cursor.getColumnIndex(DataContract.Favorites.COLUMN_LOGO_URL)));
                        intent.putExtra("vendor_commission", cursor.getFloat(cursor.getColumnIndex(DataContract.Favorites.COLUMN_COMMISSION)));
                        intent.putExtra("vendor_id", cursor.getLong(cursor.getColumnIndex(DataContract.Favorites.COLUMN_VENDOR_ID)));
                        context.startActivity(intent);
                    }
                });
            }
        }

        public FavoritesRecyclerAdapter(Context context) {
            this.context = context;
            cursorAdapter = new FavoritesCursorAdapter(context, null);
            picasso = Picasso.with(context);
        }

        @Override
        public FavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (parent instanceof RecyclerView) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
                view.setFocusable(true);
                return new FavoritesViewHolder(view);
            } else {
                throw new RuntimeException("Not bound to RecyclerView");
            }
        }

        @Override
        public void onBindViewHolder(FavoritesViewHolder holder, int position) {
            cursor = getCursor();
            cursor.moveToPosition(position);
            final String logoUrl = cursor.getString(cursor.getColumnIndex(DataContract.Favorites.COLUMN_LOGO_URL));
            String cashBack = cursor.getString(cursor.getColumnIndex(DataContract.Favorites.COLUMN_COMMISSION));
            picasso.load(logoUrl).into(holder.vhStoreLogo);
            int benefit = cursor.getInt(cursor.getColumnIndex(DataContract.Favorites.COLUMN_OWNERS_BENEFIT));
            if (!cashBack.equals("0")) {
                holder.vhCashBack.setText(cashBack + "% " + context.getString(R.string.cash_back));
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
            return cursorAdapter.getCount();
        }

        public void changeCursor(Cursor cursor) {
            cursorAdapter.changeCursor(cursor);
            notifyDataSetChanged();
        }

        public Cursor getCursor() {
            return cursorAdapter.getCursor();
        }

        private class FavoritesCursorAdapter extends CursorAdapter {

            public FavoritesCursorAdapter(Context context, Cursor cursor) {
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
