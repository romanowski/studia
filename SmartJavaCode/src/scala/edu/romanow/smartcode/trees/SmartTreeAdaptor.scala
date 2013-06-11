package edu.romanow.smartcode.trees

import org.antlr.runtime.tree.CommonTreeAdaptor
import org.antlr.runtime.Token
import edu.romanow.smartcode.TokenUtil
import edu.romanow.smartcode.contexts.Context
import edu.romanow.antlr.JavaParser._
import sun.nio.cs.ext.PCK

/**
 * Created with IntelliJ IDEA.
 * User: jar
 * Date: 08.05.12
 * Time: 10:02
 * To change this template use File | Settings | File Templates.
 */

class SmartTreeAdaptor(initContext: Context) extends CommonTreeAdaptor {


  override def create(t: Token): BaseTree = {

    val out = if (t == null)
      new NullTree
    else
      t.getType match {
        case PCK_T => new PCKTree(t)
        case LINE_T => new LineTree(t)
        case IMPORT_T => new ImportTree(t) //for imports
        case BLOCK_T => new BlockTree(t)
        case METHOD_T => new FunctionTree(t)
        case VARIABLE_T => new VariableTree(t)
        case TYPE_NAME_T => new TypeTree(t)
        case VAR_NAME_T => new TypeTree(t)
        case METHOD_RET_VAL_T => new TypeTree(t)
        case IDENTIFIER => new IdentifierTree(t)
        case ARG_T => new ArgsTree(t)
        case EXP_T => new ExpressionTree(t)
        case FUN_ARG_T  => new FunctionParamTree(t)

        //enums and class
        case CLASS_T => new EnumClassTree(t)
        case ENUM => new EnumClassTree(t)

        case _ =>
          new BaseTree(t)
      }

    out.context = initContext
    out
  }


}
