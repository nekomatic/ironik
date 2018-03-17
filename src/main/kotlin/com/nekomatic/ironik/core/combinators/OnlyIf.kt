package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.Input
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any> IParser<T, TStreamItem>.onlyIf(name: String, condition: IParser<T, TStreamItem>): IParser<T, TStreamItem> =
        Parser(
                fun(input: IInput<TStreamItem>): ParserResult<T, TStreamItem> {
                    val thisResult = this.parse(input)
                    return when (thisResult) {
                        is ParserResult.Failure -> ParserResult.Failure(
                                expected = name,
                                position = thisResult.position
                        )
                        is ParserResult.Success -> {
                            val newInput = Input(thisResult.payload, 0)
                            val resultB = condition.parse(newInput)
                            when (resultB) {
                                is ParserResult.Success -> thisResult
                                is ParserResult.Failure -> ParserResult.Failure(
                                        expected = name,
                                        position = input.position
                                )
                            }
                        }
                    }
                }
        )