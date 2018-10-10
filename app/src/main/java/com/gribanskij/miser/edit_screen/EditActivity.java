package com.gribanskij.miser.edit_screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.gribanskij.miser.utils.AbstractActivity;


public class EditActivity extends AbstractActivity {

    @Override
    public Fragment createFragment(Bundle bundle) {
        Intent intent = getIntent();
        Bundle transaction = intent.getBundleExtra(AbstractActivity.TRANSACTION);
        return EditFragment.newInstance(transaction);
    }
}
