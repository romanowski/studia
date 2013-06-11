import io.Source

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 02.04.13
 * Time: 23:37
 * To change this template use File | Settings | File Templates.
 */
object SpellCheckTestApp extends App {
  Dictionary.contains("ala")
  Source.stdin.getLines().zipWithIndex.foreach {
    case (w, line) =>
      val t = System.currentTimeMillis()
      val res = SpellChecker.spellCheck(w)
      val time = System.currentTimeMillis() - t
      println("%s for word: %s got: [%s] in %s millis".format(line, w, res.mkString(", "), time))
  }
}
