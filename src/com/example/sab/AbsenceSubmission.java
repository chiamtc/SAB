package com.example.sab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
public class AbsenceSubmission extends Fragment {
	
	private Button btnClear;
	private EditText txtReason;
	private EditText txtStudentName;
	private Button btnSubmit;
	private String studentName;
	private String studentID;
	private ProgressDialog pDialog;
	String result;
	JSONParser jParser = new JSONParser();
	
	private static String url_all_products = "http://justfortestbit302.site40.net/insert.php";
	private static final String TAG_SUCCESS = "code";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
          View android = inflater.inflate(R.layout.absence, container, false);
          
          //UI declaration 
          btnClear = (Button)android.findViewById(R.id.btnClear);
          btnSubmit = (Button)android.findViewById(R.id.btnSubmit);
          txtReason = (EditText)android.findViewById(R.id.editText1);
          txtStudentName = (EditText)android.findViewById(R.id.txtStudentName);
          
          //get value from previous page
          studentName=getActivity().getIntent().getExtras().getString("studentName");
          studentID =getActivity().getIntent().getExtras().getString("studentID");
          
          //set text to txtStudentName when launch
          txtStudentName.setText(studentName);
          
          //btnClear function
          btnClear.setOnClickListener(new View.OnClickListener() {
  			
  			@Override
  			public void onClick(View v) {
  				txtReason.setText("");
  				
  				}//end onClick
  			});//end setOnClickListener
          
          //btnSubmit function
          btnSubmit.setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				String reason = txtReason.getText().toString();
    				Log.d("seehow", reason+studentID);
    				new SubmitAbsence().execute(reason,studentID);
    				
    			}//end onClick
    		});//end setOnClickListener
          
          
          
          return android;
          
	}//end onCreateView
	
	 /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class SubmitAbsence extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Submitting Absence. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("attendance", args[0]));
            params.add(new BasicNameValuePair("studentID", args[1]));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_products, "POST", params);
 
            // Check your log cat for JSON reponse
            Log.d("result?: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                Log.d("result?: ", Integer.toString(success));
                if (success == 1) {
                	result = "Absence Submission Succeeded";
                }else {
                	result = "Absence Submission Failed";
                }
                
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return result;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
        	super.onPostExecute(file_url);
        	Log.d("file url", file_url);
            pDialog.dismiss();

            Toast.makeText(getActivity(), file_url,Toast.LENGTH_SHORT).show();

            
        }
 
    }//end of class SubmitAbsence
    
	
}