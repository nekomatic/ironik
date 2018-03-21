package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser


infix fun <TStreamItem : Any, TInput : IInput<TStreamItem>> fragmentParser<TStreamItem, TInput>.thenConsume(that: fragmentParser<TStreamItem, TInput>): fragmentParser<TStreamItem, TInput> =

        fun(input: TInput): ParserResult<TStreamItem, TInput> {
            val thisResult = this(input)
            return when (thisResult) {
                is ParserResult.Failure -> com.nekomatic.ironik.core.ParserResult.Failure(
                        position = input.position,
                        column = input.column,
                        line = input.line
                )
                is ParserResult.Success -> {
                    val thatResult = that(thisResult.remainingInput)
                    when (thatResult) {
                        is ParserResult.Failure -> com.nekomatic.ironik.core.ParserResult.Failure<TStreamItem, TInput>(
                                position = thisResult.remainingInput.position,
                                column = thisResult.remainingInput.column,
                                line = thisResult.remainingInput.line
                        )
                        is ParserResult.Success -> com.nekomatic.ironik.core.ParserResult.Success<TStreamItem, TInput>(
                                remainingInput = thatResult.remainingInput,
                                payload = thisResult.payload,
                                position = input.position,
                                column = input.column,
                                line = input.line
                        )
                    }
                }
            }
        }