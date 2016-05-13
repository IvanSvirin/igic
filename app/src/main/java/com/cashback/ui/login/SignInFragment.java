package com.cashback.ui.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.model.AuthObject;
import com.cashback.rest.event.LoginEvent;
import com.cashback.rest.request.MerchantsRequest;
import com.cashback.rest.request.SignInRequest;
import com.cashback.ui.MainActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class SignInFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
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
            getContext().startActivity(new Intent(getContext(), MainActivity.class));
        }

        @OnClick(R.id.forgotPasswordButton)
        public void onRestore() {
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
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
