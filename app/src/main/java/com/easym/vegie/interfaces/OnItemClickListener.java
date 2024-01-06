package com.easym.vegie.interfaces;

import android.view.View;

import com.easym.vegie.Utils.EnumClicks;

public interface OnItemClickListener {
    void onItemClickListener(EnumClicks where, View view, int position, Object obj1, Object obj2, Object obj3, boolean isChecked);

}
