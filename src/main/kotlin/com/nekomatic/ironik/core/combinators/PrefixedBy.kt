package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

infix fun <T1 : Any, T2 : Any, TStreamItem : Any> IParser<T1, TStreamItem>.prefixedBy(prefix: IParser<T2, TStreamItem>): IParser<T1, TStreamItem> {
    val name = "${prefix.name} and then ${this.name}"
    return Parser(
            name = name,
            parseFunction = fun(input: IInput<TStreamItem>): ParserResult<T1, TStreamItem> {
                val resultA = prefix.parse(input)
                return when (resultA) {
                    is ParserResult.Failure -> ParserResult.Failure(name, input.position)
                    is ParserResult.Success -> {
                        val resultB = this.parse(resultA.remainingInput)
                        when (resultB) {
                            is ParserResult.Failure -> ParserResult.Failure(name, resultA.remainingInput.position)
                            is ParserResult.Success -> ParserResult.Success(
                                    expected = name,
                                    value = resultB.value,
                                    remainingInput = resultB.remainingInput,
                                    payload = resultB.payload,
                                    position = input.position
                            )
                        }
                    }
                }
            })
}