package com.gribanskij.miser.asynctask;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.gribanskij.miser.R;
import com.gribanskij.miser.dashboard.CurrencyDialog;
import com.gribanskij.miser.dashboard.EditDialog;

/**
 * Created by SESA175711 on 07.07.2017.
 */

public class AsyncQueryTask extends AsyncQueryHandler {

    private Context context;

    public AsyncQueryTask(ContentResolver cr, Context context) {
        super(cr);
        this.context = context;
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
        if (result == 0) Toast.makeText(context, R.string.db_error, Toast.LENGTH_SHORT).show();
        else Toast.makeText(context, R.string.delete_ok, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);
        if (uri == null) Toast.makeText(context, R.string.db_error, Toast.LENGTH_SHORT).show();
        else Toast.makeText(context, R.string.Insert_OK, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
        switch (token) {
            case CurrencyDialog.TOKEN_CURRENCY: {
                if (result > 0)
                    Toast.makeText(context, R.string.currency_changed, Toast.LENGTH_SHORT).show();
                break;
            }
            case EditDialog.TOKEN_CATEGORY_NAME: {
                if (result > 0)
                    Toast.makeText(context, R.string.category_rename_ok, Toast.LENGTH_SHORT).show();
                break;
            }
            default: {
                break;
            }
        }

    }
}
