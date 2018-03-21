package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser

infix fun <TStreamItem : Any, TInput : IInput<TStreamItem>> fragmentParser<TStreamItem, TInput>.onlyIfTrue(predicate: (List<TStreamItem>) -> Boolean): fragmentParser<TStreamItem, TInput> =

        fun(input: TInput): ParserResult<TStreamItem, TInput> {
            val thisResult = this(input)
            return when (thisResult) {
                is ParserResult.Failure -> thisResult
                is ParserResult.Success -> {
                    when (predicate(thisResult.payload)) {
                        true -> thisResult
                        false -> ParserResult.Failure(
                                position = input.position,
                                column = input.column,
                                line = input.line
                        )
                    }
                }
            }
        }
