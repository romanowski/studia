import scala.Predef._
import util.matching.Regex

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 02.04.13
 * Time: 21:07
 * To change this template use File | Settings | File Templates.
 */
object DistanceManager {


  val polonicaMap =
    "eoaslzzcn".map(_.toString.r).zip("ęóąśłżźćń".map(_.toString))

  val ortErrosMap = {
    val base = Seq(
      "rz" -> "ż",
      "ó" -> "u",
      "ch" -> "h",
      "en" -> "ę",
      "om" -> "ą",
      "sz" -> "ż",
      "cz" -> "ż"
    )
    (((base ++ base.map(_.swap)).filter(_._1 != "h")) :+ ("[^c]h" -> "ch")).map(el => el._1.r -> el._2)
  }


  val letters = "qwęertyuioópaąsśdfghjklłżzźxcćvbnńm"

  val keyboardsLetter = List("qwertyuiop", "asdfghjkl", "zxcvbnm").flatMap {
    letters =>
      letters.zip(letters.drop(1)) ++ letters.drop(1).zip(letters)
  }.map(el => el._1.toString.r -> el._2.toString)

  def polonicaWords(w: String): Seq[String] = {
    polonicaMap.flatMap {
      replaceForAllMatch(w)
    }
  }

  def ortErrorWords(w: String): Seq[String] = {
    ortErrosMap.flatMap {
      replaceForAllMatch(w)
    }
  }

  def typos(w: String): Seq[String] = {
    keyboardsLetter.flatMap {
      replaceForAllMatch(w)
    }
  }

  def replaceForAllMatch(base: String)(el: (Regex, String)) = {
    el._1.findAllMatchIn(base).map(
      m =>
        base.substring(0, m.start) + el._2 + base.substring(m.end)
    )
  }

  type ProduceFuc = String => Seq[String]

  def concat(funcs: ProduceFuc*)(w: String) = {
    funcs.foldLeft(Seq(w)) {
      case (words, func) => words.flatMap(func)
    }
  }

  def swaps(w: String): Seq[String] = {
    if (w.length > 2) {
      (2 to w.length).map {
        i => new StringBuilder(w.substring(0, i - 2))
          .append(w.substring(i - 2, i).reverse)
          .append(w.substring(i))
          .toString()
      }
    } else {
      if (w.length > 1) {
        List(w.reverse)
      } else {
        Nil
      }
    }
  }

  def replace(w: String): Seq[String] = {
    (0 to w.length - 1).flatMap {
      i =>
        letters.filter(w(i) !=).map {
          l => new StringBuilder(w.substring(0, i))
            .append(l)
            .append(w.substring(i + 1))
            .toString()
        }
    } ++ letters.filter(w.last !=).map(w.substring(0, w.length - 1) +)
  }

  def add(w: String): Seq[String] = {
    (1 to w.length).flatMap {
      i =>
        letters.map {
          l => new StringBuilder(w.substring(0, i))
            .append(l)
            .append(w.substring(i))
            .toString()
        }
    } ++ letters.map(_ + w)
  }

  def remove(w: String): Seq[String] = {
    (1 to w.length - 1).map {
      i =>
        new StringBuilder(w.substring(0, i))
          .append(w.substring(i + 1))
          .toString()
    } :+ w.substring(1)
  }
}

