package com.cashback.ui.featured;

import android.app.ProgressDialog;
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

import com.cashback.rest.event.ExtrasEvent;
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

public class ExtraTabFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private FragmentUi fragmentUi;
    private static ProgressDialog progressDialog;

    public static ExtraTabFragment newInstance() {
        return new ExtraTabFragment();
    }

    public ExtraTabFragment() {
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
        fragmentUi = new FragmentUi(view);
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
        fragmentUi.extraAdapter.notifyDataSetChanged();
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
        fragmentUi.extraAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        fragmentUi.extraAdapter.changeCursor(null);
    }

    public void onEvent(ExtrasEvent event) {
        if (event.isSuccess) {
            getLoaderManager().restartLoader(MainActivity.EXTRAS_LOADER, null, this);
        } else {
            Utilities.showFailNotification(event.message, getContext());
        }
    }

    public void onEvent(FavoritesEvent event) {
        progressDialog.dismiss();
        if (event.isSuccess) {
            fragmentUi.extraAdapter.notifyDataSetChanged();
        } else {
            Utilities.showFailNotification(event.message, getContext());
        }
    }


    public class FragmentUi {
        private ExtraRecyclerAdapter extraAdapter;
        @Bind(R.id.extra_recycler_view)
        RecyclerView extraRecyclerView;

        public FragmentUi(View view) {
            ButterKnife.bind(this, view);
            initListAdapter();
        }

        private void initListAdapter() {
            extraAdapter = new ExtraRecyclerAdapter(getActivity());
            extraRecyclerView.setHasFixedSize(true);
            extraRecyclerView.setAdapter(extraAdapter);
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }
    }

    public static class ExtraRecyclerAdapter extends RecyclerView.Adapter<ExtraRecyclerAdapter.ExtraViewHolder> {
        final private Context context;
        private ExtraCursorAdapter cursorAdapter;
        private Picasso picasso;
        private Cursor cursor;

        public class ExtraViewHolder extends RecyclerView.ViewHolder {
            public ImageView vhStoreLogo;
            public TextView vhCashBack;
            public TextView vhBtnShopNow;
            public AppCompatImageView vhShareButton;
            public ImageView vhFavorite;
            public TextView vhWas;

            public ExtraViewHolder(View itemView) {
                super(itemView);
                vhStoreLogo = (ImageView) itemView.findViewById(R.id.storeLogo);
                vhFavorite = (ImageView) itemView.findViewById(R.id.favorite);
                vhCashBack = (TextView) itemView.findViewById(R.id.cashBack);
                vhBtnShopNow = (TextView) itemView.findViewById(R.id.btnShopNow);
                vhWas = (TextView) itemView.findViewById(R.id.was);
                vhShareButton = (AppCompatImageView) itemView.findViewById(R.id.shareButton);
                vhShareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utilities.isLoggedIn(context)) {
                            int position = getAdapterPosition();
                            cursor.moveToPosition(position);
                            LaunchActivity.shareMerchantLink(context, cursor.getString(cursor.getColumnIndex(DataContract.Extras.COLUMN_AFFILIATE_URL)),
                                    cursor.getLong(cursor.getColumnIndex(DataContract.Extras.COLUMN_VENDOR_ID)), cursor.getString(cursor.getColumnIndex(DataContract.Extras.COLUMN_LOGO_URL)));
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
                            long id = cursor.getLong(cursor.getColumnIndex(DataContract.Extras.COLUMN_VENDOR_ID));
                            Uri uri = Uri.withAppendedPath(DataContract.URI_FAVORITES, String.valueOf(id));
                            Cursor c = context.getContentResolver().query(uri, null, null, null, null);
                            int count = 0;
                            if (c != null) {
                                count = c.getCount();
                                c.close();
                            }
                            if (count == 0) {
                                new FavoritesRequest(context).addMerchant(id);
                            } else {
                                new FavoritesRequest(context).deleteMerchant(id);
                            }
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
                            intent.putExtra("vendor_id", cursor.getLong(cursor.getColumnIndex(DataContract.Extras.COLUMN_VENDOR_ID)));
                            intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.Extras.COLUMN_AFFILIATE_URL)));
                            intent.putExtra("vendor_commission", cursor.getFloat(cursor.getColumnIndex(DataContract.Extras.COLUMN_COMMISSION)));
                            context.startActivity(intent);
                        } else {
                            Bundle loginBundle = new Bundle();
                            loginBundle.putString(Utilities.CALLING_ACTIVITY, "MainActivity");
                            Utilities.needLoginDialog(context, loginBundle);
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
                        intent.putExtra("affiliate_url", cursor.getString(cursor.getColumnIndex(DataContract.Extras.COLUMN_AFFILIATE_URL)));
                        intent.putExtra("vendor_logo_url", cursor.getString(cursor.getColumnIndex(DataContract.Extras.COLUMN_LOGO_URL)));
                        intent.putExtra("vendor_commission", cursor.getFloat(cursor.getColumnIndex(DataContract.Extras.COLUMN_COMMISSION)));
                        intent.putExtra("vendor_id", cursor.getLong(cursor.getColumnIndex(DataContract.Extras.COLUMN_VENDOR_ID)));
                        context.startActivity(intent);
                    }
                });
            }
        }

        public ExtraRecyclerAdapter(Context context) {
            this.context = context;
            cursorAdapter = new ExtraCursorAdapter(context, null);
            picasso = Picasso.with(context);
        }

        @Override
        public ExtraViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (parent instanceof RecyclerView) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_extra, parent, false);
                view.setFocusable(true);
                return new ExtraViewHolder(view);
            } else {
                throw new RuntimeException("Not bound to RecyclerView");
            }
        }

        @Override
        public void onBindViewHolder(ExtraViewHolder holder, int position) {
            cursor = getCursor();
            cursor.moveToPosition(position);
            final long vendorId = cursor.getLong(cursor.getColumnIndex(DataContract.Extras.COLUMN_VENDOR_ID));
            final String logoUrl = cursor.getString(cursor.getColumnIndex(DataContract.Extras.COLUMN_LOGO_URL));
            String cashBack = String.valueOf(cursor.getFloat(cursor.getColumnIndex(DataContract.Extras.COLUMN_COMMISSION)));
            String wasCashBack = cursor.getString(cursor.getColumnIndex(DataContract.Extras.COLUMN_COMMISSION_WAS));
            Uri uri = Uri.withAppendedPath(DataContract.URI_FAVORITES, String.valueOf(vendorId));
            Cursor c = context.getContentResolver().query(uri, null, null, null, null);
            int count = 0;
            if (c != null) {
                count = c.getCount();
                c.close();
            }
            if (!(wasCashBack.equals("") || wasCashBack.equals(" "))) {
                holder.vhWas.setText("Was " + wasCashBack + "%");
            }
            holder.vhWas.setPaintFlags(holder.vhWas.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            picasso.load(logoUrl).into(holder.vhStoreLogo);
            holder.vhCashBack.setText("+ " + cashBack);
            if (count == 0) {
                holder.vhFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favoriteoff));
            } else {
                holder.vhFavorite.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite));
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

        private class ExtraCursorAdapter extends CursorAdapter {

            public ExtraCursorAdapter(Context context, Cursor cursor) {
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
