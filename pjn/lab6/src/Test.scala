import io.{Codec, Source}

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 05.06.13
 * Time: 00:57
 * To change this template use File | Settings | File Templates.
 */
object Test extends App {
  val reg = """[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]+""".r


  val ngrams = NGramBuilder.buildNGrams(4, scala.collection.mutable.Map()) {
    Source.fromFile("books/potop.txt")(Codec("ISO-8859-2")).getLines().map(_.toLowerCase).flatMap(reg.findAllIn)
  }
  println(readLine())
}
