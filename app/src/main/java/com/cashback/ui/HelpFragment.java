package com.cashback.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cashback.App;
import com.cashback.R;
import com.cashback.ui.account.TourActivity;
import com.cashback.ui.web.BrowserActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ui.MainActivity;

public class HelpFragment extends Fragment {
    public static final String TAG_HELP_FRAGMENT = "I_help_fragment";
    private FragmentUi fragmentUi;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_help, container, false);
        fragmentUi = new FragmentUi(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = fragmentUi.getToolbar();
        ((MainActivity) getActivity()).setAssociateToolbar(toolbar);
        //Google Analytics
        App app = (App) getActivity().getApplication();
        Tracker tracker = app.getDefaultTracker();
        tracker.setScreenName("Help");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.help);
    }

    public class FragmentUi {
        private Context context;
        @Bind(R.id.toolbar)
        Toolbar toolbar;

        public FragmentUi(HelpFragment fragment, View view) {
            this.context = fragment.getContext();
            ButterKnife.bind(this, view);
        }

        @OnClick({R.id.howItWorksFrame, R.id.sendAppFeedbackFrame, R.id.paymentQuestionsFrame, R.id.howToUseFrame, R.id.whereIsMineFrame, R.id.privacySecurityFrame, R.id.termsConditionsFrame})
        public void onClicks(View view) {
            Intent intent;
            switch (view.getId()) {
                case R.id.howItWorksFrame:
                    intent = new Intent(context, TourActivity.class);
                    startActivity(intent);
                    break;
                case R.id.sendAppFeedbackFrame:
                    intent = new Intent(context, BrowserActivity.class);
                    intent.putExtra("title", context.getResources().getString(R.string.send_app_feedback));
                    intent.putExtra("url", getString(R.string.send_app_feedback_path));
                    startActivity(intent);
                    break;
                case R.id.paymentQuestionsFrame:
                    intent = new Intent(context, BrowserActivity.class);
                    intent.putExtra("title", context.getResources().getString(R.string.payment_questions));
                    intent.putExtra("url", getString(R.string.payment_questions_path));
                    startActivity(intent);
                    break;
                case R.id.howToUseFrame:
                    intent = new Intent(context, BrowserActivity.class);
                    intent.putExtra("title", context.getResources().getString(R.string.how_to_use));
                    intent.putExtra("url", getString(R.string.how_to_use_path));
                    startActivity(intent);
                    break;
                case R.id.whereIsMineFrame:
                    intent = new Intent(context, BrowserActivity.class);
                    intent.putExtra("title", context.getResources().getString(R.string.where_is_mine));
                    intent.putExtra("url", getString(R.string.where_is_my_donation_path));
                    startActivity(intent);
                    break;
                case R.id.privacySecurityFrame:
                    intent = new Intent(context, BrowserActivity.class);
                    intent.putExtra("title", context.getResources().getString(R.string.privacy_security));
                    intent.putExtra("url", getString(R.string.privacy_and_security_path));
                    startActivity(intent);
                    break;
                case R.id.termsConditionsFrame:
                    intent = new Intent(context, BrowserActivity.class);
                    intent.putExtra("title",context.getResources().getString(R.string.terms_conditions));
                    intent.putExtra("url", getString(R.string.terms_and_conditions_path));
                    startActivity(intent);
                    break;
            }
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }

        private Toolbar getToolbar() {
            return toolbar;
        }
    }
}
