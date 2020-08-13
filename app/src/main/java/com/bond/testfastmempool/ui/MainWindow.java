package com.bond.testfastmempool.ui;

/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.bond.testfastmempool.ui.i.IMainViewFrag;

public class MainWindow extends FrameLayout {
  boolean needRecreate = true;
  IMainViewFrag curActiveFrag = null;

  public IMainViewFrag  getActiveFrag() {  return  curActiveFrag ;  }

  public MainWindow(Context context) {
    super(context);
    //createViews(context);
  }

  public MainWindow(Context context, AttributeSet attrs) {
    super(context, attrs);
    //createViews(context);
  }

  public MainWindow(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    //createViews(context);
  }

  public void checkDelCurFrag(IMainViewFrag frag){
    if (curActiveFrag == frag) {
      removeCurFrag();
    }
  }

  private void removeCurFrag() {
    if (null != curActiveFrag) {
      //curActiveFrag.onStopMainView();
      removeView(curActiveFrag.getMainView());
      curActiveFrag = null;
    }
  }

  public void setCurActiveFrag(IMainViewFrag frag){
    removeCurFrag();
    curActiveFrag  =  frag;
    addView(frag.getMainView(),
        new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
  }



  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    if  (null  !=  curActiveFrag)  {
      // curActiveFrag.measure(widthMeasureSpec, heightMeasureSpec);
      measureChildWithMargins(curActiveFrag.getMainView(),
          widthMeasureSpec, 0,
          heightMeasureSpec, 0);
    }

    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    //super.onLayout(changed, left, top, right, bottom);
    int widht  =  right  -  left;
    int height  =  bottom  -  top;

    if (null  !=  curActiveFrag) {
      curActiveFrag.getMainView().layout(0, 0,  widht,  height);
    }
  }

  public void onDestroy(){
    needRecreate = true;
    removeAllViews();
    curActiveFrag = null;
  }
}