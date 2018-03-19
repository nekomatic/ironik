package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.parsers.Parser
import com.nekomatic.types.Tuple02
import com.nekomatic.types.flatten

infix fun <T1 : Any, T2 : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> IParser<T1, TStreamItem, TInput>.surroundedBy(that: IParser<T2, TStreamItem, TInput>): IParser<T1, TStreamItem, TInput> =
        Parser(
                { input: IInput<TStreamItem> ->
                    (that then this then that mapValue { it: Tuple02<Tuple02<T2, T1>, T2> -> it.item01.item02 }).parse(input)
                }
        )

fun <T1 : Any, T2 : Any, T3 : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> IParser<T1, TStreamItem, TInput>.surroundedBy(left: IParser<T2, TStreamItem, TInput>, right: IParser<T3, TStreamItem, TInput>): IParser<T1, TStreamItem, TInput> =
        Parser(
                { input: IInput<TStreamItem> ->
                    (left then this then right mapValue { it: Tuple02<Tuple02<T2, T1>, T3> -> it.item01.item02 }).parse(input)
                }
        )