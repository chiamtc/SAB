package com.example.sab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

public class TabPagerAdapater extends FragmentPagerAdapter {
    public TabPagerAdapater(FragmentManager fm) {
    super(fm);
    // TODO Auto-generated constructor stub
  }
  @Override
  public Fragment getItem(int i) {
    switch (i) {
        case 0:
            //Fragement for Android Tab
            return new ChildrenSchedules();
        case 1:
           //Fragment for Ios Tab
            return new AbsenceSubmission();
        case 2:
        	return new ViewGrades();
        }
    return null;
  }
  @Override
  public int getCount() {
    // TODO Auto-generated method stub
    return 3; //No of Tabs
  }
    }
