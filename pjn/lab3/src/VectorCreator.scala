import collection.SortedMap

object VectorCreator {


  val wordsCount = new collection.mutable.HashMap[String, Int]() {
    override def default(key: String): Int = 0
  }

  lazy val stopWord = {
    val out = wordsCount.toSeq.sortBy(_._2)(Ordering.Int.reverse).take(200).map(_._1).toSeq
    out
  }

  var req = "[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]+".r

  def createWords(line: String): Vector[Seq[String]] = {
    val out = Vector(line,
      (req findAllIn line).toList.map(_.toLowerCase))

    out.vec.foreach {
      w =>
        wordsCount(w) = wordsCount(w) + 1
    }
    out
  }

  def filterWords(words: Seq[String]) = words.filter(_.length >= 3).filterNot(stopWord.contains)

  def vectorMap[T, V](vecFun: V => T)(words: Vector[V]): Vector[T] = Vector(words.line, vecFun(words.vec))
}

case class Vector[+T](line: String, vec: T)
