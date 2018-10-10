package com.gribanskij.miser.categories;

import com.jjoe64.graphview.DefaultLabelFormatter;

/**
 * Created by SESA175711 on 31.10.2017.
 */

public class LabelFormatterCategories extends DefaultLabelFormatter {

    private String[] names;

    public LabelFormatterCategories(String[] names) {
        super();
        this.names = names;
    }

    @Override
    public String formatLabel(double value, boolean isValueX) {
        if (!isValueX) {
            return super.formatLabel(value, isValueX);
        } else {
            return getCategoryName(value);
        }
    }

    private String getCategoryName(double v) {
        int i = ((int) v);
        return names[i];
    }

    private String getSum(double value) {

        return null;
    }


}



