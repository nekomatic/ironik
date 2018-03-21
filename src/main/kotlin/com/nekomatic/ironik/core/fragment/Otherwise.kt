package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.InputFactory
import com.nekomatic.ironik.core.InputBase
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser

fun <TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> fragmentParser<TItem, TIn, TStr, TF>.otherwise(name: String, that: fragmentParser<TItem, TIn, TStr, TF>): fragmentParser<TItem, TIn, TStr, TF> =

        fun(input: TIn): ParserResult<TItem, TIn, TStr, TF> {
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
