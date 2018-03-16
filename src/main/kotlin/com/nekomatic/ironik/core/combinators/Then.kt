package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.chain
import com.nekomatic.ironik.core.parsers.Parser

fun <T1 : Any, T2 : Any, T3 : Any, TStreamItem : Any> IParser<T1, TStreamItem>.then(second: IParser<T2, TStreamItem>, func: (T1, T2) -> T3): IParser<T3, TStreamItem> {
    val name = "${this.name} then ${second.name}"
    return Parser(
            name = name,
            parseFunction = fun(input: IInput<TStreamItem>): ParserResult<T3, TStreamItem> =
                    this.parse(input).chain(second) { t1, t2 -> func(t1, t2) })
}

infix fun <T01 : Any, T02 : Any, TStreamItem : Any> IParser<T01, TStreamItem>.then(second: IParser<T02, TStreamItem>) =
        this.then(second) { v1, v2 -> com.nekomatic.types.Tuple02(v1, v2) }