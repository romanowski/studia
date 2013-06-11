import io.{Codec, Source}

object PotopReaderScala3 extends App {
  print(Source.fromFile("potop.txt")(Codec("ISO-8859-2")).getLines().toSeq
    .flatMap(line => ("[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]+".r findAllIn line)).groupBy(_.toLowerCase).map {
    case (w, list) => w -> list.size
  }.toSeq.sortBy(_._2)(Ordering.Int.reverse).mkString("\n"))
}