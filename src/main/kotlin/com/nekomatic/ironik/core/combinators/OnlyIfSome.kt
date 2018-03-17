package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser
import com.nekomatic.types.Option

fun <T : Any, TStreamItem : Any> IParser<Option<T>, TStreamItem>.onlyIfSome(name: String): IParser<T, TStreamItem> =
        Parser(
                fun(input: IInput<TStreamItem>): ParserResult<T, TStreamItem> {
                    val thisResult = this.parse(input)
                    return when (thisResult) {
                        is ParserResult.Failure -> thisResult
                        is ParserResult.Success -> {
                            val option = thisResult.value
                            when (option) {
                                is Option.Some -> ParserResult.Success(
                                        value = option.value,
                                        remainingInput = thisResult.remainingInput,
                                        position = thisResult.position,
                                        payload = thisResult.payload
                                )
                                is Option.None -> ParserResult.Failure(
                                        expected = name,
                                        position = input.position
                                )
                            }
                        }
                    }
                }
        )