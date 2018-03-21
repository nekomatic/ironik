package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser

fun <TStreamItem : Any, TInput : IInput<TStreamItem>> oneOrMore(parser: fragmentParser<TStreamItem, TInput>): fragmentParser<TStreamItem, TInput> =

        fun(input: TInput): ParserResult<TStreamItem, TInput> {
            val parserResult = parser(input)
            return when (parserResult) {
                is ParserResult.Failure -> ParserResult.Failure(
                        position = input.position,
                        column = input.column,
                        line = input.line
                )
                is ParserResult.Success -> {
                    val resultB: ParserResult<TStreamItem, TInput> = zeroOrMore(parser)(parserResult.remainingInput)
                    return when (resultB) {
                        is ParserResult.Failure<TStreamItem, TInput> -> ParserResult.Failure(
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
