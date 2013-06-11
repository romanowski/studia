import io.Source

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 04.05.13
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
object App extends App {


  var res = 0
  var all = 0
  Source.stdin.getLines().foreach {
    line => {
      val w :: base :: Nil = line.split(",").toList
      val cb = Stemmer.baseForm(w)
      all += 1
      if (base == cb) {
        res += 1
      } else {
        println("bad form: %s -> %s (%s)".format(w, cb, base))
      }
    }
  }
  println("got: %s/%s (%s%%)".format(res, all, 100.0 * res / all))

}
