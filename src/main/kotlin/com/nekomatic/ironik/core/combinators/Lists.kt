package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser


infix fun <T : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> IParser<T, TStreamItem, TInput>.join(that: IParser<T, TStreamItem, TInput>): IParser<List<T>, TStreamItem, TInput> =
        Parser(
                fun(input: IInput<TStreamItem>): ParserResult<List<T>, TStreamItem, TInput> {
                    val thisResult = this.parse(input)
                    return when (thisResult) {
                        is ParserResult.Failure -> ParserResult.Failure(
                                expected = thisResult.expected,
                                position = input.position,
                                column = input.column,
                                line = input.line
                        )
                        is ParserResult.Success -> {
                            val thatResult = that.parse(thisResult.remainingInput)
                            when (thatResult) {
                                is ParserResult.Failure -> ParserResult.Failure<TStreamItem, TInput>(
                                        expected = thatResult.expected,
                                        position = thisResult.remainingInput.position,
                                        column = thisResult.remainingInput.column,
                                        line = thisResult.remainingInput.line
                                )
                                is ParserResult.Success -> ParserResult.Success<List<T>, TStreamItem, TInput>(
                                        value = kotlin.collections.listOf(thisResult.value, thatResult.value),
                                        remainingInput = thatResult.remainingInput,
                                        payload = thisResult.payload + thatResult.payload,
                                        position = input.position,
                                        column = input.column,
                                        line = input.line
                                )
                            }
                        }
                    }
                }
        )

infix fun <T1 : Any, T2 : Any, TStreamItem : Any, TStream : IInput<TStreamItem>> IParser<T1, TStreamItem, TStream>.listOfSeparatedBy(separator: IParser<T2, TStreamItem, TStream>) =
        (this.mapValue { listOf(it) } join zeroOrMore(this prefixedBy separator)).mapValue { it.flatten() }
