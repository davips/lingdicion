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

import util.parsing.combinator.{ImplicitConversions, JavaTokenParsers, PackratParsers, RegexParsers}
import io.Source
import collection.mutable
import lamdheal.AST._
import lamdheal.ASTtypes._

import scala.util.matching.Regex

object Parseador extends RegexParsers with ImplicitConversions with JavaTokenParsers with PackratParsers {

  class ParserException(val str: String) extends Exception(str)

  implicit def toLogged(name: String) = new {
    def \[T](p: Parser[T]) = {
      println();
      log(p)(name)
    }
  }

  def parse(texto: String, web: Boolean) = {
    val texto2 = if (texto.startsWith("[")) texto else s"[$texto]"
    val texto3 = texto2.replaceAll(""" *(\r|\n|\r\n|\n\r)""", "\n")
    println(texto3)
    val u = parseAll(dict, texto3)
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
        println(f)
      }
    }
    u
  }

  type P[+T] = PackratParser[T]

  lazy val dict: P[ListExpr] = "[" ~> (rep1sep(assign | tuple, ",") ^^ ListExpr) <~ "]"
  lazy val assign: P[ItemExpr] = (unpack|identifier) ~ ("→" ~> (prepend |appl | expr)) ^^ AssignExpr
  lazy val tuple: P[ItemExpr] = (prepend|expr ) ~ ("←" ~> ( unpack|appl | expr)) ^^ PatternExpr
  lazy val expr: P[Expr] = {
    (prepend
      | identifier
      | dict
      | wholeNumber ^^ { x => NumberExpr(x.toDouble) }
      //            | ("\"" | "“") ~> simple_string <~ ("\"" | "”") ^^ {case s => parse_string(transform(s))}
      //            | not_parseable_string
      | ("\'" | "‘") ~> simple_character <~ ("\'" | "’") ^^ { x => CharacterExpr(transform(x).head) }
      | ("" ~! "") ~> failure("expression expected..."))
  }
  lazy val identifier: P[Expr] = ident ^^ { x => Ident(x) }
  lazy val appl: P[Expr] = ((appl | dict | identifier) ~ expr) ^^ ApplyE

  lazy val headtail:P[Expr ~ Expr] = expr ~ ("&" ~> expr)
  lazy val prepend: P[Expr] = headtail ^^ PrependE
  lazy val unpack: P[Expr] = headtail ^^ UnpackE

  //   def equality(h: Has_) = sum(h) * ("==" ^^^ EqualE | "!=" ^^^ DiffE | ">=" ^^^ GreaterEqual | "<=" ^^^ LesserEqual | ">" ^^^ Greater | "<" ^^^ Lesser)

  //   def sum(h: Has_) = list_concatenation(h) * ("+" ^^^ Add | "-" ^^^ Sub)

  //   def list_concatenation(h: Has_) = product(h) * ("++" ^^^ ConcatenateListExpr)

  //   def product(h: Has_) = power(h) * ("*" ^^^ Mul | "/" ^^^ Div | "%" ^^^ Resto)
  //
  //   def power(h: Has_) = application(h) * ("^" ^^^ Pow)


  def transform(s: String) = s.replace("\\n", "\n").replace("\\”", "”")


  def simple_character = """([^"^”^'^’^\\]|\\[\\'"n])""".r

  def simple_string = """([^"^”^\\]|\\[\\'"n])*""".r


  //  lazy val mutable_ident = """\$[a-zA-Z_]\w*""".r

  //   lazy val boolean = "true" ^^^ BooleanExpr(b = true) | "false" ^^^ BooleanExpr(b = false)

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

}
