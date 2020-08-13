package com.bond.testfastmempool.servs;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bond.testfastmempool.MainActivity;
import com.bond.testfastmempool.Presenter;
import com.bond.testfastmempool.R;
import com.bond.testfastmempool.db.SpecSettings;
import com.bond.testfastmempool.db.StaticConsts;
import com.bond.testfastmempool.tools.FileAdapter;
import com.bond.testfastmempool.tools.NDKstaff;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Class used for the client Binder.  Because we know this service always
 * runs in the same process as its clients, we don't need to deal with IPC.
 */
public class LocalService extends Service {
  //////////////////////////////////////////////////////////
//  Public stuff
  public void check_online_state() {
    //if (SpecSettings.is_server_online) {
      enshure_online();
   // }
//    else {
//      inc_keepRunID();
//    }
    return;
  } // check_online_state

//////////////////////////////////////////////////////////
//  Public system stuff

  public class LocalBinder extends Binder {
    public LocalService getService() {
      // Return this instance of LocalService so clients can call public methods
      return LocalService.this;
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    int threads_cnt = -1;
    try {
      threads_cnt = intent.getIntExtra("threads_cnt", -1);
    } catch (Exception e)
    {
      Log.e(TAG, "onStartCommand", e);
    }
    if (threads_cnt > 0)
    {
      queue_for_tests.add(threads_cnt);
      check_online_state();
    }
    //return super.onStartCommand(intent, flags, startId);
    return START_STICKY;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    SpecSettings.set_service_context(this);
  }

  @Override
  public void onDestroy() {
    inc_keepRunID(); // должен быть уверен что нить остановлена
    SpecSettings.on_Service_Destroy(this);
    if (SpecSettings.is_server_online) { // беспредел лечим
      Intent i = new Intent(this, LocalService.class);
      startService(i);
    }
    super.onDestroy();
  }


  // Private
  ////////////////////////////////////////////////////////////
  static final int FOREGROUND_SERVICE_ID = 30050947;
  final String TAG  =  "LocalService";
  private final String WTAG = "LS.WorkerThread";
  final static Handler guiHandler = new Handler(Looper.getMainLooper());
  // Binder given to clients
  final IBinder binder = new LocalBinder();

  public static volatile int keepRunID  =  0;
  volatile Thread localThread = null;
  public volatile int lastTime  =  0;

  static final ConcurrentLinkedQueue<Integer> queue_for_tests = new ConcurrentLinkedQueue<Integer>();


  private void enshure_online()
  {
    int curTime  =  (int)(System.currentTimeMillis() % StaticConsts.MAX_INT);
    if  (curTime - lastTime > StaticConsts.MSEC_KEEP_ALIVE)  {
      inc_keepRunID();
      Log.w(WTAG,"enshure_online().restart after msec="
        + String.valueOf(curTime - lastTime) );
      resume();
    }
    return;
  } // enshure_online

  private Notification createNotification (int  idRtikerText,
                                           int  idRcontentText)  {
    Notification notification  =  null;
    try {
      CharSequence ticker  =  getText(idRtikerText);
      CharSequence text  =  getText(idRcontentText);
      Intent notificationIntent = new Intent(this, MainActivity.class);
      notificationIntent.setAction(StaticConsts.ACTION_GET_NOTHING);
      notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
          notificationIntent, 0);

      notification = new Notification.Builder(this)
          .setSmallIcon(R.drawable.wifi71)//R.drawable.up3)  // the status icon
          .setTicker(ticker)  // the status text
          .setWhen(System.currentTimeMillis())  // the time stamp
          .setContentTitle(getText(R.string.str_server_online))  // the label of the entry
          .setContentText(text)  // the contents of the entry
          .setContentIntent(pendingIntent)  // The intent to send when the entry is clicked
          .build();

    } catch (Exception e) {
      Log.e(TAG, "showNotification:  error", e);
    }
    return notification;
  }

  final Runnable start_LocalForeground = new Runnable() {
    @Override
    public void run() {
        startForeground(FOREGROUND_SERVICE_ID ,
            createNotification(R.string.foreground_service_started,
                R.string.fsrv_text1));
      return;
    }
  };

  final Runnable stop_LocalForeground = new Runnable() {
    @Override
    public void run() {
      stopForeground(true);
      stopSelf();
    }
  };



  private void send_test_result_to_GUI(String result)
  {
    // save in case of GUI sleeps:
    FileAdapter.saveFile(StaticConsts.TEST_RESULT_FILE, result, this);
    // try send:
    LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
    Intent i = new Intent(StaticConsts.SPEC_BROADCAST_GUI);
    i.putExtra(StaticConsts.TEST_RESULT_FILE, result);
    if (!manager.sendBroadcast(i)) {
      Log.w(TAG, "send_ServOnline_to_GUI() fail..");
    }
    return;
  }


  private int inc_keepRunID() {
    ++keepRunID;
    if  (keepRunID  >  10000) {
      keepRunID -= 10000;
    }
    return keepRunID;
  }

  public void pause()  {
    //Log.e(WTAG, "Start pause() ");
    synchronized (WTAG)  {
      inc_keepRunID();
      localThread = null;
    }  //  synchronized
    //Log.e(WTAG, "Start pause()-END ");
  }

  public synchronized void resume()  {
    //Log.e(WTAG,"resume() START");
    pause();
    lastTime  =  (int)(System.currentTimeMillis() % StaticConsts.MAX_INT);
    synchronized (WTAG) {
      localThread  =  new Thread(new WorkerThread(inc_keepRunID()));
      localThread.start();
    }
    //Log.e(WTAG,"resume() START-END");
  }

  private class WorkerThread implements Runnable {
    final int threadID;

    WorkerThread(int  worker_threadID) {
      threadID  =  worker_threadID;
    }

    @Override
    public void run() {
      Log.w(WTAG,"WorkerThread STARTED");
      guiHandler.post(start_LocalForeground);
      try  {
        //while  (threadID == keepRunID && SpecSettings.is_server_online)  {
        while  (threadID == keepRunID)  {
          lastTime =  (int)(System.currentTimeMillis() % StaticConsts.MAX_INT);
          Integer threads_cnt = queue_for_tests.poll();
          if (null == threads_cnt)  break;
          String res = NDKstaff.doTest(threads_cnt);
          send_test_result_to_GUI(res);
        } // while main loop
      } catch (Exception e) {
        Log.e(WTAG,"error".intern(), e);
      }
      guiHandler.post(stop_LocalForeground);
      Log.w(WTAG,"WorkerThread STOPED");
      lastTime =  0; // aka thread stopped
    }  // run

  }  // worker


} // LocalService
