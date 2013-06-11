import io.Source

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 24.04.13
 * Time: 08:41
 * To change this template use File | Settings | File Templates.
 */
object Dict {
  lazy val dict = {
    Source.fromFile("dict.txt").getLines().flatMap {
      line => {
        val list = line.split(", ").toList
        list.map(_ -> list.head)
      }
    }.toMap
  }

  def stem(w: String): Option[String] = dict.get(w.toLowerCase)
}
