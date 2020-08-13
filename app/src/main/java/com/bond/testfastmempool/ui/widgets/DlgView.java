package com.bond.testfastmempool.ui.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

public class DlgView extends Dialog {
  //WidImageView imageView;
  //View rootView;
  public DlgView(Context context, View view) {
    super(context, android.R.style.Theme_NoTitleBar_Fullscreen);
    //imageView = new WidImageView(context);
//    addView(imageView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
//        FrameLayout.LayoutParams.MATCH_PARENT));
    //rootView = view;
    setContentView(view);
    return;
  }

} // DlgView
