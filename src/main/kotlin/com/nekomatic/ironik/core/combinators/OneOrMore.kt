package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any> oneOrMore(parser: Parser<T, TStreamItem>): Parser<List<T>, TStreamItem> =
        Parser("at least one of ${parser.name}",
                fun(input: IInput<TStreamItem>): ParserResult<List<T>, TStreamItem> {

                    val resultA = parser.parse(input)

                    return when (resultA) {
                        is ParserResult.Failure -> ParserResult.Failure(
                                expected = "(at least one of '" + resultA.expected + "')",
                                position = input.position
                        )
                        is ParserResult.Success -> {

                            val resultB = (zeroOrMore(parser)).parse(resultA.remainingInput)
                            return when (resultB) {
                                is ParserResult.Failure -> ParserResult.Failure(
                                        expected = "(at least one of '" + resultB.expected + "')",
                                        position = input.position
                                )
                                is ParserResult.Success -> ParserResult.Success(
                                        expected = "at least one of ${parser.name}",
                                        value = listOf(resultA.value) + resultB.value,
                                        remainingInput = resultB.remainingInput,
                                        payload = resultA.payload + resultB.payload,
                                        position = input.position
                                )
                            }
                        }
                    }
                })