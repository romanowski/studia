package edu.romanow.smartcode.links

import reflect.Method

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 08.05.12
 * Time: 19:33
 * To change this template use File | Settings | File Templates.
 */

object LinkUtil {

  def init() {

  }

  def getMethod(name: String, clazz: Class[_], params: Class[_]*): Option[java.lang.reflect.Method] = {

    val m = clazz.getMethod(name, params: _*)
    if (m != null) {
       Some(m);
    }
    else
      None;
  }
}
