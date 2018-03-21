package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser
import com.nekomatic.types.Tuple02

fun <TStreamItem : Any, TInput : IInput<TStreamItem>> fragmentParser<TStreamItem, TInput>.consume(): fragmentParser<TStreamItem, TInput> =

        fun(input: TInput): ParserResult<TStreamItem, TInput> {
            val thisResult = this(input)
            return when (thisResult) {
                is ParserResult.Failure -> ParserResult.Failure(
                        position = input.position,
                        column = input.column,
                        line = input.line
                )
                is ParserResult.Success -> ParserResult.Success<TStreamItem, TInput>(
                        remainingInput = thisResult.remainingInput,
                        payload = listOf(),
                        position = input.position,
                        column = input.column,
                        line = input.line
                )
            }
        }
