package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

infix fun <T1 : Any, T2 : Any, TStreamItem : Any> Parser<T1, TStreamItem>.prefixedBy(prefix: Parser<T2, TStreamItem>) =
        Parser("${prefix.name} and then ${this.name}",
                fun(input: IInput<TStreamItem>): ParserResult<T1, TStreamItem> {
                    val resultA = prefix.parse(input)
                    return when (resultA) {
                        is ParserResult.Failure -> ParserResult.Failure(resultA.expected, input.position)
                        is ParserResult.Success -> {

                            val resultB = this.parse(resultA.remainingInput)
                            when (resultB) {
                                is ParserResult.Failure -> ParserResult.Failure(resultB.expected, resultA.remainingInput.position)
                                is ParserResult.Success -> ParserResult.Success(
                                        expected = "${prefix.name} and then ${this.name}",
                                        value = resultB.value,
                                        remainingInput = resultB.remainingInput,
                                        payload = resultB.payload,
                                        position = input.position
                                )
                            }
                        }
                    }
                })