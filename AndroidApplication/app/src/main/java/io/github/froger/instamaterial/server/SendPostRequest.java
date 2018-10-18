package io.github.froger.instamaterial.server;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by alex on 2/7/17.
 */

public class SendPostRequest extends AsyncTask<String, Void, String> {
    public static String KEY_SUCCESS = "success";
    public static String KEY_MSG = "msg";
    public static String KEY_DATA = "data";

    public static String URL_SERVER= "http://172.20.10.4:3000";

    public static String USER_ID = "userid";
    public static String PASSWORD = "password";
    public static String EMAIL = "email";
    public static String ST_NO = "stno";
    public static String F_NAME = "fname";
    public static String L_NAME = "lname";
    public static String PHONE_NUM = "phonenumber";
    public static String PHOTO_ID = "photoid";
    public static String COUNT = "count";
    public static String FOLLOWER = "follower";
    public static String FOLLOWING = "following";
    public static String WRITING = "writing";
    public static String ACCESS_LEVEL = "accesslevel";
    public static String PICTURE = "picture";
    public static String DATE = "date";
    public static String N_LIKE = "nlike";
    public static String LIKED = "liked";
    public static String PROFILE_PHOTO = "prophoto";



    public interface AsyncResponse {
        void processFinish(String output);
    }
    public AsyncResponse delegate = null;

    public JSONObject postDataParams;

    public SendPostRequest(JSONObject postDataParams, AsyncResponse delegate){
        this.postDataParams = postDataParams;
        this.delegate = delegate;

    }
    protected void onPreExecute(){}

    protected String doInBackground(String... arg0) {

        try {

            URL url = new URL(URL_SERVER + arg0[0]); // here is your URL path

            Log.e("params",postDataParams.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader in=new BufferedReader(new
                        InputStreamReader(
                        conn.getInputStream()));

                StringBuffer sb = new StringBuffer("");
                String line="";

                while((line = in.readLine()) != null) {

                    sb.append(line);
                    break;
                }

                in.close();
                return sb.toString();

            }
            else {
                return new String("false : "+responseCode);
            }
        }
        catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }

    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println(result);
        delegate.processFinish(result);
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}