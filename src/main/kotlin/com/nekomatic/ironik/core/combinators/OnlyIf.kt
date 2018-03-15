package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.Input
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

infix fun <T : Any, TStreamItem : Any> Parser<T, TStreamItem>.onlyIf(second: Parser<T, TStreamItem>) =
        Parser("${this.name} only if ${second.name}",
                fun(input: IInput<TStreamItem>): ParserResult<T, TStreamItem> {

                    val resultA = this.parse(input)
                    return when (resultA) {
                        is ParserResult.Failure -> resultA
                        is ParserResult.Success -> {
                            val newInput = Input(resultA.payload, 0)

                            val resultB = second.parse(newInput)
                            when (resultB) {
                                is ParserResult.Success -> resultA
                                is ParserResult.Failure -> ParserResult.Failure(
                                        expected = resultB.expected,
                                        position = input.position
                                )
                            }
                        }
                    }
                })