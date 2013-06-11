import collection.immutable.SortedMap
import DistanceManager._

object SpellChecker {

  case class State(produced: Map[String, Double], bestWords: IndexedSeq[(Double, String)])

  val maxWords = 5

  type W = (ProduceFuc, Double)

  val short = (polonicaWords _ :: typos _ :: ortErrorWords _ :: swaps _ :: Nil).map(_ -> 0.9)

  def mergeFunctions(l1: Seq[W], l2: Seq[W]): Seq[W] = {
    l1.flatMap {
      case (f1, w1) => l2.map {
        case (f2, w2) => concat(f1, f2) _ -> w1 * w2
      }
    }
  }

  val long = (replace _ :: add _ :: remove _ :: Nil).map(_ -> 0.5)

  val distanceFunc: Seq[W] =
    (short ++ long ++ mergeFunctions(short, short) ++ mergeFunctions(long, short) ++ mergeFunctions(mergeFunctions(long, short), short)
      ).toStream

  def spellCheck(word: String): Seq[String] = {
    var currentState = State(Map(word -> 1.0), IndexedSeq())
    distanceFunc.map {
      case (func, distance) =>
        currentState = produceNewState(func, currentState, word, distance)
        distance
    }.takeWhile(_ => keepSearching(currentState)).toList

    currentState.bestWords.take(5).map(_._2)
  }

  def produceNewState(func: ProduceFuc, state: State, w: String, distance: Double) = {
    val newWords = func(w).filterNot(state.produced.contains)
    val correct = newWords.filter(Dictionary.contains)

    val min = state.bestWords.lastOption.map(_._1).getOrElse(0.0)
    val added = correct.map(w => possibility(w, distance) -> w).filter(_._1 > min)

    val produced = newWords.flatMap {
      w =>
        state.produced.get(w).orElse(Option(distance))
          .filter(distance >=).map(w ->)
    }

    State(state.produced ++ produced, (state.bestWords ++ added).sorted.take(2 * maxWords))
  }

  def possibility(w: String, distance: Double): Double = distance

  def keepSearching(s: State) = {
    s.produced.size < 20000
  }


}
