package com.example.projektdyplomowyankiety;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainMenu extends AppCompatActivity implements AdapterView.OnItemClickListener, ConectionReciver.ContectivityRecListener {
    private static final String TAG = "FRAGMENT COMPLETE SURVEY"; //extends ActionBarActivity

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private TextView tvToolbar = null;
    private ImageView imViewLogout;
    private String[] itemsText;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mLvDrawerMenu;
    private MenuDrawerAdapter mDrawerMenuAdapter;
    private SharedPreferences sharedPref;
    private String surNotName = "";
    private boolean used = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private String goToComplete;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        goToComplete = getIntent().getStringExtra("surNotName");


        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLvDrawerMenu = (ListView) findViewById(R.id.lv_drawer_menu);
        itemsText = getResources().getStringArray(R.array.strMenuItems);


        //find textView in toolbar
        for (int i = 0; i < toolbar.getChildCount(); ++i) {
            View child = toolbar.getChildAt(i);
            if (child instanceof TextView) {
                tvToolbar = (TextView) child;
            }
            if (child instanceof ImageView) {
                imViewLogout = (ImageView) child;
            }
        }

        imViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder
                        = new AlertDialog
                        .Builder(MainMenu.this);
                builder.setMessage(R.string.strlogOutQuest);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.strNo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton(R.string.strYes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //logout
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainMenu.this, EntryActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Wylogowano", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


        tvToolbar.setText(itemsText[0]);
        List<MenuDrawerItem> menuItems = generateDrawerMenuItems();
        mDrawerMenuAdapter = new MenuDrawerAdapter(getApplicationContext(), menuItems);
        mLvDrawerMenu.setAdapter(mDrawerMenuAdapter);

        mLvDrawerMenu.setOnItemClickListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            surNotName = b.getString("surNotName");
        }

        if (savedInstanceState == null) {
            setFragment(0, CalendarFragment.class);
        }

    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private List<MenuDrawerItem> generateDrawerMenuItems() {

        String[] itemsText = getResources().getStringArray(R.array.strMenuItems);
        TypedArray itemsIcon = getResources().obtainTypedArray(R.array.drawer_icons);

        List<MenuDrawerItem> result = new ArrayList<MenuDrawerItem>();

        for (int i = 0; i < itemsText.length; i++) {
            MenuDrawerItem item = new MenuDrawerItem();
            item.setText(itemsText[i]);
            item.setIcon(itemsIcon.getResourceId(i, -1));
            result.add(item);
        }

        return result;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (goToComplete != null) {
            goToComplete = null;
        }


        switch (position) {
            case 0:
                if(networkConnection())
                {
                    setFragment(0, CalendarFragment.class);
                    tvToolbar.setText(itemsText[0]);
                }
                else
                {
                    Toast.makeText(this,"Brak dostępu do internetu",Toast.LENGTH_LONG).show();
                }
                break;
            case 1:
                if(networkConnection())
                {
                setFragment(1, CreateSurveyFragment.class);
                tvToolbar.setText(itemsText[1]);
                }
                else
                {
                    Toast.makeText(this,"Brak dostępu do internetu",Toast.LENGTH_LONG).show();
                }
                break;
            case 2:
                if(networkConnection())
                {
                setFragment(2, CompleteSurveyFragment.class);
                tvToolbar.setText(itemsText[2]);
                }
                else
                {
                    Toast.makeText(this,"Brak dostępu do internetu",Toast.LENGTH_LONG).show();
                }
                break;
            case 3:
                if(networkConnection())
                {
                setFragment(3, FragmentToCSV.class);
                tvToolbar.setText(itemsText[3]);
                }
                else
                {
                    Toast.makeText(this,"Brak dostępu do internetu",Toast.LENGTH_LONG).show();
                }
                break;
            case 4:
                if(networkConnection())
                {
                setFragment(4, ManageNotificationsFragment.class);
                tvToolbar.setText(itemsText[4]);
                }
                else
                {
                    Toast.makeText(this,"Brak dostępu do internetu",Toast.LENGTH_LONG).show();
                }
                break;
            case 6:
                if(networkConnection())
                {
                setFragment(6, FragmentChangePassword.class);
                tvToolbar.setText(itemsText[6]);
                }
                else
                {
                    Toast.makeText(this,"Brak dostępu do internetu",Toast.LENGTH_LONG).show();
                }
                break;
            case 7:
                if(networkConnection())
                {
                mDrawerLayout.closeDrawer(mLvDrawerMenu);
                mLvDrawerMenu.invalidateViews();
                }
                else
                {
                    Toast.makeText(this,"Brak dostępu do internetu",Toast.LENGTH_LONG).show();
                }
                break;

        }
    }


    public void goToCalendarFragment() {

        CalendarFragment nextFrag = new CalendarFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();
        tvToolbar.setText(itemsText[0]);

    }

    public void goToCompleteSurveyFragment() {

        CompleteSurveyFragment nextFrag = new CompleteSurveyFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();
        tvToolbar.setText(itemsText[2]);
    }


    @Override
    public void onResume() {
        super.onResume();


        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.EXTRA_CAPTIVE_PORTAL);

        ConectionReciver conectionReciver = new ConectionReciver();
        registerReceiver(conectionReciver,intentFilter);

       // CalendarFragment.getInstance().setConnectivityListener(this);






        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_container);

        if (currentFragment instanceof CompleteSurveyFragment) {
            Log.v("TAG", "find the current fragment");
            setFragment(2, CompleteSurveyFragment.class);
            try {
                ((CompleteSurveyFragment) currentFragment).recyclerViewAdapter.dialogEditManager.cancelDialog();
            } catch (Exception e) {

            }
        }

        //    Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();

    }

    public boolean networkConnection()
    {
        ConnectivityManager conectivymanager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = conectivymanager.getActiveNetworkInfo();
        if (networkinfo == null) {
            return false;
        } else
            return true;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
            super.onBackPressed();
        }
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void setFragment(int position, Class<? extends Fragment> fragmentClass) {
        try {


            if (goToComplete != null) {
                position = 2;
                fragmentClass = CompleteSurveyFragment.class;
                tvToolbar.setText(itemsText[2]);
                Bundle bundle = new Bundle();
                bundle.putString("surNotName", goToComplete);


                Fragment fragment = fragmentClass.newInstance();
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment, fragmentClass.getSimpleName());
                fragmentTransaction.commit();

                mLvDrawerMenu.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mLvDrawerMenu);
                mLvDrawerMenu.invalidateViews();

            } else {

                Fragment fragment = fragmentClass.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment, fragmentClass.getSimpleName());
                fragmentTransaction.commit();

                mLvDrawerMenu.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mLvDrawerMenu);
                mLvDrawerMenu.invalidateViews();

            }

        } catch (Exception ex) {
            Log.e("setFragment", ex.getMessage());
        }
    }

    @Override
    public void onNetworkConnectionCHanged(boolean isConnected) {
        setFragment(1, CreateSurveyFragment.class);
        tvToolbar.setText(itemsText[1]);
    }
}
