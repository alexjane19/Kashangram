//package io.github.froger.instamaterial.server;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.AsyncTask;
//import android.preference.PreferenceManager;
//import android.util.Log;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.io.UnsupportedEncodingException;
//import java.net.MalformedURLException;
//import java.net.ProtocolException;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.net.ssl.HttpsURLConnection;
//
///**
// * Created by Alex Jane on 6/17/2015.
// */
//public class Login extends AsyncTask<String, String, String> {
//
//    public static String DATA_KEY = "data_key";
//
//    public static String USER_ID="userid";
//    public static String PASSWORD="password";
//
//        private String URL_NEW_PREDICTION = "http://172.16.195.160:3000/api/login";
//        SharedPreferences preferences;
//        Context context;
//        public Login(Context ctx){
//            context=ctx;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//
//        @Override
//        protected String doInBackground(String... arg0) {
//
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            params.add(new BasicNameValuePair(USER_ID, "alexjane019"));
//            params.add(new BasicNameValuePair(PASSWORD, "123456"));
//            System.out.println(params);
//
//            ServiceHandler serviceClient = new ServiceHandler();
//            String json = serviceClient.makeServiceCall(URL_NEW_PREDICTION,
//                    ServiceHandler.POST, params);
//            System.out.println(json);
//
//            String msg = updateSQLite(json);
//            return msg;
//
//
//        }
//        public String updateSQLite(String response){
//            // Create GSON object
//            try {
//                // Extract JSON array from the response
//                JSONArray arr = new JSONArray(response);
//
//                JSONObject obj = (JSONObject)arr.get(0);
//                System.out.println(arr.length());
//
//                // If no of array elements is not zero
//                if(arr.length() != 0){
//                    // Loop through each array element, get JSON object which has userid and username
////                    HashMap<String, String> message = new HashMap<>();
////                    message.put(ID, obj.get(ID).toString());
////                    message.put(TITLE, obj.get(TITLE).toString());
////                    message.put(URLIMAGE, obj.get(URLIMAGE).toString());
////                    message.put(URLTARGET, obj.get(URLTARGET).toString());
////                    message.put(PURPOSE, obj.get(PURPOSE).toString());
////                    message.put(NOTIFY, obj.get(NOTIFY).toString());
////                    message.put(VERSION, obj.get(VERSION).toString());
////                    message.put(DATE, obj.get(DATE).toString());
//
//                }
//            } catch (JSONException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                return "unsuccessful";
//
//            }
//            catch (java.lang.NullPointerException e){
//                e.printStackTrace();
//            }
//            return "successful";
//        }
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//        }
//    }