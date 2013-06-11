import io.Source

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 04.05.13
 * Time: 16:26
 * To change this template use File | Settings | File Templates.
 */


case class Rule(what: String, how: String)

case class WordRule(word: String, rule: Rule) {
}

object StemTree {

  val words: IndexedSeq[WordRule] = {
    Source.fromFile("dict.txt").getLines().flatMap {
      line => line.split(", ").toList match {
        case _ :: Nil => Nil
        case base :: other => {
          val revertedBase = base.reverse
          List(WordRule(revertedBase, Rule("", ""))) ++
            other.map(w => {
              val common = base.zip(w).takeWhile(el => el._1 == el._2).map(_._1).mkString
              WordRule(w.reverse, Rule(w.replaceFirst(common, "").reverse, base.replaceFirst(common, "").reverse))
            })
        }
      }
    }.toIndexedSeq.sortBy(_.word)
  }

  def matchingRules(end: String): Seq[WordRule] = {
    var ei = words.size - 1;
    var si = 0;
    while (si < ei) {
      val ni = (ei + si) / 2
      println(si, ni, ei, words(ni).word, end)
      if (words(ni).word > end) {
        ei = ni - 1
      } else {
        if (words(ni).word < end) {
          si = ni + 1
        } else {
          si = ni
          ei = ni
        }

      }
    }
    (si to words.size).toStream.map(words.apply).takeWhile(_.word.startsWith(end))
  }

  def getRule(end: String): Option[Rule] = {
    matchingRules(end) match {
      case Nil => None
      case rules => Some(rules.groupBy(_.rule).maxBy(_._2.size)._1)
    }
  }

}
