object Main extends App {
  Clustering.cluster(Metrics.cosine, 0.32)(io.Source.stdin.getLines
    .toList
    .map(VectorCreator.createWords)
    .map(VectorCreator.vectorMap(Vectors.ngramsCreator(4))))
    .foreach {
    line => println(line.map(_.line).mkString("", "\n", "\n##\n"))
  }
}
