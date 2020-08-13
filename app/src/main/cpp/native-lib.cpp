#include <jni.h>
#include <string>

#include "cppsrc/ndkstaff.h"

NDKstaff ndkStaff;

//extern "C" JNIEXPORT jstring JNICALL
//Java_com_bond_testfastmempool_MainActivity_stringFromJNI(
//    JNIEnv *env,
//    jobject /* this */) {
//  std::string hello = "Hello from C++";
//  return env->NewStringUTF(hello.c_str());
//}

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
  return  ndkStaff.jni_OnLoad(vm);
}

JNIEXPORT jstring JNICALL
Java_com_bond_testfastmempool_tools_NDKstaff_doTest(
    JNIEnv *env,
    jobject, /* this */
    jint threads_cnt
    ) {
  //std::string hello = "Hello from C++";
  //return env->NewStringUTF(hello.c_str());
  return env->NewStringUTF(ndkStaff.doTest(threads_cnt).c_str());
}

#ifdef __cplusplus
}
#endif
