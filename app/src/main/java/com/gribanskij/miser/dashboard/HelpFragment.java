package com.gribanskij.miser.dashboard;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.gribanskij.miser.R;


public class HelpFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public HelpFragment() {
    }

    public static HelpFragment newInstance(String param1, String param2) {
        HelpFragment fragment = new HelpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_info, container, false);
        ExpandableListView expandableListView = view.findViewById(R.id.expandableList_info);
        String[] grope_names = getResources().getStringArray(R.array.questions);
        String[] gropes = getResources().getStringArray(R.array.answers);
        expandableListView.setAdapter(new AdapterInfo(getContext(), gropes, grope_names));
        return view;
    }

    private class AdapterInfo extends BaseExpandableListAdapter {

        private Context context;
        private String[] gropes;
        private String[] gropes_name;

        private AdapterInfo(Context context, String[] gropes, String[] gropes_names) {

            this.context = context;
            this.gropes = gropes;
            this.gropes_name = gropes_names;

        }

        @Override
        public int getGroupCount() {

            if (gropes_name != null) {
                return gropes_name.length;
            }
            return 0;
        }

        @Override
        public int getChildrenCount(int i) {
            return 1;
        }

        @Override
        public Object getGroup(int i) {
            return gropes_name[i];
        }

        @Override
        public Object getChild(int i, int i1) {
            return gropes[i];
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.question_view, viewGroup, false);
            }

            if (b) {
                //Изменяем что-нибудь, если текущая Group раскрыта
            } else {
                //Изменяем что-нибудь, если текущая Group скрыта
            }

            TextView textGroup = view.findViewById(R.id.questeion_view);
            textGroup.setText(gropes_name[i]);

            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.answer_view, viewGroup, false);
            }

            TextView textChild = view.findViewById(R.id.answer_text);
            textChild.setText(gropes[i]);

            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }
    }
}
