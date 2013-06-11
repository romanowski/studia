import io.{Codec, Source}
import java.util.Date

object PotopReaderScala extends App {
  val txt = Source.fromFile("potop.txt")(Codec("ISO-8859-2")).mkString
  val t1: Long = new Date().getTime

  var list = ("[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]+".r findAllIn txt).toSeq.groupBy(getStem).map {
    case (w, list) => w -> list.size
  }.toSeq.sortBy(_._2)(Ordering.Int.reverse)
  val t2: Long = new Date().getTime

  println(list.mkString("\n"))
  println("loaded in %s millis".format(t2 - t1))

  def getStem(w: String) = w.toLowerCase
}