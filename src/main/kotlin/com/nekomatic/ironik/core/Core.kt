package com.nekomatic.ironik.core

import com.nekomatic.ironik.core.parsers.Parser
import com.nekomatic.types.Option

typealias genericParser<T, TStreamItem> = (IInput<TStreamItem>) -> ParserResult<T, TStreamItem>

fun <TStreamItem : Any> createParser(name: String, match: (TStreamItem) -> Boolean): IParser<TStreamItem, TStreamItem> =
        Parser(
                parseFunction = { input: IInput<TStreamItem> ->
                    val currentItem: Option<TStreamItem> = input.item
                    when (currentItem) {
                        Option.None -> ParserResult.Failure<TStreamItem>(
                                expected = name,
                                position = input.position
                        )
                        is Option.Some -> {
                            val current = currentItem.value
                            if (match(current)) {
                                val remaining = input.next()
                                ParserResult.Success<TStreamItem, TStreamItem>(
                                        value = current,
                                        remainingInput = remaining,
                                        payload = listOf(current),
                                        position = input.position
                                )
                            } else
                                ParserResult.Failure<TStreamItem>(
                                        expected = name,
                                        position = input.position
                                )
                        }
                    }
                })

fun <T1 : Any, T2 : Any, T3 : Any, TStreamItem : Any> ParserResult<T1, TStreamItem>.chainResults(parser: IParser<T2, TStreamItem>, func: (T1, T2) -> T3): ParserResult<T3, TStreamItem> =
        when (this) {
            is ParserResult.Failure -> this
            is ParserResult.Success -> {
                val result = parser.parse(this.remainingInput)
                when (result) {
                    is ParserResult.Success -> ParserResult.Success(
                            value = func(this.value, result.value),
                            remainingInput = result.remainingInput,
                            payload = this.payload + result.payload,
                            position = this.position
                    )
                    is ParserResult.Failure -> result
                }
            }
        }