package com.example.sab;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
 
public class MainPage extends ListActivity {
 
    // Progress Dialog
    private ProgressDialog pDialog;
    private TextView txtChildrenList;
    String name;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
 
    ArrayList<HashMap<String, String>> childrenList;
 
    // url to get all products list
    private static String url_all_products = "http://justfortestbit302.site40.net/get_all_children.php";
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_CHILDREN = "children";
    private static final String TAG_STUID = "studentID";
    private static final String TAG_STUNAME = "studentName";
 
    // products JSONArray
    JSONArray products = null;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_children);
        txtChildrenList = (TextView)findViewById(R.id.txtTitle);
        // Hashmap for ListView
        childrenList = new ArrayList<HashMap<String, String>>();
        name= getIntent().getExtras().getString("username");
        Toast.makeText(getApplicationContext(),"Welcome Back!", Toast.LENGTH_SHORT).show();
        // Loading products in Background Thread
        new LoadAllChildren().execute(name);
 
        // Get listview
        ListView lv = getListView();
 
        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String stuid = ((TextView) view.findViewById(R.id.studentid)).getText()
                        .toString();
                String stuName= ((TextView)view.findViewById(R.id.name)).getText().toString();
               
                // Starting new intent
               Intent in = new Intent(getBaseContext(),
                        SwipePages.class);
                // sending pid to next activity
                in.putExtra("studentID", stuid);
                in.putExtra("studentName", stuName);
                startActivity(in);
               
            }
        });
 
    }
    
    
    
    // Response from Edit Product Activity
/*   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
 
    }
*/
 
    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllChildren extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainPage.this);
            pDialog.setMessage("Loading profile. Please wait...");
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
            params.add(new BasicNameValuePair("username", args[0]));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_products, "POST", params);
 
            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_CHILDREN);
 
                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);
 
                        // Storing each json item in variable
                        String id = c.getString(TAG_STUID);
                        String name = c.getString(TAG_STUNAME);
 
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
 
                        // adding each child node to HashMap key => value
                        map.put(TAG_STUID, id);
                        map.put(TAG_STUNAME, name);
 
                        // adding HashList to ArrayList
                        childrenList.add(map);
                    }
                } else {
                    // no products found
                    // Launch Add New product Activity
                    
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            MainPage.this, childrenList,
                            R.layout.selected_child, new String[] { TAG_STUID,
                                    TAG_STUNAME},
                            new int[] { R.id.studentid, R.id.name });
                    // updating listview
                    setListAdapter(adapter);
                }
            });
            txtChildrenList.setText("Children List");
        }
 
    }
}