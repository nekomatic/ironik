package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.InputFactory
import com.nekomatic.ironik.core.InputBase
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser

fun <TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> not(parser: fragmentParser<TItem, TIn, TStr, TF>): fragmentParser<TItem, TIn, TStr, TF> =

        fun(input: TIn): ParserResult<TItem, TIn, TStr, TF> {

            val parserResult = parser(input)
            return if (parserResult is ParserResult.Success)
                ParserResult.Failure<TItem, TIn, TStr, TF>(
                        position = input.position,
                        column = input.column,
                        line = input.line
                )
            else{
                val any: fragmentParser<TItem, TIn, TStr, TF> = any()
                any(input)
            }

        }
