package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.parsers.Parser

infix fun <T1 : Any, T2 : Any, TStreamItem : Any> Parser<T1, TStreamItem>.surroundedBy(that: Parser<T2, TStreamItem>): Parser<T1, TStreamItem> {
    val name = "${this.name} surrounded by ${that.name}"
    return this sufixedBy that prefixedBy that renameTo name
}