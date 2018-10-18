package io.github.froger.instamaterial.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.Future;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import io.github.froger.instamaterial.R;
import io.github.froger.instamaterial.Utils;
import io.github.froger.instamaterial.server.SendPostRequest;

import static io.github.froger.instamaterial.server.SendPostRequest.ACCESS_LEVEL;
import static io.github.froger.instamaterial.server.SendPostRequest.KEY_MSG;
import static io.github.froger.instamaterial.server.SendPostRequest.KEY_SUCCESS;
import static io.github.froger.instamaterial.server.SendPostRequest.PHOTO_ID;
import static io.github.froger.instamaterial.server.SendPostRequest.PICTURE;
import static io.github.froger.instamaterial.server.SendPostRequest.URL_SERVER;
import static io.github.froger.instamaterial.server.SendPostRequest.USER_ID;
import static io.github.froger.instamaterial.server.SendPostRequest.WRITING;
import static io.github.froger.instamaterial.ui.activity.SignupCompleteActivity.URL_UPLOAD;
import static io.github.froger.instamaterial.ui.activity.TakePhotoActivity.photoPath;

/**
 * Created by Miroslaw Stanek on 21.02.15.
 */
public class PublishActivity extends BaseActivity {
    public static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";
    private static final String URL_POST = "/api/post";


    @BindView(R.id.tbFollowers)
    ToggleButton tbFollowers;
    @BindView(R.id.tbDirect)
    ToggleButton tbDirect;
    @BindView(R.id.ivPhoto)
    ImageView ivPhoto;
    @BindView(R.id.etDescription)
    EditText etDescription;

    private boolean propagatingToggleState = false;
    private Uri photoUri;
    private int photoSize;

    public static void openWithPhotoUri(Activity openingActivity,Uri photoUri) {
        Intent intent = new Intent(openingActivity, PublishActivity.class);
        intent.putExtra(ARG_TAKEN_PHOTO_URI,  photoUri);
        openingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_grey600_24dp);
        photoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);

        if (savedInstanceState == null) {
            photoUri = getIntent().getParcelableExtra(ARG_TAKEN_PHOTO_URI);
        } else {
            photoUri = savedInstanceState.getParcelable(ARG_TAKEN_PHOTO_URI);
        }
        updateStatusBarColor();

        ivPhoto.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ivPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
                loadThumbnailPhoto();
                return true;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(0xff888888);
        }
    }

    private void loadThumbnailPhoto() {
        ivPhoto.setScaleX(0);
        ivPhoto.setScaleY(0);
        Picasso.with(this)
                .load(photoUri)
                .centerCrop()
                .resize(photoSize, photoSize)
                .into(ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        ivPhoto.animate()
                                .scaleX(1.f).scaleY(1.f)
                                .setInterpolator(new OvershootInterpolator())
                                .setDuration(400)
                                .setStartDelay(200)
                                .start();
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_publish) {
            bringMainActivityToTop();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void bringMainActivityToTop() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(MainActivity.ACTION_SHOW_LOADING_ITEM);
        sendPost(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_TAKEN_PHOTO_URI, photoUri);
    }

    @OnCheckedChanged(R.id.tbFollowers)
    public void onFollowersCheckedChange(boolean checked) {
        if (!propagatingToggleState) {
            propagatingToggleState = true;
            tbDirect.setChecked(!checked);
            propagatingToggleState = false;
        }
    }

    @OnCheckedChanged(R.id.tbDirect)
    public void onDirectCheckedChange(boolean checked) {
        if (!propagatingToggleState) {
            propagatingToggleState = true;
            tbFollowers.setChecked(!checked);
            propagatingToggleState = false;
        }
    }
    private void sendPost(final Intent intent){
        final JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put(USER_ID, MainActivity.getRegistrationUserId(this));
            postDataParams.put(WRITING, etDescription.getText());
            postDataParams.put(ACCESS_LEVEL, true);
            postDataParams.put(PICTURE, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Future uploading = Ion.with(this)
                .load(URL_SERVER + URL_UPLOAD)
                .setMultipartFile("image", photoPath)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        try {
                            JSONObject jobj = new JSONObject(result.getResult());
                            System.out.println("response upload: " + jobj);
                            boolean b = (boolean) jobj.get(KEY_SUCCESS);
                            if (b) {
                                postDataParams.put(PHOTO_ID, jobj.get(PHOTO_ID));
                                new SendPostRequest(postDataParams, new SendPostRequest.AsyncResponse() {
                                    @Override
                                    public void processFinish(String output) {
                                        try {
                                            JSONObject obj = new JSONObject(output);
                                            boolean b = (boolean) obj.get(KEY_SUCCESS);
                                            if (b) {
                                                System.out.println(obj.get(KEY_MSG));
                                                startActivity(intent);

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }).execute(URL_POST);

                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                });

    }
}
