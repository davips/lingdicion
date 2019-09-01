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
//import java.io.{FileWriter, FileOutputStream, PrintStream}
//import sys.process.Process
//import cafebabe.AbstractByteCodes._
//import cafebabe.ByteCodes._
//import cafebabe._
//
//object Compiler {
//
//   import AST._
//
//   var web = true
//   var fw: FileWriter = null
//
//   //func as parametro   int(*compar)(const void *, const void *)
//   //pointer to func   void (*foo)(int);
//   var arquivo = "noname"
//
//   def eval(ast: Printer, arq: String, web1: Boolean) {
//      arquivo = arq
//      //      val classFile = new ClassFile(arquivo, None)
//      //      classFile.setSourceFile(arq + ".java")
//      //      classFile.addDefaultConstructor
//      //      val main = classFile.addMainMethod.codeHandler
//      //      fw = new FileWriter(arq)
//      //      fw.write("#include <stdio.h>\nint main() {\n")
//      web = web1
//      val c = new Context
//      c.eval(ast)
//      //      main << RETURN
//      //      main.freeze
//      //      classFile.writeToFile(arq + ".class")
//      //      fw.write("\treturn 0;\n}\n")
//      //      fw.close()
//   }
//
//   type Environment = collection.immutable.Map[String, Printer]
//
//   //   case object Evaluate extends Func {
//   //      override def toString() = "evaluate (built-in)"
//   //
//   //      def apply(arg: Printer) {
//   //         def f(x: String) {
//   //            val ast = Parseador parse x
//   //            Interpreter eval(ast, args)
//   //         }
//   //         f(arg.asInstanceOf[String])
//   //      }
//   //   }
//
//   // built-in
//   val initEnv: Environment = Map()
//
//
//   val out = new PrintStream(new FileOutputStream("/dev/null"))
//   //lil.log")) // System.out
//   val flusher = new java.io.PrintWriter(out)
//   val interpret = {
//      val settings = new scala.tools.nsc.GenericRunnerSettings(println _)
//      settings.usejavacp.value = true
//      //      settings.
//      new scala.tools.nsc.interpreter.IMain(settings, flusher)
//   }
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
//   //   def tipo(e:Printer)
//   //warm start
//   //   interpret.interpret(" ")
//
//   class Context(var env: Environment = initEnv) {
//      override def toString = "Environment: " + env.mkString(", ")
//
//      def changeEnv(env1: Environment) { env = env1 }
//
//      def evalas(x: Printer) = {
//         val ex = eval(x)
//         if (x != ex) x + " evaluated as " + ex else x
//      }
//
//      def eval(ex: Printer): Printer = ex match {
//         case LambdaE(Ident(nome), body) => {
////            val classFile = new ClassFile(arquivo + "lam", None)
////            classFile.setSourceFile(arquivo + "lam.java")
////            //            classFile.addDefaultConstructor
////            val method = classFile.addMethod(tipo.jvm, "lamdheal", tipo.jvm)
////            method.setFlags(Flags.METHOD_ACC_STATIC) // | Flags.METHOD_ACC_PUBLIC)
////
////            val fun = method.codeHandler
////            //            eval(body)
////            fun << Comment("asd") << DLoad(1) << Ldc(2d) << DMUL << DRETURN
////            fun.freeze
//
////            classFile.writeToFile(arquivo + "lam.class")
////            Thread.sleep(1000)
//            val pr = Process("javap -c -private " + arquivo + "lam.class")
//            println (pr.!!)
//
//            //            fun << New("lamdheal.runtime.LambdaE") << Ldc(34) <<
//            //               InvokeSpecial("HelloWorld", "<init>", "(I)V")
//            //            //            fw.write(tipo(lam.param) + " ")
//            //            //            Closure(this, lam)
//            Empty
//         }
//         case Shell(e: Printer) => {
//            eval(e) match {
//               case StrE(txt) => {
//                  if (web) {
//                     sys.error("Shell commands cannot be executed in web mode.")
//                  } else {
//                     executa_shell(txt)
//                  }
//               }
//               case x => sys.error("Shell command needs to be string, not " + evalas(e) + ".")
//            }
//         }
//         case Zipa(li: List[Printer]) => {
//            val liv = li map eval
//            if (liv.forall(_.isInstanceOf[ListExpr])) {
//               val liv2 = liv.map(x => x.asInstanceOf[ListExpr].exprs)
//               val maxl = liv2.maxBy(x => x.length).length
//               val l2 = liv2.map(x => x ++ (1 to maxl - x.length).toList.map(_ => Empty))
//               val l3 = l2.transpose
//               ListExpr(l3 map Tuple)
//            } else {
//               sys.error("Only lists can be tupled, not " + evalas(liv.head) + liv.map(x => " and " + evalas(x)) + ".")
//            }
//         }
//         case ApplyE(c1: Printer, c2: Printer) => {
//            (eval(c1), c2) match {
//               case (Takehead, arg: Printer) => {
//                  eval(arg) match {
//                     case ListExpr(exprs) => exprs.head
//                     case ListaAppInv(exprs) => exprs.head
//                     case StrE(s) => CharacterExpr(s.toList.head)
//                     case x => sys.error("@ needs a list_of_nongen or string to take the head off, not " + evalas(arg) + ".")
//                  }
//               }
//               case (Arg, e: Printer) => eval(e) match {
//                  //                  case NumberExpr(i) => {
//                  //                     if (i >= args.length) {
//                  //                        sys.error(i + " out of arguments bounds " + args.length)
//                  //                     } else {
//                  //                        StrE(args(i.toInt))
//                  //                     }
//                  //                  }
//                  case i => sys.error(evalas(e) + " is not a number to get argument")
//               }
//               case (PrintE, e: Printer) => {
//                  eval(e) match {
//                     case c: CharacterExpr => print(c.c)
//                     case t: StrE => print(t.s)
//                     case x => print(x)
//                  }
//                  Empty
//               }
//               case (PrintLnE, e: Printer) => {
//                  eval(e) match {
//                     case c: CharacterExpr => println(c.c)
//                     case t: StrE => println(t.s)
//                     case x => println(x)
//                  }
//                  Empty
//               }
//               case (f: Closure, a: Printer) => f(eval(a)) //coloquei eval(a), pq variaveis externas definidas depois do lamdheal (mesmo antes da app) não pertencem ao escopo interno.
//               case (ListaAppInv(exprs), a: Printer) => {
//                  val lis = exprs.map(x => eval(ApplyE(x, a))).filterNot(Empty ==)
//                  if (lis.forall(_.isInstanceOf[Default]))
//                     ListExpr((lis map {case Default(xx) => xx; case xx => xx}).filterNot(Empty ==))
//                  else
//                     ListExpr(lis.filterNot(_.isInstanceOf[Default]).filterNot(Empty ==))
//               }
//               case (ApplicationInversor, a: Printer) => eval(a) match {
//                  case ListExpr(exprs) => ListaAppInv(exprs)
//                  case _ => sys.error("'?' is not applicable to " + evalas(a) + ".")
//               }
//               case (Reverse, a: Printer) => eval(a) match {
//                  case ListExpr(exprs) => ListExpr(exprs.reverse)
//                  case ListaAppInv(exprs) => ListaAppInv(exprs.reverse)
//                  case StrE(exprs) => StrE(exprs.reverse)
//                  case _ => sys.error("'?' is not applicable to " + evalas(a) + ".")
//               }
//               case (StrE(l0), a: Printer) => {
//                  val exprs = l0.toList.map(CharacterExpr)
//                  val lis = exprs.map(x => eval(ApplyE(a, x))).filterNot(Empty ==)
//                  if (lis.forall(_.isInstanceOf[Default]))
//                     ListExpr((lis map {
//                        case Default(xx) => xx
//                        case xx => xx
//                     }).filterNot(Empty ==))
//                  else
//                     ListExpr(lis.filterNot(_.isInstanceOf[Default]).filterNot(Empty ==))
//               }
//               case (ListExpr(exprs), a: Printer) => {
//                  val lis = exprs.map(x => eval(ApplyE(a, x))).filterNot(Empty ==)
//                  if (lis.forall(_.isInstanceOf[Default]))
//                     ListExpr((lis map {case Default(xx) => xx; case xx => xx}).filterNot(Empty ==))
//                  else
//                     ListExpr(lis.filterNot(_.isInstanceOf[Default]).filterNot(Empty ==))
//               }
//               case (Tuple(exprs), a: Printer) => {
//                  var default = false
//                  val rets = exprs.dropRight(1) map {
//                     x => eval(ApplyE(x, a)) match {
//                        case NotApplicable(e1, e2) => eval(EqualE(e1, e2))
//                        case b: BooleanExpr => b
//                        case d: Default => {default = true; d}
//                        case other: Printer => eval(EqualE(other, a))
//                     }
//                  }
//                  val res = eval(if (exprs.last == LambdaE(Ident("_"), Ident("_"))) a else exprs.last)
//                  if (default) {
//                     Default(res)
//                  } else {
//                     if (rets forall (BooleanExpr(b = true) ==)) res else Empty
//                  }
//               }
//               case (Empty, e: Printer) => Default(e)
//               case (a: NumberExpr, b: Printer) => eval(b) match {
//                  case nb@NumberExpr(vb) => if (vb < 0) eval(Add(a, nb)) else NotApplicable(a, b)
//                  case _ => NotApplicable(a, b)
//               }
//               case (a: Printer, b: Printer) => NotApplicable(a, b) //{warning(evalas(a) + " is not applicable to " + evalas(b) + "."); NotApplicable(a, b)}
//            }
//         }
//         case Program(exprs) => ListExpr(exprs map eval)
//         case BlockE(exprs) => {
//            val newenv = env
//            val context = new Context(newenv)
//            val res = exprs map (context eval)
//            env = env ++ context.env.filter(x => x._1.startsWith("$") && env.keySet.contains(x._1)) //mutability
//            res.filterNot(Empty ==).last
//         }
//         case ListaInterval(a: Printer, b: Printer) => (eval(a), eval(b)) match {
//            case (NumberExpr(v1), NumberExpr(v2)) => ListExpr((v1.toInt to v2.toInt).toList map (_.toDouble) map NumberExpr)
//            case _ => sys.error("'..' requires two numbers, not " + evalas(a) + "  and  " + evalas(b) + ".")
//         }
//         case Assign(id, expr) => {
//            val exp = eval(expr)
//            env += (id -> exp)
//            Empty
//         }
//         case EqualE(e1, e2) => {
//            (eval(e1), eval(e2)) match {
//               //               case (BooleanExpr(a), BooleanExpr(b)) => {
//               //                  val classFile = new ClassFile(arquivo + "lam", None)
//               //                  classFile.setSourceFile(arquivo + "lam.java")
//               //                  classFile.addDefaultConstructor
//               //                  val handler = classFile.addMethod("?", "lamdheal", "?")
//               //                  handler.setFlags(Flags.METHOD_ACC_STATIC) //Flags.METHOD_ACC_PUBLIC
//               //                  val fun = handler.codeHandler
//               //                  //            fun << IStore
//               //                  //            fun << RETURN
//               //                  fun.freeze
//               //                  classFile.writeToFile(arquivo + "lam.class")
//               //
//               //                  fun << New("lamdheal.runtime.LambdaE") << Ldc(34) <<
//               //                     InvokeSpecial("HelloWorld", "<init>", "(I)V")
//               //               }
//               case (StrE(a), StrE(b)) => BooleanExpr(a == b)
//               case (NumberExpr(a), NumberExpr(b)) => BooleanExpr(a == b)
//               case (CharacterExpr(a), CharacterExpr(b)) => BooleanExpr(a == b)
//               case (a, b) => sys.error("'==' requires expressions of the same type, not " + evalas(a) + " and " + evalas(b))
//            }
//         }
//         case DiffE(e1, e2) => BooleanExpr(eval(e1) != eval(e2))
//         case GreaterEqual(e1, e2) => (eval(e1), eval(e2)) match {
//            case (CharacterExpr(v1), CharacterExpr(v2)) => BooleanExpr(v1 >= v2)
//            case (StrE(v1), StrE(v2)) => BooleanExpr(v1 >= v2)
//            case (NumberExpr(v1), NumberExpr(v2)) => BooleanExpr(v1 >= v2)
//            case _ => sys.error("'>=' requires two numbers or characters, not " + evalas(e1) + " and " + evalas(e2))
//         }
//         case LesserEqual(e1, e2) => (eval(e1), eval(e2)) match {
//            case (CharacterExpr(v1), CharacterExpr(v2)) => BooleanExpr(v1 <= v2)
//            case (StrE(v1), StrE(v2)) => BooleanExpr(v1 <= v2)
//            case (NumberExpr(v1), NumberExpr(v2)) => BooleanExpr(v1 <= v2)
//            case _ => sys.error("'<=' requires two numbers or characters, not " + evalas(e1) + " and " + evalas(e2))
//         }
//         case Greater(e1, e2) => (eval(e1), eval(e2)) match {
//            case (CharacterExpr(v1), CharacterExpr(v2)) => BooleanExpr(v1 > v2)
//            case (StrE(v1), StrE(v2)) => BooleanExpr(v1 > v2)
//            case (NumberExpr(v1), NumberExpr(v2)) => BooleanExpr(v1 > v2)
//            case _ => sys.error("'>' requires two numbers, characters or strings, not " + evalas(e1) + " and " + evalas(e2))
//         }
//         case Lesser(e1, e2) => (eval(e1), eval(e2)) match {
//            case (CharacterExpr(v1), CharacterExpr(v2)) => BooleanExpr(v1 < v2)
//            case (StrE(v1), StrE(v2)) => BooleanExpr(v1 < v2)
//            case (NumberExpr(v1), NumberExpr(v2)) => BooleanExpr(v1 < v2)
//            case _ => sys.error("'<' requires two numbers or characters, not " + evalas(e1) + " and " + evalas(e2))
//         }
//         case Add(x, y) => (eval(x), eval(y)) match {
//            case (NumberExpr(v1), NumberExpr(v2)) => NumberExpr(v1 + v2)
//            case (ListExpr(a), ListExpr(b)) => ListExpr(a ++ b)
//            case (StrE(t), NumberExpr(b)) => StrE(t + b)
//            case (StrE(t), StrE(t2)) => StrE(t + t2)
//            case (NumberExpr(b), StrE(t)) => StrE(b + t)
//            case _ => sys.error("'+' requires number, string or list_of_nongen+list_of_nongen, not " + evalas(x) + " and " + evalas(y))
//         }
//         case Sub(x, y) => (eval(x), eval(y)) match {
//            case (NumberExpr(v1), NumberExpr(v2)) => NumberExpr(v1 - v2)
//            case _ => sys.error("'-' requires two numbers, not " + evalas(x) + " and " + evalas(y))
//         }
//         case Mul(x, y) => (eval(x), eval(y)) match {
//            case (NumberExpr(v1), NumberExpr(v2)) => NumberExpr(v1 * v2)
//            case _ => sys.error("'*' requires two numbers, not " + evalas(x) + " and " + evalas(y))
//         }
//         case Div(x, y) => (eval(x), eval(y)) match {
//            case (NumberExpr(v1), NumberExpr(v2)) => NumberExpr(v1 / v2)
//            case _ => sys.error("'/' requires two numbers, not " + evalas(x) + " and " + evalas(y))
//         }
//         case Resto(x, y) => (eval(x), eval(y)) match {
//            case (NumberExpr(v1), NumberExpr(v2)) => NumberExpr(v1 % v2)
//            case _ => sys.error("'%' requires two numbers, not " + evalas(x) + " and " + evalas(y))
//         }
//         case Pow(x, y) => (eval(x), eval(y)) match {
//            case (NumberExpr(v1), NumberExpr(v2)) => NumberExpr(math.pow(v1, v2))
//            case _ => sys.error("'^' requires two numbers, not " + evalas(x) + " and " + evalas(y))
//         }
//         case Ident(id) => {
//            val xx = env getOrElse(id, sys.error("Undefined var " + id + "."))
//            eval(xx)
//         }
//         case x => x //{warning(x + " was left unevaluated."); x} //ListExpr, NumberExpr, Tuple, CharacterExpr, StrE
//      }
//
//      def executa_shell(code: String) = {
//         val gera = Process(code)
//         gera.!! //run()
//         ListExpr(gera.lines.toList.map(x => StrE(x)))
//      }
//
//      def executa(code: String) = {
//         val code2 = "val res_1 = (" + code + ")"
//         //         println("[" + code2 + "] <- scala code")
//         interpret.interpret(code2)
//         val res = interpret.valueOfTerm("res_1")
//         if (res == None) Empty
//         else res.get match {
//            case s: String => StrE(s) //parse s to get type beyond number and string?
//            case x => NumberExpr(x.asInstanceOf[Double])
//         }
//      }
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
//      def printMethods1(id: String) {
//         // low-level
//         val meths = Class.forName(id).getFields ++ Class.forName(id).getMethods
//         println(meths take 5 mkString "\n")
//         println("_" + meths(1))
//      }
//      //   printMethods1("lamdheal.Asd")
//      printMethods1("java.lang.System")
//   }
//
//
//   def magia = {
//      val srcA = """  object O{        def foo = println("Hello World in object O")} ; """
//
//      val srcB = """ O.foo """
//
//      val srcC = """
//  trait T{            def foo:String}
//  class A extends T{
//    def foo = "Hello World from srcC"
//    override def toString = "this is A in a src"
//  }
//                 """
//      val out = System.out
//      val flusher = new java.io.PrintWriter(out)
//      val interpret = {
//         val settings = new scala.tools.nsc.GenericRunnerSettings(println _)
//         settings.usejavacp.value = true
//         //      settings.
//         new scala.tools.nsc.interpreter.IMain(settings, flusher)
//      }
//      interpret.interpret(srcA)
//      //   interpret.interpret(srcB)
//      interpret.compileString(srcC)
//      val classA = interpret.classLoader.findClass("A")
//      println("  " + classA)
//      val constructors = classA.getDeclaredConstructors
//      val myinstance = constructors(0).newInstance()
//      println(myinstance)
//
//      //this still throws an classCastException
//      println("++ " + myinstance)
//      //but everything else works
//   }
//
//}
