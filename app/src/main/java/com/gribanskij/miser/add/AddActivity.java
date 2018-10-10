package com.gribanskij.miser.add;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.gribanskij.miser.utils.AbstractActivity;
import com.gribanskij.miser.utils.TimeUtils;

public class AddActivity extends AbstractActivity {

    @Override
    public Fragment createFragment(Bundle bundle) {
        Intent intent = getIntent();
        if (intent != null) {
            int mType = intent.getIntExtra(TimeUtils.TYPE, TimeUtils.TYPE_COST);
            int mCategory = intent.getIntExtra(TimeUtils.CATEGORY, TimeUtils.DEFAULT_CATEGORY);
            return AddFragment.newInstance(mType, mCategory);
        }
        return AddFragment.newInstance(TimeUtils.TYPE_COST, TimeUtils.DEFAULT_CATEGORY);
    }
}
