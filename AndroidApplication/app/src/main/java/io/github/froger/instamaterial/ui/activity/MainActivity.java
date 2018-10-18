package io.github.froger.instamaterial.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import butterknife.BindView;
import butterknife.OnClick;
import io.github.froger.instamaterial.R;
import io.github.froger.instamaterial.Utils;
import io.github.froger.instamaterial.pojo.FeedItem;
import io.github.froger.instamaterial.server.SendPostRequest;
import io.github.froger.instamaterial.ui.adapter.FeedAdapter;
import io.github.froger.instamaterial.ui.adapter.FeedItemAnimator;
import io.github.froger.instamaterial.ui.adapter.UserProfileAdapter;
import io.github.froger.instamaterial.ui.view.FeedContextMenu;
import io.github.froger.instamaterial.ui.view.FeedContextMenuManager;

import static io.github.froger.instamaterial.server.SendPostRequest.ACCESS_LEVEL;
import static io.github.froger.instamaterial.server.SendPostRequest.DATE;
import static io.github.froger.instamaterial.server.SendPostRequest.F_NAME;
import static io.github.froger.instamaterial.server.SendPostRequest.KEY_DATA;
import static io.github.froger.instamaterial.server.SendPostRequest.KEY_MSG;
import static io.github.froger.instamaterial.server.SendPostRequest.KEY_SUCCESS;
import static io.github.froger.instamaterial.server.SendPostRequest.LIKED;
import static io.github.froger.instamaterial.server.SendPostRequest.L_NAME;
import static io.github.froger.instamaterial.server.SendPostRequest.N_LIKE;
import static io.github.froger.instamaterial.server.SendPostRequest.PHONE_NUM;
import static io.github.froger.instamaterial.server.SendPostRequest.PHOTO_ID;
import static io.github.froger.instamaterial.server.SendPostRequest.PICTURE;
import static io.github.froger.instamaterial.server.SendPostRequest.PROFILE_PHOTO;
import static io.github.froger.instamaterial.server.SendPostRequest.URL_SERVER;
import static io.github.froger.instamaterial.server.SendPostRequest.USER_ID;
import static io.github.froger.instamaterial.server.SendPostRequest.WRITING;
import static io.github.froger.instamaterial.ui.activity.SignupCompleteActivity.URL_UPLOAD;


public class MainActivity extends BaseDrawerActivity implements FeedAdapter.OnFeedItemClickListener,
        FeedContextMenu.OnFeedContextMenuItemClickListener {
    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    private static final String URL_FEED = "/api/feed";


    @BindView(R.id.rvFeed)
    RecyclerView rvFeed;
    @BindView(R.id.btnCreate)
    FloatingActionButton fabCreate;
    @BindView(R.id.content)
    CoordinatorLayout clContent;

    private FeedAdapter feedAdapter;
    private String _login_user;
    private boolean pendingIntroAnimation;
    List<FeedItem> feedItems= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _login_user = getRegistrationUserId(getApplicationContext());
        boolean state = getCompleteRegistration(getApplicationContext());
        System.out.println(_login_user);
        if(_login_user.isEmpty()){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else if (!state){
            Intent intent = new Intent(MainActivity.this, SignupCompleteActivity.class);
            intent.putExtra(SignupCompleteActivity.class.getSimpleName(), _login_user);
            startActivity(intent);
        }
        else {
            try {
                setupFeed();

                if (savedInstanceState == null) {
                    pendingIntroAnimation = true;
                } else {
                        //feedAdapter.updateItems(false,feedItems);
                }
            }catch (java.lang.IndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }

    }

    private void setupFeed() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvFeed.setLayoutManager(linearLayoutManager);
        feedAdapter = new FeedAdapter(MainActivity.this, feedItems);
        feedAdapter.setOnFeedItemClickListener(MainActivity.this);
        rvFeed.setAdapter(feedAdapter);

        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put(USER_ID, _login_user);
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
                        for (int i=0; i<arr.length(); i++){
                            JSONObject object = (JSONObject)arr.get(i);
                            FeedItem feedItem = new FeedItem(object.getString(USER_ID),object.getString(PHOTO_ID),
                                    object.getString(WRITING), object.getBoolean(ACCESS_LEVEL), object.getBoolean(PICTURE),
                                    object.getString(DATE), object.getInt(N_LIKE),
                                    object.getString(LIKED).equals("") ? false : true, object.getString(PROFILE_PHOTO));
                            feedItems.add(feedItem);
                        }
                        feedAdapter.updateItems(true,feedItems);
                        //feedItems.addAll(feedItems1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute(URL_FEED);


        rvFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                FeedContextMenuManager.getInstance().onScrolled(recyclerView, dx, dy);
            }
        });
        rvFeed.setItemAnimator(new FeedItemAnimator());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ACTION_SHOW_LOADING_ITEM.equals(intent.getAction())) {
            showFeedLoadingItemDelayed();
        }


    }

    private void showFeedLoadingItemDelayed() {
        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put(USER_ID, _login_user);
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
                        feedItems.clear();
                        System.out.println(obj.get(KEY_MSG));
                        JSONArray arr = new JSONArray(String.valueOf(obj.get(KEY_DATA)));
                        for (int i=0; i<arr.length(); i++){
                            JSONObject object = (JSONObject)arr.get(i);
                            FeedItem feedItem = new FeedItem(object.getString(USER_ID),object.getString(PHOTO_ID),
                                    object.getString(WRITING), object.getBoolean(ACCESS_LEVEL), object.getBoolean(PICTURE),
                                    object.getString(DATE), object.getInt(N_LIKE),
                                    object.getString(LIKED).equals("") ? false : true, object.getString(PROFILE_PHOTO));
                            feedItems.add(feedItem);
                        }
                        rvFeed.smoothScrollToPosition(0);
                        feedAdapter.showLoadingView();
                        //feedAdapter.updateItems(true,feedItems);
                        //feedItems.addAll(feedItems1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute(URL_FEED);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                rvFeed.smoothScrollToPosition(0);
//                feedAdapter.showLoadingView();
//            }
//        }, 500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;
    }

    private void startIntroAnimation() {
        fabCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));

        int actionbarSize = Utils.dpToPx(56);
        getToolbar().setTranslationY(-actionbarSize);
        getIvLogo().setTranslationY(-actionbarSize);
        getInboxMenuItem().getActionView().setTranslationY(-actionbarSize);

        getToolbar().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        getIvLogo().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);
        getInboxMenuItem().getActionView().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                })
                .start();
    }

    private void startContentAnimation() {
        fabCreate.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();
            //feedAdapter.updateItems(true,feedItems);
    }

    @Override
    public void onCommentsClick(View v, int position) {
        final Intent intent = new Intent(this, CommentsActivity.class);
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onMoreClick(View v, int itemPosition) {
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, itemPosition, this);
    }

    @Override
    public void onProfileClick(View v) {
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        startingLocation[0] += v.getWidth() / 2;
        UserProfileActivity.startUserProfileFromLocation(startingLocation, this);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onReportClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onSharePhotoClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCopyShareUrlClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCancelClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @OnClick(R.id.btnCreate)
    public void onTakePhotoClick() {
        int[] startingLocation = new int[2];
        fabCreate.getLocationOnScreen(startingLocation);
        startingLocation[0] += fabCreate.getWidth() / 2;
        TakePhotoActivity.startCameraFromLocation(startingLocation, this);
        overridePendingTransition(0, 0);
    }

    public void showLikedSnackbar() {
        Snackbar.make(clContent, "Liked!", Snackbar.LENGTH_SHORT).show();
    }


    public static String getRegistrationUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(LoginActivity.class.getSimpleName(),Context.MODE_PRIVATE);
        String loginUser = prefs.getString(USER_ID, "");
        System.out.println(loginUser);
        if (loginUser.isEmpty()) {
            return "";
        }

        return loginUser;
    }
    private boolean getCompleteRegistration(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(LoginActivity.class.getSimpleName(),Context.MODE_PRIVATE);
        String fname = prefs.getString(F_NAME, "");
        if (fname.equals("null")) {
            return false;
        }
        return true;
    }
}