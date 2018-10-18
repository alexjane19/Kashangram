package io.github.froger.instamaterial.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import io.github.froger.instamaterial.R;
import io.github.froger.instamaterial.server.SendPostRequest;
import io.github.froger.instamaterial.ui.adapter.UserProfileAdapter;
import io.github.froger.instamaterial.ui.utils.CircleTransformation;
import io.github.froger.instamaterial.ui.view.RevealBackgroundView;

import static io.github.froger.instamaterial.server.SendPostRequest.COUNT;
import static io.github.froger.instamaterial.server.SendPostRequest.EMAIL;
import static io.github.froger.instamaterial.server.SendPostRequest.FOLLOWER;
import static io.github.froger.instamaterial.server.SendPostRequest.FOLLOWING;
import static io.github.froger.instamaterial.server.SendPostRequest.F_NAME;
import static io.github.froger.instamaterial.server.SendPostRequest.KEY_DATA;
import static io.github.froger.instamaterial.server.SendPostRequest.KEY_MSG;
import static io.github.froger.instamaterial.server.SendPostRequest.KEY_SUCCESS;
import static io.github.froger.instamaterial.server.SendPostRequest.L_NAME;
import static io.github.froger.instamaterial.server.SendPostRequest.PASSWORD;
import static io.github.froger.instamaterial.server.SendPostRequest.PHONE_NUM;
import static io.github.froger.instamaterial.server.SendPostRequest.PHOTO_ID;
import static io.github.froger.instamaterial.server.SendPostRequest.ST_NO;
import static io.github.froger.instamaterial.server.SendPostRequest.USER_ID;

/**
 * Created by Miroslaw Stanek on 14.01.15.
 */
public class UserProfileActivity extends BaseDrawerActivity implements RevealBackgroundView.OnStateChangeListener {
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    private static final String URL_PHOTO_NUM = "/api/photo/num";
    private static final String URL_FOLLOWS_NUM = "/api/follow/num";
    private static final String URL_PHOTO = "/api/photo";

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    @BindView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    @BindView(R.id.rvUserProfile)
    RecyclerView rvUserProfile;

    @BindView(R.id.tlUserProfileTabs)
    TabLayout tlUserProfileTabs;

    @BindView(R.id.ivUserProfilePhoto)
    ImageView ivUserProfilePhoto;
    @BindView(R.id.vUserDetails)
    View vUserDetails;
    @BindView(R.id.btnFollow)
    Button btnFollow;
    @BindView(R.id.vUserStats)
    View vUserStats;
    @BindView(R.id.vUserProfileRoot)
    View vUserProfileRoot;

    @BindView(R.id.tvNameProfile)
    TextView tvNameProfile;
    @BindView(R.id.tvUserProfile)
    TextView tvUserProfile;
    @BindView(R.id.tvPostProfile)
    TextView tvPostProfile;
    @BindView(R.id.tvFollowerProfile)
    TextView tvFollowerProfile;
    @BindView(R.id.tvFollowingProfile)
    TextView tvFollowingProfile;

    private int avatarSize;
   // private String profilePhoto;
    private UserProfileAdapter userPhotosAdapter;
    private HashMap<String,String> infoProfile;
    public static void startUserProfileFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        infoProfile = getInfoProfile(getApplicationContext());
        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        //this.profilePhoto = getString(R.string.user_profile_photo);

        Picasso.with(this)
                .load(URL_UPLOADED_IMAGE + infoProfile.get(PHOTO_ID))
                .placeholder(R.mipmap.profile)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivUserProfilePhoto);
        tvNameProfile.setText(infoProfile.get(F_NAME) + " " + infoProfile.get(L_NAME));
        tvUserProfile.setText("@" + infoProfile.get(USER_ID));
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put(USER_ID, infoProfile.get(USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostRequest(postDataParams, new SendPostRequest.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    JSONObject obj = new JSONObject(output);
                    boolean b = (boolean) obj.get(KEY_SUCCESS);
                    if (b) {
                        System.out.println(obj.get(KEY_MSG));
                        JSONArray arr = new JSONArray(String.valueOf(obj.get(KEY_DATA)));
                        JSONObject data = (JSONObject)arr.get(0);
                        tvPostProfile.setText(String.valueOf(data.get(COUNT)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute(URL_PHOTO_NUM);

        new SendPostRequest(postDataParams, new SendPostRequest.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    JSONObject obj = new JSONObject(output);
                    boolean b = (boolean) obj.get(KEY_SUCCESS);
                    if (b) {
                        System.out.println(obj.get(KEY_MSG));
                        JSONObject data = new JSONObject(String.valueOf(obj.get(KEY_DATA)));
                        tvFollowerProfile.setText(String.valueOf(data.get(FOLLOWER)));
                        tvFollowingProfile.setText(String.valueOf(data.get(FOLLOWING)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute(URL_FOLLOWS_NUM);
        setupTabs();
        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);
    }

    private void setupTabs() {
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_grid_on_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_list_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_place_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_label_white));
    }

    private void setupUserProfileGrid() {
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvUserProfile.setLayoutManager(layoutManager);
        rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (userPhotosAdapter != null)
                    userPhotosAdapter.setLockedAnimations(true);
            }
        });
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
            if (userPhotosAdapter != null)
                userPhotosAdapter.setLockedAnimations(true);
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            rvUserProfile.setVisibility(View.VISIBLE);
            tlUserProfileTabs.setVisibility(View.VISIBLE);
            vUserProfileRoot.setVisibility(View.VISIBLE);
            JSONObject postDataParams = new JSONObject();
            try {
                postDataParams.put(USER_ID, infoProfile.get(USER_ID));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new SendPostRequest(postDataParams, new SendPostRequest.AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    try {
                        JSONObject obj = new JSONObject(output);
                        boolean b = (boolean) obj.get(KEY_SUCCESS);
                        if (b) {
                            System.out.println(obj.get(KEY_MSG));
                            JSONArray arr = new JSONArray(String.valueOf(obj.get(KEY_DATA)));
                            userPhotosAdapter = new UserProfileAdapter(UserProfileActivity.this, arr);
                            rvUserProfile.setAdapter(userPhotosAdapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(URL_PHOTO);
            animateUserProfileOptions();
            animateUserProfileHeader();
        } else {
            tlUserProfileTabs.setVisibility(View.INVISIBLE);
            rvUserProfile.setVisibility(View.INVISIBLE);
            vUserProfileRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void animateUserProfileOptions() {
        tlUserProfileTabs.setTranslationY(-tlUserProfileTabs.getHeight());
        tlUserProfileTabs.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
    }

    private void animateUserProfileHeader() {
           vUserProfileRoot.setTranslationY(-vUserProfileRoot.getHeight());
           ivUserProfilePhoto.setTranslationY(-ivUserProfilePhoto.getHeight());
           vUserDetails.setTranslationY(-vUserDetails.getHeight());
           vUserStats.setAlpha(0);

           vUserProfileRoot.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
           ivUserProfilePhoto.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
           vUserDetails.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
           vUserStats.animate().alpha(1).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR).start();
    }
    private HashMap<String, String>  getInfoProfile(Context context) {
        HashMap<String, String> hashMap = new HashMap<>();
        SharedPreferences prefs = context.getSharedPreferences(LoginActivity.class.getSimpleName(),Context.MODE_PRIVATE);
        hashMap.put(USER_ID, prefs.getString(USER_ID, ""));
        hashMap.put(F_NAME, prefs.getString(F_NAME, ""));
        hashMap.put(L_NAME, prefs.getString(L_NAME, ""));
        hashMap.put(EMAIL, prefs.getString(EMAIL, ""));
        hashMap.put(PHONE_NUM, prefs.getString(PHONE_NUM, ""));
        hashMap.put(ST_NO, prefs.getString(ST_NO, ""));
        hashMap.put(PHOTO_ID, prefs.getString(PHOTO_ID, "") + ".jpg");
        return hashMap;
    }

}
