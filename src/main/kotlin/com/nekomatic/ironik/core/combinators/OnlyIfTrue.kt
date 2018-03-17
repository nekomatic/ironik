package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any> IParser<T, TStreamItem>.onlyIfTrue(name: String, predicate: (T) -> Boolean): IParser<T, TStreamItem> =
        Parser(
                fun(input: IInput<TStreamItem>): ParserResult<T, TStreamItem> {
                    val thisResult = this.parse(input)
                    return when (thisResult) {
                        is ParserResult.Failure -> thisResult
                        is ParserResult.Success -> {
                            when (predicate(thisResult.value)) {
                                true -> thisResult
                                false -> ParserResult.Failure(
                                        expected = name,
                                        position = input.position
                                )
                            }
                        }
                    }
                }
        )