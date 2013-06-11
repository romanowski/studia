object Metrics {
  def cosine(v1: Vectors.SimpleMapVector)(v2: Vectors.SimpleMapVector): Double =
    v1.foldLeft(0.0) {
      case (ret, (k, v)) => ret + v2.get(k).map(v *).getOrElse(0.0)
    } / (v1.size + v2.size)

  def dice(v1: Vectors.SimpleMapVector)(v2: Vectors.SimpleMapVector): Double =
    1 - (v1.foldLeft(0.0) {
      case (ret, (k, v)) => ret + v2.get(k).map(_ => 2.0).getOrElse(0.0)
    } / (v1.size + v2.size))


}
