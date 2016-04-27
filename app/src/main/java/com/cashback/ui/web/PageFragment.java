package com.cashback.ui.web;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cashback.R;
import com.cashback.db.DataContract;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by I.Svirin on 4/25/2016.
 */
public class PageFragment extends Fragment {

    public static PageFragment newInstance() {
        PageFragment fragment = new PageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.layout_browser_page, container, false);
        TextView couponCode = (TextView) view.findViewById(R.id.couponCode);
        Button shopNowButton = (Button) view.findViewById(R.id.shopNowButton);
        if (getArguments().getString(DataContract.OfferEntry.COLUMN_CODE).length() < 4) {
            couponCode.setVisibility(View.INVISIBLE);
            shopNowButton.setText(getResources().getString(R.string.btn_shop_now));
        } else {
            couponCode.setVisibility(View.VISIBLE);
            couponCode.setText(getArguments().getString(DataContract.OfferEntry.COLUMN_CODE));
            shopNowButton.setText(getResources().getString(R.string.btn_copy_code));
        }
        TextView restrictions = (TextView) view.findViewById(R.id.restrictions);
        restrictions.setText(getArguments().getString(DataContract.OfferEntry.COLUMN_DESCRIPTION) + " Exp. " + getArguments().getString(DataContract.OfferEntry.COLUMN_EXPIRE));

        shopNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                Intent intent = new Intent(getContext(), BrowserActivity.class);
                getContext().startActivity(intent);
            }
        });
        return view;
    }
}
