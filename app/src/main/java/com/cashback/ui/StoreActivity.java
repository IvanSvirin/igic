package com.cashback.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.cashback.R;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by I.Svirin on 4/15/2016.
 */
public class StoreActivity extends AppCompatActivity {
    @Bind(R.id.appbar_layout)
    AppBarLayout appBarLayout;
    @Bind(R.id.storeLogo)
    ImageView storeLogo;
    @Bind(R.id.cashBack)
    TextView cashBack;
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

        Bitmap bitmap = ((BitmapDrawable)storeLogo.getDrawable()).getBitmap();
        Palette.Builder pb = new Palette.Builder(bitmap);
        Palette palette = pb.generate();
        Palette.Swatch swatch = palette.getVibrantSwatch();
        int color = swatch != null ? swatch.getRgb() : -7292864;
        appBarLayout.setBackgroundColor(color);
    }
}
