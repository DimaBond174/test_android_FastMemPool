package com.bond.testfastmempool.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static android.content.Context.MODE_PRIVATE;

public class SpecSettings {
  public static volatile boolean is_server_online = false;
  public static volatile boolean camera_need_grayscale = true;
  public static volatile boolean camera_is_grayscale = false;
  public static volatile int camera_width = 1920;
  public static volatile int camera_height = 1080;

  public static void set_gui_context(Context in_context)
  {
    wCommonLock.lock();
    if (null == context || context_from_service)
    { // Всегда перезаписываю контекст в GUI
      context  =  in_context;
      context_from_service = false;
      load();
    }
    wCommonLock.unlock();
    return;
  }

  public static void set_service_context(Context in_context)
  {
    wCommonLock.lock();
    if (null == context)
    {
      context  =  in_context;
      context_from_service = true;
      load();
    }
    wCommonLock.unlock();
  }

  public static void on_GUI_Destroy()
  {
    if (!context_from_service)
    {
      wCommonLock.lock();
      if (null != context)
      {
        save();
        context  =  null;
      }
      wCommonLock.unlock();

    }

    return;
  }

  public static void on_Service_Destroy(Context in_context)
  {
    //assert (context_from_service);
    //if (context_from_service)
    {
      wCommonLock.lock();
      if (null == context)
      {
        context = in_context;
        context_from_service = true;
      }
      save();
      if (context_from_service)
      {
        context  =  null;
      }

      wCommonLock.unlock();
    }

    return;
  }


  public String get_dsPrivate() {
    rCommonLock.lock();
    String re = dsPrivate;
    rCommonLock.unlock();
    return re;
  }

  public static void saveParam(String key,  String value)
  {
    mapString.put(key, value);
    return;
  }

  public static String getString(String key)
  {
    String re = mapString.get(key);
    if (null == re)
    {
      rCommonLock.lock();
      try {
        SharedPreferences appPrefs =
            context.getSharedPreferences("SnsPref", MODE_PRIVATE);
        re = appPrefs.getString(key, "");
      } catch (Exception e) {
        Log.e(TAG, "getString():", e);
        re = "";
      }
      rCommonLock.unlock();
    }
    return re;
  }

  ///////////////////////////////////////////
//  Private
  private static final String TAG = "SpecSettings";
  private static final ReentrantReadWriteLock rwCommonLock = new ReentrantReadWriteLock();
  private static final Lock rCommonLock = rwCommonLock.readLock();
  private static final Lock wCommonLock = rwCommonLock.writeLock();
  private static volatile Context context  =  null;
  private static volatile boolean context_from_service  =  false;
  private static volatile String dsPrivate = null;
  public static volatile Map<String, String> mapString = new ConcurrentHashMap<String,String>();

  private static void save()
  {
    try {

      SharedPreferences appPrefs =
          context.getSharedPreferences("SnsPref", MODE_PRIVATE);
      SharedPreferences.Editor editor = appPrefs.edit();
      for (Map.Entry<String, String> entry : mapString.entrySet()) {
        editor.putString(entry.getKey(), entry.getValue());
      }
      //editor.putString("dsPublic", dsPublic);
      //editor.putString("SSID", SSID);
      //editor.putString("smartUIFrag", smartUIFrag);
//      editor.putInt("tasksCacheMaxSize", tasksCacheMaxSize);
//      //  editor.putInt("wifiModType", wifiModType);
//      editor.putLong("settingFlags", settingFlags);
//      editor.putLong("keepFreeDiskSpace", keepFreeDiskSpace);
//      editor.putLong("lastFreeDB", lastFreeDB);
//      editor.putBoolean("doDiskCache", doDiskCache);
//      editor.putBoolean("agreeEULA", agreeEULA);
      //editor.putBoolean("snsIsOFF", snsIsOFF);
      //editor.putInt("sqliteDBschema", sqliteDBschema);
      // Commit the edits!
      editor.commit();
      //re=true;
    } catch (Exception e) {
      Log.e(TAG, "save() error:", e);
    }
    return;
  }

  private static void load()
  {
    //wCommonLock.lock();
    // loading from XML
    try {
      SharedPreferences appPrefs =
          context.getSharedPreferences("SnsPref", MODE_PRIVATE);
      dsPrivate=context.getFilesDir().getPath()+"/specnet";
//
//
//      settingFlags = appPrefs.getLong("settingFlags", 0L);
//      setDsPublic();
//
//      tasksCacheMaxSize = appPrefs.getInt("tasksCacheMaxSize", MAX_CACHE);
//      doDiskCache = appPrefs.getBoolean("doDiskCache", false);
//      sqliteDBschema = appPrefs.getInt("sqliteDBschema", sqliteDBschema);
//      agreeEULA = appPrefs.getBoolean("agreeEULA", false);
////            wifiModType = appPrefs.getInt("wifiModType", 0);
////            if (wifiModType < 0  ||  wifiModType > 3)  {
////                wifiModType  =  0;
////            }
//
//      keepFreeDiskSpace = appPrefs.getLong("keepFreeDiskSpace", 32000000L);
//      lastFreeDB = appPrefs.getLong("lastFreeDB", 0L);
//
//      certLifeTime = appPrefs.getLong("certLifeTime", MAXcertLifeTime);
//      maxWiFiCachedPeers = appPrefs.getInt("maxWiFiCachedPeers", 100);
//      masterFlags = appPrefs.getLong("masterFlags", masterFlags);
//      lastSystemMsgDate = appPrefs.getLong("lastSystemMsgDate", lastSystemMsgDate);
//      maxWiFiConnections = appPrefs.getInt("maxWiFiConnections", 10);
//      avatarMailLife = appPrefs.getInt("avatarMailLife", 365);
//      idleConnLife = appPrefs.getInt("idleConnLife", 15);
//      internet_check_mail_time = appPrefs.getInt("internet_check_mail_time", internet_check_mail_time);
//      minLvlBatteryCharge = appPrefs.getInt("minLvlBatteryCharge", 10);
//      groupMailLife  = appPrefs.getInt("groupMailLife", 10);
//      changeWiFi = appPrefs.getBoolean("changeWiFi", false);
//
//      internet_mode_type  =  appPrefs.getInt("internet_mode_type", internet_mode_type);
//      notification_settings  =  appPrefs.getInt("notification_settings", notification_settings);
//
//      msgSound = appPrefs.getString("msgSound",  null);
//
//      internet_port  =  appPrefs.getInt("internet_port", 443);
//      internet_host  =  appPrefs.getString("internet_host",  internet_host);
//      usr_web_URL_spec  =  new String[5];
//      usr_web_URL_spec[0]  =  appPrefs.getString("usr_web_URL",  def_web_URL);
//      usr_web_URL_spec[1]  =  appPrefs.getString("usr_web_HostPrefix",  def_web_HostPrefix);
//      usr_web_URL_spec[2]  =  appPrefs.getString("usr_web_HostPostfix",  def_web_HostPostfix);
//      usr_web_URL_spec[3]  =  appPrefs.getString("usr_web_PortPrefix",  def_web_PortPrefix);
//      usr_web_URL_spec[4]  =  appPrefs.getString("usr_web_PortPostfix",  def_web_PortPostfix);

    } catch (Exception e) {
      Log.e(TAG, "loadAll error:", e);
    }
    //wCommonLock.unlock();
    return;
  }

} // SpecSettings
