package io.github.froger.instamaterial.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.Future;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.froger.instamaterial.R;
import io.github.froger.instamaterial.server.SendPostRequest;

import static io.github.froger.instamaterial.server.SendPostRequest.EMAIL;
import static io.github.froger.instamaterial.server.SendPostRequest.F_NAME;
import static io.github.froger.instamaterial.server.SendPostRequest.KEY_MSG;
import static io.github.froger.instamaterial.server.SendPostRequest.KEY_SUCCESS;
import static io.github.froger.instamaterial.server.SendPostRequest.L_NAME;
import static io.github.froger.instamaterial.server.SendPostRequest.PASSWORD;
import static io.github.froger.instamaterial.server.SendPostRequest.PHONE_NUM;
import static io.github.froger.instamaterial.server.SendPostRequest.PHOTO_ID;
import static io.github.froger.instamaterial.server.SendPostRequest.ST_NO;
import static io.github.froger.instamaterial.server.SendPostRequest.URL_SERVER;
import static io.github.froger.instamaterial.server.SendPostRequest.USER_ID;


public class SignupCompleteActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private static final String URL_COMPLETE_REGISTER = "/api/register/complete";
    public static final String URL_UPLOAD = "/api/upload";

    @BindView(R.id.image_profile) CircleImageView _profileImage;
    @BindView(R.id.input_fname) EditText _fnameText;
    @BindView(R.id.input_lname) EditText _lnameText;
    @BindView(R.id.input_pnumber) EditText _pnumberText;
    @BindView(R.id.btn_continue) Button _continueButton;

    private String path ="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_complete);
        ButterKnife.bind(this);
        Ion.getDefault(this).configure().setLogging("ion-sample", Log.DEBUG);
        _continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fintent = new Intent(Intent.ACTION_GET_CONTENT);
                fintent.setType("image/jpeg");
                try {
                    startActivityForResult(fintent, 100);
                } catch (ActivityNotFoundException e) {

                }
            }
        });
//        _loginLink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Finish the registration screen and return to the Login activity
//                finish();
//            }
//        });
    }

    public void signup() {


        if (!validate()) {
            onSignupFailed();
            return;
        }

        _continueButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupCompleteActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();
        String user = getIntent().getStringExtra(SignupCompleteActivity.class.getSimpleName());
        System.out.println("complete "+ user);
        String fname = _fnameText.getText().toString();
        String lname = _lnameText.getText().toString();
        String phonenumber = _pnumberText.getText().toString();


        final JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put(USER_ID, user);
            postDataParams.put(F_NAME, fname);
            postDataParams.put(L_NAME, lname);
            postDataParams.put(PHONE_NUM, phonenumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("path: " + path);
        if(!path.equals("")) {
            File f = new File(path);
            Future uploading = Ion.with(SignupCompleteActivity.this)
                    .load(URL_SERVER + URL_UPLOAD)
                    .setMultipartFile("image", f)
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
                                    updateInforamtion(postDataParams,progressDialog);

                                }

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        }
                    });
        }else{
            updateInforamtion(postDataParams,progressDialog);
        }



    }

    public void updateInforamtion(final JSONObject postDataParams, final ProgressDialog progressDialog){
        new SendPostRequest(postDataParams, new SendPostRequest.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    JSONObject obj = new JSONObject(output);
                    boolean b = (boolean) obj.get(KEY_SUCCESS);
                    if (b) {
                        System.out.println(obj.get(KEY_MSG));
                        onSignupSuccess();
                        updateInfoUser(getApplicationContext(),postDataParams);
                        progressDialog.dismiss();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).execute(URL_COMPLETE_REGISTER);

    }

    public void onSignupSuccess() {
        _continueButton.setEnabled(true);
        //setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _continueButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String fname = _fnameText.getText().toString();
        String lname = _lnameText.getText().toString();
        String phonenumber = _pnumberText.getText().toString();

        if (fname.isEmpty() /*|| user.length() < 3*/) {
            _fnameText.setError("at least 3 characters");
            valid = false;
        } else {
            _fnameText.setError(null);
        }

        if (lname.isEmpty()) {
            _lnameText.setError("enter a valid email address");
            valid = false;
        } else {
            _lnameText.setError(null);
        }

        if (phonenumber.isEmpty() || phonenumber.length() < 10 || phonenumber.length() > 10 || phonenumber.charAt(0) == '0') {
            _pnumberText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _pnumberText.setError(null);
        }

        return valid;
    }

    private void updateInfoUser(Context ctx, JSONObject data) {
        final SharedPreferences prefs = ctx.getSharedPreferences(LoginActivity.class.getSimpleName(),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString(F_NAME, String.valueOf(data.get(F_NAME)));
            editor.putString(L_NAME, String.valueOf(data.get(L_NAME)));
            editor.putString(PHONE_NUM, String.valueOf(data.get(PHONE_NUM)));
            editor.putString(PHOTO_ID, String.valueOf(data.get(PHOTO_ID)));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.apply();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {

                    path = getRealPathFromURI_API19(getApplicationContext(),data.getData());
                    System.out.println("path onAct: " + path);
                    //System.out.println("data.getData(): " + data.getData().getPath());

                    _profileImage.setImageURI(data.getData());
//                    upload.setVisibility(View.VISIBLE);

                }
        }
    }
    private String getPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        System.out.println("proj: " + proj[0]);
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        System.out.println("loader: " + loader);

        Cursor cursor = loader.loadInBackground();
        System.out.println("cursor: " + cursor);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        System.out.println("column_index: " + column_index);

        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if(cursor != null){
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


}