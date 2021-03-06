/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#ifndef ILOG_H
#define ILOG_H

#include <string>
#include <cstdio>

class  ILog  {
 public:
  virtual  ~ILog()  {  }
  virtual void rawLog(char  lvl,  const std::string &str)  =  0;

    /*
     * log works as fprintf
     * lvl = "i"/"w"/"e" == "info", "warn", "error"
     * tag = where
     * fmt + args = what
    */
#if defined(Windows)
	template<typename ... Args>
	void log(const char * lvl, const char * fmt, Args ... args) {
		char buf[1024];
		if (0 < sprintf_s(buf, 1024, fmt, args...)) {
			rawLog(lvl, buf);
		}
	}
#else
  template<typename ... Args>
  void  log(char lvl, const char  *fmt,  Args ... args)  {
    auto  szData  =  snprintf(nullptr,  0,  fmt,  args...);
    if  (szData  >  0)  {
      ++szData; //include \0
      std::string  strData;
      strData.reserve(szData);
      strData.resize(szData  -  1);
      snprintf(&strData.front(),  szData,  fmt,  args...);
      rawLog(lvl, strData);
    }
  }
#endif
};

#endif // ILOG_H
