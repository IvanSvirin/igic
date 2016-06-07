package com.cashback.ui.account;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cashback.R;

public class StepFragment extends Fragment {

    static final String ARGUMENT_PAGE_STEP = "arg_page_step";
    private int pagerStep;

    static StepFragment newInstance(int page) {
        StepFragment pageFragment = new StepFragment();
        Bundle arg = new Bundle();
        arg.putInt(ARGUMENT_PAGE_STEP, page);
        pageFragment.setArguments(arg);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pagerStep = getArguments().getInt(ARGUMENT_PAGE_STEP);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        if (pagerStep == 0) {
            view = inflater.inflate(R.layout.fragment_pager_tour_step_0, container, false);
        } else if (pagerStep == 1) {
            view = inflater.inflate(R.layout.fragment_pager_tour_step_1, container, false);
        } else if (pagerStep == 2) {
            view = inflater.inflate(R.layout.fragment_pager_tour_step_2, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_pager_tour_step_3, container, false);
        }
        return view;
    }
}