package io.github.froger.instamaterial.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.froger.instamaterial.R;
import io.github.froger.instamaterial.server.SendPostRequest;

import static io.github.froger.instamaterial.server.SendPostRequest.EMAIL;
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

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public static final String SHP_USER_ID = "shp_userid";


    private static final int REQUEST_SIGNUP = 0;
    private static final String URL_LOGIN = "/api/login";


    @BindView(R.id.input_user) EditText _userText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String user = _userText.getText().toString();
        String password = _passwordText.getText().toString();

        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put(USER_ID, user);
            postDataParams.put(PASSWORD, password);
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
                        storeLoginUser(getApplicationContext(),data);
                        progressDialog.dismiss();
                        onLoginSuccess();



                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).execute(URL_LOGIN);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                String user = data.getStringExtra(LoginActivity.class.getSimpleName());
                _userText.setText(user);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String user = _userText.getText().toString();
        String password = _passwordText.getText().toString();

        if (user.isEmpty()) {
            _userText.setError("enter a valid user");
            valid = false;
        } else {
            _userText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }


//        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            _emailText.setError("enter a valid email address");
//            valid = false;
//        } else {
//            _emailText.setError(null);
//        }
        return valid;
    }

    private void storeLoginUser(Context ctx, JSONObject data) {
        final SharedPreferences prefs = ctx.getSharedPreferences(LoginActivity.class.getSimpleName(),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString(USER_ID, String.valueOf(data.get(USER_ID)));
            editor.putString(EMAIL, String.valueOf(data.get(EMAIL)));
            editor.putString(F_NAME, String.valueOf(data.get(F_NAME)));
            editor.putString(L_NAME, String.valueOf(data.get(L_NAME)));
            editor.putString(PHONE_NUM, String.valueOf(data.get(PHONE_NUM)));
            editor.putString(ST_NO, String.valueOf(data.get(ST_NO)));
            editor.putString(PASSWORD, String.valueOf(data.get(PASSWORD)));
            editor.putString(PHOTO_ID, String.valueOf(data.get(PHOTO_ID)));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.apply();
    }
}
