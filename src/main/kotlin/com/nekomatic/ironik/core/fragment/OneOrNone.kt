package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.InputFactory
import com.nekomatic.ironik.core.InputBase
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser

fun <TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> oneOrNone(parser: fragmentParser<TItem, TIn, TStr, TF>): fragmentParser<TItem, TIn, TStr, TF> =

        fun(input: TIn): ParserResult<TItem, TIn, TStr, TF> {
            val parserRresult: ParserResult<TItem, TIn, TStr, TF> = parser(input)
            return when (parserRresult) {
                is ParserResult.Failure -> ParserResult.Success<TItem, TIn, TStr, TF>(
                        remainingInput = input,
                        payload = listOf(),
                        position = input.position,
                        column = input.column,
                        line = input.line
                )
                is ParserResult.Success -> parserRresult
            }
        }
