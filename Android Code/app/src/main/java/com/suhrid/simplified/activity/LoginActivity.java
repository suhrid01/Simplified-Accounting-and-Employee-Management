/**
 * Author: Suhrid Ranjan Das
 */
package com.suhrid.simplified.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



import com.suhrid.simplified.R;
import com.suhrid.simplified.app.AppController;
import com.suhrid.simplified.helper.SQLiteHandler;
import com.suhrid.simplified.helper.SessionManager;


public class LoginActivity extends Activity implements LocationListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    LocationManager locationManager;
    String mprovider,e_id;
    String lat, lon;

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION",
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //FirebaseInstanceIDService.onTokenRefresh();
        if (shouldAskPermissions()) {
            askPermissions();
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        mprovider = locationManager.getBestProvider(criteria, true);

        if (mprovider != null && !mprovider.equals("")) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                askPermissions();
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(mprovider);
            locationManager.requestLocationUpdates(mprovider, 1500, 1, this);

            if (location != null)
                onLocationChanged(location);
            else

                Toast.makeText(getApplicationContext(), "No Location Provider Found Check Your Code", Toast.LENGTH_SHORT).show();
        }


        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);
        //Button btnLinkToForgotPassword= (Button) findViewById(R.id.btnLinkToForgotPassword);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        String is_staff = user.get("is_staff");
        String emp = user.get("emp_type");

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            if (is_staff.equals("1")) {
                Intent intent = new Intent(LoginActivity.this,
                        PendingOrders.class);
                startActivity(intent);
                finish();
            } else {
                if (emp.equalsIgnoreCase("Sales")) {

                    Intent intent = new Intent(LoginActivity.this,
                            EmpSalesDash.class);
                    startActivity(intent);
                    finish();
                } else if (emp.equalsIgnoreCase("manager")) {
                    Intent intent = new Intent(LoginActivity.this,
                            EmpManDash.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(LoginActivity.this,
                            EmpAccDash.class);
                    startActivity(intent);
                    finish();
                }

            }
        }


        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });





    }


    @Override
    public void onLocationChanged(Location location) {


        lon= String.valueOf(location.getLongitude());
        lat= String.valueOf(location.getLatitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request


        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();



        StringRequest strReq = new StringRequest(Method.POST,
                "http://suhrid1theinceptor.000webhostapp.com/userlogin/login.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
               // Log.i("tagconvertstr", "["+response+"]");
                //Log.d(TAG, "Login Response: " + response.toString());
                //Toast.makeText(getApplicationContext(),response, Toast.LENGTH_SHORT).show();

                // Toast.makeText(getApplicationContext(), SharedPreference.getInstance(getApplicationContext()).getToken(), Toast.LENGTH_LONG).show();

                // Log.d(TAG, "TOKEN Response: " + SharedPreference.getInstance(getApplicationContext()).getToken());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);


                    int error = jObj.getInt("error");;


                    //Toast.makeText(getBaseContext(), u_id, Toast.LENGTH_LONG).show();
                    // Check for error node in json
                    if (error==1) {
                         e_id = jObj.getString("eid");
                        String e_name = jObj.getString("ename");

                        //Toast.makeText(getApplicationContext(), "Successfully Logged In!", Toast.LENGTH_SHORT).show();
                        session.setLogin(true);
                        String isadmin = jObj.getString("isadmin");
                        String etype=jObj.getString("emptype");

                        db.addUser(e_id, isadmin,etype);

                        // Launch main activity
                        if (isadmin.equals("0")) {
                            sendloc();

                                if(etype.equalsIgnoreCase("Sales")){
                                    Intent intent = new Intent(LoginActivity.this,
                                            EmpSalesDash.class);
                                    startActivity(intent);
                                    finish();}
                                else if(etype.equalsIgnoreCase("Accounts")){
                                    Intent intent = new Intent(LoginActivity.this,
                                            EmpAccDash.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else if(etype.equalsIgnoreCase("Manager")){
                                        Intent intent = new Intent(LoginActivity.this,
                                                EmpManDash.class);
                                        startActivity(intent);
                                        finish();
                                }

                        } else {
                            Intent intent = new Intent(LoginActivity.this,
                                    PendingOrders.class);

                            startActivity(intent);
                            finish();
                        }

                    } else {

                        // Error in login. Get the error message
                        Toast.makeText(getApplicationContext(),"Login Failed,Try Again", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

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
                params.put("username", email);
                params.put("password", password);
                //params.put("token", SharedPreference.getInstance(getApplicationContext()).getToken());
                return params;
            }
        };

        // Adding request to request queue
         AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void sendloc(){


            String tag_string_req = "req_login";
            pDialog.setMessage("Location...");
            showDialog();
            String url = "http://suhrid1theinceptor.000webhostapp.com/userlogin/send_location_api.php";
            StringRequest strReq = new StringRequest(Request.Method.POST,
                    url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                  //  Log.i("tagconvertstr", "[" + response + "]");
                    //Log.d(TAG, " Response: " + response);
                    hideDialog();
                    //Toast.makeText(getApplicationContext(),"Location sent", Toast.LENGTH_LONG).show();
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
                    params.put("latitude", String.valueOf(lat));
                    params.put("longitude", String.valueOf(lon));
                    params.put("eid", e_id);
                    params.put("title", "LOGIN");
                    //params.put("token", SharedPreference.getInstance(getApplicationContext()).getToken());
                    return params;
                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

        }




    @Override
    public void onBackPressed() {

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
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
