package com.cashback.ui.login;

import android.accounts.AccountManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.model.AuthObject;
import com.cashback.rest.event.LoginEvent;
import com.cashback.rest.request.SignInRequest;
import com.cashback.ui.MainActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.plus.Account;

import java.io.IOException;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class SignInFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int GOOGLE_AUTH = 111;
    private final static String G_PLUS_SCOPE = "oauth2:https://www.googleapis.com/auth/plus.me";
    private final static String USER_INFO_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
    private final static String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
    private final static String SCOPES = G_PLUS_SCOPE + " " + USER_INFO_SCOPE + " " + EMAIL_SCOPE;
    private FragmentUi fragmentUi;
    private CallbackManager callbackManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_in_fragmentt, container, false);
        fragmentUi = new FragmentUi(this, view);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(LoginEvent event) {
        if (event.isSuccess) {
            Utilities.saveUserEntry(getActivity(), true);
            Utilities.saveUserToken(getActivity(), event.getToken());
            startActivity(new Intent(getContext(), MainActivity.class));
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

        @OnClick(R.id.nativeLoginButton)
        public void onNativeLogin() {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content), R.string.alert_about_empty_fields, Snackbar.LENGTH_SHORT).show();
            } else {
                AuthObject authObject = new AuthObject();
                authObject.setAuthType("email");
//                authObject.setAuthType(AuthObject.AuthType.email);
                authObject.setEmail(email);
                authObject.setPassword(password);
                new SignInRequest(getContext(), authObject).fetchData();
            }
        }

        @OnClick(R.id.facebookLoginButton)
        public void onFBSignUp() {
            LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile", "user_friends"));
        }

        @OnClick(R.id.googleLoginButton)
        public void onGoogleLogin() {
            Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"}, false, null, null, null, null);
            startActivityForResult(intent, GOOGLE_AUTH);
        }

        @OnClick(R.id.forgotPasswordButton)
        public void onRestore() {
            startActivity(new Intent(getActivity(), RestoreActivity.class));
        }

        public FragmentUi(SignInFragment fragment, View view) {
            ButterKnife.bind(this, view);
            registerFbCallback();
        }

        private void registerFbCallback() {
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // TODO: 4/19/2016 TEST - will be deleted
                    getContext().startActivity(new Intent(getContext(), MainActivity.class));

//                    AccessToken token = loginResult.getAccessToken();
//                    String fbToken = token.getToken();
//                    AuthObject authObject = new AuthObject();
//                    authObject.setAuthType(AuthObject.AuthType.facebook);
//                    authObject.setToken(fbToken);
//                    new SignInRequest(getContext(), authObject).fetchData();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_AUTH && resultCode == LoginActivity.RESULT_OK) {
            // TODO: 4/19/2016 TEST - will be deleted
            getContext().startActivity(new Intent(getContext(), MainActivity.class));

//            final String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
//            try {
//                String token = GoogleAuthUtil.getToken(getActivity(), accountName, SCOPES);
//                AuthObject authObject = new AuthObject();
//                authObject.setAuthType(AuthObject.AuthType.google);
//                authObject.setToken(token);
//                new SignInRequest(getContext(), authObject).fetchData();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (GoogleAuthException e) {
//                e.printStackTrace();
//            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
