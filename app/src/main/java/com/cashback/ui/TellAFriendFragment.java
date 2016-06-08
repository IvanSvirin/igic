package com.cashback.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cashback.R;
import com.cashback.db.DataContract;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

public class TellAFriendFragment extends Fragment {
    public static final String TAG_TELL_A_FRIEND_FRAGMENT = "I_tell_a_friend_fragment";
    private FragmentUi fragmentUi;

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
        Toolbar toolbar = fragmentUi.getToolbar();
        ((MainActivity) getActivity()).setAssociateToolbar(toolbar);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.title_tell_a_friend_fragment);
    }

    public class FragmentUi {
        private Context context;
        @Bind(R.id.toolbar)
        Toolbar toolbar;
        @Bind(R.id.fbButton)
        ImageView fbButton;
        @Bind(R.id.twButton)
        ImageView twButton;
        @Bind(R.id.shButton)
        ImageView gButton;

        @OnClick(R.id.fbButton)
        void fbShare() {
            // TODO: 6/7/2016 only for iConsumer
//            Cursor cursor = getContext().getContentResolver().query(DataContract.URI_CHARITY_ACCOUNTS, null, null, null, null);
//            cursor.moveToFirst();
//            String referrerId = cursor.getString(cursor.getColumnIndex(DataContract.CharityAccounts.COLUMN_REFERRER_ID));
//            String referralLink = String.format(getString(R.string.referral_reg_exp), referrerId);

            BranchUniversalObject branchUniversalObject = new BranchUniversalObject();
            LinkProperties linkProperties = new LinkProperties();
            branchUniversalObject.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
                @Override
                public void onLinkCreate(String url, BranchError error) {
                    ShareDialog dialog = new ShareDialog(getActivity());
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(getString(R.string.share_title) + "\n")
                            .setContentDescription("")
                            .setContentUrl(Uri.parse(url))
                            .build();
                    dialog.show(linkContent);
                }
            });
        }

        @OnClick(R.id.twButton)
        void twShare() {
            // TODO: 6/7/2016 only for iConsumer
//            Cursor cursor = getContext().getContentResolver().query(DataContract.URI_CHARITY_ACCOUNTS, null, null, null, null);
//            cursor.moveToFirst();
//            String referrerId = cursor.getString(cursor.getColumnIndex(DataContract.CharityAccounts.COLUMN_REFERRER_ID));
//            String referralLink = String.format(getString(R.string.referral_reg_exp), referrerId);

            BranchUniversalObject branchUniversalObject = new BranchUniversalObject();
            LinkProperties linkProperties = new LinkProperties();
            branchUniversalObject.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
                @Override
                public void onLinkCreate(String url, BranchError error) {
                    String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s", urlEncode(getString(R.string.share_title) + "\n"), url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

                    List<ResolveInfo> matches = getActivity().getPackageManager().queryIntentActivities(intent, 0);
                    for (ResolveInfo info : matches) {
                        if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                            intent.setPackage(info.activityInfo.packageName);
                        }
                    }
                    startActivity(intent);
                }
            });
        }

        @OnClick(R.id.shButton)
        void share() {
            // TODO: 6/7/2016 only for iConsumer
//            Cursor cursor = getContext().getContentResolver().query(DataContract.URI_CHARITY_ACCOUNTS, null, null, null, null);
//            cursor.moveToFirst();
//            String referrerId = cursor.getString(cursor.getColumnIndex(DataContract.CharityAccounts.COLUMN_REFERRER_ID));
//            String referralLink = String.format(getString(R.string.referral_reg_exp), referrerId);

            BranchUniversalObject branchUniversalObject = new BranchUniversalObject();
            LinkProperties linkProperties = new LinkProperties();
            branchUniversalObject.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
                @Override
                public void onLinkCreate(String url, BranchError error) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_title) + "\n" + url);
                    context.startActivity(Intent.createChooser(share, "Share Text"));
                }
            });
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
