package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any> oneOrNone(parser: Parser<T, TStreamItem>, default: T): Parser<T, TStreamItem> {
    val name = "${parser.name} or none"
    return Parser(
            name = name,
            parseFunction = fun(input: IInput<TStreamItem>): ParserResult<T, TStreamItem> {
                val resultA = parser.parse(input)
                return when (resultA) {
                    is ParserResult.Failure -> ParserResult.Success(name, default, input, listOf(), input.position)
                    is ParserResult.Success -> resultA
                }
            })
}