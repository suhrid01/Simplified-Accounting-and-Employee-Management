package com.suhrid.simplified.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.suhrid.simplified.R;
import com.suhrid.simplified.activity.AddEmployee;
import com.suhrid.simplified.activity.AddOrders;
//import com.suhrid.simplified.activity.AdminProfile;
import com.suhrid.simplified.activity.AssignedOrders;
import com.suhrid.simplified.activity.LoginActivity;
import com.suhrid.simplified.activity.OrderHistory;
import com.suhrid.simplified.activity.PendingOrders;
import com.suhrid.simplified.activity.ViewEmployee;
import com.suhrid.simplified.app.AppController;
import com.suhrid.simplified.helper.SQLiteHandler;
import com.suhrid.simplified.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.HashMap;
import java.util.Map;

//import pub.devrel.easypermissions.EasyPermissions;

import static com.suhrid.simplified.app.AppController.TAG;

/**
 * Created by Abhro on 23-04-2018.
 */

public class EmpProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout sDrawerLayout;
    private ActionBarDrawerToggle sToggle;
    private EditText stname;
    //private TextView stlname;
    private EditText stdob;
    private EditText stemail;
    private TextView stsec;
    private TextView stgname;
    private EditText staddress;
    private EditText stphone;
    private JSONArray jsonarray;
    private SQLiteHandler db;
    private SessionManager session;
    Button submit;
    public String u_id;
    private static final int SELECT_PICTURE = 0;
    private ImageView imageView;
    private ProgressDialog pDialog;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminprofile);
        sDrawerLayout = (DrawerLayout)findViewById(R.id.adrawerlayout);
        sToggle = new ActionBarDrawerToggle(this,sDrawerLayout,R.string.drawer_open,R.string.drawer_close);
        sDrawerLayout.addDrawerListener(sToggle);
        sToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigation = (NavigationView) findViewById(R.id.snavigation);
        navigation.setNavigationItemSelectedListener(this);




        stname= (EditText) findViewById(R.id.user_profile_name);
        stname.setEnabled(false);
        submit=(Button)findViewById(R.id.submit);
        submit.setVisibility(View.INVISIBLE);
        //stlname=(TextView)findViewById(R.id.st_lname);
        stdob=(EditText) findViewById(R.id.stdob);
        stdob.setEnabled(false);
        stemail=(EditText) findViewById(R.id.stemail);
        stemail.setEnabled(false);
        stsec=(TextView)findViewById(R.id.stsec);
        // stgname=(TextView)findViewById(R.id.stguardian);
        stphone=(EditText) findViewById(R.id.stphone);
        stphone.setEnabled(false);
        staddress=(EditText) findViewById(R.id.staddress);
        staddress.setEnabled(false);
        imageView = (ImageView) findViewById(R.id.user_profile_photo);
      /*  imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selelctImage();

            }

        });*/
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);



        db = new SQLiteHandler(getApplicationContext());
        isNetworkConnectionAvailable();
        final HashMap<String, String> user = db.getUserDetails();




        // session manager
        session = new SessionManager(getApplicationContext());
        u_id = user.get("u_id");



        /*String is_staff = user.get("is_staff");
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            if(is_staff.equals("false"))
            {
                studentprofile(u_id);
            }
            else
            {
                teacherprofile(u_id);
            }
        }*/
        studentprofile(u_id);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.help_btn)
        {
            stdob.setFocusable(true);
            stdob.setEnabled(true);
            stdob.setClickable(true);
            stdob.setFocusableInTouchMode(true);
            stdob.setLongClickable(true);

            stname.setFocusable(true);
            stname.setEnabled(true);
            stname.setClickable(true);
            stname.setFocusableInTouchMode(true);
            stname.setLongClickable(true);

            stemail.setFocusable(true);
            stemail.setEnabled(true);
            stemail.setClickable(true);
            stemail.setFocusableInTouchMode(true);
            stemail.setLongClickable(true);

            staddress.setFocusable(true);
            staddress.setEnabled(true);
            staddress.setClickable(true);
            staddress.setFocusableInTouchMode(true);
            staddress.setLongClickable(true);

            stphone.setFocusable(true);
            stphone.setEnabled(true);
            stphone.setClickable(true);
            stphone.setFocusableInTouchMode(true);
            stphone.setLongClickable(true);

            submit.setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }*/
   /* private void selelctImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = getPath(data.getData());
            imageView.setImageBitmap(bitmap);
        }
    }

    private Bitmap getPath(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(column_index);
        // cursor.close();
        // Convert file path into bitmap image using below line.
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        return bitmap;
    }*/


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


    private void studentprofile(final String u_id) {

        String tag_string_req = "req_login";
        pDialog.setMessage("Fetching Details...");
        showDialog();

        String url = "http://suhrid1theinceptor.000webhostapp.com/userlogin/employee_details_api.php";
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("tagconvertstr", "["+response+"]");
                Log.d(TAG, "Login Response: " + response);
                //Toast.makeText(getBaseContext(), response.toString(), Toast.LENGTH_LONG).show();
                hideDialog();
                try {

                    JSONObject jObj = new JSONObject(response);
                    String st_fname = jObj.getString("ename");
                    //String st_lname = jObj.getString("last_name");
                    String st_dob = jObj.getString("DOB");
                    String st_email = jObj.getString("email");
                    //String st_sec = jObj.getString("sec");
                    String st_phone = jObj.getString("phone_no");
                    //String st_gurdian = jObj.getString("g_name");
                    //String st_address = jObj.getString("address");
                    stname.setText(st_fname);
                    stemail.setText("Email: " + st_email);

                    stdob.setText("Date Of Birth: " + st_dob);
                    stphone.setText("Phone: " + st_phone);

                    staddress.setText("Address: LalBazaar,Kolkata- 700001" );

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
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("eid", u_id);
                //params.put("password", password);
                //params.put("token", SharedPreference.getInstance(getApplicationContext()).getToken());
                return params;
            }


        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (sToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (this.sDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            this.sDrawerLayout.closeDrawer(GravityCompat.START);
            new AlertDialog.Builder(this)
                    // .setIcon(R.drawable.dialog_warning)
                    .setTitle("Closing ATTENDR")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(a);

                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        } else
        {
            this.sDrawerLayout.closeDrawer(GravityCompat.START);
            new AlertDialog.Builder(this)
                    // .setIcon(R.drawable.dialog_warning)
                    .setTitle("Closing App")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(a);

                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                session.setLogin(false);

                db.deleteUsers();
                new AlertDialog.Builder(this)
                        //  .setIcon(R.drawable.dialog_warning)
                        .setTitle("Logging Out")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(EmpProfile.this, LoginActivity.class);
                                startActivity(intent);
                                finish();

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

                // Launching the login activity

                return true;

            case R.id.addordersem:

                // Launching the login activity
                Intent i = new Intent(EmpProfile.this, AddOrderSem.class);
                startActivity(i);
                return true;
            case R.id.tprofile:

                // Launching the login activity
                Intent ih = new Intent(EmpProfile.this, EmpProfile.class);
                startActivity(ih);
                return true;

            case R.id.dashboard:

                // Launching the login activity
                Intent ic = new Intent(EmpProfile.this, EmpSalesDash.class);
                startActivity(ic);
                return true;


            default:
                sDrawerLayout = (DrawerLayout) findViewById(R.id.adrawerlayout);
                sDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
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

}

