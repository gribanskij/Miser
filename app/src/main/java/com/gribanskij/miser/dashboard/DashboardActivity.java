package com.gribanskij.miser.dashboard;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.gribanskij.miser.R;
import com.gribanskij.miser.util.IabHelper;
import com.gribanskij.miser.util.IabResult;
import com.gribanskij.miser.util.Inventory;
import com.gribanskij.miser.util.Purchase;
import com.gribanskij.miser.utils.AbstractActivity;
import com.gribanskij.miser.utils.TimeUtils;


public class DashboardActivity extends AbstractActivity {


    private static final String LOG_TAG = DashboardActivity.class.getSimpleName();
    private static final int RC_REQUEST = 10003;
    private final String SKU_DISABLE_ADS = "sku_ads_disable";
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inv) {

            if (result.isFailure()) return;

            if (inv.hasPurchase(SKU_DISABLE_ADS)) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.disable_adMob_key), true);
                editor.apply();
            }
        }
    };
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase info) {

            if (result.isFailure()) {
                Toast.makeText(getApplicationContext(), getString(R.string.info_error_ads_disabling),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (info.getSku().equals(SKU_DISABLE_ADS)) {
                Toast.makeText(getApplicationContext(), getString(R.string.info_disable_ads),
                        Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(getString(R.string.disable_adMob_key), true);
                editor.apply();
            }
        }
    };
    private IabHelper mHelper;

    @Override
    public Fragment createFragment(Bundle bundle) {
        long begin_date = TimeUtils.getBegin_month();
        long end_date = TimeUtils.getEnd_month();
        return DashboardFragment.newInstance(begin_date, end_date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isAdsDisabled = sharedPreferences.getBoolean(getString(R.string.disable_adMob_key), false);
        if (!isAdsDisabled) {
            MobileAds.initialize(this, getString(R.string.miser_adMob_ID));
        }

        StringBuilder builder = new StringBuilder();
        builder.append(getString(R.string.base64_1));
        builder.append(getString(R.string.base64_2));
        builder.append(getString(R.string.base64_3));
        builder.append(getString(R.string.base64_4));
        String base64EncodedPublicKey;
        base64EncodedPublicKey = builder.toString();
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        //mHelper.enableDebugLogging(true);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    Log.d(LOG_TAG, "Problem setting up In-app Billing: " + result);
                    return;
                }
                Log.d(LOG_TAG, "Setup successful. Querying inventory.");

                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    Log.d(LOG_TAG, "Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menu_item_disabling_adMob: {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                if (sharedPreferences.getBoolean(getString(R.string.disable_adMob_key), false))
                    return true;

                try {
                    mHelper.launchPurchaseFlow(this, SKU_DISABLE_ADS, RC_REQUEST, mPurchaseFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), getString(R.string.info_error_ads_disabling),
                            Toast.LENGTH_SHORT).show();
                }

                return true;
            }
            case R.id.menu_item_notification: {

                FragmentManager manager = getSupportFragmentManager();
                DialogFragment dialog = new NotificationDialog();
                dialog.show(manager, "");
                return true;
            }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (mHelper == null || !mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;

    }
}
