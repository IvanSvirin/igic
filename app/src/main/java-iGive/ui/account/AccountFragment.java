package ui.account;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import rest.RestUtilities;

import com.cashback.rest.event.SettingsEvent;
import com.cashback.rest.request.CharitySettingsRequest;

import db.DataContract;
import ui.MainActivity;

import com.cashback.ui.account.StoreVisitsActivity;
import com.cashback.ui.web.BrowserActivity;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.Status;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class AccountFragment extends Fragment {
    public static final String TAG_ACCOUNT_FRAGMENT = "I_account_fragment";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private FragmentUi fragmentUi;
    private boolean gotAnswer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new CharitySettingsRequest(getContext()).fetchData();
        gotAnswer = false;
        setHasOptionsMenu(true);
        //Google Analytics
        App app = (App) getActivity().getApplication();
        Tracker tracker = app.getDefaultTracker();
        tracker.setScreenName("Account");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        if (App.googleApiClient == null) {
            App.googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        }
                    })
                    .addApi(Auth.GOOGLE_SIGN_IN_API)
                    .build();
        }
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
        EventBus.getDefault().register(this);
        getActivity().setTitle(R.string.item_account);
        App.googleApiClient.connect();
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
                intent.setData(Uri.parse("market://details?id=com.igive.button"));
                if (!StartRateAppActivity(intent)) {
                    //Market (Google play) app seems not installed, let's try to open a webbrowser
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.igive.button"));
                    if (!StartRateAppActivity(intent)) {
                        //Well if this also fails, we have run out of options, inform the user.
                        Toast.makeText(getActivity(), "Could not open Android market, please install the market app.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.action_logout:
                Utilities.removeUserToken(getContext());
                Utilities.removeEmail(getContext());
                Utilities.saveUserEntry(getContext(), false);
                getContext().getContentResolver().delete(DataContract.URI_CASH_BACK_ACCOUNT, null, null);
                getContext().getContentResolver().delete(DataContract.URI_CHARITY_ACCOUNT, null, null);
                getContext().getContentResolver().delete(DataContract.URI_FAVORITES, null, null);
                getContext().getContentResolver().delete(DataContract.URI_PAYMENTS, null, null);
                getContext().getContentResolver().delete(DataContract.URI_SHOPPING_TRIPS, null, null);
                getContext().getContentResolver().delete(DataContract.URI_ORDERS, null, null);
                getContext().getContentResolver().delete(DataContract.URI_CHARITY_ORDERS, null, null);
                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut();
                }
                if (checkPlayServices()) {
                    Auth.GoogleSignInApi.signOut(App.googleApiClient);
                }
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
            fragmentUi.newsSwitcher.setChecked(Utilities.isWeeklyNewsNotifyOn(getContext()));
            fragmentUi.purchaseSwitcher.setChecked(Utilities.isPurchaseNotifyOn(getContext()));
        }
    }

    public class FragmentUi {
        private Context context;
        @Bind(R.id.toolbar)
        Toolbar toolbar;
        @Bind(R.id.nextCheckAmountValue)
        TextView nextCheckAmountValue;
        @Bind(R.id.myDonationsValue)
        TextView myDonationsValue;
        @Bind(R.id.pendingAmountValue)
        TextView pendingAmountValue;
        @Bind(R.id.totalPaidValue)
        TextView totalPaidValue;
        @Bind(R.id.totalRaisedValue)
        TextView totalRaisedValue;
        @Bind(R.id.news_switcher)
        SwitchCompat newsSwitcher;
        @Bind(R.id.purchase_switcher)
        SwitchCompat purchaseSwitcher;
        private String memberSettingsUrl;

        public FragmentUi(AccountFragment fragment, View view) {
            this.context = fragment.getContext();
            ButterKnife.bind(this, view);
            initData();
        }

        private void initData() {
            Cursor cursor = getContext().getContentResolver().query(DataContract.URI_CHARITY_ACCOUNT, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            nextCheckAmountValue.setText("$" + String.format("%.2f", cursor.getFloat(cursor.getColumnIndex(DataContract.CharityAccounts.COLUMN_NEXT_CHECK_AMOUNT))));
            pendingAmountValue.setText("$" + String.format("%.2f", cursor.getFloat(cursor.getColumnIndex(DataContract.CharityAccounts.COLUMN_PENDING_AMOUNT))));
            totalPaidValue.setText("$" + String.format("%.2f", cursor.getFloat(cursor.getColumnIndex(DataContract.CharityAccounts.COLUMN_TOTAL_PAID_AMOUNT))));
            myDonationsValue.setText("$" + String.format("%.2f", cursor.getFloat(cursor.getColumnIndex(DataContract.CharityAccounts.COLUMN_EARNED_TOTAL))));
            totalRaisedValue.setText("$" + String.format("%.2f", cursor.getFloat(cursor.getColumnIndex(DataContract.CharityAccounts.COLUMN_TOTAL_RAISED))));
            String date = cursor.getString(cursor.getColumnIndex(DataContract.CharityAccounts.COLUMN_MEMBER_DATE));
            memberSettingsUrl = cursor.getString(cursor.getColumnIndex(DataContract.CharityAccounts.COLUMN_MEMBER_SETTINGS_URL)) + "?token=" + Utilities.retrieveUserToken(context);
            cursor.close();
            newsSwitcher.setChecked(Utilities.isWeeklyNewsNotifyOn(getContext()));
            purchaseSwitcher.setChecked(Utilities.isPurchaseNotifyOn(getContext()));
            newsSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (gotAnswer) {
                        new CharitySettingsRequest(context).changeData(isChecked, Utilities.isPurchaseNotifyOn(getContext()));
                        gotAnswer = false;
                    }
                }
            });
            purchaseSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (gotAnswer) {
                        new CharitySettingsRequest(context).changeData(Utilities.isWeeklyNewsNotifyOn(getContext()), isChecked);
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

        @OnClick({R.id.storeVisitsFrame, R.id.shoppingReportFrame, R.id.memberSettingsFrame})
        public void onClicks(View view) {
            Intent intent;
            switch (view.getId()) {
                case R.id.storeVisitsFrame:
                    intent = new Intent(context, StoreVisitsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.shoppingReportFrame:
                    intent = new Intent(context, ShoppingReportActivity.class);
                    startActivity(intent);
                    break;
                case R.id.memberSettingsFrame:
                    intent = new Intent(context, BrowserActivity.class);
                    intent.putExtra("title", getResources().getString(R.string.member_settings));
                    intent.putExtra("url", memberSettingsUrl);
                    startActivity(intent);
                    break;

            }
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
            }
            return false;
        }
        return true;
    }
}
