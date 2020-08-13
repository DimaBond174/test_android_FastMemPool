/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#include "ndkstaff.h"
#include <string>
#include <map>
#include <vector>
#include <functional>
#include <thread>
#include <tools/speclogger.h>

int  NDKstaff::jni_OnLoad(JavaVM* vm)  {
  return jniHelper.jni_OnLoad(vm);
}  // jni_OnLoad

// Importing test methods from other translation units:
extern bool test_fastmempool(int  cnt,  std::size_t each_size);
extern bool test_mempool(int  cnt,  std::size_t each_size);
extern bool test_OS_malloc(int  cnt,  std::size_t each_size);
using TestFun = std::function<bool(int  cnt,  std::size_t each_size)>;


std::string NDKstaff::doTest(int threads_cnt)
{
  const char * kTAG = "NDKstaff::doTest";
  const int test_size = 1000001;
  //const int test_size = 100001;
  std::map<std::string, TestFun> map_fun;
  map_fun.emplace("test_fastmempool", test_fastmempool);
  map_fun.emplace("test_OS_malloc", test_OS_malloc);

  std::string re("{\"N_threads\":{");
  // start multi threaded
  re.append("\"threads\":")
  .append(std::to_string(threads_cnt))
  .append(",\"testers\":[");
  bool next_tester = false;
  for (auto &&it : map_fun)
  {
    if (next_tester) re.push_back(',');
    next_tester = true;
    re.append("{\"name\":\"")
      .append(it.first)
      .append("\",\"times\":[");
    bool next_time = false;
    for (int cnt = 1000; cnt < test_size; cnt *= 10)
    {
      std::vector<std::thread> vec_threads;
      int64_t start = std::chrono::duration_cast<std::chrono::milliseconds>
          (std::chrono::system_clock::now().time_since_epoch()).count();
      for (int n = 0; n < threads_cnt; ++n) {
        //vec_threads.emplace_back(run_TestFun(it.second,  cnt,  256));
        vec_threads.emplace_back(it.second,  cnt,  256);
      }
      // Wait:
      for (auto& it2 : vec_threads) {
        it2.join();
      }
      int64_t end = std::chrono::duration_cast<std::chrono::milliseconds>
          (std::chrono::system_clock::now().time_since_epoch()).count();
      if (next_time) re.push_back(',');
      next_time = true;
      re.append(std::to_string(end - start));
      LOGW("N thread %s{%d}=%d", it.first.c_str(), cnt,(end - start));
    } //  for (int cnt
    re.append("]}"); // tester
  } // for (auto &&it : map_fun)
  re.append("]}, \"1_threads\":{")
    .append("\"threads\":1,")
    .append("\"testers\":[");
  map_fun.emplace("test_mempool", test_mempool);
  next_tester = false;
  for (auto &&it : map_fun)
  {
    if (next_tester) re.push_back(',');
    next_tester = true;
    re.append("{\"name\":\"")
      .append(it.first)
      .append("\",\"times\":[");
    bool next_time = false;
    for (int cnt = 1000; cnt < test_size; cnt *= 10)
    {
      int64_t start = std::chrono::duration_cast<std::chrono::milliseconds>
          (std::chrono::system_clock::now().time_since_epoch()).count();
      it.second(cnt, 256);
      int64_t end = std::chrono::duration_cast<std::chrono::milliseconds>
          (std::chrono::system_clock::now().time_since_epoch()).count();
      if (next_time) re.push_back(',');
      next_time = true;
      re.append(std::to_string(end - start));
      LOGW("1 thread %s{%d}=%d", it.first.c_str(), cnt,(end - start));
    } //for (int cnt
    re.append("]}"); // tester
  } // for (auto &&it
  //end multi threaded
  // start single threaded
  // end single threaded
  re.append("]}}");
  return re;
}
