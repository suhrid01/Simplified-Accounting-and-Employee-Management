package com.suhrid.simplified.activity;

import android.Manifest;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.suhrid.simplified.R;
import com.suhrid.simplified.app.AppController;
import com.suhrid.simplified.helper.SQLiteHandler;
import com.suhrid.simplified.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.suhrid.simplified.app.AppController.TAG;

public class MapsActivity extends BaseActivity implements  OnMapReadyCallback,LocationListener {


    EditText eid;
    GoogleMap googlemap;
    //MapFragment mapFragment;
    Button gps;
    private DrawerLayout tDrawerLayout;
    private ActionBarDrawerToggle tToggle;
    private SQLiteHandler db;
    private SessionManager session;
    private List<String> data = new ArrayList<String>();
    private ProgressDialog pDialog;
    private int i;
    Double lat1,long1;
    private ImageView tool;
    private RecyclerView recyclerView1;
    private JSONArray data1;
    private TextView fname;
    LocationManager locationManager;
    String mprovider,e_id;
    Double lat11, lon11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentOfView(R.layout.activity_maps,this);

        tDrawerLayout = (DrawerLayout) findViewById(R.id.adrawerlayout);
        tToggle = new ActionBarDrawerToggle(this, tDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        tDrawerLayout.addDrawerListener(tToggle);
        tToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionManager(getApplicationContext());

        NavigationView navigation = (NavigationView) findViewById(R.id.snavigation);
        navigation.setNavigationItemSelectedListener(this);
        db = new SQLiteHandler(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        gps = (Button) findViewById(R.id.btngps);

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


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        eid = (EditText) findViewById(R.id.eid);

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e_id = eid.getText().toString().trim();
                gpsset(e_id);
            }
        });
    }


    @Override
    public void onLocationChanged(Location location) {


        lon11= location.getLongitude();
        lat11= location.getLatitude();

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


    public void gpsset(final String e_id) {
        String tag_string_req = "req_login";
        pDialog.setMessage("Fetching GPS History...");
        showDialog();
        data.clear();
        StringRequest strReq = new StringRequest(Request.Method.POST,"http://suhrid1theinceptor.000webhostapp.com/userlogin/get_location_api.php", new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                //  inputSearch.setVisibility(View.VISIBLE);
                //tool.setVisibility(View.VISIBLE);
                Log.i("tagconvertstr", "[" + response + "]");
                hideDialog();
                Log.d(TAG, "Attend Response: " + response);
                try {


                    JSONObject jObj = new JSONObject(response);

                    data1 = jObj.getJSONArray("respond");

                    for (i = 0; i < data1.length(); i++) {
                        try {
                            JSONObject json = data1.getJSONObject(i);
                            String lon = json.getString("longitude");
                            String lat = json.getString("latitude");
                            String title = json.getString("title");
                            //data.add(lat);
                            long1 = Double.parseDouble(lon);
                            lat1 = Double.parseDouble(lat);
                            LatLng placelocation=new LatLng(lat1,long1);
                            Marker placemarker = googlemap.addMarker(new MarkerOptions().position(placelocation).title(title));
                            googlemap.moveCamera(CameraUpdateFactory.newLatLng(placelocation));
                            googlemap.animateCamera(CameraUpdateFactory.zoomTo(10),5000,null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //MapsActivity.ProductAdapter adapter = new MapsActivity.ProductAdapter(MapsActivity.this, data);


                    //recyclerView1.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                //Log.e(TAG, "Login Err: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Fill all the details", Toast.LENGTH_LONG).show();

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();

                params.put("eid", e_id);
               // params.put("date", date);
                // params.put("h_date", date);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void checkNetworkConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    public boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if (isConnected) {
            Log.d("Network", "Connected");
            return true;
        } else {
            checkNetworkConnection();
            Log.d("Network", "Not Connected");
            return false;
        }
    }

    private String getid(int position) {
        String id = "";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            id = json.getString("eid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return id;
    }

    private String getname(int position) {
        String name = "";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            name = json.getString("order_desc");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return name;
    }

    private String getamount(int position) {
        String am = "";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            am = json.getString("amount");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return am;
    }

    private String getemp_id(int position) {
        String eid = "";
        try {
            //Getting object of given index
            JSONObject json = data1.getJSONObject(position);

            //Fetching name from that object
            eid = json.getString("eid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return eid;
    }


    public class ProductAdapter extends RecyclerView.Adapter<MapsActivity.ProductAdapter.ProductViewHolder> {


        //this context we will use to inflate the layout
        private Context mCtx;

        //we are storing all the products in a list
        private List<String> productList;

        //getting the context and product list with constructor
        public ProductAdapter(Context mCtx, List<String> productList) {
            //super(context, resource, objects);
            this.mCtx = mCtx;
            this.productList = productList;
        }


        @Override
        public MapsActivity.ProductAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //inflating and returning our view holder
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            View view = inflater.inflate(R.layout.list_item, null);
            return new MapsActivity.ProductAdapter.ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MapsActivity.ProductAdapter.ProductViewHolder holder, final int position) {
            //getting the product of the specified position
            //Product product = productList.get(position);


            //binding the data with the viewholder views
            SpannableString content = new SpannableString("\u2022 " + getname(position));
            content.setSpan(new UnderlineSpan(), 2, content.length(), 0);

            holder.textViewTitle.setText(
                    content);
            holder.textViewShortDesc.setText("Employee Collected: "+getemp_id(position));
            // holder.textViewRating.setText(String.valueOf(product.getRating()));
            holder.textViewPrice.setText("Amount: "+ getamount(position));
            holder.textViewTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "Invoice will be generated. Coming Soon", Toast.LENGTH_LONG).show();

                    Intent i = new Intent(MapsActivity.this, CompanyDetails.class);
                    Bundle bundle = new Bundle();
                    //Add your data from getFactualResults method to bundle
                    bundle.putString("USER_ID", getid(position));
                    //bundle.putString("USER_AMOUNT", getamount(position));
                    //Add the bundle to the intent
                    i.putExtras(bundle);

                    //Fire the second activity
                    startActivity(i);
                }
            });
            //holder.imageView.setImageDrawable(;

        }


        @Override
        public int getItemCount() {
            return productList.size();
        }


        class ProductViewHolder extends RecyclerView.ViewHolder {

            TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice;
            ImageView imageView;

            public ProductViewHolder(View itemView) {
                super(itemView);

                textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
                textViewShortDesc = (TextView) itemView.findViewById(R.id.textViewShortDesc);
                //textViewRating = itemView.findViewById(R.id.textViewRating);
                textViewPrice = (TextView) itemView.findViewById(R.id.textViewPrice);
                // imageView = (ImageView)itemView.findViewById(R.id.imageView);
            }
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

    @Override
    public void onMapReady(GoogleMap gMap) {

        googlemap = gMap;
        googlemap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        try {
            googlemap.setMyLocationEnabled(true);
        } catch (SecurityException se) {

        }

        googlemap.setBuildingsEnabled(true);
        googlemap.getUiSettings().setZoomControlsEnabled(true);
       // LatLng placelocation=new LatLng(lat11,lon11);
        //Marker placemarker = googlemap.addMarker(new MarkerOptions().position(placelocation).title("HI ADMIN"));
       // googlemap.moveCamera(CameraUpdateFactory.newLatLng(placelocation));
       // googlemap.animateCamera(CameraUpdateFactory.zoomTo(10),1000,null);

    }
}






