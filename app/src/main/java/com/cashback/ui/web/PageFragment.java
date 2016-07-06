package com.cashback.ui.web;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cashback.R;

public class PageFragment extends Fragment {
    public static final String COUPON_CODE = "coupon_code";
    public static final String VENDOR_ID = "vendor_id";
    public static final String EXPIRATION_DATE = "expiration_date";
    public static final String RESTRICTIONS = "restrictions";
    public static final String GOT_CODE = "got_code";
    public static final String AFFILIATE_URL = "affiliate_url";

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
                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(COUPON_CODE, getArguments().getString(COUPON_CODE));
                clipboardManager.setPrimaryClip(clipData);
                getActivity().finish();
                Intent intent = new Intent(getContext(), BrowserDealsActivity.class);
                intent.putExtra(VENDOR_ID, getArguments().getLong(VENDOR_ID));
                intent.putExtra(AFFILIATE_URL, getArguments().getString(AFFILIATE_URL));
                if (getArguments().getString(COUPON_CODE).length() >= 4) {
                    intent.putExtra(GOT_CODE, true);
                } else {
                    intent.putExtra(GOT_CODE, false);
                }
                getContext().startActivity(intent);
            }
        });
        return view;
    }
}
