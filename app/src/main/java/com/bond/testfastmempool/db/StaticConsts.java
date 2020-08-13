package com.bond.testfastmempool.db;

import android.view.Menu;

public class StaticConsts {
  public static final int MAX_ITEMS  =  1000;
  public static final int MSEC_KEEP_ALIVE = 10000; // 10 sec is MAX for any Calculation
  public static final int MSEC_RETRY = 5000;

  public static final int START_ITEMS  =  1000;
  public static final int MAX_INT  =  2147000647;
  public static final long MSEC_ISDEAD  =  25000;  // millisec
  public static final long MSEC_SLEEP  =  100;
  public static final long MSEC_GITHUB_LIMIT  =  1000;

  //Коды сообщений:
  public static final int M_GO_FRAG_UI_MSG = 111;

  //Test Case Types:
  public static final int CASE_Big_Byte_Array  =  0;
  public static final int CASE_Strings  =  1;

  public static final String UI_BRO_URI = "com.bond.android_ipc.UI.BRO.URI";
  public static final String EXTR_TIME = "time";
  public static final String FILES_URI_LIST= "files URI list";

  public static final int RQS_GET_PERMITIONS  =  1;
  public static final int RQS_GET_CONTENT  =  2;
  public static final int  REQUEST_CAMERA_PERMISSION = 7;
  public static final int RQS_AUTH  =  9001;
  public static String ACTION_GET_NOTHING = "com.bond.android_ipc.pofig";
  public static final int FrgActivityForResult  =  1;

  // XML настроек юзера - идентификаторы должны быть уникальные
  public static final int PARM_MainView2Tabs_curTab = 1;
  public static final int PARM_MainView2Tabs_searchTab0 = 2;
  public static final int PARM_MainView2Tabs_searchTab1 = 3;
  public static final int PARM_MainViewDetail_RecyclerDataItem = 4;
  // XML

  public static final int MENU_UiMain  =  Menu.FIRST;
  //public static final String FirstFragTAG = "Frag_Camera";
  public static final String FirstFragTAG = "Frag_Main";
  public static final String MainViewDetail = "MainViewDetail";
  public static final int MENU_UiHistory  =  Menu.FIRST  +  1;
  public static final String UiHistoryTAG = "UiHistoryFrag";
  public static final int MENU_UiHistoryClear  =  Menu.FIRST  +  2;
  public static final int MENU_UiSettings  =  Menu.FIRST  +  3;
  public static final String UiSettingsTAG = "UiSettingsFrag";
  public static final int MENU_UiSettingsDef  =  Menu.FIRST  +  4;
  public static final int MENU_Exit =  Menu.FIRST  +  5;

  public final static String SPEC_BROADCAST_GUI   = "com.bond.specnet.gui";
  public final static String TEST_RESULT_FILE   = "test_result.json";
  public static final int TEST_RESULT_SIGNAL =  1652;
  public static final int SERV_err_no_camera =  -1;
  public static final int SERV_online =  1;
}