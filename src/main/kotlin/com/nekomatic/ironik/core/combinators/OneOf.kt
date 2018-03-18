package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> oneOf(name: String, vararg parsers: IParser<T, TStreamItem, TInput>): IParser<T, TStreamItem, TInput> =
        Parser(
                fun(input: IInput<TStreamItem>): ParserResult<T, TStreamItem, TInput> {
                    for (parser in parsers) {
                        val parserResult = parser.parse(input)
                        if (parserResult is ParserResult.Success) return parserResult
                    }
                    return ParserResult.Failure(name, input.position)
                }
        )