package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamElement : Any> oneOrNone(parser: Parser<T, TStreamElement>, default: T) =
        Parser("${parser.name} or none",
                fun(input: IInput<TStreamElement>): ParserResult<T, TStreamElement> {
                    val resultA = parser.parse(input)
                    return when (resultA) {
                        is ParserResult.Failure -> ParserResult.Success("${parser.name} or none", default, input, listOf(), input.position)
                        is ParserResult.Success -> resultA
                    }
                })