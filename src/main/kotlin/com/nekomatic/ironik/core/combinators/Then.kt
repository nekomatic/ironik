package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.chain
import com.nekomatic.ironik.core.parsers.Parser

fun <T1 : Any, T2 : Any, T3 : Any, TStreamElement : Any> Parser<T1, TStreamElement>.then(second: Parser<T2, TStreamElement>, func: (T1, T2) -> T3) =
        Parser("${this.name} then ${second.name}",
                fun(input: IInput<TStreamElement>): ParserResult<T3, TStreamElement> =
                        this.parse(input).chain(second) { t1, t2 -> func(t1, t2) })
