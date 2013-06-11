package edu.romanow.smartcode

import contexts.{ImportWalker, ContextManager, ContextTreeWalker, GodContext}
import java.io.{InputStream, FileInputStream, FileOutputStream, File}
import trees.{SmartTreeAdaptor, BaseTree}
import edu.romanow.antlr.{JavaParser, JavaLexer}
import org.antlr.runtime.{TokenRewriteStream, ANTLRStringStream}
import io.Source
import sun.nio.ch.IOUtil
import annotation.tailrec
import java.io

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 9/19/12
 * Time: 12:28 AM
 * To change this template use File | Settings | File Templates.
 */
object FileUtils {


  def copy(in: InputStream, out: File) = {
    var tmp = new Array[Byte](1024)
    var read = 0;
    val fo = new FileOutputStream(out)

    do {
      if (read > 0) {
        fo.write(tmp, 0, read)
      }
      read = in.read(tmp)

    } while (read > 0)
    fo.close()

  }

  /*new FileOutputStream(out) getChannel() transferFrom(
new FileInputStream(in) getChannel, 0, Long.MaxValue)*/

  def handleDirRec(inPath: String, outPath: String) {
    try {
      val out = new File(outPath)
      out.mkdirs()

      val css = new File(out, "css")
      css.mkdirs()
      val js = new File(out, "js")
      js.mkdir()


      val loadRes = getClass.getResourceAsStream _

      copy(loadRes("/css/style.css"), new File(css, "style.css"))
      copy(loadRes("/js/code.js"), new File(js, "code.js"))
      copy(loadRes("/js/jquery.js"), new File(js, "jquery.js"))

      // getClass.

      val in = new File(inPath)

      val list = in.list().flatMap(name => handleInner(name, in))

      list.foreach {
        el =>
          ContextTreeWalker.walk(el._2)
      }
      list.foreach {
        el =>
          val file = new File(out, el._1.replace(".java", ".html"))
          file.getParentFile.mkdirs()
          Util.printToFile(file) {
            writter =>
              writter.write(HTMLGenerator.compact(HTMLGenerator.generate(el._2, getRelativePath(file, out))))
          }
      }

      ContextManager
    } catch {
      case e => e.printStackTrace()
    }
  }


  def relPath(form: String, to: String): String = {

    def skip(el: (List[String], List[String])): (List[String], List[String]) =
      el match {
        case (fh :: ft, th :: tt) if fh == th =>
          skip(ft -> tt)
        case _ =>
          el
      }

    skip(form.split(File.separator).dropRight(1).toList -> to.split(File.separator).toList) match {
      case (formList, toList) =>
        (formList.map(_ => "..") ::: toList) mkString (File.separator)
    }
  }

  def getRelativePath(from: File, to: File): String = {
    relPath(from.getAbsolutePath, to.getAbsolutePath)
  }


  def handleInner(fileName: String, base: File): Seq[(String, BaseTree)] = {
    val file = new File(base, fileName)
    if (file.isDirectory) {
      file.list().flatMap(name => handleInner(fileName + File.separator + name, base))
    }
    else {
      try {
        if (fileName.endsWith(".java")) {
          val lex = new JavaLexer(new ANTLRStringStream(Source.fromFile(file.getAbsolutePath).mkString))
          val tokens = new TokenRewriteStream(lex)
          val parser = new JavaParser(tokens)

          parser.setTreeAdaptor(new SmartTreeAdaptor(GodContext.createForFile(fileName.replace(".java", ".html"))))
          val compilationUnit = parser.compilationUnit()

          compilationUnit.getTree match {
            case bt: BaseTree =>
              List(fileName -> ImportWalker.walk(bt))
            case _ =>
              Nil
          }
        } else Nil
      } catch {
        case e =>
          println("Bad file: " + file.getAbsolutePath)
          //e.printStackTrace()
          Nil
      }
    }
  }


  def time[A](func: => A, what: String): A = {
    val t = System.nanoTime()

    val out = func

    println("%s took %d millis.".format(what, (System.nanoTime() - t) / 1000000))
    out
  }

}
