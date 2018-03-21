package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser

fun <TStreamItem : Any, TInput : IInput<TStreamItem>> fragmentParser<TStreamItem, TInput>.lookAhead(): fragmentParser<TStreamItem, TInput> =

        fun(input: TInput): ParserResult<TStreamItem, TInput> {
            val thisResult = this(input)
            return when (thisResult) {
                is ParserResult.Failure -> ParserResult.Failure(
                        position = input.position,
                        column = input.column,
                        line = input.line
                )
                is ParserResult.Success -> ParserResult.Success<TStreamItem, TInput>(
                        remainingInput = input,
                        payload = listOf(),
                        position = input.position,
                        column = input.column,
                        line = input.line
                )
            }
        }