package com.cashback.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.cashback.R;
import com.cashback.db.DataInsertHandler;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by I.Svirin on 4/15/2016.
 */
// TODO: 4/19/2016 TEST - will be deleted

public class StoreActivity extends AppCompatActivity {
    @Bind(R.id.appbar_layout)
    AppBarLayout appBarLayout;
    @Bind(R.id.storeLogo)
    ImageView storeLogo;
    @Bind(R.id.cashBack)
    TextView cashBack;
    @Bind(R.id.restrictions)
    TextView restrictions;
    @Bind(R.id.expireDate)
    TextView expireDate;
    @Bind(R.id.info)
    ImageView info;
    private String logoUrl;
    private Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_storee);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        ButterKnife.bind(this);

        Intent intent = getIntent();
        logoUrl = intent.getStringExtra("vendor_logo_url");
        picasso = Picasso.with(this);
        picasso.load(logoUrl)
                .into(storeLogo);
        cashBack.setText(intent.getStringExtra("vendor_commission"));
        restrictions.setText(intent.getStringExtra("restriction"));
        expireDate.setText(getString(R.string.prefix_expire) + intent.getStringExtra("expiration_date"));

        Bitmap bitmap = ((BitmapDrawable) storeLogo.getDrawable()).getBitmap();
        Palette.Builder pb = new Palette.Builder(bitmap);
        Palette palette = pb.generate();
        Palette.Swatch swatch = palette.getVibrantSwatch();
        int color = swatch != null ? swatch.getRgb() : -7292864;
        appBarLayout.setBackgroundColor(color);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDescriptionDialog("");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDescriptionDialog(String message) {
        InfoDialog dialog = InfoDialog.newInstance(message);
        dialog.setCancelable(true);
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        dialog.show(getSupportFragmentManager(), "TAG_DIALOG");
    }

    public static class InfoDialog extends DialogFragment {

        private static String description;

        static InfoDialog newInstance(String message) {
            description = message;
            return new InfoDialog();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.view_dialog, container, false);
            TextView tv = (TextView) v.findViewById(R.id.description);
            tv.setText(description);
            return v;
        }
    }
}
