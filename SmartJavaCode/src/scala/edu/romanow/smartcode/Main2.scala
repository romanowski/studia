package edu.romanow.smartcode

import contexts.{GodContext, ContextTreeWalker}
import org.antlr.runtime.{TokenRewriteStream, ANTLRStringStream}
import edu.romanow.antlr.{JavaParser, JavaLexer}
import java.io.File
import io.Source
import edu.romanow.smartcode.trees.{SmartTreeAdaptor, BaseTree}

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 08.05.12
 * Time: 18:12
 * To change this template use File | Settings | File Templates.
 */

object Main2 extends App {

  if (args.length < 2) {
    print("usage: smartCode srcDir outDir")
  } else {
    FileUtils.handleDirRec(args(0), args(1))
  }

}
