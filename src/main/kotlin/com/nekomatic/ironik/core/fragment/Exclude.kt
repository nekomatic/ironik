package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser

fun <TStreamItem : Any, TInput : IInput<TStreamItem>> not(parser: fragmentParser<TStreamItem, TInput>): fragmentParser<TStreamItem, TInput> =

        fun(input: TInput): ParserResult<TStreamItem, TInput> {

            val parserResult = parser(input)
            return if (parserResult is ParserResult.Success)
                ParserResult.Failure<TStreamItem, TInput>(
                        position = input.position,
                        column = input.column,
                        line = input.line
                )
            else{
                val any: fragmentParser<TStreamItem, TInput> = any()
                any(input)
            }

        }
