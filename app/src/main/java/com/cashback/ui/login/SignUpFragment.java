package com.cashback.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cashback.R;
import com.cashback.ui.MainActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpFragment extends Fragment {
    private FragmentUi fragmentUi;
    private CallbackManager callbackManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_up_fragmentt, container, false);
        fragmentUi = new FragmentUi(this, view);
        return view;
    }

    public class FragmentUi {
        @Bind(R.id.facebookSingUpButton)
        LoginButton facebookSingUpButton;

        @OnClick(R.id.nativeSingUpButton)
        public void onNativeSignUp() {
            getContext().startActivity(new Intent(getContext(), MainActivity.class));
        }

        @OnClick(R.id.googleSingUpButton)
        public void onGoogleSignUp() {
            getContext().startActivity(new Intent(getContext(), MainActivity.class));
        }

        public FragmentUi(SignUpFragment fragment, View view) {
            ButterKnife.bind(this, view);
            registerFbCallback();
        }

        private void registerFbCallback() {
            facebookSingUpButton.setReadPermissions("email", "public_profile", "user_friends");
            facebookSingUpButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // TODO: 4/19/2016 TEST - will be deleted
                    getContext().startActivity(new Intent(getContext(), MainActivity.class));
                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onError(FacebookException error) {
                }
            });
        }
    }
}