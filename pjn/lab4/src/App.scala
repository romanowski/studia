import io._

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 24.04.13
 * Time: 08:44
 * To change this template use File | Settings | File Templates.
 */
object App extends App {
  def words = Source.fromFile("random.txt")(Codec("ISO-8859-2")).getLines().toSeq
    .flatMap(line => ("[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]+".r findAllIn line)).flatMap(Dict.stem).groupBy(el => el).map {
    case (w, list) => w -> list.size
  }.toSeq.sortBy(_._2)(Ordering.Int.reverse)

  val sum = words.foldLeft(0)((s, w) => s + w._2)
  val zipped = words.zipWithIndex

  var tmpS = 0;
  val middleIndex = zipped.dropWhile {
    el =>
      tmpS = tmpS + el._1._2
      2 * tmpS < sum
  }.head._2

  def printToFile(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter("data.txt")
    try {
      op(p)
    } finally {
      p.close()
    }
  }


  printToFile(
    pw => zipped.foreach {
      case ((_, occ), i) => pw.write("%s %s\n".format(i, occ))
    })

  println(middleIndex)
}