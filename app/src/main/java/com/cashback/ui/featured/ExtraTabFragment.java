package com.cashback.ui.featured;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
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
import com.cashback.rest.event.ExtrasEvent;
import com.cashback.rest.request.FavoritesRequest;
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

public class ExtraTabFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private FragmentUi fragmentUi;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_common_extra, container, false);
        fragmentUi = new FragmentUi(this, view);
        if (!Utilities.isActiveConnection(getActivity())) {
            Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), R.string.alert_about_connection, Snackbar.LENGTH_SHORT).show();
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MainActivity.EXTRAS_LOADER, null, this);
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
        if (id == MainActivity.EXTRAS_LOADER) {
            loader = new CursorLoader(getActivity());
            loader.setUri(DataContract.URI_EXTRAS);
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

    public void onEvent(ExtrasEvent event) {
        if (event.isSuccess) {
            getLoaderManager().restartLoader(MainActivity.EXTRAS_LOADER, null, this);
        }
    }

    public class FragmentUi {
        private boolean isGridLayout;
        private FeaturedAdapter featuredAdapter;
        private NestedListView nestedListView;
        private GridView gridView;

        public FragmentUi(ExtraTabFragment fragment, View view) {
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
                        Uri uri = Uri.withAppendedPath(DataContract.URI_EXTRAS, String.valueOf(id));
                        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        intent.putExtra("vendor_id", cursor.getLong(cursor.getColumnIndex(DataContract.Extras.COLUMN_VENDOR_ID)));
                        intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.Extras.COLUMN_AFFILIATE_URL)));
                        intent.putExtra("vendor_commission", cursor.getFloat(cursor.getColumnIndex(DataContract.Extras.COLUMN_COMMISSION)));
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
                    Uri uri = Uri.withAppendedPath(DataContract.URI_MERCHANTS, String.valueOf(shareId));
                    Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();
                    LaunchActivity.shareLink(context, cursor.getString(cursor.getColumnIndex(DataContract.Merchants.COLUMN_AFFILIATE_URL)), shareId);
                }
            });
            featuredAdapter.setOnFavoriteClickListener(new FeaturedAdapter.OnFavoriteClickListener() {
                @Override
                public void onFavoriteClick(long favoriteId) {
                    Uri uri = Uri.withAppendedPath(DataContract.URI_FAVORITES, String.valueOf(favoriteId));
                    Cursor c = context.getContentResolver().query(uri, null, null, null, null);
                    int count = c.getCount();
                    if (count == 0) {
                        new FavoritesRequest(context).addMerchant(favoriteId);
                    } else {
                        new FavoritesRequest(context).deleteMerchant(favoriteId);
                    }
                    featuredAdapter.notifyDataSetChanged();
                }
            });
            AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = featuredAdapter.getCursor();
                    cursor.moveToPosition(position);
                    Intent intent = new Intent(context, StoreActivity.class);
                    intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.Extras.COLUMN_AFFILIATE_URL)));
                    intent.putExtra("vendor_logo_url", cursor.getString(cursor.getColumnIndex(DataContract.Extras.COLUMN_LOGO_URL)));
                    intent.putExtra("vendor_commission", cursor.getFloat(cursor.getColumnIndex(DataContract.Extras.COLUMN_COMMISSION)));
                    intent.putExtra("vendor_id", cursor.getLong(cursor.getColumnIndex(DataContract.Extras.COLUMN_VENDOR_ID)));
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
        private OnFavoriteClickListener onFavoriteClickListener;
        private Picasso picasso;

        public FeaturedAdapter(Context context, Cursor c, int flags, boolean gridType) {
            super(context, c, flags);
            GRID_TYPE_FLAG = gridType;
            this.context = context;
            picasso = Picasso.with(context);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.item_common_extra, parent, false);
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
            final long vendorId = cursor.getLong(cursor.getColumnIndex(DataContract.Extras.COLUMN_VENDOR_ID));
            final String logoUrl = cursor.getString(cursor.getColumnIndex(DataContract.Extras.COLUMN_LOGO_URL));
            String cashBack = String.valueOf(cursor.getFloat(cursor.getColumnIndex(DataContract.Extras.COLUMN_COMMISSION)));
            String wasCashBack = cursor.getString(cursor.getColumnIndex(DataContract.Extras.COLUMN_COMMISSION_WAS));
            Uri uri = Uri.withAppendedPath(DataContract.URI_FAVORITES, String.valueOf(vendorId));
            Cursor c = context.getContentResolver().query(uri, null, null, null, null);
            int count = c.getCount();
            if (GRID_TYPE_FLAG) {
                GridViewHolder holder = (GridViewHolder) view.getTag();
                holder.vhWas.setText("Was " + wasCashBack + "%");
                holder.vhWas.setPaintFlags(holder.vhWas.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                picasso.load(logoUrl).into(holder.vhStoreLogo);
                holder.vhCashBack.setText(cashBack);
                if (count == 0) {
                    holder.vhFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favoriteoff));
                } else {
                    holder.vhFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite));
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
                holder.vhFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onFavoriteClickListener.onFavoriteClick(vendorId);
                    }
                });
            } else {
                ViewHolder holder = (ViewHolder) view.getTag();
                holder.vhWas.setText("Was " + wasCashBack + "%");
                holder.vhWas.setPaintFlags(holder.vhWas.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                picasso.load(logoUrl).into(holder.vhStoreLogo);
                holder.vhCashBack.setText(cashBack);
                if (count == 0) {
                    holder.vhFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favoriteoff));
                } else {
                    holder.vhFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite));
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
                holder.vhFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onFavoriteClickListener.onFavoriteClick(vendorId);
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

        public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
            onFavoriteClickListener = listener;
        }

        public interface OnFavoriteClickListener {
            void onFavoriteClick(long favoriteId);
        }


        public static class ViewHolder {
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
            @Bind(R.id.was)
            TextView vhWas;

            public ViewHolder(View convertView) {
                ButterKnife.bind(this, convertView);
            }
        }

        public static class GridViewHolder {
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
            @Bind(R.id.was)
            TextView vhWas;

            public GridViewHolder(View convertView) {
                ButterKnife.bind(this, convertView);
            }
        }

    }
}
