/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#ifndef ANDROID_CACHE_NDKstaff_H
#define ANDROID_CACHE_NDKstaff_H

#include <jni.h>
#include "tools/JNIHelper.h"
#include <memory>
#include <shared_mutex>

class NDKstaff {
 public:
  int  jni_OnLoad(JavaVM* vm);

  std::string doTest(int threads_cnt);
 private:
  JNIHelper  jniHelper;
  //std::shared_ptr<ITestCase>  test_case;
  std::shared_mutex  test_case_mutex;

  //std::shared_ptr<ITestCase> get_test_case();
  //void  set_test_case(std::shared_ptr<ITestCase>  new_test_case);
};


#endif //ANDROID_CACHE_NDKstaff_H
