package io.github.froger.instamaterial.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import static io.github.froger.instamaterial.server.SendPostRequest.KEY_DATA;
import static io.github.froger.instamaterial.server.SendPostRequest.KEY_MSG;
import static io.github.froger.instamaterial.server.SendPostRequest.KEY_SUCCESS;
import static io.github.froger.instamaterial.server.SendPostRequest.PASSWORD;
import static io.github.froger.instamaterial.server.SendPostRequest.ST_NO;
import static io.github.froger.instamaterial.server.SendPostRequest.USER_ID;


public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private static final String URL_REGISTER = "/api/register";

    @BindView(R.id.input_user) EditText _userText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_stNo) EditText _stNoText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String user = _userText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String stno = _stNoText.getText().toString();


        JSONObject postDataParams = new JSONObject();
        try {
            postDataParams.put(USER_ID, user);
            postDataParams.put(EMAIL, email);
            postDataParams.put(ST_NO, stno);
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
                        onSignupSuccess();
                        progressDialog.dismiss();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).execute(URL_REGISTER);

    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(LoginActivity.class.getSimpleName(),_userText.getText().toString());
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String user = _userText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String stno = _stNoText.getText().toString();

        if (user.isEmpty() /*|| user.length() < 3*/) {
            _userText.setError("at least 3 characters");
            valid = false;
        } else {
            _userText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        if (stno.isEmpty() || stno.length() < 10 || stno.length() > 10) {
            _stNoText.setError("10 alphanumeric characters");
            valid = false;
        } else {
            _stNoText.setError(null);
        }

        return valid;
    }
}