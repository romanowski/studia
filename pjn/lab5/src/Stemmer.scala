/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 04.05.13
 * Time: 16:29
 * To change this template use File | Settings | File Templates.
 */
object Stemmer {

  val minLen = 3

  def baseForm(word: String): String = {
    val inverted = word.reverse
    val rule = (minLen to inverted.size).reverse.toStream
      .map(index => inverted.substring(0, index))
      .flatMap(StemTree.getRule).headOption

    rule.map {
      case Rule(what, how) =>
        inverted.replaceFirst(what, how).reverse
    }.getOrElse(word)
  }

}
