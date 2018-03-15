package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any> zeroOrMore(parser: Parser<T, TStreamItem>): Parser<List<T>, TStreamItem> =
        Parser("zero or more of ${parser.name}",
                fun(input: IInput<TStreamItem>): ParserResult<List<T>, TStreamItem> {
                    tailrec fun parseNext(currentInput: IInput<TStreamItem>, accumulatorList: List<ParserResult.Success<T, TStreamItem>> = listOf<ParserResult.Success<T, TStreamItem>>())
                            : ParserResult<List<T>, TStreamItem> {
                        val result = parser.parse(currentInput)
                        return when (result) {
                            is ParserResult.Failure -> ParserResult.Success(
                                    expected = "zero or more of ${parser.name}",
                                    value = accumulatorList.map { r -> r.value },
                                    remainingInput = currentInput,
                                    payload = if (accumulatorList.isEmpty()) listOf<TStreamItem>() else accumulatorList.map { r: ParserResult.Success<T, TStreamItem> -> r.payload }.reduce({ a, b -> a + b }),
                                    position = input.position
                            )
                            is ParserResult.Success -> parseNext(result.remainingInput, accumulatorList + result)
                        }
                    }
                    return parseNext(input)
                })