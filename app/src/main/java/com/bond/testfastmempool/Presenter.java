package com.bond.testfastmempool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.bond.testfastmempool.db.SpecTheme;
import com.bond.testfastmempool.db.StaticConsts;
import com.bond.testfastmempool.tools.FileAdapter;
import com.bond.testfastmempool.ui.i.FragmentKey;
import com.bond.testfastmempool.ui.i.IActivity;

/*
* WiKi MVP: The presenter acts upon the model and the view. It retrieves data from repositories (the model),
* and formats it for display in the view.
*
* */
public class Presenter {
  ///////////////////////////////////////////////////
  //public:
  public static volatile IActivity iActivity = null;

  public static void goFragment(String frag_name)
  {
    if (null != iActivity)
    {
      final FragmentKey fragmentKey = new FragmentKey(frag_name);
      Runnable run_go_frag = new Runnable() {
        @Override
        public void run() {
          try {
            iActivity.showMainView(fragmentKey, 0, null);
          } catch (Exception e) {
            Log.e(TAG, "goFragment():error:", e);
          }
        }
      };
      runOnGUIthread(run_go_frag);
    }
    return;
  }

  public static void send_to_Fragment(String frag_name, int msgType, Object obj)
  {
    if (null != iActivity)
    {
      final FragmentKey fragmentKey = new FragmentKey(frag_name);
      final int f_msgType = msgType;
      final Object f_obj = obj;
      Runnable run_go_frag = new Runnable() {
        @Override
        public void run() {
          try {
            iActivity.showMainView(fragmentKey, f_msgType, f_obj);
          } catch (Exception e) {
            Log.e(TAG, "send_to_Fragment():error:", e);
          }
        }
      };
      runOnGUIthread(run_go_frag);
    }
    return;
  }

  public static void runOnGUIthread (Runnable r) {
    guiHandler.post(r);
  }  //runOnGUIthreadDelay

  public static void runOnGUIthreadDelay (Runnable r, long delay) {
    //try {
    if (0l == delay) {
      guiHandler.post(r);
    } else {
      guiHandler.postDelayed(r, delay);
    }
//    } catch (Exception e) {
//      Log.e(TAG, "runOnGUIthread error:", e );
//    }
  }  //runOnGUIthreadDelay

  public static void makeToastText(final String text, final int toastDuration) {
    runOnGUIthread(new Runnable() {
      @Override
      public void run() {
        try {
          Toast.makeText(SpecTheme.context, text, toastDuration).show();
        } catch (Exception e) {}
      }
    });
  }


  public static void onResume(IActivity in_iActivity)
  {
    iActivity = in_iActivity;
    try {
      // keepAliveThread.resume();
      IntentFilter filter = new IntentFilter();
      filter.addAction(StaticConsts.SPEC_BROADCAST_GUI);
      LocalBroadcastManager.getInstance(SpecTheme.context).registerReceiver(bro, filter);
    } catch (Exception e) {
      Log.e(TAG, "onResume():", e);
    }
    return;
  }

  public static void onPause() {
    try {
      LocalBroadcastManager.getInstance(SpecTheme.context).unregisterReceiver(bro);
    } catch (Exception e) {
      Log.e(TAG, "onResume():", e);
    }
    iActivity = null;
    return;
  }

  public static String get_test_result()
  {
    if (null == st_test_result)
    {
      load_test_result();
    }
    return st_test_result;
  }

  ///////////////////////////////////////////////////
  //private:
  static final String TAG = "Presenter";
  final static Handler guiHandler = new Handler(Looper.getMainLooper());
  static String st_test_result = null;
  static private void set_cur_test_result(String test_result)
  {
    st_test_result = test_result;
    return;
  }

  static void load_test_result()
  {
    st_test_result = FileAdapter.readFile(StaticConsts.TEST_RESULT_FILE,  SpecTheme.context);
    if (null == st_test_result  || st_test_result.isEmpty())
    {
      st_test_result = FileAdapter.loadAssetString(SpecTheme.context, "test_result.json");
    }
    return;
  }

  private static final BroadcastReceiver bro = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      try {
        String test_result = intent.getStringExtra(StaticConsts.TEST_RESULT_FILE);
        if (null != test_result)
        {
          set_cur_test_result(test_result);
          send_to_Fragment("Frag_Main",
              StaticConsts.TEST_RESULT_SIGNAL, null);
        }
        // Значит сервис поднялся, можно байндить
     //   Controller.doBind();
//        Bundle b = null;
//        if (null!=intent) b=intent.getExtras();
//        //boolean allOK=snsStarted;
//        //boolean needLoad=false;//!snsStarted;
//          MsgTemplate m = (MsgTemplate) b.getParcelable("msg");

      } catch (Exception e) {
        Log.e(TAG,"error:",e);
      }

    }
  }; //bro

} //Presenter