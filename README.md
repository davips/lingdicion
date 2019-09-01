lingdicion
==========
lingdicion is a homoiconic language made of dictionaries.
It is intended to be the dictionary sweet spot counterpart of Lisp.

Intro
-----
lingdicion is a minimalistic, lazy and immutable language.
Focused on ease of learning and clear syntax, it is intended to explore the possibilities that emerge from such a strong restriction like have the dictionary as its most important element.
For now, the interpreter is written in Scala and should produce compilable C code in the future.

For developpers:
It is highly dependent on Scala Parser Combinators and can be forked to easily suit your own language parsing needs.

License
-------
lingdicion is under GPL, see COPYING for details.
Third party libraries are under different conditions,
please see SCALA-license.txt, YETI-license and JANINO-license for details.

Distribution of your applications written/compiled in lingdicion
---------------------------------------------------------
You can do whatever you want with your own applications written in lamdheal. Please note that your applications will depend on Yeti runtime.
Additionally, applications with embedded Java will depend on Janino library.
Please make the proper reference to the licenses.


TODO
====
 * finish all needed to run benchmarks
 * get line numbers from scala parser combinators to feed debugger
 
 OUTDATED?
 =========
 * replace command line yeti compilation by a call to yeti compiler classes
 * implement tuples
 * provide zip of lists (& operator)
 * implement type variable (like list'a', list'b' etc.) to disallow comparison ('==', '>=') of different types, avoid concatenation of different list types etc.
 * implement embedded java code support with janino (parameters are marked with '$')
   return type should be given to be tested by a java routine in runtime
   Syntax: '[num]' "List list = new ArrayList(); for (int i=0; i<$n; i++) list.add(i); return list;"
 * implement fast repeatable embedded java code support with janino; user can define a function that wil be called multiple times with varying arguments
