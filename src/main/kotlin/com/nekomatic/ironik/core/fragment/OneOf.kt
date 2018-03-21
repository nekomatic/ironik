package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser

fun <TStreamItem : Any, TInput : IInput<TStreamItem>> oneOf(vararg tokenParsers: fragmentParser<TStreamItem, TInput>): fragmentParser<TStreamItem, TInput> =

        fun(input: TInput): ParserResult<TStreamItem, TInput> {
            for (parser in tokenParsers) {
                val parserResult = parser(input)
                if (parserResult is ParserResult.Success) return parserResult
            }
            return ParserResult.Failure(
                    position = input.position,
                    column = input.column,
                    line = input.line
            )
        }
