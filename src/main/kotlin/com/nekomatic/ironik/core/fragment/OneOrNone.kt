package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser

fun <T : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> oneOrNone(parser: fragmentParser<TStreamItem, TInput>, defaultValue: T): fragmentParser<TStreamItem, TInput> =

        fun(input: TInput): ParserResult<TStreamItem, TInput> {
            val parserRresult: ParserResult<TStreamItem, TInput> = parser(input)
            return when (parserRresult) {
                is ParserResult.Failure -> ParserResult.Success<TStreamItem, TInput>(
                        remainingInput = input,
                        payload = listOf(),
                        position = input.position,
                        column = input.column,
                        line = input.line
                )
                is ParserResult.Success -> parserRresult
            }
        }
