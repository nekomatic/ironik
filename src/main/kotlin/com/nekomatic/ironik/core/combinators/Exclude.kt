package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any> anythingBut(parser: IParser<T, TStreamItem>): IParser<TStreamItem, TStreamItem> =
        Parser(
                { input: IInput<TStreamItem> ->
                    val parserResult = parser.parse(input)
                    if (parserResult is ParserResult.Success)
                        ParserResult.Failure(
                                position = input.position,
                                expected = "Anything which is not '${parserResult.payload.joinToString { it.toString() }}'"
                        )
                    else
                        any<TStreamItem>().parse(input)
                }
        )