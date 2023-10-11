package com.example.kouizine;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LoginSwicther extends FragmentPagerAdapter {
    private Context context ;
    private static int totaltabs=2;

    public LoginSwicther(FragmentManager fm, Context context , int totaltabs){
        super(fm);
        this.context=context;
        this.totaltabs=totaltabs;
    }

    @Override
    public int getCount() {
        return totaltabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                Logintabfrag logintabfrag = new Logintabfrag();
                return logintabfrag;
            case 1:
                Signuptabfrag signuptabfrag = new Signuptabfrag();
                return signuptabfrag;
            default:
                return null;
        }
    }
}
