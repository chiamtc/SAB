package com.example.sab;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	RequestParams params = new RequestParams();
    GoogleCloudMessaging gcmObj;
    String regId = "";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    
    public static final String REG_ID = "regId";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String NAME = "name";
	private EditText txtUsername;
	private EditText txtPassword;
	private Button btnLogin;
	private Button btnRegister;
	private TextView lbl;
	private TextView lbl2;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	String result = ""; 
	String line = null;
	InputStream is=null;
	Context appContext;
	private static String retrieveUrl = "http://justfortestbit302.site40.net/parentsLogin.php";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        appContext= getApplicationContext();
        txtUsername = (EditText)findViewById(R.id.txtUsername1);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        pDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        pDialog.setMessage("Please wait...");
        // Set Cancelable as False
        pDialog.setCancelable(false);
        btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String usrname = txtUsername.getText().toString();
				String pw = txtPassword.getText().toString();
				if(usrname.equals("") || pw.equals("")) {
					Toast.makeText(getBaseContext(), "Empty field(s) detected. Please fill in blank space",
							Toast.LENGTH_SHORT).show();
				}else{
					new LoginClass().execute(usrname,pw);
				}
				
			}//end onClick
			
			
		});//end setOnClickListener

        SharedPreferences prefs = getSharedPreferences("UserDetails",Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        String username = prefs.getString(USERNAME, "");
        String password = prefs.getString(PASSWORD, "");
        String name = prefs.getString(NAME, "");
        //String registrationId = "";
 
        if (!TextUtils.isEmpty(registrationId)) {
            Intent i = new Intent(appContext, MainPage.class);
            i.putExtra("regId", registrationId);
            i.putExtra("username", username);
            i.putExtra("password", password);
            i.putExtra("name", name);
            startActivity(i);
            finish();
        }
    }//end onCreate()
    
    public void RegisterUser(View view) {
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        
       if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            if (checkPlayServices()) {
 
                // Register Device in GCM Server
                registerInBackground(username,password);
            }
        
        // When Email is invalid
        else {
            Toast.makeText(appContext, "Please enter valid email",
                    Toast.LENGTH_LONG).show();
        }
    }
       }
    
    private void registerInBackground( final String username,  final String password) {
        new AsyncTask<Void, Void, String>() {  
            @Override
            protected String doInBackground(Void... params) { 
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging.getInstance(appContext);
                    }
                    regId = gcmObj.register(ApplicationConstants.GOOGLE_PROJ_ID);
                    msg = "Registration ID :" + regId;
                    Log.d("msg",msg);
 
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }
 
            @Override
            protected void onPostExecute(String msg) { 
                if (!TextUtils.isEmpty(regId)) {
                    storeRegIdinSharedPref(appContext, regId, username ,password);
                    Toast.makeText(
                    		appContext,
                            "Registered with GCM Server successfully.\n\n"
                                    + msg, Toast.LENGTH_SHORT).show();
                    
                } else {
                    Toast.makeText(
                    		appContext,
                            "Reg ID Creation Failed.\n\nEither you haven't enabled Internet or GCM server is busy right now. Make sure you enabled Internet and try registering again after some time."
                                    + msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }
    
    private void storeRegIdinSharedPref(Context context, String regId,String username, String password) {
        SharedPreferences prefs = getSharedPreferences("UserDetails",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, password);
        editor.commit();
        Log.d("regid", regId);
        Log.d("username", username);
        Log.d("password", password);
        storeRegIdinServer(regId, username,password);
        
    }
    
    private void storeRegIdinServer(String regId2, final String username, String password) {
        pDialog.show();
        params.put("username", username);
        params.put("password", password);
        params.put("regId", regId);
        Log.d("reg_id", regId);
        Log.d("username", username);
        Log.d("password", password);
        System.out.println("Email id = " + username + " Reg Id = " + regId);
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ApplicationConstants.APP_SERVER_URL, params,
                new AsyncHttpResponseHandler() { 
                    // When the response returned by REST has Http
                    // response code '200'
                    @Override
                    public void onSuccess(String response) { 
                        // Hide Progress Dialog
                    	pDialog.hide();
                        if (pDialog!= null) {
                        	pDialog.dismiss();
                        }
                        Toast.makeText(appContext,
                                "Reg Id shared successfully with Web App ",
                                Toast.LENGTH_LONG).show();
                        Intent i = new Intent(appContext,MainPage.class);
                        i.putExtra("regId", regId);
                        i.putExtra("username", username);
                        startActivity(i);
                        finish();
                    }
 
                    // When the response returned by REST has Http
                    // response code other than '200' such as '404',
                    // '500' or '403' etc
                    @Override
                    public void onFailure(int statusCode, Throwable error,
                            String content) {
                        // Hide Progress Dialog
                        pDialog.hide();
                        if (pDialog != null) {
                            pDialog.dismiss();
                        }
                        // When Http response code is '404'
                        if (statusCode == 404) {
                            Toast.makeText(appContext,
                                    "Requested resource not found",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code is '500'
                        else if (statusCode == 500) {
                            Toast.makeText(appContext,
                                    "Something went wrong at server end",
                                    Toast.LENGTH_LONG).show();
                        }
                        // When Http response code other than 404, 500
                        else {
                            Toast.makeText(
                            		appContext,
                                    "Unexpected Error occcured! [Most common Error: Device might "
                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    
   
    class LoginClass extends AsyncTask<String,Void,String>{
    		
    	protected void onPreExecute() {
    			super.onPreExecute();
                pDialog = new ProgressDialog(LoginActivity.this);
                pDialog.setMessage("Logging in..");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
    		}
    		
			@Override
			protected String doInBackground(String... arg0) { 
				
				
				
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", arg0[0]));
				params.add(new BasicNameValuePair("password", arg0[1]));
				
				
		       JSONObject jobj = jsonParser.makeHttpRequest(retrieveUrl, "POST", params);
		       try {
		    	   int success = jobj.getInt("success");
		    	   if(success ==1) {
		    		   
		    		   String name = jobj.getString("name");
		    		   String retrievedRegId = jobj.getString("gcmregid");
		    		   result += name+ "," + retrievedRegId;
		    		   return result+","+arg0[0];
		    	   }
		       }catch(JSONException e) {
		    	   Log.e("fail 3", e.toString());
		       }
		       
			return null;
			
			}//end doInBackground
			
			protected void onPostExecute(String file_url) { 
				super.onPostExecute(file_url);
				
				pDialog.dismiss();
				if(file_url !=null) {
					String[] temp = file_url.split(",");
					Log.d("temp0", temp[0]);	//name
					Log.d("temp1", temp[1]);	//regid
					Log.d("temp2", temp[2]);	//username
					SharedPreferences prefs = getSharedPreferences("UserDetails",Context.MODE_PRIVATE);
			        SharedPreferences.Editor editor = prefs.edit();
			        editor.putString(REG_ID, temp[1]);
			        editor.putString(USERNAME, temp[2]);
			        editor.putString("name", temp[0]);
			        editor.commit();
					Toast.makeText(getBaseContext(), "Welcome " +temp[0],
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(appContext, MainPage.class);
					
					intent.putExtra("username", temp[2]);
					intent.putExtra("regId", temp[1]);
                    startActivity(intent); 
                    finish();
                    
				}else {
					Toast.makeText(getBaseContext(), "Sorry, username or password is incorrect.",
							Toast.LENGTH_SHORT).show();
				}
			}//end onPostExecute
  
    	} //end LoginClass

    	 // Check if Google Playservices is installed in Device or not
    private boolean checkPlayServices() {

            int resultCode = GooglePlayServicesUtil
                    .isGooglePlayServicesAvailable(this);
            // When Play services not found in device
            if (resultCode != ConnectionResult.SUCCESS) {
                if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                    // Show Error dialog to install Play services
                    GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                            PLAY_SERVICES_RESOLUTION_REQUEST).show();
                } else {
                    Toast.makeText(
                    		appContext,
                            "This device doesn't support Play services, App will not work normally",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                return false;
            } else {
                Toast.makeText(
                		appContext,
                        "This device supports Play services, App will work normally",
                        Toast.LENGTH_LONG).show();
            }
            return true;
        }
    	
    protected void onResume() {
    	super.onResume();
    	checkPlayServices();
    	}
}
