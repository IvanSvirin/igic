package com.cashback.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.cashback.R;


public class AutofitRecyclerView extends RecyclerView {

    private int columnWidth = -1;
    private int layoutManager = 0;
    private LayoutManager manager;

    public AutofitRecyclerView(Context context) {
        super(context);
        init(context, null);
    }

    public AutofitRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutofitRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(
                    attrs,
                    new int[]{android.R.attr.columnWidth});
            try {
                columnWidth = array.getDimensionPixelSize(0, -1);
            } finally {
                array.recycle();
            }
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.AutofitRecyclerView,
                    0, 0);
            try {
                layoutManager = a.getInt(R.styleable.AutofitRecyclerView_layoutAutoManager, 0);
            } finally {
                a.recycle();
            }
        }
        if (layoutManager == 0) {
            manager = new LinearLayoutManager(context);
            setLayoutManager(manager);
        } else if (layoutManager == 1) {
            manager = new GridLayoutManager(getContext(), 2);
            setLayoutManager(manager);
        }
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (layoutManager == 1) {
            if (columnWidth > 0) {
                int spanCount = Math.max(1, getMeasuredWidth() / columnWidth);
                if (spanCount > 3) spanCount = 3;
                ((GridLayoutManager) manager).setSpanCount(spanCount);
            }
        }
    }
}
