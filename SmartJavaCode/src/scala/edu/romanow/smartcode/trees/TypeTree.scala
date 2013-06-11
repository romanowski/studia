package edu.romanow.smartcode.trees

import org.antlr.runtime.Token
import edu.romanow.smartcode.{Extractor, TokenUtil}
import edu.romanow.antlr.JavaParser._
import edu.romanow.smartcode.contexts._
import net.liftweb.common.{Box, Empty, Full}
import edu.romanow.smartcode.html.HTMLUtil
import xml.{Node, Utility}

/**
 * Created with IntelliJ IDEA.
 * User: krzysiek
 * Date: 9/16/12
 * Time: 8:39 AM
 * To change this template use File | Settings | File Templates.
 */
class TypeTree(t: Token) extends ImaginaryTree(t) {

  override def asXML() =
    HTMLUtil.concat(traverse(this.basicChildren, context).map {
      el => el match {
        case (Full(loc), list) =>

          val file = context.fileContext.fileName
          if (file != loc.file)
            loc.toString

          <a class={"type"}
             onclick={loc.fullLink(context.file)}
             href="javascript://"
             onmouseover={loc.onHover(context.file)}
             onmouseout={HTMLUtil.onMouseOut}>
            {el._2.flatMap(_.asXML)}
          </a>

        case (_, list) =>
          HTMLUtil.concat(list map (_.asXML))
      }
    })


  def href = typeName


  def traverse(current: List[BaseTree], context: Context): (List[(Box[Location], List[BaseTree])]) =
    current match {
      case child :: args :: tail if args.getType == ARG_T =>
        getFunc(context, child.getText, args.all(EXP_T)) match {
          case (Full(loc), Full(ctx)) =>
            val res = traverse(tail, ctx)
            (Full(ctx.thisLoc) -> List(child, args)) :: res
          case (Full(loc), _) =>
            List((Full(loc) -> List(child, args)), Empty -> tail)
          case _ =>
            List(Empty -> current)
        }
      case child :: tail =>
        child.getType match {
          case THIS =>
            val res = traverse(tail, context.thisClass)
            (Full(context.thisLoc) -> List(child)) :: res
          case SUPER =>
            context.thisClass.parentClassContext match {
              case Full(ctx) =>
                val res = traverse(tail, ctx)
                (Full(ctx.thisLoc) -> List(child)) :: res
              case _ =>
                List(Empty -> current)
            }
          case IDENTIFIER =>
            val name = child.token.getText
            context.varLoc(name) match {
              case Full(loc) =>
                context.fileContext.classContext(loc.v.varType) match {
                  case Full(ctx) =>
                    val res = traverse(tail, ctx)
                    (Full(loc) -> List(child)) :: res
                  case _ =>
                    (Full(loc) -> List(child)) :: (Empty -> tail) :: Nil
                }
              case _ =>
                val file = child.context.file
                context.fileContext.classContext(name) match {
                  case Full(ctx) =>
                    val res = traverse(tail, ctx)
                    (Full(ctx.thisLoc) -> List(child)) :: res
                  case _ =>
                    this.context.fileContext.fullName(name) match {
                      case Full(name) =>
                        (Full(NotKnownLocation(name)) -> List(child)) :: (Empty -> tail) :: Nil
                      case _ =>
                        (Empty -> List(child)) :: (Empty -> tail) :: Nil
                    }

                }
            }
          case _ =>
            val res = traverse(tail, context)
            (Empty -> List(child)) :: res
        }
      case Nil => Nil
    }


  //TODO params types -> try
  def getFunc(context: Context, name: String, args: List[BaseTree]): (Box[Location], Box[Context]) = {
    context.functionLoc(name, args.size) match {
      case Full(loc: FuncLocation) =>
        (Full(loc), ContextManager.get(loc.func.retVal))
      case Full(loc) =>
        (Full(loc), Empty)
      case _ =>
        (Full(NotKnownLocation(context.thisClass.clazz.fullName + "." + name)), Empty)
    }
  }


  lazy val typeName = Extractor.extractType(this)

}
