package edu.romanow.smartcode.trees

import edu.romanow.smartcode.Extractor
import org.antlr.runtime.Token

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 9/16/12
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
case class IdentifierTree(t: Token) extends BaseTree(t) {


  override def asXML = <span>
    {super.asXML}
  </span>

  lazy val name = Extractor.extractName(this)

  def href = "ala"

}
