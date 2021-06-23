package com.card.infoshelf.Requests;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.card.infoshelf.profileFragments.Imagefragment;
import com.card.infoshelf.profileFragments.videoFragment;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;

public class RequestsTabAccessAdapter extends FragmentPagerAdapter {
    public RequestsTabAccessAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public RequestsTabAccessAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                SentRequestFragment sentRequestFragment = new SentRequestFragment();
                return sentRequestFragment;
            case 1:
                ReceiveRequestFragment receiveRequestFragment = new ReceiveRequestFragment();
                return receiveRequestFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {




        switch (position)
        {
            case 0:
                return "Sent";

            case 1:
                return "Received";

            default:
                return null;
        }
    }
}
