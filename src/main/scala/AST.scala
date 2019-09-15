/*  Copyright 2019 Davi Pereira dos Santos
    This file is part of lingdicion.
    Initially written according to the guidelines in the Masterarbeit of Eugen Labun.

    Lamdheal is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Lamdheal is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with lamdheal.  If not, see <http://www.gnu.org/licenses/>. */
package lamdheal

import lamdheal.ASTtypes._

object ASTtypes {

  sealed abstract class ExprType

  case object EmptyT extends ExprType {
    override def toString = "'empty'"
  }

  case object BooleanT extends ExprType {
    override def toString = "'boolean'"
  }

  case object NumberT extends ExprType {
    override def toString = "'number'"
  }

  case object CharacterT extends ExprType {
    override def toString = "'character'"
  }

  case class ListT(elements_type: ExprType) extends ExprType {
    override def toString = "'list of " + elements_type + "'"
  }

  case class FunctionT(from: ExprType, to: ExprType) extends ExprType

  case class Variable(id: Int) extends ExprType {
    var instance: Option[ExprType] = None
    lazy val name = HindleyMilner.nextUniqueName
  }

}

object AST {

  sealed abstract class Expr {
    //      def dressed_string = toString

    var t: ExprType = _
  }

  sealed abstract class TypeE extends Expr {
    def jvm: String
  }

  case object Empty extends Expr {
    t = EmptyT

    override def toString = "Ø"
  }

  //  case class BooleanExpr(b: Boolean) extends Expr {
  //    t = BooleanT
  //
  //    override def toString = b.toString
  //
  //    def jvm = "Z"
  //  }

  case class NumberExpr(n: Double) extends TypeE {
    t = NumberT

    override def toString = n.toString

    def jvm = "D"
  }

  case class CharacterExpr(c: Char) extends TypeE {
    t = CharacterT

    //    override def dressed_string = "'" + c.toString + "'"

    override def toString = c.toString

    def jvm = "C"
  }

  sealed abstract class ItemExpr extends Expr

  case class PatternExpr(k: Expr, v: Expr) extends ItemExpr {
    override def toString = (k -> v).toString
  }

  case class AssignExpr(k: Expr, v: Expr) extends ItemExpr {
    override def toString = (k -> v).toString
  }

  case class LambdaE(param: String, body: Expr) extends ItemExpr {
    override def toString = param + " => " + body
  }

  case class ListExpr(l: List[ItemExpr]) extends Expr {
    t = ListT(EmptyT)

    override def toString: String = "[" + l.mkString(", ") + "]"

    //    override def dressed_string = dress(t, exprs)
  }

  def nake(t: ExprType, exprs: Array[Expr]) = if (t == ListT(CharacterT)) str2print(exprs.mkString) else "[" + exprs.mkString(", ") + "]"

  //  def dress(t: ExprType, exprs: Array[Expr]) = if (t == ListT(CharacterT)) "\"" + str2print(exprs.mkString) + "\"" else "[" + exprs.map(_.dressed_string).mkString(", ") + "]"

  def str2print(str: String) = str.replace("\n", "\\n").replace("”", "\\”")

  //  case class ConcatenateListExpr(e1: Expr, e2: Expr) extends Expr {
  //    t = ListT(EmptyT)
  //
  //    override def toString = e1 + " ++ " + e2
  //
  //    override def dressed_string = e1.dressed_string + " ++ " + e2.dressed_string
  //  }
  //
  //  case class Assign(name: String, expr: Expr) extends Expr {
  //    t = EmptyT
  //
  //    override def toString = name + " = " + expr.toString
  //
  //    override def dressed_string = name + " = " + expr.dressed_string
  //  }

  case class Shell(e: Expr) extends Expr

  case class Eval() extends Expr

  case object Show extends Expr

  case object CommLineArgE extends Expr

  case class Program(l: Array[Expr]) extends Expr {
    override def toString = "\n" + l.mkString("\n")
  }

  case class BlockE(l: List[Expr]) extends Expr {
    override def toString = "(" + l.mkString("\n ") + ")"
  }

  //   case class Zipa(exprs: List[ExprType]) extends ExprType

  case object Reverse extends Expr {
    override def toString = "!"
  }

  case object Takehead extends Expr {
    override def toString = "@"
  }

  case object Taketail extends Expr {
    override def toString = "~"
  }


  case class Scalacode(items: List[String]) extends Expr {
    //      override def toString = "Scalacode{\n" + items.mkString("|") + "\n}"
  }


  case object PrintE extends Expr

  case object PrintLnE extends Expr

  case object ApplicationInversor extends Expr {
    override def toString = "?"
  }

  case class EqualE(e1: Expr, e2: Expr) extends Expr

  case class DiffE(e1: Expr, e2: Expr) extends Expr

  case class GreaterEqual(e1: Expr, e2: Expr) extends Expr

  case class LesserEqual(e1: Expr, e2: Expr) extends Expr

  case class Greater(e1: Expr, e2: Expr) extends Expr

  case class Lesser(e1: Expr, e2: Expr) extends Expr

  //   sealed abstract case class MathOpE(e1: ExprType, e2: ExprType) extends ExprType {
  //      override def toString = e1 + " " + f + " " + e2
  //
  //      def f: String
  //   }

  case class Add(e1: Expr, e2: Expr) extends Expr {
    def f = "+"
  }

  case class Sub(e1: Expr, e2: Expr) extends Expr {
    def f = "-"
  }

  case class Mul(e1: Expr, e2: Expr) extends Expr {
    def f = "*"
  }

  case class Div(e1: Expr, e2: Expr) extends Expr {
    def f = "/"
  }

  case class Resto(e1: Expr, e2: Expr) extends Expr {
    def f = "%"
  }

  case class Pow(e1: Expr, e2: Expr) extends Expr {
    def f = "^"
  }

  case class Neg(e1: Expr) extends Expr

  case class ApplyE(func: Expr, arg: Expr) extends Expr {
    override def toString = "(" + func + " " + arg + ")"
  }

  case class PrependE(item: Expr, list: Expr) extends Expr {
    override def toString =  item + " & " + list
  }

  case class UnpackE(item: Expr, list: Expr) extends Expr {
    override def toString = item + " & " + list
  }

  case class NotApplicable(e1: Expr, arg: Expr) extends Expr

  case class Ident(name: String) extends Expr {
    override def toString = name
  }

  case class Default(value: Expr) extends Expr {
    override def toString = "Def{" + value + "}"
  }


  case class Tuple(l: List[Expr]) extends Expr {
    override def toString = "Tupla{" + l.mkString(": ") + "}"
  }

  case class ListaInterval(a: Expr, b: Expr) extends Expr {
    override def toString = "[" + a + " .. " + b + "]"
  }


  //   case class StrE(s: String) extends TypeE {
  //      def toLista = ListExpr(s.toArray map CharacterExpr)
  //
  //      def jvm = "Ljava/lang/String;"
  //   }


  //   case class ListaAppInv(exprs: List[ExprType]) extends ExprType {
  //      override def toString = "[}" + exprs.mkString(", ") + "{]"
  //      def dressed_string = "[}" + exprs.mkString(", ") + "{]"
  //   }


  case object Free extends TypeE {
    def jvm = "Free"
  }


  //  case class Closure(ctxt: Interpreter.Context, lam: LambdaE) extends Expr {
  //    override def toString = "Closure " +
  //      lam.param.toString + ",\n Body{" + lam.body + "}\n)"
  //
  //    def apply(arg: Expr) = {
  //      val newctxt = new Interpreter.Context(ctxt.env ++ List(lam.param -> arg))
  //      val res = newctxt eval lam.body
  //      ctxt.env = ctxt.env ++ newctxt.env.filter(x => x._1.startsWith("$") && ctxt.env.keySet.contains(x._1)) //mutability
  //      res
  //    }
  //  }

}






