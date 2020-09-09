package com.example.projektdyplomowyankiety;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EntryActivity extends AppCompatActivity {

    private SectionPagerAdapter mSectionPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.containterViewPager);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent i = new Intent(EntryActivity.this, MainMenu.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }



        setupViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager vwPager) {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FragmentLoginActivity(), "FragmentLoginActivity");
       adapter.addFragment(new FragmentRegisterActivity(), "FragmentRegisterActivity");
        vwPager.setAdapter(adapter);
    }

    public void setViewPager(int fragNumber) {
        mViewPager.setCurrentItem(fragNumber);
    }
}
