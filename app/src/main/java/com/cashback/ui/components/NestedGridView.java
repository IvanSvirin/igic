package com.cashback.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;


public class NestedGridView extends GridView {

    private static final int MAXIMUM_LIST_ITEMS_VIEWABLE = 99;

    public NestedGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int newHeight = 0;
        int columnCount = getNumColumns();
        int verticalSpacing = getVerticalSpacing();

        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            ListAdapter listAdapter = getAdapter();
            if (listAdapter != null && !listAdapter.isEmpty()) {
                int listPosition = 0;
                int measuredLastItem = 0;
                int countItems = listAdapter.getCount();
                for (listPosition = 0; listPosition < countItems
                        && listPosition < MAXIMUM_LIST_ITEMS_VIEWABLE; listPosition++) {
                    View listItem = listAdapter.getView(listPosition, null, this);
                    //now it will not throw a NPE if listItem is a ViewGroup instance
                    if (listItem instanceof ViewGroup) {
                        listItem.setLayoutParams(new LayoutParams(
                                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                    }
                    listItem.measure(widthMeasureSpec, heightMeasureSpec);
                    newHeight += listItem.getMeasuredHeight();
                    if (columnCount > 1) {
                        newHeight += verticalSpacing;
                        if (listPosition + 1 == countItems)
                            measuredLastItem = listItem.getMeasuredHeight();
                    }
                }
                if (columnCount > 1 && columnCount < 3 && newHeight > 0)
                    newHeight = countItems % 2 == 0 ? newHeight / columnCount : (newHeight / columnCount) + measuredLastItem / 2;
                else if (columnCount >= 3 && newHeight > 0)
                    newHeight = countItems % 3 == 0 ? newHeight / columnCount : (int) ((newHeight / columnCount) + measuredLastItem * 0.75);
            }
            if ((heightMode == MeasureSpec.AT_MOST) && (newHeight > heightSize)) {
                if (newHeight > heightSize) {
                    newHeight = heightSize;
                }
            }
        } else {
            newHeight = getMeasuredHeight();
        }
        setMeasuredDimension(getMeasuredWidth(), newHeight);
    }


}