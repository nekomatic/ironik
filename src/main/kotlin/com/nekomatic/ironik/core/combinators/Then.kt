package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.chainResults
import com.nekomatic.ironik.core.parsers.Parser

fun <T1 : Any, T2 : Any, T3 : Any, TStreamItem : Any> IParser<T1, TStreamItem>.then(second: IParser<T2, TStreamItem>, func: (T1, T2) -> T3): IParser<T3, TStreamItem> =
        Parser(
                { input: IInput<TStreamItem> ->
                    this.parse(input).chainResults(second) { t1, t2 -> func(t1, t2) }
                }
        )

infix fun <T01 : Any, T02 : Any, TStreamItem : Any> IParser<T01, TStreamItem>.then(second: IParser<T02, TStreamItem>) =
        Parser(
                { input: IInput<TStreamItem> ->
                    (this.then(second) { v1, v2 -> com.nekomatic.types.Tuple02(v1, v2) }).parse(input)
                }
        )