package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser


infix fun <T : Any, TStreamItem : Any> IParser<T, TStreamItem>.join(second: IParser<T, TStreamItem>):  IParser<List<T>, TStreamItem> {
    return Parser(name = "",
            parseFunction = fun(input: IInput<TStreamItem>): ParserResult<List<T>, TStreamItem> {
                val resultA = this.parse(input)
                return when (resultA) {
                    is ParserResult.Failure -> ParserResult.Failure(resultA.expected, input.position)
                    is ParserResult.Success -> {
                        val resultB = second.parse(resultA.remainingInput)
                        when (resultB) {
                            is ParserResult.Failure -> ParserResult.Failure(resultB.expected, resultA.remainingInput.position)
                            is ParserResult.Success -> ParserResult.Success(
                                    expected = name,
                                    value = kotlin.collections.listOf(resultA.value, resultB.value),
                                    remainingInput = resultB.remainingInput,
                                    payload = resultA.payload + resultB.payload,
                                    position = input.position
                            )
                        }
                    }
                }
            })
}

infix fun <T1 : Any, T2 : Any, TStreamItem : Any> IParser<T1, TStreamItem>.listOfSeparatedBy(separator: IParser<T2, TStreamItem>) =
        (this.mapValue { listOf(it) } join zeroOrMore(this prefixedBy separator)).mapValue { it.flatten() }
