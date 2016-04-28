package com.cashback.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cashback.R;
import com.cashback.ui.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpFragment extends Fragment {
    private FragmentUi fragmentUi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_up_fragmentt, container, false);
        fragmentUi = new FragmentUi(this, view);
        return view;
    }

    public class FragmentUi {
        @OnClick(R.id.nativeSingUpButton)
        public void onNativeSignUp() {
            getContext().startActivity(new Intent(getContext(), MainActivity.class));
        }

        @OnClick(R.id.facebookSingUpButton)
        public void onFBSignUp() {
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
//            facebookLoginButton.setReadPermissions("email", "public_profile", "user_friends");
//            facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//                @Override
//                public void onSuccess(LoginResult loginResult) {
//                }
//
//                @Override
//                public void onCancel() {
//                }
//
//                @Override
//                public void onError(FacebookException error) {
//                }
//            });
        }
    }
}