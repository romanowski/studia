import java.io.File
import management.ManagementFactory

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 05.06.13
 * Time: 00:04
 * To change this template use File | Settings | File Templates.
 */
object GenerateNgrams extends App {

  val in :: out :: _ = args.toList


  val f = new File(in)
  val files = (if (f.isDirectory) {
    f.list().filterNot(_.startsWith(".")).map((in + "/") +).toList
  } else in :: Nil)


  def ngramsFromFiles(files: List[String], n: Int): Seq[(String, Int)] = {
    (files.foldLeft(scala.collection.mutable.Map[String, Int]()) {
      case (map, file) =>
        println(s"start for $n for file $file")
        NGramBuilder.createNGrams(file, n, map)
    }).toSeq.sortBy(_._2)
  }

  def withProb(ngrams: Seq[(String, Int)]): Seq[(String, Double)] = {
    val all = ngrams.foldLeft(0) {
      case (sum, (_, v)) => sum + v
    }.toDouble
    ngrams.map(el => el._1 -> (el._2 / all))
  }


  (2 to 10) foreach {
    n =>
      printToFile(new File(out.replace("#", n.toString))) {
        pw =>
          val ngrams = withProb(ngramsFromFiles(files, n))

          ngrams.foreach(el => pw.print(s"${el._2 } : ${el._1.replace('#', ' ')}\n"))
      }
  }

  def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try {
      op(p)
    } finally {
      p.close()
    }
  }

}
