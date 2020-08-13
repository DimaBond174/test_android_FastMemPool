package com.bond.testfastmempool.ui.widgets;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.view.View;

/**
 * Created by dbond on 15.04.18.
 */

public class LTextInputEditText extends TextInputEditText {

    public LTextInputEditText(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), getMeasuredHeight());
    }
}