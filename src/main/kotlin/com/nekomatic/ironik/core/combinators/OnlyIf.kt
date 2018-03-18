package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.Input
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> IParser<T, TStreamItem, TInput>.onlyIf(name: String, condition: IParser<T, TStreamItem, TInput>): IParser<T, TStreamItem, TInput> =
        Parser(
                fun(input: IInput<TStreamItem>): ParserResult<T, TStreamItem, TInput> {
                    val thisResult = this.parse(input)
                    return when (thisResult) {
                        is ParserResult.Failure -> ParserResult.Failure(
                                expected = name,
                                position = thisResult.position
                        )
                        is ParserResult.Success -> {
                            val newInput: IInput<TStreamItem> = Input(thisResult.payload, 0)
                            val resultB: ParserResult<T, TStreamItem, TInput> = condition.parse(newInput)
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