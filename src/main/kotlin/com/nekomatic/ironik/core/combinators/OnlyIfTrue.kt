package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> IParser<T, TStreamItem, TInput>.onlyIfTrue(name: String, predicate: (T) -> Boolean): IParser<T, TStreamItem, TInput> =
        Parser(
                fun(input: IInput<TStreamItem>): ParserResult<T, TStreamItem, TInput> {
                    val thisResult = this.parse(input)
                    return when (thisResult) {
                        is ParserResult.Failure -> thisResult
                        is ParserResult.Success -> {
                            when (predicate(thisResult.value)) {
                                true -> thisResult
                                false -> ParserResult.Failure(
                                        expected = name,
                                        position = input.position,
                                        column = input.column,
                                        line = input.line
                                )
                            }
                        }
                    }
                }
        )