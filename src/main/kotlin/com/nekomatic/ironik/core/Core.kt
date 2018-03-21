package com.nekomatic.ironik.core

import com.nekomatic.types.Option

typealias fragmentParser<TItem, TIn, TStr, TF> = (TIn) -> ParserResult<TItem, TIn, TStr, TF>

fun <TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> createParser(match: (TItem) -> Boolean): fragmentParser<TItem, TIn, TStr, TF> =

        fun(input: TIn): ParserResult<TItem, TIn, TStr, TF> {
            val currentItem: Option<TItem> = input.item
            return when (currentItem) {
                Option.None -> ParserResult.Failure<TItem, TIn, TStr, TF>(
                        position = input.position,
                        column = input.column,
                        line = input.line
                )
                is Option.Some -> {
                    val current = currentItem.value
                    if (match(current)) {
                        val remaining: TIn = input.next()
                        ParserResult.Success<TItem, TIn, TStr, TF>(
                                remainingInput = remaining,
                                payload = listOf(current),
                                position = input.position,
                                column = input.column,
                                line = input.line
                        )
                    } else
                        ParserResult.Failure<TItem, TIn, TStr, TF>(
                                position = input.position,
                                column = input.column,
                                line = input.line
                        )
                }
            }
        }

fun <TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> createParser(match: TItem): fragmentParser<TItem, TIn, TStr, TF> =

        fun(input: TIn): ParserResult<TItem, TIn, TStr, TF> {
            val currentItem: Option<TItem> = input.item
            return when (currentItem) {
                Option.None -> ParserResult.Failure<TItem, TIn, TStr, TF>(
                        position = input.position,
                        column = input.column,
                        line = input.line
                )
                is Option.Some -> {
                    val current = currentItem.value
                    if (current == match) {
                        val remaining = input.next()
                        ParserResult.Success<TItem, TIn, TStr, TF>(
                                remainingInput = remaining,
                                payload = listOf(current),
                                position = input.position,
                                column = input.column,
                                line = input.line
                        )
                    } else
                        ParserResult.Failure<TItem, TIn, TStr, TF>(
                                position = input.position,
                                column = input.column,
                                line = input.line
                        )
                }
            }
        }


fun <TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> ParserResult<TItem, TIn, TStr, TF>.chainResults(parser: fragmentParser<TItem, TIn, TStr, TF>) =
        when (this) {
            is ParserResult.Failure -> this
            is ParserResult.Success -> {
                val result = parser(this.remainingInput)
                when (result) {
                    is ParserResult.Success -> ParserResult.Success<TItem, TIn, TStr, TF>(
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