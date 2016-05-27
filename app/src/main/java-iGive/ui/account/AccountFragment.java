package com.cashback.ui.account;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cashback.R;
import com.cashback.db.DataContract;
import com.cashback.rest.RestUtilities;
import com.cashback.rest.event.CouponsEvent;
import com.cashback.ui.MainActivity;
import com.cashback.ui.login.LoginActivity;
import com.cashback.ui.web.BrowserActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import ui.account.YourOrderHistoryActivity;

/**
 * Created by I.Svirin on 4/11/2016.
 */
public class AccountFragment extends Fragment {
    public static final String TAG_ACCOUNT_FRAGMENT = "I_account_fragment";
    private FragmentUi fragmentUi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RestUtilities.syncDistantData(this.getContext(), RestUtilities.TOKEN_CHARITY_ORDERS);
        RestUtilities.syncDistantData(this.getContext(), RestUtilities.TOKEN_SHOPPING_TRIPS);
        setHasOptionsMenu(true);
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
//        getLoaderManager().initLoader(MainActivity.IMAGE_LOADER, null, this);
        Toolbar toolbar = fragmentUi.getToolbar();
        ((MainActivity) getActivity()).setAssociateToolbar(toolbar);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.item_account);
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
                // TODO: 4/19/2016 TEST - will be deleted
                getContext().startActivity(new Intent(getContext(), LoginActivity.class));
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

    public class FragmentUi {
        private Context context;
        @Bind(R.id.toolbar)
        Toolbar toolbar;
        @Bind(R.id.nextCheckAmountValue)
        TextView nextCheckAmountValue;
        @Bind(R.id.pendingAmountValue)
        TextView pendingAmountValue;
        @Bind(R.id.totalPaidValue)
        TextView totalPaidValue;
        @Bind(R.id.totalRaisedValue)
        TextView totalRaisedValue;
        @Bind(R.id.totalPaidDate)
        TextView totalPaidDate;

        public FragmentUi(AccountFragment fragment, View view) {
            this.context = fragment.getContext();
            ButterKnife.bind(this, view);
            initData();
        }

        private void initData() {
            Cursor cursor = getContext().getContentResolver().query(DataContract.URI_CHARITY_ACCOUNTS, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            pendingAmountValue.setText("$ " + String.valueOf(cursor.getFloat(cursor.getColumnIndex(DataContract.CharityAccounts.COLUMN_PENDING_AMOUNT))));
            totalPaidValue.setText("$ " + String.valueOf(cursor.getFloat(cursor.getColumnIndex(DataContract.CharityAccounts.COLUMN_TOTAL_PAID_AMOUNT))));
            totalRaisedValue.setText("$ " + String.valueOf(cursor.getFloat(cursor.getColumnIndex(DataContract.CharityAccounts.COLUMN_TOTAL_RAISED))));
            String date = cursor.getString(cursor.getColumnIndex(DataContract.CharityAccounts.COLUMN_MEMBER_DATE));
            totalPaidDate.setText(date.substring(5, 7) + "/" + date.substring(8, 10) + "/" + date.substring(0, 4));
            cursor.close();
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }

        private Toolbar getToolbar() {
            return toolbar;
        }

        @OnClick({R.id.shoppingTripsFrame, R.id.yourOrderHistoryFrame, R.id.helpFrame, R.id.memberSettingsFrame})
        public void onClicks(View view) {
            Intent intent;
            switch (view.getId()) {
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
                case R.id.memberSettingsFrame:
                    intent = new Intent(context, BrowserActivity.class);
                    startActivity(intent);
                    break;

            }
        }
    }
}
