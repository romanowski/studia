package edu.romanow.smartcode.contexts

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 9/15/12
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
case class Function(name: String, retVal: String, params: List[Variable]) {
  def funcFindName = name + "-"+ params.size
}
