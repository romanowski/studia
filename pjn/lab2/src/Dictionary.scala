import io.{Codec, Source}

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 02.04.13
 * Time: 22:09
 * To change this template use File | Settings | File Templates.
 */
object Dictionary {

  lazy val dic: Set[String] = Source.fromFile("dicts/dict.txt").getLines().toSet

  def contains(w: String) = dic.contains(w)

}
