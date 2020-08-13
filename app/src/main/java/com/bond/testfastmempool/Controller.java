package com.bond.testfastmempool;

/*
* WiKi MVC: The controller is responsible for responding to the user input and perform interactions on the data model objects.
* The controller receives the input, it validates the input and then performs the business operation that modifies the state of the data model.
* http://www.plainionist.net/Implementing-Clean-Architecture-Controller-Presenter/
*
* What is the role of the controller?
The controller takes user input, converts it into the request model defined by the use case interactor and passes this to the same.

The request object accepted by the controller is defined by the controller. We do NOT want the controller to depend on the view or types defined in the framework circle.

Such request objects are usually simple data transfer objects (DTO). Depending on the view technology a request object may contain typed information (e.g. WPF) or just strings (e.g. HTML). It is the role of the controller to convert the given information into a format which is most convenient for and defined by the use case interactor. For that the controller may have some simple if-then-else or parser logic but we do not want to have any processing logic inside the controller.

Finally the controller simply calls an API on the use case interactor to trigger the processing.
* */

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.bond.testfastmempool.db.SpecSettings;
import com.bond.testfastmempool.db.SpecTheme;
import com.bond.testfastmempool.servs.LocalService;

public class Controller {
  public static void start_test(int threads_cnt)
  {
    Intent i = new Intent(SpecTheme.context, LocalService.class);
    i.putExtra("threads_cnt",  threads_cnt);
    SpecTheme.context.startService(i);
    return;
  }

  public static void check_service_online_state()
  {
    if (SpecSettings.is_server_online)
    {
      Intent i = new Intent(SpecTheme.context, LocalService.class);
      SpecTheme.context.startService(i);
      if (mBound)
      {
        mService.check_online_state();
      }
//      else  { // из Presenter bro
//        doBind();
//      }
    } else {
      if (mBound)
      {
        doUnBind();
      }
    }
    return;
  } // check_service_online_state



//  public static void stop() {
//    stopFrameAnalyser();
//  }
//
//  public static void startFrameAnalyser() {
//    frameAnalyser.start(Presenter.activityViewModel.iNeuro);
//  }
//
//  public static void stopFrameAnalyser() {
//    frameAnalyser.stop();
//  }
  //////////////////////////////////////////
  //  private:
  private static final String TAG = "Controller";
  private static volatile boolean mBound = false;
  private static volatile LocalService mService = null;
  //static final FrameAnalyser  frameAnalyser = new FrameAnalyser();
  //static final LocalService localService = n


  public static void doBind() {
    try {
      Intent intent = new Intent(SpecTheme.context, LocalService.class);
      SpecTheme.context.bindService(intent, connection, Context.BIND_ABOVE_CLIENT);
      //SpecTheme.context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
      //Log.w(TAG, "doBind(): success");
    } catch (Exception e) {
      Log.e(TAG, "doBind():", e);
    }
    return;
  }

  public static void  doUnBind() {
    if (null != mService)
    {
      try {
        mService.check_online_state();
        SpecTheme.context.unbindService(connection);
        //mBound = false;
      } catch (Exception e) {
        Log.e(TAG, "doUnBind():", e);
      }
    }
    return;
  }

  /** Defines callbacks for service binding, passed to bindService() */
  private static final ServiceConnection connection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName className,
                                   IBinder service) {
      // We've bound to LocalService, cast the IBinder and get LocalService instance
      LocalService.LocalBinder binder = (LocalService.LocalBinder) service;
      mService = binder.getService();
      mBound = true;
      try {
        mService.check_online_state();
      } catch (Exception e) {
        Log.e(TAG, "error".intern(), e);
      }
      return;
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {
      mBound = false;
      mService = null;
    }
  };
}