package com.card.infoshelf.Requests;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.card.infoshelf.UserProfileFragments.UserAboutFragment;
import com.card.infoshelf.UserProfileFragments.UserDocumentFragment;
import com.card.infoshelf.UserProfileFragments.UserImagefragment;
import com.card.infoshelf.UserProfileFragments.UserVideoFragment;
import com.card.infoshelf.profileFragments.AboutFragment;
import com.card.infoshelf.profileFragments.Imagefragment;
import com.card.infoshelf.profileFragments.documentFragment;
import com.card.infoshelf.profileFragments.videoFragment;

public class userProfileTabAccessAdaptor extends FragmentPagerAdapter {

    public userProfileTabAccessAdaptor(@NonNull FragmentManager fm) {
        super(fm);
    }

    public userProfileTabAccessAdaptor(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                Imagefragment imagefragment = new UserImagefragment();
                return imagefragment;
            case 1:
                videoFragment vFragment = new UserVideoFragment();
                return vFragment;
            case 2:
                documentFragment dFragment = new UserDocumentFragment();
                return  dFragment;
            case 3:
                AboutFragment aboutFragment = new UserAboutFragment();
                return aboutFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 4;
    }
}
