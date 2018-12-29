package com.suhrid.simplified.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.suhrid.simplified.R;
import com.suhrid.simplified.app.AppConfig;
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

public class AddOrders2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout tDrawerLayout;
    private ActionBarDrawerToggle tToggle;
    private SQLiteHandler db;
    private SessionManager session;
    private List<String> data = new ArrayList<String>();
    private List<String> databill = new ArrayList<String>();
    private ProgressDialog pDialog;
    private int i;
    private ImageView tool;
    private RecyclerView recyclerView1;
    private JSONArray data1;
    private EditText inputsearch,edit_quantity;
    private TextView fname;

    private int c;

    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_orders2);

        tDrawerLayout = (DrawerLayout) findViewById(R.id.adrawerlayout);
        tToggle = new ActionBarDrawerToggle(this, tDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        tDrawerLayout.addDrawerListener(tToggle);
        tToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        session = new SessionManager(getApplicationContext());
        inputsearch=(EditText)findViewById(R.id.search);
        NavigationView navigation = (NavigationView) findViewById(R.id.snavigation);
        navigation.setNavigationItemSelectedListener(this);
        db = new SQLiteHandler(getApplicationContext());
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        recyclerView1 = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));

        getdeatils();



        inputsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                //adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // TODO Auto-generated method stub
                filter(editable.toString());
            }
        });
    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<String> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (String s : data) {
            //if the existing elements contains the search input
            if (s.toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(filterdNames);


    }

   // FETCHING PRODUCT LIST FROM API

    private void getdeatils() {



        String tag_string_req = "req_login";
        pDialog.setMessage("Fetching Order History...");
        showDialog();
        data.clear();
        StringRequest strReq = new StringRequest(Request.Method.GET,"http://simplifiedsolutions.in/test-billing/api/products", new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                //  inputSearch.setVisibility(View.VISIBLE);
                //tool.setVisibility(View.VISIBLE);
                Log.i("tagconvertstr", "[" + response + "]");
                hideDialog();
                Log.d(TAG, "Attend Response: " + response);
                try {


                    JSONObject jObj = new JSONObject(response);

                    data1 = jObj.getJSONArray("data");
                    for (i = 0; i < data1.length(); i++) {
                        try {
                            JSONObject json = data1.getJSONObject(i);
                            String name = json.getString("name");
                            data.add(name);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                     adapter = new ProductAdapter(AddOrders2.this, data);


                    recyclerView1.setAdapter(adapter);
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
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    public void checkNetworkConnection () {
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

    public boolean isNetworkConnectionAvailable () {
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


    private int getsellingprice(int position) {
        int desc =0;

        try {
            //Getting json object
            //JSONObject json = data1.getJSONObject(i);

            JSONObject json1 = data1.getJSONObject(position);

            desc = json1.getInt("sell_price");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return desc;
    }

    private String getdesc(int position) {
        String desc = "";

        try {
            //Getting json object
            //JSONObject json = data1.getJSONObject(i);

            JSONObject json1 = data1.getJSONObject(position);

            desc = json1.getString("name");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return desc;
    }

    private String getid(int position) {
        String desc = "";

        try {
            //Getting json object
            //JSONObject json = data1.getJSONObject(i);

            JSONObject json1 = data1.getJSONObject(position);

            desc = json1.getString("id");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return desc;
    }

    private String getquantity(int position) {
        String desc = "";

        try {
            //Getting json object
            //JSONObject json = data1.getJSONObject(i);

            JSONObject json1 = data1.getJSONObject(position);

            desc = json1.getString("quantity");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return desc;
    }



    public class ProductAdapter extends RecyclerView.Adapter<AddOrders2.ProductAdapter.ProductViewHolder> {


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
        public AddOrders2.ProductAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //inflating and returning our view holder
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            View view = inflater.inflate(R.layout.list_row, null);
            return new AddOrders2.ProductAdapter.ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final AddOrders2.ProductAdapter.ProductViewHolder holder, final int position) {
            //getting the product of the specified position
            //Product product = productList.get(position);


            //binding the data with the viewholder views
            SpannableString content = new SpannableString("\u2022 " + getdesc(position));
            content.setSpan(new UnderlineSpan(), 2, content.length(), 0);
            holder.btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    c++;

                    holder.edit_quantity.setText("" + c);
                    String am = String.valueOf(c * getsellingprice(position));
                    holder.textViewShortDesc.setText("Amount: " + am);
                }
            });
            holder.btn_sub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    c--;
                    holder.edit_quantity.setText("" + c);
                    String am = String.valueOf(c * getsellingprice(position));
                    holder.textViewShortDesc.setText("Amount: " + am);
                }
            });

            holder.checkBox.setOnCheckedChangeListener(null);
            holder.textViewTitle.setText(
                    content);
            holder.textViewShortDesc.setText("Amount: " + getsellingprice(position) * c);
            // holder.textViewRating.setText(String.valueOf(product.getRating()));
            holder.textViewPrice.setText("Available: " + getquantity(position));
            holder.textViewTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "Invoice will be generated. Coming Soon", Toast.LENGTH_LONG).show();

                }
            });

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        databill.add(getdesc(position));
                        Toast.makeText(getApplicationContext(), getdesc(position), Toast.LENGTH_LONG).show();
                    }
                }
            });
            //holder.imageView.setImageDrawable(;

        }


        @Override
        public int getItemCount() {
            return productList.size();
        }

        public void filterList(ArrayList<String> filterdNames) {
            this.productList = filterdNames;
            notifyDataSetChanged();
        }


        class ProductViewHolder extends RecyclerView.ViewHolder {

            TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice;
            Button btn_add, btn_sub;
            EditText edit_quantity;
            ImageView imageView;
            CheckBox checkBox;

            public ProductViewHolder(View itemView) {
                super(itemView);

                textViewTitle = (TextView) itemView.findViewById(R.id.from_name);
                textViewShortDesc = (TextView) itemView.findViewById(R.id.plist_price_text);
                //textViewRating = itemView.findViewById(R.id.textViewRating);
                btn_add = (Button) itemView.findViewById(R.id.plus);
                btn_sub = (Button) itemView.findViewById(R.id.minus);
                checkBox = (CheckBox) itemView.findViewById(R.id.check);
                edit_quantity = (EditText) itemView.findViewById(R.id.id_quantity);
                textViewPrice = (TextView) itemView.findViewById(R.id.plist_weight_text);
                // imageView = (ImageView)itemView.findViewById(R.id.imageView);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (tToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                                Intent intent = new Intent(AddOrders2.this, LoginActivity.class);
                                startActivity(intent);
                                finish();

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

                // Launching the login activity

                return true;


            case R.id.addorder:
                // Launching the login activity
                Intent i = new Intent(AddOrders2.this, AddOrders.class);
                startActivity(i);
                return true;
            case R.id.assignorder:

                // Launching the login activity
                Intent ih = new Intent(AddOrders2.this, AssignedOrders.class);
                startActivity(ih);
                return true;

            case R.id.dashboard:
                // Launching the login activity
                Intent id = new Intent(AddOrders2.this, AdminDashboard.class);
                startActivity(id);
                return true;

            case R.id.pendingorder:

                // Launching the login activity
                Intent ic = new Intent(AddOrders2.this, PendingOrders.class);
                startActivity(ic);
                return true;
            case R.id.rememployee:
                // Launching the login activity
                Intent irem = new Intent(AddOrders2.this, RemoveEmployee.class);
                startActivity(irem);
                return true;


            /*case R.id.addorder2:
                // Launching the login activity
                Intent ip = new Intent(AddOrders2.this, AddOrders2.class);
                startActivity(ip);
                return true;*/



            case R.id.orderhistory:
                // Launching the login activity
                Intent io = new Intent(AddOrders2.this, OrderHistory.class);
                startActivity(io);
                return true;

            default:
                tDrawerLayout =(DrawerLayout)findViewById(R.id.adrawerlayout);
                tDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
        }

    }
    @Override
    public void onBackPressed() {
        if (this.tDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.tDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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




