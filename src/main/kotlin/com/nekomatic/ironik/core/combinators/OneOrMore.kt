package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any> oneOrMore(parser: IParser<T, TStreamItem>): IParser<List<T>, TStreamItem> =
        Parser(
                fun(input: IInput<TStreamItem>): ParserResult<List<T>, TStreamItem> {
                    val parserResult = parser.parse(input)
                    return when (parserResult) {
                        is ParserResult.Failure -> ParserResult.Failure(
                                expected = "(at least one of '" + parserResult.expected + "')",
                                position = input.position
                        )
                        is ParserResult.Success -> {
                            val resultB = (zeroOrMore(parser)).parse(parserResult.remainingInput)
                            return when (resultB) {
                                is ParserResult.Failure -> ParserResult.Failure(
                                        expected = "(at least one of '" + resultB.expected + "')",
                                        position = input.position
                                )
                                is ParserResult.Success -> ParserResult.Success(
                                        value = listOf(parserResult.value) + resultB.value,
                                        remainingInput = resultB.remainingInput,
                                        payload = parserResult.payload + resultB.payload,
                                        position = input.position
                                )
                            }
                        }
                    }
                }
        )