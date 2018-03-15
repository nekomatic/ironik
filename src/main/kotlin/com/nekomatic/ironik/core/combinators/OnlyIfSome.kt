package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser
import com.nekomatic.types.Option

fun <T : Any, TStreamItem : Any> Parser<Option<T>, TStreamItem>.onlyIfSome(): Parser<T, TStreamItem> =
        Parser("Some ${this.name}",
                fun(input: IInput<TStreamItem>): ParserResult<T, TStreamItem> {

                    val resultA = this.parse(input)
                    return when (resultA) {
                        is ParserResult.Failure -> resultA
                        is ParserResult.Success -> {
                            val option = resultA.value
                            when (option) {
                                is Option.Some -> ParserResult.Success(
                                        expected = "Some ${this.name}",
                                        value = option.value,
                                        remainingInput = resultA.remainingInput,
                                        position = resultA.position,
                                        payload = resultA.payload
                                )
                                is Option.None -> ParserResult.Failure(
                                        expected = "Some ${this.name}",
                                        position = input.position
                                )
                            }
                        }
                    }
                })