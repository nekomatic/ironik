package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> zeroOrMore(parser: IParser<T, TStreamItem, TInput>): IParser<List<T>, TStreamItem, TInput> =
        Parser(
                fun(input: IInput<TStreamItem>): ParserResult<List<T>, TStreamItem, TInput> {
                    tailrec fun parseNext(currentInput: IInput<TStreamItem>, accumulatorList: List<ParserResult.Success<T, TStreamItem, TInput>> = listOf<ParserResult.Success<T, TStreamItem, TInput>>()): ParserResult<List<T>, TStreamItem, TInput> {
                        val parserResult = parser.parse(currentInput)
                        return when (parserResult) {
                            is ParserResult.Failure -> ParserResult.Success(
                                    value = accumulatorList.map { r -> r.value },
                                    remainingInput = currentInput,
                                    payload = if (accumulatorList.isEmpty())
                                        listOf<TStreamItem>()
                                    else
                                        accumulatorList.map { r: ParserResult.Success<T, TStreamItem, TInput> -> r.payload }.reduce({ a, b -> a + b }),
                                    position = input.position
                            )
                            is ParserResult.Success -> parseNext(parserResult.remainingInput, accumulatorList + parserResult)
                        }
                    }
                    return parseNext(input)
                }
        )