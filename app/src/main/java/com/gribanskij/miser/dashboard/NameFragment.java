package com.gribanskij.miser.dashboard;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gribanskij.miser.R;
import com.gribanskij.miser.sql_base.MiserContract;
import com.gribanskij.miser.utils.TimeUtils;

import java.util.ArrayList;


public class NameFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String DIALOG_EDIT = "edit";
    private static final int CURSOR_INCOME_CATEGORY_NAMES = 10;
    private static final int CURSOR_COST_CATEGORY_NAMES = 20;
    private static final int CURSOR_ACCOUNT_CATEGORY_NAMES = 30;

    private String[] costCategories;
    private String[] incomeCategories;
    private String[] accountCategories;
    private ArrayList<String[]> gropes;
    private String[] grope_names;

    private Cursor accountCursor;
    private Cursor costCursor;
    private Cursor incomeCursor;
    private ExpandableListView expandableListView;


    public NameFragment() {
    }

    public static NameFragment newInstance(String param1, String param2) {
        NameFragment fragment = new NameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int COUNT_OF_GROPES = 3;
        gropes = new ArrayList<>();
        costCategories = new String[TimeUtils.MAX_COST_CATEGORIES];
        incomeCategories = new String[TimeUtils.MAX_INCOM_CATEGORIES];
        accountCategories = new String[TimeUtils.MAX_ACCOUNTS];
        grope_names = new String[COUNT_OF_GROPES];

        grope_names[TimeUtils.TYPE_INCOM] = getResources().getString(R.string.incom_card);
        grope_names[TimeUtils.TYPE_COST] = getResources().getString(R.string.cost_card);
        grope_names[TimeUtils.TYPE_ACCOUNTS] = getResources().getString(R.string.accounts_grope);

        gropes.add(costCategories);
        gropes.add(incomeCategories);
        gropes.add(accountCategories);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        expandableListView = v.findViewById(R.id.expandableListView);
        expandableListView.setAdapter(new ExpListAdapter(getContext(), gropes, grope_names));
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CURSOR_ACCOUNT_CATEGORY_NAMES, null, this);
        getLoaderManager().initLoader(CURSOR_INCOME_CATEGORY_NAMES, null, this);
        getLoaderManager().initLoader(CURSOR_COST_CATEGORY_NAMES, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = null;
        String[] projection = null;
        String selection = null;
        String sortOrder = null;
        String[] selectionArg = null;

        switch (id) {
            case CURSOR_ACCOUNT_CATEGORY_NAMES: {
                uri = MiserContract.CategoryTable.CATEGORIES_URI;
                projection = new String[]{MiserContract.CategoryTable.Cols.CATEGORY_ID, MiserContract.CategoryTable.Cols.CATEGORY_NAME};
                selection = MiserContract.CategoryTable.Cols.TYPE + " = ? ";
                selectionArg = new String[]{Integer.toString(MiserContract.TYPE_ACCOUNTS)};
                break;
            }
            case CURSOR_COST_CATEGORY_NAMES: {
                uri = MiserContract.CategoryTable.CATEGORIES_URI;
                projection = new String[]{MiserContract.CategoryTable.Cols.CATEGORY_ID, MiserContract.CategoryTable.Cols.CATEGORY_NAME};
                selection = MiserContract.CategoryTable.Cols.TYPE + " = ? ";
                selectionArg = new String[]{Integer.toString(MiserContract.TYPE_COST)};
                break;
            }
            case CURSOR_INCOME_CATEGORY_NAMES: {
                uri = MiserContract.CategoryTable.CATEGORIES_URI;
                projection = new String[]{MiserContract.CategoryTable.Cols.CATEGORY_ID, MiserContract.CategoryTable.Cols.CATEGORY_NAME};
                selection = MiserContract.CategoryTable.Cols.TYPE + " = ? ";
                selectionArg = new String[]{Integer.toString(MiserContract.TYPE_INCOM)};
                break;
            }
            default: {
                break;
            }
        }

        return new CursorLoader(getContext(), uri, projection, selection, selectionArg, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        final int CATEGORY_NAME = 1;

        switch (loader.getId()) {
            case CURSOR_ACCOUNT_CATEGORY_NAMES: {
                //swapCursor(accountCursor, data);
                accountCursor = data;

                if (data.moveToFirst()) {
                    for (int i = 0; i < TimeUtils.MAX_ACCOUNTS; i++) {
                        accountCategories[i] = data.getString(CATEGORY_NAME);
                        data.moveToNext();
                    }
                }
                expandableListView.invalidateViews();
                break;
            }
            case CURSOR_COST_CATEGORY_NAMES: {
                //swapCursor(costCursor, data);
                costCursor = data;

                if (data.moveToFirst()) {
                    for (int i = 0; i < TimeUtils.MAX_COST_CATEGORIES; i++) {
                        costCategories[i] = data.getString(CATEGORY_NAME);
                        data.moveToNext();
                    }
                }
                expandableListView.invalidateViews();
                break;
            }
            case CURSOR_INCOME_CATEGORY_NAMES: {
                //swapCursor(incomeCursor, data);
                incomeCursor = data;

                if (data.moveToFirst()) {
                    for (int i = 0; i < TimeUtils.MAX_INCOM_CATEGORIES; i++) {
                        incomeCategories[i] = data.getString(CATEGORY_NAME);
                        data.moveToNext();
                    }
                }
                expandableListView.invalidateViews();
                break;
            }
            default: {
                if (data != null) data.close();
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case CURSOR_ACCOUNT_CATEGORY_NAMES: {
                //swapCursor(accountCursor, null);
                accountCursor = null;
                break;
            }
            case CURSOR_COST_CATEGORY_NAMES: {
                //swapCursor(costCursor, null);
                costCursor = null;
                break;
            }
            case CURSOR_INCOME_CATEGORY_NAMES: {
                //swapCursor(incomeCursor, null);
                incomeCursor = null;
                break;
            }
            default: {
                break;
            }
        }

    }

    private void swapCursor(Cursor oldCursor, Cursor newCursor) {
        if (oldCursor != null && oldCursor != newCursor) oldCursor.close();
    }

    private class ExpListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private ArrayList<String[]> gropes;
        private String[] gropes_name;

        private ExpListAdapter(Context context, ArrayList<String[]> gropes, String[] names) {
            this.context = context;
            this.gropes = gropes;
            gropes_name = names;
        }

        @Override
        public int getGroupCount() {
            return gropes.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return gropes.get(groupPosition).length;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return gropes.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return (gropes.get(groupPosition))[childPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.group_view, parent, false);
            }

            if (isExpanded) {
                //Изменяем что-нибудь, если текущая Group раскрыта
            } else {
                //Изменяем что-нибудь, если текущая Group скрыта
            }

            TextView textGroup = convertView.findViewById(R.id.textGrope);
            textGroup.setText(gropes_name[groupPosition]);

            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.child_view, parent, false);
            }

            TextView textChild = convertView.findViewById(R.id.textChild);

            final String name = (gropes.get(groupPosition))[childPosition];
            textChild.setText(name);
            ImageButton button = convertView.findViewById(R.id.image_child_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FragmentManager manager = getFragmentManager();
                    EditDialog dialog = EditDialog.newInstance(groupPosition, childPosition, name);
                    dialog.show(manager, DIALOG_EDIT);
                }
            });
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}
