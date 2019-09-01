import lamdheal.Lamdheal

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

class Main(args: Array[String]) {
   def ini() {
//      println("Lamdheal")

      var input = "input"
      var web = false
      val aa = if (args.length > 0) {
         if (args.head == "--web") {
            web = true
            List(" ")
         } else {
            input = args.head
            args.tail.toList
         }
      } else List()

      Lamdheal.run_file(input)
   }
}

object Mm extends App {
   val asd = new Main(args)
   asd.ini()
}
