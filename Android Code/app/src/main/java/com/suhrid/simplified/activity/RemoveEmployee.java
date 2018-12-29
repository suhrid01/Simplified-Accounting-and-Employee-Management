package com.suhrid.simplified.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.suhrid.simplified.R;
import com.suhrid.simplified.app.AppController;
import com.suhrid.simplified.helper.SQLiteHandler;
import com.suhrid.simplified.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.suhrid.simplified.app.AppController.TAG;

public class RemoveEmployee extends BaseActivity {

    public String date;
    private EditText dateet;
    private SQLiteHandler db;
    private SessionManager session;
    private TextView fname;
    private TextView s_in;
    private TextView s_out;
    private DatePickerDialog datePickerDialog;
    private DrawerLayout sDrawerLayout;
    private ActionBarDrawerToggle sToggle;
    //An ArrayList for Spinner Items
    private ArrayList<String> eidlist;
    private int status1;
    public String u_id, eid, cid;
    private ProgressDialog pDialog;
    //JSON Array
    private EditText datepicktd, amount;
    private JSONArray data1;
    public MaterialSpinner eidspinner;
    public MaterialSpinner cidspinner;
    private Button assignvisit;

    private DatePickerDialog datePickerDialog3;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentOfView(R.layout.activity_removeemployee,this);
        sDrawerLayout = (DrawerLayout) findViewById(R.id.adrawerlayout);
        sToggle = new ActionBarDrawerToggle(this, sDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        sDrawerLayout.addDrawerListener(sToggle);
        sToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigation = (NavigationView) findViewById(R.id.snavigation);
        navigation.setNavigationItemSelectedListener(this);


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        assignvisit = (Button) findViewById(R.id.btnasignvisit);

       /* fname = (TextView) findViewById(R.id.welcome);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Ordinary.ttf");
        fname.setTypeface(typeface);
        fname.setText("WELCOME ADMIN");*/


        //sec1=(TextView)findViewById(R.id.section);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        isNetworkConnectionAvailable();
        final HashMap<String, String> user = db.getUserDetails();


        // session manager
        session = new SessionManager(getApplicationContext());
        u_id = user.get("u_id");

//Initializing the ArrayList
        eidlist = new ArrayList<String>();
        eidlist.add("Select Employee");
        //Initializing Spinner
        eidspinner = (MaterialSpinner) findViewById(R.id.eidspin);

        String tag_string_req2 = "req_login";
        //pDialog.setMessage("Adding Order...");
        //showDialog();
        String url2 = "http://suhrid1theinceptor.000webhostapp.com/userlogin/employee_list_api.php";
        StringRequest strReq2 = new StringRequest(Request.Method.GET,
                url2, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("tagconvertstr", "[" + response + "]");
                Log.d(TAG, " Response: " + response);
                // hideDialog();
                //Toast.makeText(getApplicationContext(),"Order added", Toast.LENGTH_LONG).show();
                try {
                    JSONObject jObj = new JSONObject(response);
                    data1 = jObj.getJSONArray("respond");

                    for (int i = 0; i < data1.length(); i++) {
                        try {
                            //Getting json object
                            JSONObject json = data1.getJSONObject(i);

                            //Adding the name of the student to array list
                            eidlist.add(json.getString("ename"));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(RemoveEmployee.this,
                            android.R.layout.simple_spinner_dropdown_item, eidlist) {
                        @Override
                        public boolean isEnabled(int position) {
                            if (position == 0) {
                                // Disable the first item from Spinner
                                // First item will be use for hint
                                return false;
                            } else {
                                return true;
                            }
                        }

                        @Override
                        public View getDropDownView(int position, View convertView,
                                                    ViewGroup parent) {
                            View view = super.getDropDownView(position, convertView, parent);
                            TextView tv = (TextView) view;
                            if (position == 0) {
                                // Set the hint text color gray
                                tv.setTextColor(Color.GRAY);
                            } else {
                                tv.setTextColor(Color.BLACK);
                            }
                            return view;
                        }
                    };

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    eidspinner.setAdapter(adapter);


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, " Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Wrong Credentials!", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq2, tag_string_req2);



        eidspinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                eid = getid(position-1);

            }

        });

    }

    private String getid(int position) {
        String desc="" ;

        try {
            //Getting json object
            //JSONObject json = data1.getJSONObject(i);

            JSONObject json1 = data1.getJSONObject(position);

            desc = json1.getString("eid");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return desc;
    }

    public void onclick2(View view) {


        removeemployee(eid);
    }
    private void removeemployee(final String eid) {



        String tag_string_req = "req_login";
        pDialog.setMessage("Removing...");
        showDialog();
        String url = "http://suhrid1theinceptor.000webhostapp.com/userlogin/remove_employee_api.php";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("tagconvertstr", "[" + response + "]");
                Log.d(TAG, "Loginnnnnnnnnnnnnnn Response: " + response);
                hideDialog();
                Toast.makeText(getApplicationContext(),
                        "Task Assigned successfully for "+ eid, Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Wrong Credentials!", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("eid", eid);

                //params.put("token", SharedPreference.getInstance(getApplicationContext()).getToken());
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }







    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }




}
