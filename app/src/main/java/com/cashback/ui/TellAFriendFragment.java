package com.cashback.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cashback.App;
import com.cashback.R;
import com.cashback.Utilities;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

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
import ui.MainActivity;

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
        //Google Analytics
        App app = (App) getActivity().getApplication();
        Tracker tracker = app.getDefaultTracker();
        tracker.setScreenName("Tell A Friend");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

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
        @Bind(R.id.imageView)
        ImageView imageView;

        @OnClick(R.id.fbButton)
        void fbShare() {
            if (Utilities.isLoggedIn(context)) {
                BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                        .setTitle(Utilities.retrieveTellFriendText(context))
                        .setContentDescription(Utilities.retrieveTellFriendText(context))
                        .addContentMetadata("BRANCH_REFERBY", Utilities.retrieveEmail(context))
                        .setContentImageUrl(getString(R.string.logo_url));
                LinkProperties linkProperties = new LinkProperties()
                        .setFeature(getString(R.string.app_name) + ".EVENT_SHARE_APP")
                        .setChannel("SHARE_VIA_FB");
                branchUniversalObject.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
                    @Override
                    public void onLinkCreate(String url, BranchError error) {
                        ShareDialog dialog = new ShareDialog(getActivity());
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle(Utilities.retrieveTellFriendText(context) + "\n")
                                .setContentDescription(Utilities.retrieveTellFriendText(context) + "\n")
                                .setContentUrl(Uri.parse(url))
                                .build();
                        dialog.show(linkContent);
                    }
                });
            } else {
                Bundle loginBundle = new Bundle();
                loginBundle.putString(Utilities.CALLING_ACTIVITY, "MainActivity");
                Utilities.needLoginDialog(context, loginBundle);
            }
        }

        @OnClick(R.id.twButton)
        void twShare() {
            if (Utilities.isLoggedIn(context)) {
                BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                        .setTitle(Utilities.retrieveTellFriendText(context))
                        .setContentDescription(Utilities.retrieveTellFriendText(context))
                        .addContentMetadata("BRANCH_REFERBY", Utilities.retrieveEmail(context))
                        .setContentImageUrl(getString(R.string.logo_url));
                LinkProperties linkProperties = new LinkProperties()
                        .setFeature(getString(R.string.app_name) + ".EVENT_SHARE_APP")
                        .setChannel("SHARE_VIA_TWITTER");
                branchUniversalObject.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
                    @Override
                    public void onLinkCreate(String url, BranchError error) {
                        String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s", urlEncode(Utilities.retrieveTellFriendText(context)), url);
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
            } else {
                Bundle loginBundle = new Bundle();
                loginBundle.putString(Utilities.CALLING_ACTIVITY, "MainActivity");
                Utilities.needLoginDialog(context, loginBundle);
            }
        }

        @OnClick(R.id.shButton)
        void share() {
            if (Utilities.isLoggedIn(context)) {
                BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                        .setTitle(Utilities.retrieveTellFriendText(context))
                        .setContentDescription(Utilities.retrieveTellFriendText(context))
                        .addContentMetadata("BRANCH_REFERBY", Utilities.retrieveEmail(context))
                        .setContentImageUrl(getString(R.string.logo_url));
                LinkProperties linkProperties = new LinkProperties()
                        .setFeature(getString(R.string.app_name) + ".EVENT_SHARE_APP")
                        .setChannel("SHARE_VIA_SHARING_DIALOG");
                branchUniversalObject.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
                    @Override
                    public void onLinkCreate(String url, BranchError error) {
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_TEXT, Utilities.retrieveTellFriendText(context) + "\n" + url);
                        context.startActivity(Intent.createChooser(share, Utilities.retrieveTellFriendText(context)));
                    }
                });
            } else {
                Bundle loginBundle = new Bundle();
                loginBundle.putString(Utilities.CALLING_ACTIVITY, "MainActivity");
                Utilities.needLoginDialog(context, loginBundle);
            }
        }

        public FragmentUi(TellAFriendFragment fragment, View view) {
            this.context = fragment.getContext();
            ButterKnife.bind(this, view);

            DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
            int w = displaymetrics.widthPixels;
            int h = displaymetrics.heightPixels;
            Drawable drawable;
            if (h > w) {
                drawable = getContext().getResources().getDrawable(R.drawable.tellafriends);
            } else {
                drawable = getContext().getResources().getDrawable(R.drawable.tellafriends_tablet);
            }
            w = w - getContext().getResources().getDimensionPixelSize(R.dimen.padding_small) * 2;
            int wd = drawable.getIntrinsicWidth();
            int hd = drawable.getIntrinsicHeight();
            Picasso.with(context)
                    .load(R.drawable.tellafriends)
                    .resize(w, w * hd / wd)
                    .into(imageView);

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
