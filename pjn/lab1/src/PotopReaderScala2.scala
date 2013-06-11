import io.{Codec, Source}
import scala.collection.JavaConversions._
import java.util.Date

object PotopReaderScala2 extends App {
  val txt = Source.fromFile("potop.txt")(Codec("ISO-8859-2")).getLines().toSeq
  val t1: Long = new Date().getTime
  val pattern = "[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]+".r

  var list = txt.flatMap(line => (pattern findAllIn line)).groupBy(getStem).map {
    case (w, list) => w -> list.size
  }.toSeq.sortBy(_._2)(Ordering.Int.reverse)
  val t2: Long = new Date().getTime

  println(list.mkString("\n"))
  println("loaded in %s millis".format(t2 - t1))

  def getStem(w: String) = w.toLowerCase
}