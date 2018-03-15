# `ironik`

This is another attempt to create a simple parser combinator library which solves certain problem I have encountered while building `kombiparser` (https://github.com/nekomatic/kombiparser). While the kombiparser is quite happy to support recursive parsers (i.e. json) by allowing late swapping of placeholder parsers with an actual one, it does not prevent from building parsers in a way which may overflow the stack immediately at the application start. Unlike i.e. F# Kotlin does not complain about referring to a function declared later in the code - this make it possible construct a parser which does compile but will never actually work.
The goal of the `ironik` project is to create simple parser combinator library which solves this problem.

The name of this project is inspired by the `Irony` parser for .Net (https://github.com/IronyProject/Irony). I liked there the concept removing the dependency on a dedicated lexer. 
I also intend to borrow some ideas from `spache` (https://github.com/sprache/Sprache) which another parser combinator library for .Net.
