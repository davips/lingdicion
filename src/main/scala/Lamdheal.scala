package lamdheal

//import lamdheal.Interpreter.RuntimeError
import lamdheal.Parseador.ParserException

import io.Source


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
    along with lamdheal.  If not, see <http://www.gnu.org/licenses/>.*/
object Lamdheal {
   def run_file(arq: String) {
      run_string(Source.fromFile(arq + ".lid").getLines().toList.mkString("\n"))
   }

   def run_string(input: String, web: Boolean = false, command_line_Args: List[String] = List()) {
      var ti = System.currentTimeMillis()
      var tf = 0L
      try {
         //         println("___________________________________________________________________")
         ti = System.currentTimeMillis()
         val ast = Parseador parse(input, web)
         tf = System.currentTimeMillis()
         if (!web) {println("________________________________________________Time parsing " + (tf - ti) / 1000d + "s")}

        sys.exit(0)
//         //         println("___________________________________________________________________")
//         ti = System.currentTimeMillis()
//         //         val typedast =
//         HindleyMilner.check(ast)
//         tf = System.currentTimeMillis()
//         if (!web) {println("________________________________________________Time checking types " + (tf - ti) / 1000d + "s")}
//
//         //         println("___________________________________________________________________")
//         ti = System.currentTimeMillis()
////         Interpreter eval(ast, command_line_Args, web)
//         tf = System.currentTimeMillis()
//         if (!web) {println("________________________________________________Time interpreting " + (tf - ti) / 1000d + "s")}
//         //
//         //println("___________________________________________\n")
//         //         ti = System.currentTimeMillis()
//         ////         Compiler eval(ast, input, web)
//         //         tf = System.currentTimeMillis()
//         //         if (!web) {println("Time compiling " + (tf - ti) / 1000d + "s\n")}

      }
      catch {
         case e: ParserException => println("Parser error " + e.getMessage)
//         case e: TypeError => println("Type error " + e.getMessage)
//         case e: ParseTypeError => println("ParseType error " + e.getMessage)
//         case e: RuntimeError => println("Runtime error: " + e.getMessage)
//         case x: Exception => println("System error: " + x.getMessage)
      }
   }
}
