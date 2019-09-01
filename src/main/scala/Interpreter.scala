///*  Copyright 2013 Davi Pereira dos Santos
//    This file is part of lamdheal.
//    Initially written according to the guidelines in the Masterarbeit of Eugen Labun.
//
//    Lamdheal is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Lamdheal is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with lamdheal.  If not, see <http://www.gnu.org/licenses/>. */
//package lamdheal
//
//import tools.nsc.interpreter.Results.{Incomplete, Success}
//import javax.script.ScriptException
//import java.io.{FileOutputStream, PrintStream, PrintWriter}
//
//import io.Source
//import tools.nsc.{Settings, interpreter}
//import collection.mutable
//import sys.process.Process
//
//object Interpreter {
//
//   import AST._
//
//   var args = List[String]()
//
//   var web = true
//
//   class RuntimeError(str: String) extends Exception(str)
//
//   def eval(ast: Expr, args1: List[String], web1: Boolean) {
//      args = args1
//      web = web1
//      val c = new Context
//      try {
//         if (!web) {
//            //            println("\n\n AST final:" + c.eval(ast))
//            c.eval(ast)
//         } else {
//            c.eval(ast)
//         }
//      } catch {
//         case e: Exception => throw new RuntimeError("" + e.getMessage)
//      }
//   }
//
//   type Environment = collection.immutable.Map[String, Expr]
//
//   // built-in
//   val initEnv: Environment = Map()
//
//
//   val out = new PrintStream(new FileOutputStream("/dev/null"))
//   //lil.log")) // System.out
//   val flusher = new java.io.PrintWriter(out)
////   val interpret = {
////     val settings = new Settings
////     settings.usejavacp.value = true
////     settings.deprecation.value = true
////     settings.embeddedDefaults(this.getClass().getClassLoader())
////     new scala.tools.nsc.interpreter.IMain(settings)
////   }
//
//   def warning(txt: String) { println("Warning: " + txt) }
//
//   def transpose_unequal[A](xs: List[List[A]]): List[List[A]] = xs.filter(_.nonEmpty) match {
//      case Nil => Nil
//      case ys: List[List[A]] => ys.map {
//         _.head
//      } :: transpose_unequal(ys.map {
//         _.tail
//      })
//   }
//
//   //warm start
//   //   interpret.interpret(" ")
//   class Context(var env: Environment = initEnv) {
//      override def toString = "Environment: " + env.mkString(", ")
//
//      def evalas(x: Expr) = {
//         val ex = eval(x)
//         if (x != ex) x + " evaluated as " + ex else x
//      }
//
//      def eval(ex: Expr): Expr = ex match {
//         //         case NotApplicable(a, b) => sys.error(a + " is not applicable to " + b + ".")
//         case lam: LambdaE => {
//            Closure(this, lam)
//
//         }
////         case Shell(e: Expr) => {
////            eval(e) match {
////               case l: ListExpr => {
////                  if (web) {
////                     sys.error("Shell commands cannot be executed in web mode.")
////                  } else {
////                     executa_shell(l.toString)
////                  }
////               }
////               case x => sys.error("Shell command needs to be string, not " + evalas(e) + ".")
////            }
////         }
//         //         case Zipa(li: List[ExprType]) => {
//         //            val liv = li map eval
//         //            if (liv.forall(_.isInstanceOf[ListExpr])) {
//         //               val liv2 = liv.map(x => x.asInstanceOf[ListExpr].exprs)
//         //               val maxl = liv2.maxBy(x => x.length).length
//         //               val l2 = liv2.map(x => x ++ (1 to maxl - x.length).toList.map(_ => Empty))
//         //               val l3 = l2.transpose
//         //               ListExpr(l3 map Tuple)
//         //            } else {
//         //               sys.error("Only lists can be tupled, not " + evalas(liv.head) + liv.map(x => " and " + evalas(x)) + ".")
//         //            }
//         //         }
//         case ApplyE(c1: Expr, c2: Expr) => {
//            (eval(c1), c2) match {
//               case (Takehead, arg: Expr) => {
//                  eval(arg) match {
//                     case ListExpr(l) => l.head
//                     //                     case ListaAppInv(exprs) => exprs.head
//                     case x => sys.error("@ needs a list_of_nongen or string to take the head off, not " + evalas(arg) + ".")
//                  }
//               }
////               case (CommLineArgE, e: Expr) => eval(e) match {
////                  case a@NumberExpr(i) => {
////                     println("Tipo: " + a.t)
////                     if (i >= args.length) {
////                        sys.error(i + " out of arguments bounds " + args.length)
////                     } else {
////                        ListExpr(args(i.toInt).toCharArray map CharacterExpr)
////                     }
////                  }
////                  case i => sys.error(evalas(e) + " is not a number to get argument")
////               }
////               case (Show, e: Expr) => ListExpr(e.toString.toCharArray map CharacterExpr)
////               case (ev: Eval, e: Expr) =>
////                  val ast = Parseador.parse(e.dressed_string, web = false)
//////                  HindleyMilner.check(EqualE(ev.t, ast))
////                  eval(ast)
//
//               case (PrintE, e: Expr) => {print(eval(e)); Empty}
//               case (PrintLnE, e: Expr) => {println(eval(e)); Empty}
//               case (f: Closure, a: Expr) => f(eval(a)) //coloquei eval(a), pq variaveis externas definidas depois do lamdheal (mesmo antes da app) não pertencem ao escopo interno.
//               //               case (ListaAppInv(exprs), a: ExprType) => {
//               //                  val lis = exprs.map(x => eval(ApplyE(x, a))).filterNot(Empty ==)
//               //                  if (lis.forall(_.isInstanceOf[Default]))
//               //                     ListExpr((lis map {case Default(xx) => xx; case xx => xx}).filterNot(Empty ==))
//               //                  else
//               //                     ListExpr(lis.filterNot(_.isInstanceOf[Default]).filterNot(Empty ==))
//               //               }
//               //               case (ApplicationInversor, a: ExprType) => eval(a) match {
//               //                  case ListExpr(exprs) => ListaAppInv(exprs)
//               //                  case _ => sys.error("'?' is not applicable to " + evalas(a) + ".")
//               //               }
//               //               case (Reverse, a: ExprType) => eval(a) match {
//               //                  case ListExpr(exprs) => ListExpr(exprs.reverse)
//               //                  case ListaAppInv(exprs) => ListaAppInv(exprs.reverse)
//               //                  case StrE(exprs) => StrE(exprs.reverse)
//               //                  case _ => sys.error("'?' is not applicable to " + evalas(a) + ".")
//               //               }
//               //               case (StrE(l0), a: ExprType) => {
//               //                  val exprs = l0.toList.map(CharacterExpr)
//               //                  val lis = exprs.map(x => eval(ApplyE(a, x))).filterNot(Empty ==)
//               //                  if (lis.forall(_.isInstanceOf[Default]))
//               //                     ListExpr((lis map {
//               //                        case Default(xx) => xx
//               //                        case xx => xx
//               //                     }).filterNot(Empty ==))
//               //                  else
//               //                     ListExpr(lis.filterNot(_.isInstanceOf[Default]).filterNot(Empty ==))
//               //               }
//               case (ListExpr(exprs), a: Expr) => {
//                  val lis = exprs.map(x => eval(ApplyE(a, x))).filterNot(Empty == _)
//                  if (lis.forall(_.isInstanceOf[Default]))
//                     ListExpr((lis map {case Default(xx) => xx; case xx => xx}).filterNot(Empty == _))
//                  else
//                     ListExpr(lis.filterNot(_.isInstanceOf[Default]).filterNot(Empty == _))
//               }
//               case (Tuple(exprs), a: Expr) => {
//                  var default = false
//                  val rets = exprs.dropRight(1) map {
//                     x => eval(ApplyE(x, a)) match {
//                        case NotApplicable(e1, e2) => eval(EqualE(e1, e2))
//                        case b: BooleanExpr => b
//                        case d: Default => {default = true; d}
//                        case other: Expr => eval(EqualE(other, a))
//                     }
//                  }
//                  val res = eval(if (exprs.last == LambdaE("_", Ident("_"))) a else exprs.last)
//                  //                  val res = eval(if (exprs.last == LambdaE(Ident("_", Free), Ident("_", Free))) a else exprs.last)
//                  if (default) {
//                     Default(res)
//                  } else {
//                     if (rets forall (BooleanExpr(b = true) == _)) res else Empty
//                  }
//               }
//               case (Empty, e: Expr) => Default(e)
//               case (a: NumberExpr, b: Expr) => eval(b) match {
//                  case nb@NumberExpr(vb) => if (vb < 0) eval(Add(a, nb)) else NotApplicable(a, b)
//                  case _ => NotApplicable(a, b)
//               }
//               case (a: Expr, b: Expr) => NotApplicable(a, b) //{warning(evalas(a) + " is not applicable to " + evalas(b) + "."); NotApplicable(a, b)}
//            }
//         }
//         case Program(exprs) => ListExpr(exprs map eval)
//         case BlockE(exprs) => {
//            val newenv = env
//            val context = new Context(newenv)
//            val res = exprs map (context.eval)
//            env = env ++ context.env.filter(x => x._1.startsWith("$") && env.keySet.contains(x._1)) //mutability
//            val notempty = res.filterNot(Empty == _)
//            if (notempty.nonEmpty) res.filterNot(Empty == _).last else Empty
//         }
//         case ListaInterval(a: Expr, b: Expr) => (eval(a), eval(b)) match {
//            case (NumberExpr(v1), NumberExpr(v2)) => ListExpr((v1.toInt to v2.toInt).toArray map (_.toDouble) map NumberExpr)
//            case _ => sys.error("'..' requires two numbers, not " + evalas(a) + "  and  " + evalas(b) + ".")
//         }
//         case Assign(id, expr) =>
//           val exp = eval(expr)
//           env += (id -> exp)
//           Empty
//         case EqualE(e1, e2) =>
//           (eval(e1), eval(e2)) match {
//              case (BooleanExpr(a), BooleanExpr(b)) => BooleanExpr(a == b)
//              //               case (ListExpr(a), ListExpr(b)) => BooleanExpr(a.deep == b.deep)
//              case (NumberExpr(a), NumberExpr(b)) => BooleanExpr(a == b)
//              case (CharacterExpr(a), CharacterExpr(b)) => BooleanExpr(a == b)
//              case (a, b) => sys.error("'==' requires expressions of the same type, not " + evalas(a) + " and " + evalas(b))
//           }
//         //         case Diff(e1, e2) => BooleanExpr(eval(e1) != eval(e2))
//         case GreaterEqual(e1, e2) => (eval(e1), eval(e2)) match {
//            case (CharacterExpr(v1), CharacterExpr(v2)) => BooleanExpr(v1 >= v2)
//            case (NumberExpr(v1), NumberExpr(v2)) => BooleanExpr(v1 >= v2)
//            case _ => sys.error("'>=' requires two numbers or characters, not " + evalas(e1) + " and " + evalas(e2))
//         }
//         case LesserEqual(e1, e2) => (eval(e1), eval(e2)) match {
//            case (CharacterExpr(v1), CharacterExpr(v2)) => BooleanExpr(v1 <= v2)
//            case (NumberExpr(v1), NumberExpr(v2)) => BooleanExpr(v1 <= v2)
//            case _ => sys.error("'<=' requires two numbers or characters, not " + evalas(e1) + " and " + evalas(e2))
//         }
//         case Greater(e1, e2) => (eval(e1), eval(e2)) match {
//            case (CharacterExpr(v1), CharacterExpr(v2)) => BooleanExpr(v1 > v2)
//            case (NumberExpr(v1), NumberExpr(v2)) => BooleanExpr(v1 > v2)
//            case _ => sys.error("'>' requires two numbers, characters or strings, not " + evalas(e1) + " and " + evalas(e2))
//         }
//         case Lesser(e1, e2) => (eval(e1), eval(e2)) match {
//            case (CharacterExpr(v1), CharacterExpr(v2)) => BooleanExpr(v1 < v2)
//            case (NumberExpr(v1), NumberExpr(v2)) => BooleanExpr(v1 < v2)
//            case _ => sys.error("'<' requires two numbers or characters, not " + evalas(e1) + " and " + evalas(e2))
//         }
//         case Add(x, y) => NumberExpr(eval(x).asInstanceOf[NumberExpr].n + eval(y).asInstanceOf[NumberExpr].n)
//         case ConcatenateListExpr(x, y) => ListExpr(eval(x).asInstanceOf[ListExpr].exprs ++ eval(y).asInstanceOf[ListExpr].exprs)
//         case Sub(x, y) => NumberExpr(eval(x).asInstanceOf[NumberExpr].n - eval(y).asInstanceOf[NumberExpr].n)
//         case Mul(x, y) => NumberExpr(eval(x).asInstanceOf[NumberExpr].n * eval(y).asInstanceOf[NumberExpr].n)
//         case Div(x, y) => NumberExpr(eval(x).asInstanceOf[NumberExpr].n / eval(y).asInstanceOf[NumberExpr].n)
//         case Resto(x, y) => NumberExpr(eval(x).asInstanceOf[NumberExpr].n % eval(y).asInstanceOf[NumberExpr].n)
//         case Pow(x, y) => NumberExpr(math.pow(eval(x).asInstanceOf[NumberExpr].n, eval(y).asInstanceOf[NumberExpr].n))
//         case Ident(id) => {
//            val xx = env getOrElse(id, sys.error("Undefined var " + id + "."))
//            eval(xx)
//         }
//         //         case Ident(id, tipo) => {
//         //            val xx = env getOrElse(id, sys.error("Undefined var " + id + "."))
//         //            val r = eval(xx)
//         //            r match {
//         //               case t: TypeE => if (t.getClass == r.getClass) r else sys.error("Ident " + id + " has type " + tipo + ". " + evalas(xx) + " cannot be assigned to it.")
//         //               case x => x
//         //            }
//         //         }
//         case x => x //{warning(x + " was left unevaluated."); x} //ListExpr, NumberExpr, Tuple, CharacterExpr, StrE
//      }
//
//      def executa_shell(code: String) = {
//         val gera = Process(code)
//         gera.!! //run()
//         ListExpr(gera.lazyLines.toArray.map(x => ListExpr(x.toCharArray map CharacterExpr)))
//      }
//
////      def executa(code: String) = {
////         val code2 = "val res_1 = (" + code + ")"
////         //         println("[" + code2 + "] <- scala code")
////         interpret.interpret(code2)
////         val res = interpret.valueOfTerm("res_1")
////         if (res == None) Empty
////         else res.get match {
////            case s: String => ListExpr(s.toCharArray map CharacterExpr) //parse s to get type beyond number and string?
////            case x => NumberExpr(x.asInstanceOf[Double])
////         }
////      }
//   }
//
//
//   //magia =========================================================================================
//
//   def metodos = {
//      class Asd {
//         def p() { println("asdasd") }
//      }
//      //   def printMethods[T](t: T) { // requires instance
//      //   val meths = t.getClass.getMethods
//      //      println(meths take 5 mkString "\n")
//      //   }
//      def printMethods1(name: String) {
//         // low-level
//         val meths = Class.forName(name).getFields ++ Class.forName(name).getMethods
//         println(meths take 5 mkString "\n")
//         println("_" + meths(1))
//      }
//      //   printMethods1("lamdheal.Asd")
//      printMethods1("java.lang.System")
//   }
//
//
////   def magia = {
////      val srcA = """  object O{        def foo = println("Hello World in object O")} ; """
////
////      val srcB = """ O.foo """
////
////      val srcC = """
////  trait T{            def foo:String}
////  class A extends T{
////    def foo = "Hello World from srcC"
////    override def toString = "this is A in a src"
////  }
////                 """
////      val out = System.out
////      val flusher = new java.io.PrintWriter(out)
////      val interpret = {
////         val settings = new scala.tools.nsc.GenericRunnerSettings(println _)
////         settings.usejavacp.value = true
////         //      settings.
////         new scala.tools.nsc.interpreter.IMain(settings, flusher)
////      }
////      interpret.interpret(srcA)
////      //   interpret.interpret(srcB)
////      interpret.compileString(srcC)
////      val classA = interpret.classLoader.findClass("A")
////      println("  " + classA)
////      val constructors = classA.getDeclaredConstructors
////      val myinstance = constructors(0).newInstance()
////      println(myinstance)
////
////      //this still throws an classCastException
////      println("++ " + myinstance)
////      //but everything else works
////   }
//
//}
