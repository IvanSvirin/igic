package com.cashback.ui.web;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cashback.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by I.Svirin on 4/25/2016.
 */
public class PageFragment extends Fragment {
    public static int pageNumber;

    public static PageFragment newInstance(int page) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments() != null ? getArguments().getInt("num") : 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result;
        result = inflater.inflate(R.layout.layout_browser_pagee, container, false);
        TextView pageNum = (TextView) result.findViewById(R.id.pageNumber);
        pageNum.setText("Фрагмент " + String.valueOf(pageNumber + 1));
        return result;
    }
}
