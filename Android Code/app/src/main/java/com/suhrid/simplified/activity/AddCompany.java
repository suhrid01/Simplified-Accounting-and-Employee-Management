package com.suhrid.simplified.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.suhrid.simplified.R;
import com.suhrid.simplified.app.AppController;
import com.suhrid.simplified.helper.SQLiteHandler;
import com.suhrid.simplified.helper.SessionManager;

import java.util.HashMap;
import java.util.Map;

import static com.suhrid.simplified.app.AppController.TAG;

public class AddCompany extends BaseActivity{
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
    private DrawerLayout sDrawerLayout;
    private ActionBarDrawerToggle sToggle;
    // private static final String REGISTER_URL="http://suhrid1theinceptor.000webhostapp.com/userlogin/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentOfView(R.layout.activity_add_company,AddCompany.this);

        db = new SQLiteHandler(getApplicationContext());
        isNetworkConnectionAvailable();
        final HashMap<String, String> user = db.getUserDetails();


        // session manager
        session = new SessionManager(getApplicationContext());
        String u_id = user.get("u_id");
        //NavigationView navigation = (NavigationView) findViewById(R.id.snavigation);
       // navigation.setNavigationItemSelectedListener(this);
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
    }



    private void registerUser() {
        String eid = edit_eid.getText().toString().trim().toLowerCase();
        String username = edit_username.getText().toString().trim().toLowerCase();
        String email = edit_email.getText().toString().trim().toLowerCase();
        String password = edit_pass.getText().toString().trim().toLowerCase();
        //String dob = edit_dob.getText().toString().trim().toLowerCase();
        String phnno = edit_phnno.getText().toString().trim().toLowerCase();
        //String isadmin = edit_isadmin.getText().toString().trim().toLowerCase();

        register(eid,username, password, email,phnno);
    }

    private void register(final String eid, final String username, final String password,final String email,final String phnno){

        String tag_string_req = "req_login";
        pDialog.setMessage("Adding Company...");
        showDialog();
        String url = "http://suhrid1theinceptor.000webhostapp.com/userlogin/register2.php";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("tagconvertstr", "[" + response + "]");
                Log.d(TAG, " Response: " + response);
                hideDialog();
                Toast.makeText(getApplicationContext(),
                        "Company added", Toast.LENGTH_LONG).show();
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
                params.put("cid", eid);
                params.put("customername", username);
                params.put("address", email);
                params.put("password",password);
                params.put("phnno",phnno);
                //params.put("dob",dob);
                //params.put("isadmin",isadmin);
                //params.put("token", SharedPreference.getInstance(getApplicationContext()).getToken());
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }




    public void checkNetworkConnection(){
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("No internet Connection");
        builder.setMessage("Please turn on internet connection to continue");
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean isNetworkConnectionAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if(isConnected) {
            Log.d("Network", "Connected");
            return true;
        }
        else{
            checkNetworkConnection();
            Log.d("Network","Not Connected");
            return false;
        }
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
