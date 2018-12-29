package com.suhrid.simplified.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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

import java.util.HashMap;
import java.util.Map;

import static com.suhrid.simplified.app.AppController.TAG;

public class AddEmployee extends BaseActivity{
    EditText edit_eid;
    EditText edit_username;
    EditText edit_email;
    EditText edit_pass;
    EditText edit_phnno;
    EditText edit_dob;
    EditText edit_isadmin;
    Button btn_sign;
    private SQLiteHandler db;
    private SessionManager session;
    private ProgressDialog pDialog;
    Button btn_login;
    String etype;
    private DrawerLayout sDrawerLayout;
    private ActionBarDrawerToggle sToggle;
    ArrayAdapter<String> adapter;
    private MaterialSpinner s1;
    // private static final String REGISTER_URL="http://suhrid1theinceptor.000webhostapp.com/userlogin/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentOfView(R.layout.activity_addemployee,this);
        sDrawerLayout = (DrawerLayout) findViewById(R.id.adrawerlayout);
        sToggle = new ActionBarDrawerToggle(this, sDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        sDrawerLayout.addDrawerListener(sToggle);
        sToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new SQLiteHandler(getApplicationContext());
        isNetworkConnectionAvailable();
        final HashMap<String, String> user = db.getUserDetails();


        // session manager
        session = new SessionManager(getApplicationContext());
        String u_id = user.get("u_id");
        NavigationView navigation = (NavigationView) findViewById(R.id.snavigation);
        navigation.setNavigationItemSelectedListener(this);
        edit_eid = (EditText) findViewById(R.id.eid);
        edit_username = (EditText) findViewById(R.id.id_username);
        edit_email = (EditText) findViewById(R.id.id_email);
        edit_pass = (EditText) findViewById(R.id.id_pass);
        edit_dob = (EditText) findViewById(R.id.id_dob);
        edit_phnno = (EditText) findViewById(R.id.id_phone);
        edit_isadmin = (EditText) findViewById(R.id.id_isadmin);
        btn_sign = (Button) findViewById(R.id.btn_sign);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        btn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        String[] arraySpinner1 = new String[]{
                "Type of Employee","Accounts","Manager","Sales"
        };
        s1 = (MaterialSpinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arraySpinner1) {
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

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(adapter1);

        s1.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                etype = item;

            }


        });
    }



    private void registerUser() {
        String eid = edit_eid.getText().toString().trim().toLowerCase();
        String username = edit_username.getText().toString().trim().toLowerCase();
        String email = edit_email.getText().toString().trim().toLowerCase();
        String password = edit_pass.getText().toString().trim().toLowerCase();
        String dob = edit_dob.getText().toString().trim().toLowerCase();
        String phnno = edit_phnno.getText().toString().trim().toLowerCase();
        String isadmin = edit_isadmin.getText().toString().trim().toLowerCase();

        register(eid,username, password, email,dob,phnno,isadmin);
    }

    private void register(final String eid, final String username, final String password,final String email,final String dob,final String phnno,final String isadmin){

        String tag_string_req = "req_login";
        pDialog.setMessage("Adding Employee...");
        showDialog();
        String url = "http://suhrid1theinceptor.000webhostapp.com/userlogin/register.php";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("tagconvertstr", "[" + response + "]");
                Log.d(TAG, " Response: " + response);
                hideDialog();
                Toast.makeText(getApplicationContext(),
                        "Employee added", Toast.LENGTH_LONG).show();
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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("eid", eid);
                params.put("ename", username);
                params.put("email", email);
                params.put("password",password);
                params.put("phnno",phnno);
                params.put("dob",dob);
                params.put("isadmin",isadmin);
                params.put("emptype",etype);
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

    public void onClick2(View view) {
    }
}
