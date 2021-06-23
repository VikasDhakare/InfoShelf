package com.card.infoshelf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.card.infoshelf.profileFragments.AboutFragment;
import com.card.infoshelf.profileFragments.Imagefragment;
import com.card.infoshelf.profileFragments.documentFragment;
import com.card.infoshelf.profileFragments.videoFragment;

public class myProfileTabaccessAdaptor extends FragmentPagerAdapter {

    public myProfileTabaccessAdaptor(@NonNull FragmentManager fm) {
        super(fm);
    }

    public myProfileTabaccessAdaptor(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                Imagefragment imagefragment = new Imagefragment();
                return imagefragment;
            case 1:
                videoFragment vFragment = new videoFragment();
                return vFragment;
            case 2:
                documentFragment dFragment = new documentFragment();
                return  dFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

}
