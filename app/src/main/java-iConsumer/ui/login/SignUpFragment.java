package ui.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.cashback.App;
import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.model.AuthObject;
import com.cashback.rest.event.SignUpEvent;
import com.cashback.rest.request.SignInCashBackRequest;
import com.cashback.ui.StoreActivity;
import com.cashback.ui.allresults.AllResultsActivity;
import com.cashback.ui.login.LoginActivity;
import com.cashback.ui.web.BrowserDealsActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import ui.MainActivity;

public class SignUpFragment extends Fragment {
    private static final int GOOGLE_AUTH = 111;
    private final static String G_PLUS_SCOPE = "oauth2:https://www.googleapis.com/auth/plus.me";
    private final static String USER_INFO_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
    private final static String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
    private final static String SCOPES = G_PLUS_SCOPE + " " + USER_INFO_SCOPE + " " + EMAIL_SCOPE;
    private FragmentUi fragmentUi;
    private CallbackManager callbackManager;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        EventBus.getDefault().register(this);
        //Google Analytics
        App app = (App) getActivity().getApplication();
        Tracker tracker = app.getDefaultTracker();
        tracker.setScreenName("SignUp");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_up_fragmentt, container, false);
        fragmentUi = new FragmentUi(view);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(SignUpEvent event) {
        progressDialog.dismiss();
        if (event.isSuccess) {
            Utilities.saveUserEntry(getActivity(), true);
            Bundle loginBundle = getActivity().getIntent().getBundleExtra(Utilities.LOGIN_BUNDLE);
            Intent intent;
            switch (loginBundle.getString(Utilities.CALLING_ACTIVITY)) {
                case "MainActivity":
                    intent = new Intent(getContext(), MainActivity.class);
                    break;
                case "AllResultsActivity":
                    intent = new Intent(getContext(), AllResultsActivity.class);
                    break;
                case "StoreActivity":
                    intent = new Intent(getContext(), StoreActivity.class);
                    intent.putExtra(Utilities.VENDOR_ID, loginBundle.getLong(Utilities.VENDOR_ID));
                    break;
                case "BrowserDealsActivity":
                    intent = new Intent(getContext(), BrowserDealsActivity.class);
                    intent.putExtra(Utilities.VENDOR_ID, loginBundle.getLong(Utilities.VENDOR_ID));
                    intent.putExtra(Utilities.COUPON_ID, loginBundle.getLong(Utilities.COUPON_ID, 0));
                    intent.putExtra(Utilities.AFFILIATE_URL, loginBundle.getString(Utilities.AFFILIATE_URL));
                    intent.putExtra(Utilities.VENDOR_COMMISSION, loginBundle.getFloat(Utilities.VENDOR_COMMISSION));
                    break;
                default:
                    intent = new Intent(getContext(), MainActivity.class);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getContext().startActivity(intent);
            getActivity().finish();
        } else {
            fragmentUi.showFailNotification(event.message);
        }
    }

    public class FragmentUi {
        @Bind(R.id.email)
        EditText etEmail;
        @Bind(R.id.password)
        EditText etPassword;
        @Bind(R.id.referringEmail)
        EditText etReferringEmail;
        Point p;

        @OnClick(R.id.password)
        public void onPasswordClick() {
//            p = new Point();
//            showPopup(getActivity(), p);
        }

        private void showPopup(final Activity context, Point p) {
            int popupWidth = 500;
            int popupHeight = 400;
            int[] location = new int[2];
            etPassword.getLocationOnScreen(location);
            p.x = location[0];
            p.y = location[1];
            LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup);
            final PopupWindow popup = new PopupWindow(context);
            popup.setContentView(layout);
            popup.setWidth(popupWidth);
            popup.setHeight(popupHeight);
            popup.setFocusable(true);
            int OFFSET_X = 60;
            int OFFSET_Y = 90;
            popup.setBackgroundDrawable(new BitmapDrawable());
            popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);
            Button close = (Button) layout.findViewById(R.id.close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup.dismiss();
                }
            });
        }

        @OnClick(R.id.facebookSingUpButton)
        public void onFBSignUp() {
            LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));
        }

        @OnClick(R.id.nativeSingUpButton)
        public void onNativeSignUp() {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String referrerEmail = etReferringEmail.getText().toString();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), R.string.alert_about_empty_fields, Snackbar.LENGTH_SHORT).show();
            } else {
                if (isPasswordValid(password)) {
                    if (Utilities.isEmailValid(email)) {
                        if (Utilities.isEmailValid(referrerEmail)) {
                            AuthObject authObject = new AuthObject();
                            authObject.setAuthType("0");
                            authObject.setEmail(email);
                            authObject.setPassword(password);
                            authObject.setReferrerEmail(referrerEmail);
                            new SignInCashBackRequest(getContext(), authObject, "signup").fetchData();
                            progressDialog = Utilities.onCreateProgressDialog(getContext());
                            progressDialog.show();
                        } else {
                            Utilities.showFailNotification("Please enter valid email address.", getContext());
                        }
                    } else {
                        Utilities.showFailNotification("Please enter valid email address.", getContext());
                    }
                } else {
//                    p = new Point();
//                    showPopup(getActivity(), p);
                    showFailNotification(getString(R.string.password_conditions));
                }
            }
        }

        @OnClick(R.id.googleSingUpButton)
        public void onGoogleSignUp() {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(getString(R.string.google_client_id))
//                    .requestScopes(Plus.SCOPE_PLUS_LOGIN)
                    .build();
            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        }
                    })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, GOOGLE_AUTH);
        }

        public FragmentUi(View view) {
            ButterKnife.bind(this, view);
            registerFbCallback();
        }

        private void registerFbCallback() {
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    final String token = loginResult.getAccessToken().getToken();
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                AuthObject authObject = new AuthObject();
                                authObject.setAuthType("2");
                                authObject.setToken(token);
                                authObject.setFirstName(object.getString("first_name"));
                                authObject.setLastName(object.getString("last_name"));
                                authObject.setEmail(object.getString("email"));
                                authObject.setUserId(object.getString("id"));
                                new SignInCashBackRequest(getContext(), authObject, "signup").fetchData();
                                progressDialog = Utilities.onCreateProgressDialog(getContext());
                                progressDialog.show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "first_name,last_name,email");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onError(FacebookException error) {
                }
            });
        }

        private void showFailNotification(String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(message)
                    .setTitle("Failure")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }
    }

    private boolean isPasswordValid(String password) {
        int count = 0;
        if (password.length() < 8) return false;
        char[] symbols = password.toCharArray();
        String validationLowCaseString = "abcdefghijklmnopqrstuvwxyz";
        for (char c : symbols) {
            if (validationLowCaseString.indexOf(c) != -1) {
                count++;
                break;
            }
        }
        String validationDigitString = "0123456789";
        for (char c : symbols) {
            if (validationDigitString.indexOf(c) != -1) {
                count++;
                break;
            }
        }
        String validationUpperCaseString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (char c : symbols) {
            if (validationUpperCaseString.indexOf(c) != -1) {
                count++;
                break;
            }
        }
        String validationSpecialString = "!@#$%^&*()_+|~=\\{}[]:;<>?.";
        for (char c : symbols) {
            if (validationSpecialString.indexOf(c) != -1) {
                count++;
                break;
            }
        }
        return count > 2;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_AUTH && resultCode == LoginActivity.RESULT_OK) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String token = acct.getIdToken();
                AuthObject authObject = new AuthObject();
                authObject.setAuthType("1");
                authObject.setToken(token);
                String[] fullName = acct.getDisplayName().split(" ");
                authObject.setFirstName(fullName[0]);
                authObject.setLastName(fullName[1]);
                authObject.setEmail(acct.getEmail());
                authObject.setUserId(acct.getId());
                new SignInCashBackRequest(getContext(), authObject, "signup").fetchData();
                progressDialog = Utilities.onCreateProgressDialog(getContext());
                progressDialog.show();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}