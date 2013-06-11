import io.Source
import util.Random

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 24.04.13
 * Time: 11:05
 * To change this template use File | Settings | File Templates.
 */
object GenrateRandom extends App {

  val buffer = Source.fromFile("dict2.txt").getLines().toBuffer
  val size = buffer.size

  def printToFile(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter("random.txt")
    try {
      op(p)
    } finally {
      p.close()
    }
  }

  printToFile {
    pw => (1 to 20000).foreach {
      _ => pw.write(buffer(Random.nextInt(size)) + "\n")
    }
  }


}

