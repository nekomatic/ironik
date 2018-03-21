package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.InputFactory
import com.nekomatic.ironik.core.InputBase
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser

fun <TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> oneOrMore(parser: fragmentParser<TItem, TIn, TStr, TF>): fragmentParser<TItem, TIn, TStr, TF> =

        fun(input: TIn): ParserResult<TItem, TIn, TStr, TF> {
            val parserResult = parser(input)
            return when (parserResult) {
                is ParserResult.Failure -> ParserResult.Failure(
                        position = input.position,
                        column = input.column,
                        line = input.line
                )
                is ParserResult.Success -> {
                    val resultB: ParserResult<TItem, TIn, TStr, TF> = zeroOrMore(parser)(parserResult.remainingInput)
                    return when (resultB) {
                        is ParserResult.Failure<TItem, TIn, TStr, TF> -> ParserResult.Failure(
                                position = input.position,
                                column = input.column,
                                line = input.line
                        )
                        is ParserResult.Success -> ParserResult.Success(
                                remainingInput = resultB.remainingInput,
                                payload = parserResult.payload + resultB.payload,
                                position = input.position,
                                column = input.column,
                                line = input.line
                        )
                    }
                }
            }
        }
