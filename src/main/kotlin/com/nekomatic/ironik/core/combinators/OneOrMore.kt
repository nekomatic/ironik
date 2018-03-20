package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> oneOrMore(parser: IParser<T, TStreamItem, TInput>): IParser<List<T>, TStreamItem, TInput> =
        Parser(
                fun(input: IInput<TStreamItem>): ParserResult<List<T>, TStreamItem, TInput> {
                    val parserResult = parser.parse(input)
                    return when (parserResult) {
                        is ParserResult.Failure -> ParserResult.Failure(
                                expected = "(at least one of '" + parserResult.expected + "')",
                                position = input.position,
                                column = input.column,
                                line = input.line
                        )
                        is ParserResult.Success -> {
                            val resultB: ParserResult<List<T>, TStreamItem, TInput> = (zeroOrMore(parser)).parse(parserResult.remainingInput)
                            return when (resultB) {
                                is ParserResult.Failure<TStreamItem, TInput> -> ParserResult.Failure(
                                        expected = "(at least one of '" + resultB.expected + "')",
                                        position = input.position,
                                        column = input.column,
                                        line = input.line
                                )
                                is ParserResult.Success -> ParserResult.Success(
                                        value = listOf(parserResult.value) + resultB.value,
                                        remainingInput = resultB.remainingInput,
                                        payload = parserResult.payload + resultB.payload,
                                        position = input.position,
                                        column = input.column,
                                        line = input.line
                                )
                            }
                        }
                    }
                }
        )