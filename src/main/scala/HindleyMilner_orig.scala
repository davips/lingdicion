///*
//* Almost integral copy of the Andrew Forrest's adaptation for Scala
//* http://dysphoria.net/code/hindley-milner/HindleyMilner.scala
//*
//* Implementation of basic polymorphic type-checking for a simple language.
//* Based heavily on Nikita Borisovâ€™s Perl implementation at
//* http://web.archive.org/web/20050420002559/www.cs.berkeley.edu/~nikitab/courses/cs263/hm.html
//* which in turn is based on the paper by Luca Cardelli at
//* http://lucacardelli.id/Papers/BasicTypechecking.pdf
//*
//* If you run it with "scala HindleyMilner.scala" it will attempt to report the types
//* for a few example expressions. (It uses UTF-8 for output, so you may need to set your
//* terminal accordingly.)
//*
//* Do with it what you will.
//*/
//
//package lamdheal
//
//import lamdheal.TypeSystemorig.{Free, TypeT}
//
//sealed abstract class SyntaxNode
//
//case class Lambdaorig(v: String, body: SyntaxNode) extends SyntaxNode
//
////case class Ident(id: String, tp: TypeT = Free) extends SyntaxNode
//case class Ident(id: String) extends SyntaxNode
//
//case class ApplyE(fn: SyntaxNode, arg: SyntaxNode) extends SyntaxNode
//
//case class Let(v: String, defn: SyntaxNode, body: SyntaxNode) extends SyntaxNode
//
//case class Letrec(v: String, defn: SyntaxNode, body: SyntaxNode) extends SyntaxNode
//
//object SyntaxNode {
//   def string(ast: SyntaxNode): String = {
//      if (ast.isInstanceOf[Ident])
//         nakedString(ast)
//      else
//         "(" + nakedString(ast) + ")"
//   }
//
//   def nakedString(ast: SyntaxNode) = ast match {
//      case i: Ident => i.id
//      case exprs: Lambdaorig => "fn " + exprs.v + " => " + string(exprs.body)
//      case f: ApplyE => string(f.fn) + " " + string(f.arg)
//      case exprs: Let => "let " + exprs.v + " = " + string(exprs.defn) + " in " + string(exprs.body)
//      case exprs: Letrec => "letrec " + exprs.v + " = " + string(exprs.defn) + " in " + string(exprs.body)
//   }
//}
//
//class TypeError(msg: String) extends Exception(msg)
//
//class ParseTypeError(msg: String) extends Exception(msg)
//
//
//object TypeSystemorig {
//
//   type Env = Map[String, TypeT]
//
//   sealed abstract class TypeT
//
//   case object Free extends TypeT
//
//   case class Variable(id: Int) extends TypeT {
//      var instance: Option[TypeT] = None
//      lazy val id = nextUniqueName
//   }
//
//   case class ComplexType(id: String, args: Seq[TypeT]) extends TypeT
//
//   def FunctionT(from: TypeT, to: TypeT) = ComplexType("->", Array(from, to))
//
//   val Integer = ComplexType("int", Array[TypeSystemorig.TypeT]())
//   val Bool = ComplexType("bool", Array[TypeSystemorig.TypeT]())
//
//   var _nextVariableName = 'A'
//
//   // Î
//   def nextUniqueName = {
//      val result = _nextVariableName
//      _nextVariableName = (_nextVariableName.toInt + 1).toChar
//      result.toString
//   }
//
//   var _nextVariableId = 0
//
//   def newVariable: Variable = {
//      val result = _nextVariableId
//      _nextVariableId += 1
//      Variable(result)
//   }
//
//   def string(t: TypeT): String = t match {
//      case v: Variable => v.instance match {
//         case Some(i) => string(i)
//         case None => v.id
//      }
//      case ComplexType(id, args) => {
////         args map println
//         if (args.length == 0)
//            id
//         else if (args.length == 2)
//            "(" + string(args(0)) + " " + id + " " + string(args(1)) + ")"
//         else
//            args.mkString(id + " ", " ", "")
//      }
//   }
//
//
//   def analyse(ast: SyntaxNode, env: Env): TypeT = analyse(ast, env, Set.empty)
//
//   def analyse(ast: SyntaxNode, env: Env, nongen: Set[Variable]): TypeT = ast match {
//      case Ident(id) => get_type_of_identifier(id, env, nongen)
////      case Ident(id, tp) => tp
//      case ApplyE(fn, arg) => {
//         val funtype = analyse(fn, env, nongen)
//         val argtype = analyse(arg, env, nongen)
//         val resulttype = newVariable
//         unify(FunctionT(argtype, resulttype), funtype)
//         resulttype
//      }
//      case Lambdaorig(arg, body) => {
//         val argtype = newVariable
//         val resulttype = analyse(body,
//            env + (arg -> argtype),
//            nongen + argtype)
//         FunctionT(argtype, resulttype)
//      }
//      case Let(v, defn, body) => {
//         val defntype = analyse(defn, env, nongen)
//         val newenv = env + (v -> defntype)
//         analyse(body, newenv, nongen)
//      }
//      case Letrec(v, defn, body) => {
//         val newtype = newVariable
//         val newenv = env + (v -> newtype)
//         val defntype = analyse(defn, newenv, nongen + newtype)
//         unify(newtype, defntype)
//         analyse(body, newenv, nongen)
//      }
//   }
//
//   def get_type_of_identifier(id: String, env: Env, nongen: Set[Variable]): TypeT = {
//      if (env.contains(id))
//         fresh(env(id), nongen)
//      else if (isIntegerLiteral(id))
//         Integer
//      else
//         throw new ParseTypeError("Undefined symbol " + id)
//   }
//
//   def fresh(t: TypeT, nongen: Set[Variable]) = {
//      import scala.collection.mutable
//      val mappings = new mutable.HashMap[Variable, Variable]
//      def freshrec(tp: TypeT): TypeT = {
//         prune(tp) match {
//            //1
//            case v: Variable =>
//               if (isgeneric(v, nongen))
//                  mappings.getOrElseUpdate(v, newVariable)
//               else
//                  v
//            //n
//            case ComplexType(id, args) =>
//               ComplexType(id, args.map(freshrec(_)))
//         }
//      }
//
//      freshrec(t)
//   }
//
//
//   def unify(t1: TypeT, t2: TypeT) {
//      val type1 = prune(t1)
//      val type2 = prune(t2)
//      (type1, type2) match {
//         case (a: Variable, b) => if (a != b) {
//            if (occursintype(a, b))
//               throw new TypeError("recursive unification")
//            a.instance = Some(b)
//         }
//         case (a: ComplexType, b: Variable) => unify(b, a)
//         case (a: ComplexType, b: ComplexType) => {
//            if (a.id != b.id ||
//               a.args.length != b.args.length) throw new TypeError("TypeT mismatch: " + string(a) + " != " + string(b)) // â‰
//
//            for (i <- 0 until a.args.length)
//               unify(a.args(i), b.args(i))
//         }
//      }
//   }
//
//
//   // Returns the currently defining instance of t.
//   // As a side effect, collapses the list_of_nongen of type instances.
//   def prune(t: TypeT): TypeT = t match {
//      case v: Variable if v.instance.isDefined => {
//         val inst = prune(v.instance.get) //var inst
//         v.instance = Some(inst)
//         inst
//      }
//      case _ => t
//   }
//
//   // Note: must be called with v 'pre-pruned'
//   def isgeneric(v: Variable, nongen: Set[Variable]) = !(occursin(v, nongen))
//
//   // Note: must be called with v 'pre-pruned'
//   def occursintype(v: Variable, type2: TypeT): Boolean = {
//      prune(type2) match {
//         case `v` => true
//         case ComplexType(id, args) => occursin(v, args)
//         case _ => false
//      }
//   }
//
//   def occursin(t: Variable, list_of_nongen: Iterable[TypeT]) =
//      list_of_nongen exists (t2 => occursintype(t, t2))
//
//   val checkDigits = "^(\\d+)$".r
//
//   def isIntegerLiteral(id: String) = checkDigits.findFirstIn(id).isDefined
//
//}
//
//object HindleyMilner extends App {
////   Console.setOut(new java.io.PrintStream(Console.out, true, "utf-8"))
//
//   val var1 = TypeSystemorig.newVariable
//   val var2 = TypeSystemorig.newVariable
//   val pairtype = TypeSystemorig.ComplexType("+", Array(var1, var2)) // Ã—
//
//   val var3 = TypeSystemorig.newVariable
//
//   val myenv: TypeSystemorig.Env = Map.empty ++ Array(
//      "pair" -> TypeSystemorig.FunctionT(var1, TypeSystemorig.FunctionT(var2, pairtype)),
//      "true" -> TypeSystemorig.Bool,
//      "cond" -> TypeSystemorig.FunctionT(TypeSystemorig.Bool, TypeSystemorig.FunctionT(var3, TypeSystemorig.FunctionT(var3, var3))),
//      "zero" -> TypeSystemorig.FunctionT(TypeSystemorig.Integer, TypeSystemorig.Bool),
//      "pred" -> TypeSystemorig.FunctionT(TypeSystemorig.Integer, TypeSystemorig.Integer),
//      "times" -> TypeSystemorig.FunctionT(TypeSystemorig.Integer, TypeSystemorig.FunctionT(TypeSystemorig.Integer, TypeSystemorig.Integer))
//   )
//
//
//   val pair = ApplyE(ApplyE(Ident("pair"), ApplyE(Ident("f"), Ident("4"))), ApplyE(Ident("f"), Ident("true")))
//   val examples = Array[SyntaxNode](
//      // factorial
//      Letrec("factorial", // letrec factorial =
//         Lambdaorig("n", // fn n =>
//            ApplyE(
//               ApplyE(// cond (zero n) 1
//                  ApplyE(Ident("cond"), // cond (zero n)
//                     ApplyE(Ident("zero"), Ident("n"))),
//                  Ident("1")),
//               ApplyE(// times n
//                  ApplyE(Ident("times"), Ident("n")),
//                  ApplyE(Ident("factorial"),
//                     ApplyE(Ident("pred"), Ident("n")))
//               )
//            )
//         ), // in
//         ApplyE(Ident("factorial"), Ident("5"))
//      ),
//
//      // Should fail:
//      // fn x => (pair(x(3) (x(true)))
//      Lambdaorig("x",
//         ApplyE(
//            ApplyE(Ident("pair"),
//               ApplyE(Ident("x"), Ident("3"))),
//            ApplyE(Ident("x"), Ident("true")))),
//
//      // pair(f(3), f(true))
//      ApplyE(
//         ApplyE(Ident("pair"), ApplyE(Ident("f"), Ident("4"))),
//         ApplyE(Ident("f"), Ident("true"))),
//
//
//      // letrec f = (fn x => x) in ((pair (f 4)) (f true))
//      Let("f", Lambdaorig("x", Ident("x")), pair),
//
//      // fn f => f f (fail)
//      Lambdaorig("f", ApplyE(Ident("f"), Ident("f"))),
//
//      // let g = fn f => 5 in g
//      Let("g",
//         Lambdaorig("f", Ident("5")),
////         ApplyE(Ident("g"), Ident("g"))),
//      Ident("g")),
//
//      // example that demonstrates generic and non-generic variables:
//      // fn g => let f = fn x => g in pair (f 3, f true)
//      Lambdaorig("g",
//         Let("f",
//            Lambdaorig("x", Ident("g")),
//            ApplyE(
//               ApplyE(Ident("pair"),
//                  ApplyE(Ident("f"), Ident("23"))
//               ),
//               ApplyE(Ident("f"), Ident("true"))))),
//
//      // FunctionT composition
//      // fn f (fn g (fn arg (f g arg)))
//      Lambdaorig("f", Lambdaorig("g", Lambdaorig("arg", ApplyE(Ident("g"), ApplyE(Ident("f"), Ident("arg"))))))
//   )
//   for (eg <- examples) {
//      tryexp(myenv, eg)
//   }
//
//   //  }
//
//   def tryexp(env: TypeSystemorig.Env, ast: SyntaxNode) {
//      printf(SyntaxNode.string(ast) + " : ")
//      try {
//         val t = TypeSystemorig.analyse(ast, env)
//         printf(TypeSystemorig.string(t))
//
//      } catch {
//         case t: ParseTypeError => printf(t.getMessage)
//         case t: TypeError => printf(t.getMessage)
//      }
//      println()
//   }
//}
//
