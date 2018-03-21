package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser

fun <TStreamItem : Any, TInput : IInput<TStreamItem>> fragmentParser<TStreamItem, TInput>.otherwise(name: String, that: fragmentParser<TStreamItem, TInput>): fragmentParser<TStreamItem, TInput> =

        fun(input: TInput): ParserResult<TStreamItem, TInput> {
            val thisResult = this(input)
            return when (thisResult) {
                is ParserResult.Success -> thisResult
                is ParserResult.Failure -> {
                    val thatResult = that(input)
                    when (thatResult) {
                        is ParserResult.Success -> thatResult
                        is ParserResult.Failure -> ParserResult.Failure(
                                position = input.position,
                                column = input.column,
                                line = input.line
                        )
                    }
                }
            }
        }
