package com.example.projektdyplomowyankiety;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class EntryActivity extends AppCompatActivity {

    private SectionPagerAdapter mSectionPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.containterViewPager);

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
