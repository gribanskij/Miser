package com.gribanskij.miser.dashboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.gribanskij.miser.utils.AbstractActivity;

public class NameActivity extends AbstractActivity {

    @Override
    public Fragment createFragment(Bundle bundle) {
        return NameFragment.newInstance(null, null);
    }
}
