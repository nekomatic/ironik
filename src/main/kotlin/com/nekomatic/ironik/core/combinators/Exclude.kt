package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any> anythingBut(parser: IParser<T, TStreamItem>): IParser<TStreamItem, TStreamItem> {
    val name = "Anything which is not ${parser.name}"
    return Parser(
            name = name,
            parseFunction = fun(input: IInput<TStreamItem>): ParserResult<TStreamItem, TStreamItem> {
                val resultA = parser.parse(input)
                return if (resultA is ParserResult.Success)
                    ParserResult.Failure(
                            position = input.position,
                            expected = "Anything which is not " + resultA.payload.joinToString { it.toString() }
                    )
                else
                    any<TStreamItem>().parse(input)
            })
}