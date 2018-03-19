package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> anythingBut(parser: IParser<T, TStreamItem, TInput>): IParser<TStreamItem, TStreamItem, TInput> =
        Parser(
                { input: IInput<TStreamItem> ->
                    val parserResult = parser.parse(input)
                    if (parserResult is ParserResult.Success)
                        ParserResult.Failure<TStreamItem, TInput>(
                                position = input.position,
                                expected = "Anything which is not '${parserResult.payload.joinToString { it.toString() }}'",
                                column = input.column,
                                line = input.line
                        )
                    else
                        any<TStreamItem, TInput>().parse(input)
                }
        )