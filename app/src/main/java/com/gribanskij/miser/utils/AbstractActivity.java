package com.gribanskij.miser.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.gribanskij.miser.R;

public abstract class AbstractActivity extends AppCompatActivity {

    public static final String TRANSACTION = "transaction";

    protected abstract Fragment createFragment(Bundle bundle);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.activity_fragment);
        if (fragment == null) {
            fragment = createFragment(savedInstanceState);
            fm.beginTransaction().add(R.id.activity_fragment, fragment).commit();
        }
    }
}
