import scala._
import scala.annotation.tailrec


object Clustering {

  def cluster[T](distanceFunc: T => T => Double, th: Double)(vectors: Seq[Vector[T]]): Seq[Seq[Vector[T]]] = {

    def inCluster(v: Vector[T], cluster: Seq[Vector[T]]) = cluster.forall(v2 => distanceFunc(v.vec)(v2.vec) > th)

    @tailrec
    def addToCluster(v: Vector[T], data: List[List[Vector[T]]], list: List[List[Vector[T]]]): List[List[Vector[T]]] = data match {
      case Nil => List(v) :: list
      case h :: t if inCluster(v, h) => (v :: h) :: (list ++ t) //Stop here?
      case h :: t => addToCluster(v, t, h :: list)
    }

    vectors.foldLeft(List[List[Vector[T]]]()) {
      case (Nil, el) => List(List(el))
      case (list, el) =>
        addToCluster(el, list, Nil)
    }
  }
}