package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any> oneOf(vararg parsers: IParser<T, TStreamItem>): IParser<T, TStreamItem> {
    val name = parsers.map { p -> p.name }.joinToString { " or " }
    return Parser(
            name = name,
            parseFunction = fun(input: IInput<TStreamItem>): ParserResult<T, TStreamItem> {
                for (parser in parsers) {
                    val result = parser.parse(input)
                    if (result is ParserResult.Success) return result
                }
                return ParserResult.Failure(name, input.position)
            })
}