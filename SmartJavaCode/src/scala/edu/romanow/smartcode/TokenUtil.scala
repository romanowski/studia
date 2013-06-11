package edu.romanow.smartcode

import edu.romanow.antlr.JavaLexer
import java.lang.reflect.Modifier

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 08.05.12
 * Time: 12:51
 * To change this template use File | Settings | File Templates.
 */

object TokenUtil {

  lazy val primitives: Set[String] = "boolean char byte short int long float double".toUpperCase.split(" ").toSet

  lazy val types: Map[Int, String] = Map(classOf[JavaLexer].getDeclaredFields.filter(f =>
    Modifier.isStatic(f.getModifiers) && Modifier.isFinal(f.getModifiers) && Modifier.isPublic(f.getModifiers)).map(f =>
    (f.getInt(null), f.getName)): _*)


  def tokenName(id: Int) = types.get(id).getOrElse("empty")

  def tokenClass(id: Int) = tokenName(id)

  def isPrimitive(token: Int) = primitives contains tokenName(token)

}
