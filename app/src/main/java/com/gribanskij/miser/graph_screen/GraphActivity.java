package com.gribanskij.miser.graph_screen;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.gribanskij.miser.utils.AbstractActivity;
import com.gribanskij.miser.utils.TimeUtils;


/**
 * Created by santy on 07.10.2017.
 */

public class GraphActivity extends AbstractActivity {


    @Override
    protected Fragment createFragment(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Intent intent = getIntent();
        int mType = intent.getIntExtra(TimeUtils.TYPE, 0);
        return GraphFragment.newInstance(mType);
    }
}
