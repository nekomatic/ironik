package com.nekomatic.ironik.core

import com.nekomatic.types.Option

typealias fragmentParser<TStreamItem, TInput> = (TInput) -> ParserResult<TStreamItem, TInput>

fun <TStreamItem : Any, TInput : IInput<TStreamItem>> createParser(match: (TStreamItem) -> Boolean): fragmentParser<TStreamItem, TInput> =

        fun(input: TInput): ParserResult<TStreamItem, TInput> {
            val currentItem: Option<TStreamItem> = input.item
            return when (currentItem) {
                Option.None -> ParserResult.Failure<TStreamItem, TInput>(
                        position = input.position,
                        column = input.column,
                        line = input.line
                )
                is Option.Some -> {
                    val current = currentItem.value
                    if (match(current)) {
                        val remaining:TInput = input.next()
                        ParserResult.Success<TStreamItem, TInput>(
                                remainingInput = remaining,
                                payload = listOf(current),
                                position = input.position,
                                column = input.column,
                                line = input.line
                        )
                    } else
                        ParserResult.Failure<TStreamItem, TInput>(
                                position = input.position,
                                column = input.column,
                                line = input.line
                        )
                }
            }
        }

fun <TStreamItem : Any, TInput : IInput<TStreamItem>> createParser(match: TStreamItem): fragmentParser<TStreamItem, TInput> =

        fun(input: TInput): ParserResult<TStreamItem, TInput> {
            val currentItem: Option<TStreamItem> = input.item
            return when (currentItem) {
                Option.None -> ParserResult.Failure<TStreamItem, TInput>(
                        position = input.position,
                        column = input.column,
                        line = input.line
                )
                is Option.Some -> {
                    val current = currentItem.value
                    if (current == match) {
                        val remaining = input.next()
                        ParserResult.Success<TStreamItem, TInput>(
                                remainingInput = remaining,
                                payload = listOf(current),
                                position = input.position,
                                column = input.column,
                                line = input.line
                        )
                    } else
                        ParserResult.Failure<TStreamItem, TInput>(
                                position = input.position,
                                column = input.column,
                                line = input.line
                        )
                }
            }
        }


fun <TStreamItem : Any, TInput : IInput<TStreamItem>> ParserResult<TStreamItem, TInput>.chainResults(parser: fragmentParser<TStreamItem, TInput> ) =
        when (this) {
            is ParserResult.Failure -> this
            is ParserResult.Success -> {
                val result = parser(this.remainingInput)
                when (result) {
                    is ParserResult.Success -> ParserResult.Success<TStreamItem, TInput>(
                            remainingInput = result.remainingInput,
                            payload = this.payload + result.payload,
                            position = this.position,
                            column = this.column,
                            line = this.line
                    )
                    is ParserResult.Failure -> result
                }
            }
        }