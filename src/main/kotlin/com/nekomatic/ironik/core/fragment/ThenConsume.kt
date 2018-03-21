package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.InputFactory
import com.nekomatic.ironik.core.InputBase
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser


infix fun <TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> fragmentParser<TItem, TIn, TStr, TF>.thenConsume(that: fragmentParser<TItem, TIn, TStr, TF>): fragmentParser<TItem, TIn, TStr, TF> =

        fun(input: TIn): ParserResult<TItem, TIn, TStr, TF> {
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
                        is ParserResult.Failure -> com.nekomatic.ironik.core.ParserResult.Failure<TItem, TIn, TStr, TF>(
                                position = thisResult.remainingInput.position,
                                column = thisResult.remainingInput.column,
                                line = thisResult.remainingInput.line
                        )
                        is ParserResult.Success -> com.nekomatic.ironik.core.ParserResult.Success<TItem, TIn, TStr, TF>(
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