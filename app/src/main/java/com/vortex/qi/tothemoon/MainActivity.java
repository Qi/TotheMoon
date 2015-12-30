package com.vortex.qi.tothemoon;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public FloatingActionButton fab;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Fragment fragment;
        FragmentTransaction ft;
        fragment = new dcb_fragment();
        ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment).commit();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment;
        FragmentTransaction ft;
        int id = item.getItemId();

        if (id == R.id.nav_dcb) {
            fragment = new dcb_fragment();
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();

        } else if (id == R.id.nav_bl) {
            fragment = new sbl_fragment();
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();

        } else if (id == R.id.nav_ss) {

        } else if (id == R.id.nav_sh) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_support) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String generateNum(int upper, int number, boolean unique){
        String selectedNums = "";
        Random rand = new Random();
        int[] num = new int[number];
        int temp;
        int count = 0;
        NumberFormat formatter = new DecimalFormat("00");
        if(unique){
            Boolean notExist=true;
            while(count<num.length){
                temp = rand.nextInt(upper-1)+1;
                for(int j=0;j<count;j++){
                    if(num[j]==temp){
                        notExist = false;
                        break;
                    }
                    notExist = true;
                }
                if(notExist) {
                    num[count] = temp;
                    count++;
                }
            }
            Arrays.sort(num);
            for (int aNum : num) {
                selectedNums = selectedNums + formatter.format(aNum) + " ";
            }
        }else{
            for(int i=0;i<number;i++){
                num[i] = rand.nextInt(upper-1)+1;
            }
            Arrays.sort(num);
            for (int aNum : num) {
                selectedNums = selectedNums + formatter.format(aNum) + " ";
            }
        }
        return selectedNums;
    }

}
