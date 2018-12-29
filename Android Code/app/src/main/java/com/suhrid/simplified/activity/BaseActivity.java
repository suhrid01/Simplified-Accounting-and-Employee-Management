package com.suhrid.simplified.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.suhrid.simplified.R;
import com.suhrid.simplified.helper.SQLiteHandler;
import com.suhrid.simplified.helper.SessionManager;

import java.util.HashMap;

public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private FrameLayout fl_content;
    private Activity baseActivity;
    private DrawerLayout sDrawerLayout;
    private ActionBarDrawerToggle sToggle;
    NavigationView navigation;
    public String u_id;
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        initHeader();

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
        u_id = user.get("u_id");

        navigation= (NavigationView) findViewById(R.id.snavigation);
        navigation.setNavigationItemSelectedListener(this);
    }

    private void initHeader() {
        fl_content=(FrameLayout) findViewById(R.id.fl_content);
    }


    public void setContentOfView(int layout, Activity activity) {
        baseActivity=activity;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fl_content.addView(inflater.inflate(layout, null), new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        menuHightlight();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(sToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void menuHightlight(){
        if (baseActivity instanceof AddCompany) {
            // Launching the login activity
            navigation.setCheckedItem(R.id.addclient);

        }
         else if (baseActivity instanceof AddEmployee){
                // Launching the login activity
            navigation.setCheckedItem(R.id.addemployee);
        }
        else if (baseActivity instanceof AddOrders){
            // Launching the login activity
            navigation.setCheckedItem(R.id.addorder);
        }
        else if (baseActivity instanceof AdminProfile){
            // Launching the login activity
            navigation.setCheckedItem(R.id.tprofile);
        }
        else if (baseActivity instanceof AssignedOrders){
            // Launching the login activity
            navigation.setCheckedItem(R.id.assignorder);
        }
        else if (baseActivity instanceof MapsActivity){
            // Launching the login activity
            navigation.setCheckedItem(R.id.gpsinfo);
        }
        else if (baseActivity instanceof OrderHistory){
            // Launching the login activity
            navigation.setCheckedItem(R.id.orderhistory);
        }
        else if (baseActivity instanceof PendingOrders){
            // Launching the login activity
            //navigation.setCheckedItem(R.id.pendingorder);
            navigation.setCheckedItem(R.id.dashboard);
        }
        else if (baseActivity instanceof RemoveCompany){
            // Launching the login activity
            navigation.setCheckedItem(R.id.remclient);
        }
        else if (baseActivity instanceof RemoveEmployee){
            // Launching the login activity
            navigation.setCheckedItem(R.id.rememployee);
        }
        else if (baseActivity instanceof ViewCustomer){
            // Launching the login activity
            navigation.setCheckedItem(R.id.viewclient);
        }
        else if (baseActivity instanceof ViewEmployee){
            // Launching the login activity
            navigation.setCheckedItem(R.id.viewemployee);
        }

    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        item.setChecked(true);

        switch(item.getItemId())
        {
            case R.id.logout:

               session.setLogin(false);

                db.deleteUsers();
                new android.support.v7.app.AlertDialog.Builder(this)
                        //  .setIcon(R.drawable.dialog_warning)
                        .setTitle("Logging Out")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();

                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;

            case R.id.tprofile:

                // Launching the login activity
                Intent ipr = new Intent(BaseActivity.this, AdminProfile.class);
                startActivity(ipr);
                return true;

            case R.id.addemployee:
                // Launching the login activity
                Intent i = new Intent(BaseActivity.this, AddEmployee.class);
                startActivity(i);
                return true;
            case R.id.viewemployee:

                // Launching the login activity
                Intent ih = new Intent(BaseActivity.this, ViewEmployee.class);
                startActivity(ih);
                return true;

            case R.id.assignorder:

                // Launching the login activity
                Intent ic = new Intent(BaseActivity.this, AssignedOrders.class);
                startActivity(ic);
                return true;


            case R.id.pendingorder:
                // Launching the login activity
                Intent ip = new Intent(BaseActivity.this, PendingOrders.class);
                startActivity(ip);
                return true;

            case R.id.gpsinfo:
                // Launching the login activity
                Intent ig = new Intent(BaseActivity.this, MapsActivity.class);
                startActivity(ig);
                return true;

            case R.id.orderhistory:
                // Launching the login activity
                Intent io = new Intent(BaseActivity.this, OrderHistory.class);
                startActivity(io);
                return true;

            case R.id.dashboard:
                // Launching the login activity
                Intent id = new Intent(BaseActivity.this, PendingOrders.class);
                startActivity(id);
                return true;

            case R.id.addorder:
                // Launching the login activity
                Intent iadd = new Intent(BaseActivity.this, AddOrders.class);
                startActivity(iadd);
                return true;

            case R.id.rememployee:
                // Launching the login activity
                Intent irem = new Intent(BaseActivity.this, RemoveEmployee.class);
                startActivity(irem);
                return true;


          /*  case R.id.addorder2:
                // Launching the login activity
                Intent ia = new Intent(BaseActivity.this, AddOrders2.class);
                startActivity(ia);
                return true;*/

            case R.id.addclient:
                // Launching the login activity

                Intent icl = new Intent(BaseActivity.this, AddCompany.class);
                startActivity(icl);
                return true;

            case R.id.remclient:
                // Launching the login activity
                Intent irc = new Intent(BaseActivity.this, RemoveCompany.class);
                startActivity(irc);
                return true;

            case R.id.viewclient:
                // Launching the login activity
                Intent iv = new Intent(BaseActivity.this, ViewCustomer.class);
                startActivity(iv);
                return true;



            default:
                sDrawerLayout =(DrawerLayout)findViewById(R.id.adrawerlayout);
                sDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
        }

    }




    @Override
    public void onBackPressed() {

        if (this.sDrawerLayout.isDrawerOpen(GravityCompat.START)) {
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
        } else {
            this.sDrawerLayout.closeDrawer(GravityCompat.START);
            new AlertDialog.Builder(this)
                    //.setIcon(R.drawable.dialog_warning)
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


}

