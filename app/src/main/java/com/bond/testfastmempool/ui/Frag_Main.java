package com.bond.testfastmempool.ui;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.bond.testfastmempool.Controller;
import com.bond.testfastmempool.Presenter;
import com.bond.testfastmempool.R;
import com.bond.testfastmempool.db.SpecTheme;
import com.bond.testfastmempool.db.StaticConsts;
import com.bond.testfastmempool.ui.i.FragmentKey;
import com.bond.testfastmempool.ui.i.IMainViewFrag;
import com.bond.testfastmempool.ui.i.TestParam;
import com.bond.testfastmempool.ui.widgets.ImageWithText;
import com.bond.testfastmempool.ui.widgets.WEditNumber;
import com.bond.testfastmempool.ui.widgets.WSimpleTable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class Frag_Main extends FrameLayout  implements IMainViewFrag {
    final static String TAG = "Frag_Main";
    final FragmentKey fragmentKey = new FragmentKey(TAG);
    private ScrollView scrollView;
    private Papirus papirus;
    WEditNumber w_threads_cnt;
    WSimpleTable simple_table_Nthreads;
    WSimpleTable simple_table_1threads;

    ImageWithText btnServSettings;
    ImageWithText btnCameraSettings;
    ImageWithText btnCamera;

    public Frag_Main(Context context) {
        super(context);

    } // construct  Frag_Main

    @Override
    public View getMainView() {
        return this;
    }

    @Override
    public void onStartMainView() {
        setupGUI();
        load_TEST_RESULT();
    }

    @Override
    public void onStopMainView() {
        destroyGUI();
    }


    @Override
    public boolean onActivityResultMainView(int requestCode, int resultCode, Intent data) {
        return false;
    }

    @Override
    public void onFABclick() {
        Controller.start_test(Integer.parseInt(w_threads_cnt.get_current_value()));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)  {
        simple_table_Nthreads.onTouchEvent(event);
        simple_table_1threads.onTouchEvent(event);
        scrollView.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        simple_table_Nthreads.onTouchEvent(ev);
        simple_table_1threads.onTouchEvent(ev);
        scrollView.onTouchEvent(ev);
        return false;
    }

    void fill_table(WSimpleTable table, JSONObject jsonObj)  throws Exception
    {
        table.clear();
        String str = jsonObj.getString("threads");
        JSONArray arr_testers  =  jsonObj.getJSONArray("testers");
        int len_testers = arr_testers.length();
        JSONObject tester =  arr_testers.getJSONObject(0);
        JSONArray arr_times  =  tester.getJSONArray("times");
        int len_results = arr_times.length();
        int  table_colors[]  =  new int[len_testers + 1];
        String  table_data[][] = new String[len_testers + 1][len_results + 1];
        table_colors[0]  = SpecTheme.PBlackColor;
        table_data[0][0] = str + " threads";
        StringBuilder sb = new StringBuilder(32);
        int  cur_max_items = 1000;
        // headers:
        for (int  i  =  0;  i < len_results; ++i) {
            sb.append(cur_max_items);
            table_data[0][i + 1] = sb.toString();
            cur_max_items *= 10;
            sb.setLength(0);
        }

        for (int  i  =  0;  i < len_testers; ++i) {
            table_colors[i + 1]  = SpecTheme.color_array[i % SpecTheme.color_array.length];
            tester = arr_testers.getJSONObject(i);
            table_data[i + 1][0] = tester.getString("name");
            arr_times = tester.getJSONArray("times");
            for (int  j  =  0;  j < arr_times.length();  ++j)
            {
                sb.append(arr_times.getInt(j));
                table_data[i + 1][j + 1] = sb.toString();
                sb.setLength(0);
            }
        }
        table.setTable_data(table_data,  table_colors);
        return;
    }

    void parseJSON_to_GUI(String test_result ) throws Exception
    {
        JSONObject jsonObj  =  new JSONObject(test_result);
        JSONObject jsonObj_Nthreads = jsonObj.getJSONObject("N_threads");
        fill_table(simple_table_Nthreads, jsonObj_Nthreads);
        JSONObject jsonObj_1threads = jsonObj.getJSONObject("1_threads");
        fill_table(simple_table_1threads, jsonObj_1threads);
        return;
    }

    void load_TEST_RESULT()
    {
        String test_result = Presenter.get_test_result();
        if (null != test_result)
        {
            try {
                parseJSON_to_GUI(test_result);
            } catch (Exception e)
            {
                Log.e(TAG, "load_TEST_RESULT", e);
            }
        }
        return;
    }

    @Override
    public void onMessageToMainView(int msgType, Object obj) {
        switch (msgType)
        {
            case StaticConsts.TEST_RESULT_SIGNAL:
                load_TEST_RESULT();
                break;
            default:
                break;
        }
        return;
    }

    @Override
    public FragmentKey getFragmentKey() {
        return fragmentKey;
    }

    void destroyGUI()
    {
        removeAllViews();
        scrollView     = null;
        papirus     = null;
        btnServSettings     = null;
        btnCameraSettings     = null;

        //btnNotification     = null;
        //btnAppearance     = null;
        //btnEnergy     = null;
    }

    void setupGUI()
    {
        LayoutInflater inflater = LayoutInflater.from(SpecTheme.context);
        Resources res = SpecTheme.context.getResources();
        scrollView = new ScrollView(SpecTheme.context);
        papirus = new Papirus(SpecTheme.context);
        scrollView.addView(papirus, new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT));

        w_threads_cnt = new WEditNumber(
            new TestParam(TestParam.TYPE_NUM,
                res.getText(R.string.str_threads_cnt).toString(),
                "1", 1, 16),
            SpecTheme.context, SpecTheme.PDarkColor);
        papirus.addView(w_threads_cnt, new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT));

        simple_table_Nthreads  =  new WSimpleTable(SpecTheme.context,  SpecTheme.PWhiteColor, SpecTheme.PTextColor);
        papirus.addView(simple_table_Nthreads);

        simple_table_1threads  =  new WSimpleTable(SpecTheme.context,  SpecTheme.PWhiteColor, SpecTheme.PTextColor);
        papirus.addView(simple_table_1threads);

        addView(scrollView, new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
    } // setupGUI


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        scrollView.measure(widthMeasureSpec, heightMeasureSpec);
        /* Скажем наверх насколько мы большие */
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int b = bottom - top;
        int r = right - left;

        scrollView.layout(0,0, r, b);

    }


    /**
     * Папирус
     */
    private class Papirus extends FrameLayout {
        //int lineWidht = 0;
        //        int [] lineY = new int[2];
        //int lineY = 0;


        public Papirus(Context context) {
            super(context);
            //setWillNotDraw(false);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            measureChildWithMargins(chk_Box, widthMeasureSpec, 0,
//                    heightMeasureSpec, 0);
//            measureChildWithMargins(spinner, widthMeasureSpec, 0,
//                    heightMeasureSpec, 0);
//            measureChildWithMargins(edt_AvarNameL, widthMeasureSpec, 0,
//                    heightMeasureSpec, 0);
//            measureChildWithMargins(avatarIcon.image, widthMeasureSpec, 0,
//                    heightMeasureSpec, 0);
            int count = getChildCount();
            int height = 0;
            for (int i = 0; i < count; ++i) {
                View child = getChildAt(i);
                measureChildWithMargins(child, widthMeasureSpec, 0,
                        heightMeasureSpec, 0);
                //Все один над другим ака Vertical Layout:
                height += child.getMeasuredHeight();
            }
            //int layout_height = MeasureSpec.getSize(heightMeasureSpec);


//            int curHeight = edt_AvarNameL.getMeasuredHeight();
//            spinner.measure(widthMeasureSpec,
//                    MeasureSpec.makeMeasureSpec(curHeight, MeasureSpec.AT_MOST) );

            int widht = MeasureSpec.getSize(widthMeasureSpec);
            //lineWidht = widht - SpecTheme.dpButton2Padding;
//            int curHeight = SpecTheme.dpButton2Padding
//                    + textViewWiFi.getMeasuredHeight()
//                    + SpecTheme.dpButton2Padding
//                    + radioGroupWiFi.getMeasuredHeight()
//                    + SpecTheme.dpButton2Padding;

                    /* Скажем наверх насколько мы большие */
            setMeasuredDimension(widht, height + SpecTheme.dpButtonPadding * count);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            int widht = right - left;
//            int halfWidht = textViewWiFi.getMeasuredWidth() >> 1;//avatarIcon.image.getMeasuredWidth();
//            int height = textViewWiFi.getMeasuredHeight();
//            textViewWiFi.layout(centerX - halfWidht, SpecTheme.dpButton2Padding,
//                    centerX + halfWidht, SpecTheme.dpButton2Padding + height);
//            height += SpecTheme.dpButton2Padding + SpecTheme.dpButtonPadding;
//
//            radioGroupWiFi.layout(SpecTheme.dpButton2Padding, height,
//                    SpecTheme.dpButton2Padding + radioGroupWiFi.getMeasuredWidth(),
//                    height + radioGroupWiFi.getMeasuredWidth());
            int count = getChildCount();
            int curTop = SpecTheme.dpButtonPadding;
            for (int i = 0; i < count; ++i) {
                View child = getChildAt(i);
                //Все один над другим ака Vertical Layout:
                int h = child.getMeasuredHeight();
                child.layout(0,curTop,widht,curTop+h);
                curTop+=h+SpecTheme.dpButtonPadding;
            }

        }

    }//Papirus
}
