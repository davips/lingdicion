/*
Based on the Andrew Forrest's adaptation for Scala
http://dysphoria.net/code/hindley-milner/HindleyMilner.scala

    Copyright 2019 Davi Pereira dos Santos
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
    along with lamdheal.  If not, see <http://www.gnu.org/licenses/>.

Implementation of basic polymorphic type-checking for a simple language.
Based heavily on Nikita Borisov's Perl implementation at
http://web.archive.org/web/20050420002559/www.cs.berkeley.edu/~nikitab/courses/cs263/hm.html
which in turn is based on the paper by Luca Cardelli at
http://lucacardelli.name/Papers/BasicTypechecking.pdf
Do with it what you will.
"Do with [original work] what you will", but respect the GPL license for the current version.
*/

package lamdheal

import lamdheal.AST._
import lamdheal.ASTtypes._

//case class Let(v: String, defn: Printer, body: Printer) extends Printer
//
//case class Letrec(v: String, defn: Printer, body: Printer) extends Printer

class TypeError(msg: String) extends Exception(msg)

class ParseTypeError(msg: String) extends Exception(msg)

object HindleyMilner {
  var last_line_with_errors = 0
  type Env = Map[String, ExprType]
  var globalEnv: Env = Map.empty
  var _nextVariableNameCounter = '§'
  var _nextVariableId = 0
  var last_new_var: Variable = null


  //   def FunctionT(from: ExprType, to: ExprType) = ComplexType("->", Array(from, to))

  lazy val MathOpT = FunctionT(NumberT, FunctionT(NumberT, NumberT))

  var last_types_with_errors = Array("", "")

  def nextUniqueName = {
    val result = _nextVariableNameCounter
    _nextVariableNameCounter = (_nextVariableNameCounter.toInt + 1).toChar
    result.toString
  }

  def newVariable: Variable = {
    val result = _nextVariableId
    _nextVariableId += 1
    last_new_var = Variable(result)
    last_new_var
  }

  //   def string(t: ExprType): String = t match {
  //      case v: Variable => v.instance match {
  //         case Some(i) => string(i)
  //         case None => v.id
  //      }
  //      case SimpleType(id) => id
  //      case ComplexType(id, args) => {
  //         if (args.length == 1)
  //            id + "_of_" + string(args(0))
  //         else if (args.length == 2)
  //            "(" + string(args(0)) + " " + id + " " + string(args(1)) + ")"
  //         else
  //            args.mkString(id + " ", " ", "")
  //      }
  //      case x => "" //throw new TypeError("Unmatched " + x + " to pretty-print.")
  //   }

  def check(ast: Expr) {
    _nextVariableNameCounter = 'A'
    // Known operators/keywords and their respective types.
    last_line_with_errors = 0
    val hmC = new HMContext

    def tryexp(ast: Expr) {
      last_line_with_errors += 1
      last_types_with_errors = Array("", "")
      val t = hmC.analyse(ast)
      if (ast != Empty) println("Type of " + ast + ": " + t + ".")
//      if (ast != Empty) println("Type of " + ast.dressed_string + ": " + t + ".")
    }

    ast match {
      case Program(l) => l foreach {
        tryexp(_)
      }
      case _ => tryexp(ast)
    }
  }

  class HMContext(var envi: Env = Map()) {

    def get_type_of_identifier(id: String, nongen: Set[Variable]): ExprType = {
      if (envi.contains(id))
        fresh(envi(id), nongen)
      else
        throw new ParseTypeError("at line " + last_line_with_errors + ": Undefined symbol " + id)
    }

    def fresh(t: ExprType, nongen: Set[Variable]) = {
      import scala.collection.mutable
      val mappings = new mutable.HashMap[Variable, Variable]

      def freshrec(tp: ExprType): ExprType = {
        prune(tp) match {
          case v: Variable =>
            if (isgeneric(v, nongen)) mappings.getOrElseUpdate(v, newVariable) else v
          case EmptyT => EmptyT
          case BooleanT => BooleanT
          case NumberT => NumberT
          case CharacterT => CharacterT
          case list: ListT =>
            ListT(freshrec(list.elements_type))
          case x => throw new Exception("Unmatched case: " + x)
          //               case SimpleType(name) => SimpleType(name)
          //               case ComplexType(name, args) =>
          //                  ComplexType(name, args.map(freshrec(_)))
        }
      }

      freshrec(t)
    }


    // Returns the currently defining instance of t.
    // As a side effect, collapses the list of type instances.
    def prune(t: ExprType): ExprType = t match {
      case v: Variable if v.instance.isDefined => {
        val inst = prune(v.instance.get) //var inst
        v.instance = Some(inst)
        inst
      }
      case _ => t
    }

    // Note: must be called with v 'pre-pruned'
    def isgeneric(v: Variable, nongen: Set[Variable]) = !(occursin(v, nongen))

    // Note: must be called with v 'pre-pruned'
    def occursintype(v: Variable, type2: ExprType): Boolean = {
      prune(type2) match {
        case `v` => true
        //            case ComplexType(name, args) => occursin(v, args)
        case _ => false
      }
    }

    def occursin(t: Variable, list_of_nongen: Iterable[ExprType]) =
      list_of_nongen exists (t2 => occursintype(t, t2))

    def register_types_and_throw_exception(str1: String, str2: String) = {
      last_types_with_errors(0) = str1
      last_types_with_errors(1) = str2
      throw new TypeError("at line " + last_line_with_errors + ": " + str1 + " expected, but " + str2 + " found.")
    }

    def unify(t1: ExprType, t2: ExprType) {
      val type1 = prune(t1)
      val type2 = prune(t2)
      (type1, type2) match {
        case (a: Variable, b) => if (a != b) {
          if (occursintype(a, b))
            throw new TypeError("at line " + last_line_with_errors + ": recursive unification.")
          a.instance = Some(b)
        }
        case (a, b: Variable) => unify(b, a)
        //         case (a: SimpleType, b: SimpleType) => {
        //            if (a.id != b.id) register_types_and_throw_exception(a.toString, b.toString)
        //         }
        //         //         case FunctionT(a, b) => {
        //         //            if (a.id != b.id ||
        //         //               a.args.length != b.args.length) register_types_and_throw_exception(a.toString, b.toString)
        //         //
        //         //            for (i <- 0 until a.args.length)
        //         //               unify(a.args(i), b.args(i))
        //         //         }
        //         case (a: ComplexType, b: SimpleType) => register_types_and_throw_exception(a.toString, b.toString)
        //         case (a: SimpleType, b: ComplexType) => register_types_and_throw_exception(a.toString, b.toString)
        case (a, b) => if (a.getClass != b.getClass) register_types_and_throw_exception(a.toString, b.toString)
      }
    }

    def analyse(ast: Expr): ExprType = analyse(ast, Set.empty)

    def math_op(a: Expr, b: Expr, nongen: Set[Variable]) = {
      val a_typed = analyse(a, nongen)
      val b_typed = analyse(b, nongen)
      unify(NumberT, a_typed)
      unify(NumberT, b_typed)
      NumberT
    }

    def analyse(ast: Expr, nongen: Set[Variable]): ExprType = ast match {
      case Empty => EmptyT
      case a@NumberExpr(n) => NumberT
      case CharacterExpr(s) => CharacterT
//      case BooleanExpr(s) => BooleanT
      case Add(a, b) => {
        try {
          math_op(a, b, nongen)
        } catch {
          case e: Throwable => {
            if (last_types_with_errors(0) == last_types_with_errors(1) && last_types_with_errors(0) == "'list'") {
              throw new TypeError(e.getMessage + "\nHint: only numbers can be added with '+'. To concatenate strings or lists use '++'.")
            } else {
              throw new TypeError(e.getMessage + "\nHint: only numbers can be added with '+'.")
            }
          }
        }
      }
      case Sub(a, b) => math_op(a, b, nongen)
      case Mul(a, b) => math_op(a, b, nongen)
      case Div(a, b) => math_op(a, b, nongen)
      case Pow(a, b) => math_op(a, b, nongen)
      case Resto(a, b) => math_op(a, b, nongen)
//      case Assign(id, e) => {
//        envi += (id -> analyse(e))
//        EmptyT
//      }
      case id: Ident => {
        id.t = get_type_of_identifier(id.name, nongen); id.t
      }
      case li: ListExpr => {
        val element_types = li.l map (e => analyse(e, nongen))
        if (li.t.getClass == Empty.getClass) li.t = element_types.head
        try {
          element_types.tail map (unify(element_types.head, _))
        } catch {
          case e: TypeError => throw new TypeError("at line " + last_line_with_errors + ": " + e.getMessage + "\nHint: all elements should have the same type inside a list.")
        }
        li.t
      }
//      case ConcatenateListExpr(a: Expr, b: Expr) => {
//        val aT = analyse(a, nongen)
//        val bT = analyse(b, nongen)
//        var str = ""
//        if (aT.getClass != ListT || bT.getClass != ListT)
//          str = "\nHint: only lists or strings can be concatenated ('++')."
//        if (aT.asInstanceOf[ListT].elements_type != bT.asInstanceOf[ListT].elements_type)
//          str = "\nHint: only lists of the same type can be concatenated ('++')."
//        try {
//          unify(aT, bT)
//        } catch {
//          case e: TypeError => throw new TypeError("at line " + last_line_with_errors + ": " + e.getMessage + str)
//        }
//        aT
//      }

      //EmptyT is not related to empty lists, just the opposite!
      //      case CommLineArgE => FunctionT(NumberT, ListT(CharT))
      //      case BlockE(exprs) => {
      //         val lT = exprs.map {
      //            e =>
      //               analyse(e, env, nongen)
      //         }
      //         lT.last
      //      }
//            case ApplyE(f, arg) => {
//               val funtype0 = analyse(f, env, nongen)
//               val argtype = analyse(arg, env, nongen)
//               funtype0 match {
//                  case ComplexType("'list'", elements_type) => {
//                     val nvar = newVariable
//                     unify(FunctionT(elements_type.head, nvar), argtype)
//                     ListT(nvar)
//                  }
//                  case x => {
//                     val funtype = x
//                     val resulttype = newVariable
//                     unify(funtype, FunctionT(argtype, resulttype))
//                     resulttype
//                  }
//               }
//            }
      //      case LambdaE(arg, body) => {
      //         val argtype = newVariable
      //         val resulttype = analyse(body, env + (arg -> argtype), nongen + argtype)
      //         FunctionT(argtype, resulttype)
      //      }
      //      case PrintE | PrintLnE => FunctionT(newVariable, EmptyT)
      //      case Show =>
      //      case Eval() => FunctionT(newVariable, EmptyT)
      //
      case exprs@ListaInterval(ini, fim     ) => {
        val iniT = analyse(ini, nongen)
        val fimT = analyse(fim, nongen)
        unify(iniT, NumberT)
        unify(fimT, NumberT)
        ListT(NumberT)
      }
    }
  }

}


//$x=0
//f = \\z z*z
//exprs = [1..10000] (\\y  ($x = $x + f y / 100; $x)  )
//`| $x
//n = "'`'\n"
//"the number is 'n'"

//as of rev. 266 (old HM inference):
//Time parsing 0.097s
//Time checking types 0.018s
//Time interpreting 0.567s
