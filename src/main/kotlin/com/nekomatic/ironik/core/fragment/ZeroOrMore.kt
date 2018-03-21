package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.InputFactory
import com.nekomatic.ironik.core.InputBase
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser

fun <TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> zeroOrMore(parser: fragmentParser<TItem, TIn, TStr, TF>): fragmentParser<TItem, TIn, TStr, TF> =

        fun(input: TIn): ParserResult<TItem, TIn, TStr, TF> {
            tailrec fun parseNext(currentInput: TIn, accumulatorList: List<ParserResult.Success<TItem, TIn, TStr, TF>> = listOf<ParserResult.Success<TItem, TIn, TStr, TF>>()): ParserResult<TItem, TIn, TStr, TF> {
                val parserResult = parser(currentInput)
                return when (parserResult) {
                    is ParserResult.Failure -> ParserResult.Success(
                            remainingInput = currentInput,
                            payload = if (accumulatorList.isEmpty())
                                listOf<TItem>()
                            else
                                accumulatorList.map { r: ParserResult.Success<TItem, TIn, TStr, TF> -> r.payload }.reduce({ a, b -> a + b }),
                            position = input.position,
                            column = input.column,
                            line = input.line
                    )
                    is ParserResult.Success -> parseNext(parserResult.remainingInput, accumulatorList + parserResult)
                }
            }
            return parseNext(input)
        }
