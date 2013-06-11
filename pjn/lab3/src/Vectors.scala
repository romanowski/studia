import annotation.tailrec

object Vectors {

  type SimpleMapVector = Map[String, Double]

  def simpleVectorCreator(preWords: Seq[String]): SimpleMapVector = {
    val words = VectorCreator.filterWords(preWords)

    words.map(_ -> 1.0).toMap
  }


  def createNGrams(size: Int)(w: String): List[String] = {
    @tailrec
    def inner(letters: List[Char], ngrams: List[String], current: String): List[String] = {
      letters match {
        case Nil => current :: ngrams
        case h :: tail if current.size == size =>
          inner(tail, current :: ngrams, current.substring(1) + h)
        case h :: tail =>
          inner(tail, ngrams, current + h)
      }
    }
    inner(w.toList, Nil, "")
  }

  def ngramsCreator(size: Int)(preWords: Seq[String]): SimpleMapVector = {
    val words = VectorCreator.filterWords(preWords)
    words.flatMap(createNGrams(size)).groupBy(w => w).map(el => el._1 -> el._2.size.toDouble).toMap
  }

}
