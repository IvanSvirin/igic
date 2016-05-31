package com.cashback.ui.web;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cashback.R;

/**
 * Created by I.Svirin on 4/25/2016.
 */
public class PageFragment extends Fragment {
public static final String COUPON_CODE = "coupon_code";
public static final String VENDOR_ID = "vendor_id";
public static final String EXPIRATION_DATE = "expiration_date";
public static final String RESTRICTIONS = "restrictions";

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
        if (getArguments().getString(COUPON_CODE).length() < 4) {
            couponCode.setVisibility(View.INVISIBLE);
            shopNowButton.setText(getResources().getString(R.string.btn_shop_now));
        } else {
            couponCode.setVisibility(View.VISIBLE);
            couponCode.setText(getArguments().getString(COUPON_CODE));
            shopNowButton.setText(getResources().getString(R.string.btn_copy_code));
        }
        TextView restrictions = (TextView) view.findViewById(R.id.restrictions);
        String date = getArguments().getString(EXPIRATION_DATE);
        String expire = date.substring(5, 7) + "/" + date.substring(8, 10) + "/" + date.substring(0, 4);
        restrictions.setText(getArguments().getString(RESTRICTIONS) + " Exp. " + expire);

        shopNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                Intent intent = new Intent(getContext(), BrowserActivity.class);
                intent.putExtra("vendor_id", getArguments().getLong(VENDOR_ID));
                getContext().startActivity(intent);
            }
        });
        return view;
    }
}
