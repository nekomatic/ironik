package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.parsers.Parser

infix fun <T1 : Any, T2 : Any, TStreamItem : Any> Parser<T1, TStreamItem>.surroundedBy(other: Parser<T2, TStreamItem>) =
        this sufixedBy other prefixedBy other renameTo "${this.name} surrounded by ${other.name}"