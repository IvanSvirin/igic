package ui.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.rest.event.SignUpEvent;
import com.cashback.ui.MainActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

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

    public void onEvent(AccountEvent event) {
        if (event.isSuccess) {
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
        @Bind(R.id.referringEmail)
        EditText etReferringEmail;

        @OnClick(R.id.facebookSingUpButton)
        public void onFBSignUp() {
            LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile", "user_friends"));
        }

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
                                new SignInCharityRequest(getContext(), authObject, "login").fetchData();
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
                authObject.setUserId(acct.getId());
                new SignInCharityRequest(getContext(), authObject, "signup").fetchData();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }    }
}