import java.io.{File, PrintWriter}

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 29.11.12
 * Time: 20:01
 * To change this template use File | Settings | File Templates.
 */
object Util {

  def writeInFile(name: String, content: String) {
    val pw = new PrintWriter(new File(name))
    try {
      pw.write(content)
    } finally {
      pw.close()
    }
  }


}
