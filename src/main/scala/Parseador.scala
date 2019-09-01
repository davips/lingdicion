/*  Copyright 2013 Davi Pereira dos Santos
    This file is part of lamdheal.
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

import util.parsing.combinator.{ImplicitConversions, JavaTokenParsers, RegexParsers}
import io.Source
import collection.mutable
import lamdheal.AST._
import lamdheal.ASTtypes._

object Parseador extends RegexParsers with ImplicitConversions with JavaTokenParsers {

  class ParserException(val str: String) extends Exception(str)

  implicit def toLogged(name: String) = new {
    def \[T](p: Parser[T]) = {
      println();
      log(p)(name)
    }
  }

  val separator = "," //¬\n"

  def t(input: String): String = {
    val input2 = input + ".lhe"
    //      //*println("Processing file [" + input2 + "]")
    val lines2 = Source.fromFile(input2).getLines().toList
    val lines = lines2.map {
      x =>
        if (x.startsWith("import ")) {
          //            t(x.substring(8, x.drop(8).indexOf("\"") + 8))
          t(x.drop(7))
        } else {
          x
        }
    }
    val res = lines.filterNot(_.startsWith("//")).filterNot(_.isEmpty).mkString(separator).replace("\r\n", "").trim
    val res2 = if (res.startsWith(separator)) res.drop(separator.length) else res
    val res3 = if (res2.startsWith("/*" + separator)) res2.drop(2 + separator.length) else res2
    val res4 = if (res3.endsWith("*/")) res3.dropRight(2) else res3
    if (res4.endsWith(separator)) res4 else res4 + separator
  }

  def parse(texto: String, web: Boolean): Expr = {
    val u = parseAll(program, texto)
    var line = 0
    u match {
      case Success(t, next) => {
        if (!web) u.get.l map {
          x =>
            //                  if (x != Empty) println(line + ": " + x.dressed_string)
            if (x != Empty) println(line + ": " + x)
            line += 1
        }
        u.get
      }
      case f => {
        val location = f.toString.split(": ").head.split('.').head.tail
        val error = "at line " + location + ": " + f.toString.split(": ").last
        var str = error
        if (error.contains("expected but `\"' found")) str += "\nHint: strings, like in Java, have strict rules around the characters '\\' and, obviously, " +
          "'\"'. '\\' normally is followed by one of the characters: 't', 'n', '\"', '\\' and 'r'; or it can end in strange parsing errors."
        throw new ParserException(str)
      }
    }
  }

  type P[+T] = Parser[T]

  lazy val program: Parseador.Parser[ListExpr] = list

  val list: P[ListExpr] = "[" ~> (rep1sep(assign | tuple, separator) ^^ { case l => ListExpr(l) }) <~ "]"

  lazy val ignore = """^(/\*)""".r ^^^ Empty | """^(\*/)""".r ^^^ Empty | """^(//)[^¬]*""".r ^^^ Empty | "" ^^^ Empty

  lazy val expr: P[Expr] = atomic_expr

  lazy val assign: P[ItemExpr] = identifier ~ (":" ~> atomic_expr) ^^ LambdaE
  lazy val tuple: P[ItemExpr] = atomic_expr ~ (":" ~> atomic_expr) ^^ TupleExpr


  //   def evaluate(h: Has_) = lambda(h) * ("|" ^^^ ApplyE)

  //   def tuple(h: Has_) = {
  //      val q: mutable.Queue[Has_] = mutable.Queue()
  //      val h2 = new Has_
  //      val e = lamdheal(h2) <~ ":"
  //      q.enqueue(h2)
  //      e ~! /*!*/ rep1sep({val h2 = new Has_; val e2 = lamdheal(h2); q.enqueue(h2); e2}, ":") ^^ {
  //         case a ~ b =>
  //            val exprs = List(a) ++ b
  //            Tuple((exprs zip q).map {
  //               case (i, bb) =>
  //                  if (bb.v) {
  //                     //h.v = false
  //                     //                     LambdaE(Ident("_", NumberExpr(0)), i)
  //                     LambdaE("_", i)
  //                  } else i
  //            })
  //
  //      } | lamdheal(h)
  //   }

  //   def lambda(h: Has_): P[Expr] = (("\\\\" | ",") ~> (identifier)) ~! lambda(h) ^^ {
  //      case name ~ body => LambdaE(name, body)
  //   } | equality(h)

  //   def equality(h: Has_) = sum(h) * ("==" ^^^ EqualE | "!=" ^^^ DiffE | ">=" ^^^ GreaterEqual | "<=" ^^^ LesserEqual | ">" ^^^ Greater | "<" ^^^ Lesser)

  //   def sum(h: Has_) = list_concatenation(h) * ("+" ^^^ Add | "-" ^^^ Sub)

  //   def list_concatenation(h: Has_) = product(h) * ("++" ^^^ ConcatenateListExpr)

  //   def product(h: Has_) = power(h) * ("*" ^^^ Mul | "/" ^^^ Div | "%" ^^^ Resto)
  //
  //   def power(h: Has_) = application(h) * ("^" ^^^ Pow)


  //deve ter um bom motivo para eu ter tirado o applicationlamb
  //sem ele, é preciso ( )s
  //   def applicationlamb(h: Has_) = application(h) ~ lamdheal(h) ^^ ApplyE | application(h)

  //   def application(h: Has_) = composition(h) * ("" ^^^ ApplyE)
  //
  //   var cc = -1
  //
  //   def composition(h: Has_): P[Expr] = {
  //      rep1sep(atomic_expr(h), not("..") ~> ".") ^^ {
  //         _.reduceRight(
  //            (a, b) => {cc += 1; LambdaE("§" + (cc + 64).toChar, ApplyE(a, ApplyE(b, Ident("§" + (cc + 64).toChar))))})
  //      }
  //   }

  //   def zipa(h: Has_): P[ExprType] = {
  //      (atomic_expr(h) <~ "&") ~! rep1sep(atomic_expr(h), "&") ^^ {case he ~ exprs => Zipa(Array(he) ++ exprs)} | atomic_expr(h)
  //   }


  //   def parse_string(s: String) = {
  //      //      println(">" + s + "<")
  //      val slices = s.split('\'')
  //      if (slices.length < 2) {
  //         val l = slices.length match {
  //            case 0 => ListExpr(List())
  ////            case 1 => ListExpr(s.toList map CharacterExpr)
  //         }
  //         //         exprs.t = HindleyMilner.CharT
  //         l.t = ListT(CharacterT)
  //         l
  //      } else {
  //         if (slices.length % 2 == 0) failure("Unmatched ' inside string '" + s + "'.")
  //         val list_of_strings = slices.zipWithIndex map {
  //            case (sl, i) =>
  //               if (i % 2 == 0) ListExpr(sl.toArray map CharacterExpr)
  //               else {
  //                  val ev = Eval()
  //                  //                  ev.t
  //                  ApplyE(Show, ApplyE(ev, ListExpr(sl.toCharArray map CharacterExpr)))
  //               }
  //         }
  //         val l = list_of_strings reduce (ConcatenateListExpr)
  //         l.t = ListT(CharacterT)
  //         l
  //      }
  //   }

  def transform(s: String) = s.replace("\\n", "\n").replace("\\”", "”")

  lazy val atomic_expr = {
    (
      identifier ^^ { x => Ident(x) }
        | wholeNumber ^^ { x => NumberExpr(x.toDouble) }
        //            | ("\"" | "“") ~> simple_string <~ ("\"" | "”") ^^ {case s => parse_string(transform(s))}
        //            | not_parseable_string
        ////            | boolean
        //            | inversor
        //            | arg
        | empty
        | "`" ^^^ PrintLnE | "`+" ^^^ PrintE
        | "@" ^^^ Takehead | "~" ^^^ Taketail | "!" ^^^ Reverse
        | ("\'" | "‘") ~> simple_character <~ ("\'" | "’") ^^ { x => CharacterExpr(transform(x).head) }
        | ("" ~! "") ~> failure("expression expected...")
      )
  }

  def simple_character = """([^"^”^'^’^\\]|\\[\\'"n])""".r

  def simple_string = """([^"^”^\\]|\\[\\'"n])*""".r

  lazy val identifier = mutable_ident | ident

  lazy val mutable_ident = """\$[a-zA-Z_]\w*""".r

  //   lazy val boolean = "true" ^^^ BooleanExpr(b = true) | "false" ^^^ BooleanExpr(b = false)

  //   def anon(h: Has_) = "_" ^^^ {
  //      h.v = true
  //      //            print(h.v + " ")
  //      Ident("_")
  //   }

  //   def lista(h: Has_) = lista1(h) | lista2_n(h) | empty_list

  //   def empty_list = "[\'" ~> declared_type <~ "\']" ^^ {
  //      type_expr =>
  //         val list = ListExpr(Array[Expr]())
  //         list.t = ListT(type_expr)
  //         list
  //   } | "[" ~! "]" ^^^ {
  //      failure("Empty lists must have an explicitly defined type." +
  //         "Examples of valid code: \"exprs = ['boolean']\", \"exprs = ['number']\" or " +
  //         "\"exprs = ['char']\".\nNote that a list filled with empty lists is not empty.")
  //      Empty
  //   }

  //   def not_parseable_string = ("\"" | "“") ~> "[^\"]+".r <~ ("\"" | "”") ^^ {
  //      case s => throw new ParserException("Invalid String.\nHint: There can be invalid scape sequences in \"" + s + "\". Valid escape sequences: \\n, \\', \\\" and \\\\.")
  //   }

  def declared_type: P[ExprType] = list_type | "boolean" ^^^ BooleanT | "number" ^^^ NumberT | "character" ^^^ CharacterT

  def list_type: P[ExprType] = "[" ~> declared_type <~ "]" ^^ { type_expr => ListT(type_expr) }

  //   def lista2_n(h: Has_) = {
  //      val q: mutable.Queue[Has_] = mutable.Queue()
  //      val h2 = new Has_
  //      val ee = "[" ~> "" ~! (rep(separator | ";") ~> expr(h2)) <~ rep(separator | ";") <~ ","
  //      q.enqueue(h2)
  //      ee ~! rep1sep({
  //         val h2 = new Has_
  //         val e = rep(separator | ";") ~> expr(h2) <~ rep(separator | ";")
  //         q.enqueue(h2)
  //         e
  //      }, ",") <~ "]" ^^ {
  //         case _ ~ he ~ l0 =>
  //            val l = Array(he) ++ l0
  //            val list = ListExpr((l zip q).map {
  //               case (i, b) =>
  //                  if (b.v) {
  //                     h.v = false
  //                     LambdaE("_", i)
  //                  }
  //                  else i
  //            })
  //            list.t = ListT(he.t)
  //            list
  //      }
  //   }

  //   def lista1(h: Has_) = {
  //      val h2 = new Has_
  //      "[" ~> "" ~! expr(h2) <~ "]" ^^ {
  //         case _ ~ ee =>
  //            val list = ListExpr(Array(if (h2.v) {
  //               h.v = false
  //               LambdaE("_", ee)
  //            }
  //            else ee
  //            ))
  //            list.t = ListT(ee.t)
  //            list
  //      }
  //   }

  //   def shell(h: Has_) = "{" ~> ("" ~! /*!*/ expr(h) <~ "}") ^^ {
  //      case _ ~ s => Shell(s)
  //   }
  //
  //   lazy val arg = "$" ^^^ CommLineArgE
  //
  //   class Has_ {
  //      var v = false
  //   }
  //
  //   def block(h: Has_) = {
  //      val h2 = new Has_
  //      "(" ~> ("" ~! /*!*/ rep1sep((expr(h2) | ""), separator | ";") <~ ")") ^^ {
  //         case _ ~ l =>
  //            if (h2.v) {
  //               //h.v = false
  //               LambdaE("_", BlockE(l.filter(_.isInstanceOf[Expr]).map(_.asInstanceOf[Expr])))
  //            } else {
  //               BlockE(l.filter(_.isInstanceOf[Expr]).map(_.asInstanceOf[Expr]))
  //            }
  //      }
  //   }
  //
  //   lazy val inversor = "?" ^^^ ApplicationInversor

  lazy val empty = "#" ^^^ Empty
}
