package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any> zeroOrMore(parser: IParser<T, TStreamItem>): IParser<List<T>, TStreamItem> =
        Parser(
                fun(input: IInput<TStreamItem>): ParserResult<List<T>, TStreamItem> {
                    tailrec fun parseNext(currentInput: IInput<TStreamItem>, accumulatorList: List<ParserResult.Success<T, TStreamItem>> = listOf<ParserResult.Success<T, TStreamItem>>()): ParserResult<List<T>, TStreamItem> {
                        val parserResult = parser.parse(currentInput)
                        return when (parserResult) {
                            is ParserResult.Failure -> ParserResult.Success(
                                    value = accumulatorList.map { r -> r.value },
                                    remainingInput = currentInput,
                                    payload = if (accumulatorList.isEmpty())
                                        listOf<TStreamItem>()
                                    else
                                        accumulatorList.map { r: ParserResult.Success<T, TStreamItem> -> r.payload }.reduce({ a, b -> a + b }),
                                    position = input.position
                            )
                            is ParserResult.Success -> parseNext(parserResult.remainingInput, accumulatorList + parserResult)
                        }
                    }
                    return parseNext(input)
                }
        )