package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.InputFactory
import com.nekomatic.ironik.core.InputBase
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser

fun <TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> oneOf(vararg tokenParsers: fragmentParser<TItem, TIn, TStr, TF>): fragmentParser<TItem, TIn, TStr, TF> =

        fun(input: TIn): ParserResult<TItem, TIn, TStr, TF> {
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
