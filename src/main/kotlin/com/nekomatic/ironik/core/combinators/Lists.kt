package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser


infix fun <T : Any, TStreamItem : Any> IParser<T, TStreamItem>.join(that: IParser<T, TStreamItem>): IParser<List<T>, TStreamItem> =
        Parser(
                 fun(input: IInput<TStreamItem>): ParserResult<List<T>, TStreamItem> {
                    val thisResult = this.parse(input)
                    return when (thisResult) {
                        is ParserResult.Failure -> ParserResult.Failure(thisResult.expected, input.position)
                        is ParserResult.Success -> {
                            val thatResult = that.parse(thisResult.remainingInput)
                            when (thatResult) {
                                is ParserResult.Failure -> ParserResult.Failure(thatResult.expected, thisResult.remainingInput.position)
                                is ParserResult.Success -> ParserResult.Success(
                                        value = kotlin.collections.listOf(thisResult.value, thatResult.value),
                                        remainingInput = thatResult.remainingInput,
                                        payload = thisResult.payload + thatResult.payload,
                                        position = input.position
                                )
                            }
                        }
                    }
                }
        )

infix fun <T1 : Any, T2 : Any, TStreamItem : Any> IParser<T1, TStreamItem>.listOfSeparatedBy(separator: IParser<T2, TStreamItem>) =
        (this.mapValue { listOf(it) } join zeroOrMore(this prefixedBy separator)).mapValue { it.flatten() }
