package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser

fun <TStreamItem : Any, TInput : IInput<TStreamItem>> zeroOrMore(parser: fragmentParser<TStreamItem, TInput>): fragmentParser<TStreamItem, TInput> =

        fun(input: TInput): ParserResult<TStreamItem, TInput> {
            tailrec fun parseNext(currentInput: TInput, accumulatorList: List<ParserResult.Success<TStreamItem, TInput>> = listOf<ParserResult.Success<TStreamItem, TInput>>()): ParserResult<TStreamItem, TInput> {
                val parserResult = parser(currentInput)
                return when (parserResult) {
                    is ParserResult.Failure -> ParserResult.Success(
                            remainingInput = currentInput,
                            payload = if (accumulatorList.isEmpty())
                                listOf<TStreamItem>()
                            else
                                accumulatorList.map { r: ParserResult.Success<TStreamItem, TInput> -> r.payload }.reduce({ a, b -> a + b }),
                            position = input.position,
                            column = input.column,
                            line = input.line
                    )
                    is ParserResult.Success -> parseNext(parserResult.remainingInput, accumulatorList + parserResult)
                }
            }
            return parseNext(input)
        }
