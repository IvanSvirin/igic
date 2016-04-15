package com.cashback.ui;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.cashback.R;

import butterknife.ButterKnife;

/**
 * Created by I.Svirin on 4/15/2016.
 */
public class StoreActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_store);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        setTitle();
        ButterKnife.bind(this);
    }


    Color getColor() {
//        ImageView imageView = ((ImageView) v);
//        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
//        int pixel = bitmap.getPixel(x, y);
//        int redValue = Color.red(pixel);
//        int blueValue = Color.blue(pixel);
//        int greenValue = Color.green(pixel);
        return new Color();
    }
}
