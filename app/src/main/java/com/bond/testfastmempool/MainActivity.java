package com.bond.testfastmempool;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.bond.testfastmempool.db.SpecSettings;
import com.bond.testfastmempool.db.SpecTheme;
import com.bond.testfastmempool.db.StaticConsts;
import com.bond.testfastmempool.ui.Frag_Main;
import com.bond.testfastmempool.ui.MainWindow;
import com.bond.testfastmempool.ui.i.FragmentKey;
import com.bond.testfastmempool.ui.i.IActivity;
import com.bond.testfastmempool.ui.i.IMainViewFrag;

import java.util.ArrayDeque;
import java.util.Deque;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MainActivity extends AppCompatActivity
    implements IActivity {

  final String TAG = "MainActivity";

  final FragmentKey FirstFragKey
      =  new FragmentKey(StaticConsts.FirstFragTAG);

  final Deque<FragmentKey> uiFragsControl
      =  new ArrayDeque<FragmentKey>();
  IMainViewFrag curActiveFrag  =  null;
  MainWindow mainWindow  =  null;
  //boolean guiNotStarted  =  true;
  FloatingActionButton fab  =  null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  //  this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//Получаем весь экран приложения и делаем его во весь экран
    //View decorView = getWindow().getDecorView();
    //int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
    //decorView.setSystemUiVisibility(uiOptions);

    //setContentView(R.layout.activity_inject_point);
    setContentView(R.layout.activity_main);
    mainWindow = (MainWindow) findViewById(R.id.mainWindow);
    fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onFABclick(view);
      }
    });

    setCurActivity();
    //restoreState(savedInstanceState);
    onNewIntent(getIntent());
    return;
  }

  void onFABclick(View view) {
    if (null != curActiveFrag) {
      curActiveFrag.onFABclick();
    }
    //showMessage("Click!");
    return;
  }

  private void setCurActivity() {
    //Presenter.iActivity = this;
    SpecTheme.context = this;
    Presenter.onResume(this);
    //Presenter.activityViewModel
    //   = ViewModelProviders.of(this).get(ActivityViewModel.class);
  }

  private void removeCurActivity() {
    //Presenter.iActivity = null;
    Presenter.onPause();
    SpecTheme.onDestroy();
  }


  @Override
  public void goBack() {
    onBackPressed();
  }


  @Override
  public void showMessage(String str) {
    //Snackbar.make(mainWindow, str, Snackbar.LENGTH_LONG)
     //   .setAction("Action", null).show();
    Toast.makeText(this,"Click!", Toast.LENGTH_LONG);
    return;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    if (null  !=  curActiveFrag) {
      outState.putString("curActiveFrag.fragTAG",
          curActiveFrag.getFragmentKey().fragTAG);
      //Presenter.activityViewModel.savedInstanceState = outState;
    }
    super.onSaveInstanceState(outState);
  }

//  @Override
//  protected void onRestoreInstanceState(Bundle savedInstanceState) {
//    super.onRestoreInstanceState(savedInstanceState);
//    restoreState (savedInstanceState);
//  }

//  private void restoreState (Bundle savedInstanceState) {
//    //Presenter.activityViewModel.savedInstanceState  =  savedInstanceState;
//    if  (null  ==  savedInstanceState) {  return ;  }
//    String fragTAG = savedInstanceState.getString("curActiveFrag.fragTAG");
//    if  (null  !=  fragTAG)  {
//      setCurActiveFrag(new FragmentKey(fragTAG));
//    }//if (null!=fragTAG)
//  }


  @Override
  protected void onActivityResult(int requestCode,
                                  int resultCode, Intent data)  {
    boolean go_next = true;
    if (requestCode == StaticConsts.RQS_AUTH) {
//      //Это заказывал iAuth и он либо на экране:
//      if (null != mainWindow.getActiveFrag()) {
//        go_next = !mainWindow.getActiveFrag()
//            .onActivityResultMainView(requestCode,  resultCode, data);
//      }
//      //либо не на экране:
//      if (go_next) {
//        if (null != activityViewModel.iAuth) {
//          go_next = !activityViewModel.iAuth
//              .onActivityResultMainView(requestCode,  resultCode, data);
//        }
//      }
    }  else  {
      // Обработка заказов неAuth виджетов
      if (null  !=  curActiveFrag)  {
        go_next = !curActiveFrag.onActivityResultMainView(requestCode,
            resultCode,  data);
      }
    }
    if  (go_next) {
      super.onActivityResult(requestCode, resultCode, data);
    }
  } //onActivityResult

  private void removeCurActiveFrag()
  {
    if  (null  !=  curActiveFrag)  {
      curActiveFrag.onStopMainView();
      mainWindow.checkDelCurFrag(curActiveFrag);
      curActiveFrag = null;
    }
    return;
  }

  private void setCurActiveFrag(FragmentKey key, boolean fromBackPressed)  {
    if  (null  ==  key)  return;
    if (null == curActiveFrag)
    {
      curActiveFrag =  createUiFragment(key);
      // Отстраиваем GUI обратно:
      curActiveFrag.onStartMainView();
      mainWindow.setCurActiveFrag(curActiveFrag);
    }  else  {
      if (!curActiveFrag.getFragmentKey().equals(key))
      {
        if (!fromBackPressed)
        {
          // Предыдущий фраг кладу в очередь возврата по фрагам
          uiFragsControl.add(curActiveFrag.getFragmentKey());
        }
        curActiveFrag.onStopMainView();
        mainWindow.checkDelCurFrag(curActiveFrag);
        // создаём новый:
        curActiveFrag =  createUiFragment(key);
        curActiveFrag.onStartMainView();
        mainWindow.setCurActiveFrag(curActiveFrag);
      } // else { } // целевой фраг на экране
    }

    if (null  !=  curActiveFrag  &&  curActiveFrag.getFragmentKey()
        .fragTAG.equals(StaticConsts.FirstFragTAG)) {
      // При переходе в начало очередь окон чистится:
      uiFragsControl.clear();
    }
    return;
  } //setCurActiveFrag

  IMainViewFrag createUiFragment(FragmentKey key)  {
    IMainViewFrag frg  =  null;
    switch (key.fragTAG) {

      default:
        frg  = new Frag_Main(MainActivity.this);
        break;
    }
    return frg;
  }


  @Override
  public void onStart() {
    SpecTheme.applyMetrics(MainActivity.this);
    super.onStart();
    SpecSettings.set_gui_context(this);
    //if (guiNotStarted) {
    if (null == curActiveFrag) {
      onFirstStart();
    }
    return;
  }

  @Override
  public void onResume() {
    super.onResume();
    SpecTheme.is_landscape = ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation;
    Controller.check_service_online_state();

    if (null  !=  curActiveFrag) {
      if (mainWindow.getActiveFrag() != curActiveFrag) {
        mainWindow.setCurActiveFrag(curActiveFrag);
      }
      //curActiveFrag.onStartMainView();
    }  else {
      onFirstStart();
    }
    fab.setImageDrawable(SpecTheme.play_icon);
    return;
  }

  @Override
  public void onPause() {
    // removeCurActiveFrag();

    super.onPause();
  }

  @Override
  public void onStop() {
    //removeCurActiveFrag();
    //guiNotStarted  =  true;
    //TestPresenter.onGUIstop();
    super.onStop();
  }

  private void set_Frag_from_uiFragsControl()
  {
    FragmentKey key = null;
    try {
      key = uiFragsControl.pollLast();
    } catch (Exception e) {}
    if (null  ==   key) {
      //TestPresenter.onFirstStart();
      setCurActiveFrag(FirstFragKey, true);
    } else {
      setCurActiveFrag(key, true);
    }
    return;
  }

  private void onFirstStart() {
    restoreFragsState();
    //set_Frag_from_uiFragsControl();
    //guiNotStarted  =  false;
//    if (Build.VERSION.SDK_INT  < 23) {
//      repairSSL_onAndroid4();
//    }
    return;
  }

  @Override
  protected void onDestroy() {
    //exitMain();
    saveFragsState();
    clearUiFrags();
    Controller.doUnBind();
    removeCurActivity();
    SpecSettings.on_GUI_Destroy();

    super.onDestroy();
  }


  @Override
  public void onBackPressed() {
    if (null == curActiveFrag) {
      super.onBackPressed();
    }  else if (curActiveFrag.getFragmentKey().fragTAG
        .equals(StaticConsts.FirstFragTAG)) {
      super.onBackPressed();
//      curActiveFrag = null;
//      clearUiFrags();
//      if (null == activityViewModel.iAuth) {
//        activityViewModel.iAuth = MainSettings.getAuth(this);
//      }
//      mainWindow.setCurActiveFrag(
//          activityViewModel.iAuth.getNewUI(this));
//      mainWindow.getActiveFrag().onStartMainView(activityViewModel.iUserSettings);
    }  else  {
      //setCurActiveFrag(FirstFragKey);
      set_Frag_from_uiFragsControl();
    }
    return;
  } //onBackPressed()

//  private void clearUiFrags(FragmentKey exeptFragKey)  {
//    for (FragmentKey fragKey : uiFragsControl)  {
//      if (fragKey.equals(exeptFragKey)) {
//        continue;
//      }
//    }
//    uiFragsControl.clear();
//  }

  private void clearUiFrags() {
    uiFragsControl.clear();
    removeCurActiveFrag();
    return;
  }

  @Override
  public void showMainView(FragmentKey fragmentKey, int msgType, Object obj) {
    setCurActiveFrag(fragmentKey, false);
    mainWindow.getActiveFrag().onMessageToMainView(msgType,  obj);
  }

  // Сохраняет текущий стек фрагов
  private void saveFragsState()
  {
    StringBuilder sb = new StringBuilder(1024);
    for(FragmentKey it : uiFragsControl)
    {
      sb.append(it.fragTAG).append(";");
    }
    SpecSettings.saveParam("uiFragsControl", sb.toString());
    if (null != curActiveFrag)
    {
      SpecSettings.saveParam("curActiveFrag", curActiveFrag.getFragmentKey().fragTAG);
    }
    return;
  }

  private void restoreFragsState()
  {
    clearUiFrags();
    String str = SpecSettings.getString("uiFragsControl");
    if (str.length() > 0)
    {
      String[] frags = str.split(";");
      for(String it : frags)
      {
        uiFragsControl.add(new FragmentKey(it));
      }
    }
    str = SpecSettings.getString("curActiveFrag");
    if (str.length() > 0)
    {
      setCurActiveFrag(new FragmentKey(str), true);
    } else {
      setCurActiveFrag(new FragmentKey(StaticConsts.FirstFragTAG), true);
    }
    return;
  }


} // MainActivity
