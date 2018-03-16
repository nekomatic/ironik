package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

infix fun <T1 : Any, T2 : Any, TStreamItem : Any> IParser<T1, TStreamItem>.sufixedBy(sufix: IParser<T2, TStreamItem>): IParser<T1, TStreamItem> {
    val name = "${this.name} followed by ${sufix.name}"
    return Parser(
            name = name,
            parseFunction = fun(input: IInput<TStreamItem>): ParserResult<T1, TStreamItem> {
                val resultA = this.parse(input)
                return when (resultA) {
                    is ParserResult.Failure -> ParserResult.Failure(resultA.expected, input.position)
                    is ParserResult.Success -> {

                        val resultB = sufix.parse(resultA.remainingInput)
                        when (resultB) {
                            is ParserResult.Failure -> ParserResult.Failure(resultB.expected, resultA.remainingInput.position)
                            is ParserResult.Success -> ParserResult.Success(
                                    expected = name,
                                    value = resultA.value,
                                    remainingInput = resultB.remainingInput,
                                    payload = resultA.payload,
                                    position = resultA.remainingInput.position
                            )
                        }
                    }
                }
            })
}