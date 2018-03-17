package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.parsers.Parser
import com.nekomatic.types.flatten

infix fun <T1 : Any, T2 : Any, TStreamItem : Any> IParser<T1, TStreamItem>.surroundedBy(that: IParser<T2, TStreamItem>): IParser<T1, TStreamItem> =
        Parser(
                { input: IInput<TStreamItem> ->
                    (that then this then that mapValue { it.flatten().item02 }).parse(input)
                }
        )

fun <T1 : Any, T2 : Any, T3 : Any, TStreamItem : Any> IParser<T1, TStreamItem>.surroundedBy(left: IParser<T2, TStreamItem>, right: IParser<T2, TStreamItem>): IParser<T1, TStreamItem> =
        Parser(
                { input: IInput<TStreamItem> ->
                    (left then this then right mapValue { it.flatten().item02 }).parse(input)
                }
        )