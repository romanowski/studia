import io.Source

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 02.04.13
 * Time: 23:37
 * To change this template use File | Settings | File Templates.
 */
object SpellCheckApp extends App {
  Dictionary.contains("ala")
  Source.stdin.getLines().foreach {
    w =>
      val t = System.currentTimeMillis()
      println(SpellChecker.spellCheck(w).mkString(", "))
    // print(" in %s millis".format(System.currentTimeMillis() - t))
  }
}
