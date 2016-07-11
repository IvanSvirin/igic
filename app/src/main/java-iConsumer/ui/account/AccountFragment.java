package ui.account;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cashback.App;
import com.cashback.R;
import com.cashback.Utilities;
import db.DataContract;
import com.cashback.rest.RestUtilities;
import com.cashback.rest.event.SettingsEvent;
import com.cashback.rest.request.CashBackSettingsRequest;
import com.cashback.ui.account.HelpActivity;
import com.cashback.ui.account.ShoppingTripsActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import ui.MainActivity;

public class AccountFragment extends Fragment {
    public static final String TAG_ACCOUNT_FRAGMENT = "I_account_fragment";
    private FragmentUi fragmentUi;
    private boolean gotAnswer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestUtilities.syncDistantData(this.getContext(), RestUtilities.TOKEN_CHARITY_ORDERS);
        RestUtilities.syncDistantData(this.getContext(), RestUtilities.TOKEN_SHOPPING_TRIPS);
        RestUtilities.syncDistantData(this.getContext(), RestUtilities.TOKEN_PAYMENTS);
        new CashBackSettingsRequest(getContext()).fetchData();
        gotAnswer = false;
        setHasOptionsMenu(true);
        //Google Analytics
        App app = (App) getActivity().getApplication();
        Tracker tracker = app.getDefaultTracker();
        tracker.setScreenName("Account");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_account, container, false);
        fragmentUi = new FragmentUi(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = fragmentUi.getToolbar();
        ((MainActivity) getActivity()).setAssociateToolbar(toolbar);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.item_account);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentUi.unbind();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.options_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_rate_the_app:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                //Try Google play
                intent.setData(Uri.parse("market://details?id=[Id]"));
                if (!StartRateAppActivity(intent)) {
                    //Market (Google play) app seems not installed, let's try to open a webbrowser
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?[Id]"));
                    if (!StartRateAppActivity(intent)) {
                        //Well if this also fails, we have run out of options, inform the user.
                        Toast.makeText(getActivity(), "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.action_logout:
                Utilities.removeUserToken(getContext());
                Utilities.saveUserEntry(getContext(), false);
                getActivity().finish();
                getContext().startActivity(new Intent(getContext(), MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean StartRateAppActivity(Intent intent) {
        try {
            startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    public void onEvent(SettingsEvent event) {
        if (event.isSuccess) {
            gotAnswer = true;
            fragmentUi.dealsSwitcher.setChecked(Utilities.isDealsNotifyOn(getContext()));
            fragmentUi.cashbackSwitcher.setChecked(Utilities.isCashBackNotifyOn(getContext()));
            fragmentUi.paymentsSwitcher.setChecked(Utilities.isPaymentsNotifyOn(getContext()));
        }
    }

    public class FragmentUi {
        private Context context;
        @Bind(R.id.toolbar)
        Toolbar toolbar;
        @Bind(R.id.userCashPending)
        TextView userCashPending;
        @Bind(R.id.userPayments)
        TextView userPayments;
        @Bind(R.id.nextPaymentDate)
        TextView nextPaymentDate;
        @Bind(R.id.joinDate)
        TextView joinDate;
        @Bind(R.id.trending_deals_alerts_switcher)
        SwitchCompat dealsSwitcher;
        @Bind(R.id.cash_back_alerts_switcher)
        SwitchCompat cashbackSwitcher;
        @Bind(R.id.payments_alerts_switcher)
        SwitchCompat paymentsSwitcher;

        public FragmentUi(AccountFragment fragment, View view) {
            this.context = fragment.getContext();
            ButterKnife.bind(this, view);
            initData();
        }

        private void initData() {
            Cursor cursor = getContext().getContentResolver().query(DataContract.URI_CASHBACK_ACCOUNTS, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            userCashPending.setText("$" + String.valueOf(cursor.getFloat(cursor.getColumnIndex(DataContract.CashbackAccounts.COLUMN_CASH_PENDING_AMOUNT))));
            userPayments.setText("$" + String.valueOf(cursor.getFloat(cursor.getColumnIndex(DataContract.CashbackAccounts.COLUMN_PAYMENTS_TOTAL_AMOUNT))));
            String date = cursor.getString(cursor.getColumnIndex(DataContract.CashbackAccounts.COLUMN_NEXT_PAYMENT_DATE));
            nextPaymentDate.setText(date.substring(5, 7) + "/" + date.substring(8, 10) + "/" + date.substring(0, 4));
            date = cursor.getString(cursor.getColumnIndex(DataContract.CashbackAccounts.COLUMN_MEMBER_DATE));
            joinDate.setText(date.substring(5, 7) + "/" + date.substring(8, 10) + "/" + date.substring(0, 4));
            cursor.close();
            dealsSwitcher.setChecked(Utilities.isDealsNotifyOn(getContext()));
            cashbackSwitcher.setChecked(Utilities.isCashBackNotifyOn(getContext()));
            paymentsSwitcher.setChecked(Utilities.isPaymentsNotifyOn(getContext()));
            dealsSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (gotAnswer) {
                        new CashBackSettingsRequest(context).changeData(Utilities.isCashBackNotifyOn(getContext()), isChecked, Utilities.isPaymentsNotifyOn(getContext()));
                        gotAnswer = false;
                    }
                }
            });
            cashbackSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (gotAnswer) {
                        new CashBackSettingsRequest(context).changeData(isChecked, Utilities.isDealsNotifyOn(getContext()), Utilities.isPaymentsNotifyOn(getContext()));
                        gotAnswer = false;
                    }
                }
            });
            paymentsSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (gotAnswer) {
                        new CashBackSettingsRequest(context).changeData(Utilities.isCashBackNotifyOn(getContext()), Utilities.isDealsNotifyOn(getContext()), isChecked);
                        gotAnswer = false;
                    }
                }
            });
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }

        private Toolbar getToolbar() {
            return toolbar;
        }

        @OnClick({R.id.paymentsFrame, R.id.shoppingTripsFrame, R.id.yourOrderHistoryFrame, R.id.helpFrame})
        public void onClicks(View view) {
            Intent intent;
            switch (view.getId()) {
                case R.id.paymentsFrame:
                    intent = new Intent(context, PaymentsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.shoppingTripsFrame:
                    intent = new Intent(context, ShoppingTripsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.yourOrderHistoryFrame:
                    intent = new Intent(context, YourOrderHistoryActivity.class);
                    startActivity(intent);
                    break;
                case R.id.helpFrame:
                    intent = new Intent(context, HelpActivity.class);
                    startActivity(intent);
                    break;

            }
        }
    }
}
