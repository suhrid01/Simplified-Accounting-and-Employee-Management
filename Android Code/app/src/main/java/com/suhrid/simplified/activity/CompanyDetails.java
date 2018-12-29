package com.suhrid.simplified.activity;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//import pub.devrel.easypermissions.EasyPermissions;

import static com.suhrid.simplified.app.AppController.TAG;

/**
 * Created by Abhro on 23-04-2018.
 */

public class CompanyDetails extends AppCompatActivity implements LocationListener, AdapterView.OnItemSelectedListener {
    private TextView stname;
    //private TextView stlname;
    private TextView stdob;
    private TextView stemail;
    private EditText stsec,edbank,edch,edifsc,edbnkno;
    private TextView stgname;
    private TextView staddress;
    private TextView stphone;
    private JSONArray jsonarray;
    private SQLiteHandler db;
    private SessionManager session;
    private String u_id,amount,order_desc,cname;
    private static final int SELECT_PICTURE = 0;
    private ImageView imageView;
    private ProgressDialog pDialog;
    LocationManager locationManager;
    String mprovider,e_id;
    String lat, lon;
    public Spinner payspin;
    public Button btn,btn1;
    private ArrayList<String> pay;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companydetails);
        Bundle bundle = getIntent().getExtras();

        //Extract the data…
        u_id = bundle.getString("USER_ID");
        amount = bundle.getString("USER_AMOUNT");
        order_desc= bundle.getString("USER_DESCRIPTION");
        cname= bundle.getString("USER_NAME");



        stname= (TextView) findViewById(R.id.user_profile_name);
        //stlname=(TextView)findViewById(R.id.st_lname);
        //stdob=(TextView)findViewById(R.id.stdob);
        stemail=(TextView)findViewById(R.id.stemail);
        stsec=(EditText) findViewById(R.id.stsec);
        edbank=(EditText) findViewById(R.id.bank);
        edch=(EditText) findViewById(R.id.cheque);
        edifsc=(EditText)findViewById(R.id.ifsc);
        edbnkno=(EditText)findViewById(R.id.bankacc);
        btn=(Button)findViewById(R.id.place_order);
        btn1=(Button)findViewById(R.id.place_order2);
        //stgname=(TextView)findViewById(R.id.stgurdian);
        stphone=(TextView)findViewById(R.id.stphone);
        staddress=(TextView)findViewById(R.id.staddress);
        imageView = (ImageView) findViewById(R.id.user_profile_photo);
       imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selelctImage();

            }

        });
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        payspin = (Spinner) findViewById(R.id.payment);
        payspin.setOnItemSelectedListener(this);
        pay = new ArrayList<String>();
        pay.add("Mode of payment");
        pay.add("Cash");
        pay.add("Cheque");

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(CompanyDetails.this,
                android.R.layout.simple_spinner_dropdown_item, pay) {
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

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        payspin.setAdapter(adapter2);
        //Initializing Spinner


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        mprovider = locationManager.getBestProvider(criteria, false);

        if (mprovider != null && !mprovider.equals("")) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(mprovider);
            locationManager.requestLocationUpdates(mprovider, 15000, 1, this);

            if (location != null)
                onLocationChanged(location);
            else
                Toast.makeText(getBaseContext(), "No Location Provider Found Check Your Code", Toast.LENGTH_SHORT).show();
        }



        db = new SQLiteHandler(getApplicationContext());
        isNetworkConnectionAvailable();
        final HashMap<String, String> user = db.getUserDetails();




        // session manager
        session = new SessionManager(getApplicationContext());
         e_id = user.get("u_id");



        studentprofile(u_id);
        edbnkno.setVisibility(View.INVISIBLE);
        edifsc.setVisibility(View.INVISIBLE);
        edch.setVisibility(View.INVISIBLE);
        edbank.setVisibility(View.INVISIBLE);
        stsec.setVisibility(View.INVISIBLE);
        btn.setVisibility(View.INVISIBLE);
        btn1.setVisibility(View.INVISIBLE);
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


    public void onclick2(View view) {

        String amount=stsec.getText().toString().trim();

        completeorder(u_id,amount);

    }

    private void completeorder(final String uid,final String amt) {
        String tag_string_req = "req_login";
        pDialog.setMessage("Submitting...");
        showDialog();
        String url = "http://suhrid1theinceptor.000webhostapp.com/userlogin/completeorder_api.php";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("tagconvertstr", "[" + response + "]");
                Log.d(TAG, "Loginnnnnnnnnnnnnnn Response: " + response);
                hideDialog();
                Toast.makeText(getApplicationContext(),
                        "Submitted successfully. Invoice will be generated soon", Toast.LENGTH_LONG).show();
                Intent i = new Intent(CompanyDetails.this, EmpSalesDash.class);
                startActivity(i);
                finish();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Done!", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();

                params.put("cid", uid);
                params.put("amount",amt);
                params.put("eid",e_id);
                params.put("latitude","00");
                params.put("longitude","00");
                params.put("title",cname+" for "+order_desc);
                //params.put("token", SharedPreference.getInstance(getApplicationContext()).getToken());
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void selelctImage() {
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
        //cursor.close();
        // Convert file path into bitmap image using below line.
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        return bitmap;
    }


    private void studentprofile(final String u_id) {

        String tag_string_req = "req_login";
        pDialog.setMessage("Fetching Details...");
        showDialog();

        String url = "http://suhrid1theinceptor.000webhostapp.com/userlogin/customer_details_api.php";
        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.i("tagconvertstr", "["+response+"]");
                Log.d(TAG, "Login Response: " + response);
                //Toast.makeText(getBaseContext(), response.toString(), Toast.LENGTH_LONG).show();
                hideDialog();
                try {

                    JSONObject jObj = new JSONObject(response);
                    String st_fname = jObj.getString("customername");
                    //String st_lname = jObj.getString("last_name");
                    //String st_dob = jObj.getString("DOB");
                    //String st_email = jObj.getString("email");
                    String st_sec = jObj.getString("phone_no");
                    //String st_phone = jObj.getString("amount");
                    //String st_gurdian = jObj.getString("g_name");
                    //String st_address = jObj.getString("address");
                    stname.setText(st_fname);
                    stemail.setText("Phone Number: " + st_sec);

                    stphone.setText("Amount: " + amount);
                    //stdob.setText("Phone: 9587255" );

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
                params.put("cid", u_id);
                //params.put("password", password);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        /*LinearLayout.LayoutParams layoutParams=(LinearLayout.LayoutParams)btn.getLayoutParams();
        layoutParams.setMargins(0,0,0,200);*/
        if(position==1){
            stsec.setVisibility(View.VISIBLE);
            edch.setVisibility(View.INVISIBLE);
            edifsc.setVisibility(View.INVISIBLE);
            edbnkno.setVisibility(View.INVISIBLE);
            edbank.setVisibility(View.INVISIBLE);
            btn1.setVisibility(View.VISIBLE);
            btn.setVisibility(View.INVISIBLE);
            //btn.setLayoutParams(layoutParams);


        }
        else if (position ==2){
            edifsc.setVisibility(View.VISIBLE);
            edbnkno.setVisibility(View.VISIBLE);
            edch.setVisibility(View.VISIBLE);
            edbank.setVisibility(View.VISIBLE);
            stsec.setVisibility(View.INVISIBLE);
            btn.setVisibility(View.VISIBLE);
            btn1.setVisibility(View.INVISIBLE);
        }
        else{
            edch.setVisibility(View.INVISIBLE);
            edbnkno.setVisibility(View.INVISIBLE);
            edifsc.setVisibility(View.INVISIBLE);
            edbank.setVisibility(View.INVISIBLE);
            stsec.setVisibility(View.INVISIBLE);
            btn.setVisibility(View.INVISIBLE);
            btn1.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
