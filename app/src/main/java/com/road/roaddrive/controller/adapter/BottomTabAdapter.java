package com.road.roaddrive.controller.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.road.roaddrive.ui.fragment.HomeFragment;
import com.road.roaddrive.ui.fragment.MenuFragment;
import com.road.roaddrive.ui.fragment.ProfileFragment;


public class BottomTabAdapter extends FragmentStatePagerAdapter {

    private int numOfTab;

    public BottomTabAdapter(FragmentManager fm, int numOfTab) {
        super(fm);
        this.numOfTab = numOfTab;
    }

    @Override
    public Fragment getItem(int i) {
        if (i == 0) {
            return new HomeFragment();
        } else if (i == 1) {
            return new MenuFragment();
        } else if (i == 2) {
            return new ProfileFragment();
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTab;
    }
}