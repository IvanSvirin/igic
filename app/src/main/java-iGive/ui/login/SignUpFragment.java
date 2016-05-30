package ui.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.model.AuthObject;
import com.cashback.rest.event.SignUpEvent;
import com.cashback.rest.request.SignInRequest;
import com.cashback.ui.MainActivity;
import com.cashback.ui.login.LoginActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class SignUpFragment extends Fragment {
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
        View view = inflater.inflate(R.layout.sign_up_fragmentt, container, false);
        fragmentUi = new FragmentUi(this, view);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(SignUpEvent event) {
        if (event.isSuccess) {
            Utilities.saveUserToken(getActivity(), event.getToken());
            startActivity(new Intent(getContext(), MainActivity.class));
            getActivity().finish();
        } else {
            fragmentUi.showFailNotification(event.message);
        }
    }

    public class FragmentUi {
        @Bind(R.id.firstName)
        EditText etFirstName;
        @Bind(R.id.lastName)
        EditText etLastName;
        @Bind(R.id.email)
        EditText etEmail;
        @Bind(R.id.password)
        EditText etPassword;
        @Bind(R.id.zip)
        EditText etZip;

        @OnClick(R.id.facebookSingUpButton)
        public void onFBSignUp() {
            LoginManager.getInstance().logInWithReadPermissions(SignUpFragment.this, Arrays.asList("email", "public_profile", "user_friends"));
        }

        @OnClick(R.id.nativeSingUpButton)
        public void onNativeSignUp() {
            getContext().startActivity(new Intent(getContext(), MainActivity.class));
        }

        @OnClick(R.id.googleSingUpButton)
        public void onGoogleSignUp() {
            getContext().startActivity(new Intent(getContext(), MainActivity.class));

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getContext())
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

        public FragmentUi(SignUpFragment fragment, View view) {
            ButterKnife.bind(this, view);
            registerFbCallback();
        }

        private void registerFbCallback() {
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    String token = loginResult.getAccessToken().getToken();
                    AuthObject authObject = new AuthObject();
                    authObject.setAuthType("facebook");
                    authObject.setToken(token);
                    // TODO: 4/19/2016 TEST - will be deleted
                    new SignInRequest(getContext(), authObject).fetchData();
//                new SignUpRequest(getContext(), authObject).fetchData();
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
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String token = acct.getIdToken();
                AuthObject authObject = new AuthObject();
                authObject.setAuthType("google");
                authObject.setToken(token);
                // TODO: 4/19/2016 TEST - will be deleted
                new SignInRequest(getContext(), authObject).fetchData();
//                new SignUpRequest(getContext(), authObject).fetchData();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}