package com.bond.testfastmempool.ui.i;

public interface IActivity {
  void  showMessage(String str);
  void  goBack();

  void showMainView(FragmentKey fragmentKey, int msgType, Object obj);
//  ILocalDB getLocalDB();
//  IRecyclerDataManager getGlobalDB();
//  IUserSettings getIUserSettings();
}