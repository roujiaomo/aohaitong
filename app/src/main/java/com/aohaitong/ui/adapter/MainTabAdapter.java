package com.aohaitong.ui.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class MainTabAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments = null;

    public void setFragments(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    public MainTabAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
