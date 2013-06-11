import java.io.File
import util.Random

object WordsGenerator extends App {

  val nStr :: generatedLength :: _ = args.toList

  val n = nStr.toInt

  var map = Map[String, List[(String, Int)]]()

  val f = new File("books")
  val files =
    f.list().filterNot(_.startsWith(".")).map(("books/") +).toList


  GenerateNgrams.ngramsFromFiles(files, n.toInt + 1).foreach {
    case (keyOld, pos) =>
      val last :: first = keyOld.split("#").toList.reverse
      val key = NGramBuilder.key(first.reverse)
      map = map + (key -> ((last -> pos) :: map.get(key).getOrElse(Nil)))
  }
  map = map.map(el => el._1 -> el._2.sortBy(-_._2))

  lazy val keyVector = map.keysIterator.toVector

  def random = keyVector(Random.nextInt(keyVector.size))

  def wordForKey(k: String): Option[String] = {
  //  println(s"searching for $k")
    map.get(k).map {
      list =>
        if (list.size > 1) {
          println(list.mkString("|"))
        }
        list(Random.nextInt(list.size))._1
    }
  }

  def nextWord(got: Seq[String]): String = {
    val baseKey = NGramBuilder.key(got.take(n).reverse)
    wordForKey(baseKey).getOrElse {
      val genKey = got.grouped(n)
        .dropWhile(s => wordForKey(NGramBuilder.key(s)).isEmpty)
        .take(1)
        .toList
        .headOption
      println(s"insted of $baseKey use $genKey")
      genKey.flatMap(s => wordForKey(NGramBuilder.key(s)))
        .getOrElse(wordForKey(random).get)
    }
  }


  def generate(words: List[String]) {
    val genrated = (1 to generatedLength.toInt).foldLeft(words.reverse) {
      case (text, _) =>
        nextWord(text) :: text
    }

    println("Potop mów: " + genrated.reverse.mkString(" "))
  }

  while (true) {
    println("Podaj słowa: ")

    generate(readLine().split(" ").toList)
  }
}
