package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IParser

infix fun <T1 : Any, T2 : Any, TStreamItem : Any> IParser<T1, TStreamItem>.surroundedBy(that: IParser<T2, TStreamItem>): IParser<T1, TStreamItem> {
    val name = "${this.name} surrounded by ${that.name}"
    return this sufixedBy that prefixedBy that renameTo name
}