import lamdheal.Parseador

object Repl extends App {
  val parser = Parseador
  while (true) {
    val exprSrc = scala.io.StdIn.readLine("λ> ")
    import parser.{Success, NoSuccess}
    parser.parse(exprSrc, false) match {
      case Success(expr, _) => println("Parsed: " + expr)
      case err: NoSuccess => println(err)
    }
  }
}
