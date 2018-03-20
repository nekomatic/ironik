package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser
import com.nekomatic.types.Tuple02

infix fun <T1 : Any, T2 : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> IParser<T1, TStreamItem, TInput>.prefixedBy(prefix: IParser<T2, TStreamItem, TInput>): IParser<T1, TStreamItem, TInput> =
        Parser(
                fun(input: IInput<TStreamItem>): ParserResult<T1, TStreamItem, TInput> {
                    return (prefix then this mapValue { it: Tuple02<T2, T1> -> it.item02 }).parse(input)
                }
        )