package com.gribanskij.miser.categories;

/**
 * Created by sesa175711 on 25.10.2016.
 */
class Categories {

    private String category_name;
    private float category_sum;

    Categories(String category_name, float category_sum) {
        this.category_name = category_name;
        this.category_sum = category_sum;
    }

    String getCategory_name() {
        return category_name;
    }

    void setCategory_name(String name) {
        category_name = name;
    }

    Float getCategory_sum() {
        return category_sum;
    }

    void setCategory_sum(float sum) {
        category_sum = sum;
    }

    public String toString() {
        return this.category_name;
    }
}
