package com.cashback.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.cashback.R;
import com.cashback.db.DataContract;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.ShareDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by I.Svirin on 4/11/2016.
 */
public class TellAFriendFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG_TELL_A_FRIEND_FRAGMENT = "I_tell_a_friend_fragment";
    private FragmentUi fragmentUi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_tell_a_friend, container, false);
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
        getActivity().setTitle(R.string.title_tell_a_friend_fragment);
//        EventBus.getDefault().register(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class FragmentUi {
        private Context context;
        @Bind(R.id.toolbar)
        Toolbar toolbar;
        @Bind(R.id.fbButton)
        ImageView fbButton;
        @Bind(R.id.twButton)
        ImageView twButton;
        @Bind(R.id.gButton)
        ImageView gButton;

        @OnClick(R.id.fbButton)
        void fbShare() {
            ShareDialog dialog = new ShareDialog(getActivity());

            if (AppInviteDialog.canShow()) {
                AppInviteContent content = new AppInviteContent.Builder()
                        .setApplinkUrl("https://fb.me/100011062723550")
                        .setPreviewImageUrl("")
                        .build();
                AppInviteDialog.show(getActivity(), content);
            } else if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle("")
                        .setContentDescription("")
                        .setContentUrl(Uri.parse(""))
                        .build();
                dialog.show(linkContent);
            } else {
                Snackbar.make(getActivity().getWindow().getDecorView().findViewById(android.R.id.content),
                        R.string.not_available_fb_share, Snackbar.LENGTH_SHORT).show();
            }
        }

        @OnClick(R.id.twButton)
        void twShare() {
            String titleMsg = "";
            Cursor cursor = getContext().getContentResolver().query(DataContract.URI_CHARITY_ACCOUNTS, null, null, null, null);
            cursor.moveToFirst();
            String referrerId = cursor.getString(cursor.getColumnIndex(DataContract.CharityAccounts.COLUMN_REFERRER_ID));
            String referralLink = "";
            String tweetUrl =
                    String.format("https://twitter.com/intent/tweet?text=%s&url=%s", urlEncode(titleMsg), referralLink);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

            List<ResolveInfo> matches = getActivity().getPackageManager().queryIntentActivities(intent, 0);
            for (ResolveInfo info : matches) {
                if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                    intent.setPackage(info.activityInfo.packageName);
                }
            }
            startActivity(intent);
        }

        @OnClick(R.id.gButton)
        void gShare() {
        }

        public FragmentUi(TellAFriendFragment fragment, View view) {
            this.context = fragment.getContext();
            ButterKnife.bind(this, view);
        }

        public void unbind() {
            ButterKnife.unbind(this);
        }

        private Toolbar getToolbar() {
            return toolbar;
        }
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.wtf("log2", "UTF-8 should always be supported", e);
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }
}
