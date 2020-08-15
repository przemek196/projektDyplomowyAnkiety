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
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = "FRAGMENT COMPLETE SURVEY"; //extends ActionBarActivity

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private TextView tvToolbar = null;
    private ImageView imViewLogout;
    private String[] itemsText;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mLvDrawerMenu;
    private MenuDrawerAdapter mDrawerMenuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

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

        /*
        String surveyName = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle.getParcelable("fromEdit") != null)
        {
            surveyName = bundle.getString("fromEdit");
        }

        if (!surveyName.matches("")) {
            setFragment(2, CalendarFragment.class);
        }
*/


        if (savedInstanceState == null) {
            setFragment(0, CalendarFragment.class);
        }


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
        switch (position) {
            case 0:
                setFragment(0, CalendarFragment.class);
                tvToolbar.setText(itemsText[0]);
                break;
            case 1:
                setFragment(1, CreateSurveyFragment.class);
                tvToolbar.setText(itemsText[1]);
                break;
            case 2:
                setFragment(2, CompleteSurveyFragment.class);
                tvToolbar.setText(itemsText[2]);
                break;
            case 3:
                setFragment(3, FragmentToCSV.class);
                tvToolbar.setText(itemsText[3]);
                break;
            case 4:
                setFragment(3, ManageNotificationsFragment.class);
                tvToolbar.setText(itemsText[4]);
                break;
            case 5:
                mDrawerLayout.closeDrawer(mLvDrawerMenu);
                mLvDrawerMenu.invalidateViews();
                break;
        }
    }

    public void goToCalendarFragment() {

        CalendarFragment nextFrag = new CalendarFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();

    }

    public void goToCompleteSurveyFragment() {

        CompleteSurveyFragment nextFrag = new CompleteSurveyFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();

    }


    @Override
    public void onResume() {
        super.onResume();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_container);

        if (currentFragment instanceof CompleteSurveyFragment) {
            Log.v("TAG", "find the current fragment");
            setFragment(2, CompleteSurveyFragment.class);
            try {
                ((CompleteSurveyFragment) currentFragment).recyclerViewAdapter.dialogEditManager.cancelDialog();
            } catch (Exception e) {

            }

/*
            synchronized (((CompleteSurveyFragment) currentFragment).recyclerViewAdapter)
            {
                ((CompleteSurveyFragment) currentFragment).recyclerViewAdapter.notify();
            }
*/
        }

        //    Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();

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
            Fragment fragment = fragmentClass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment, fragmentClass.getSimpleName());
            fragmentTransaction.commit();

            mLvDrawerMenu.setItemChecked(position, true);
            mDrawerLayout.closeDrawer(mLvDrawerMenu);
            mLvDrawerMenu.invalidateViews();
        } catch (Exception ex) {
            Log.e("setFragment", ex.getMessage());
        }
    }

}
