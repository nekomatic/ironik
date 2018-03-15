package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamElement : Any> oneOf(vararg parsers: Parser<T, TStreamElement>): Parser<T, TStreamElement> =
        Parser(parsers.map { p -> p.name }.joinToString { " or " },
                fun(input: IInput<TStreamElement>): ParserResult<T, TStreamElement> {

                    for (parser in parsers) {
                        val result = parser.parse(input)
                        if (result is ParserResult.Success) return result
                    }
                    return ParserResult.Failure(parsers.map { p -> p.name }.joinToString { " or " }, input.position)
                })