package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

infix fun <T1 : Any, T2 : Any, TStreamItem : Any> Parser<T1, TStreamItem>.sufixedBy(sufix: Parser<T2, TStreamItem>) =
        Parser( "${this.name} followed by ${sufix.name}",
        fun(input: IInput<TStreamItem>): ParserResult<T1, TStreamItem> {
            val resultA = this.parse(input)
            return when (resultA) {
                is ParserResult.Failure -> ParserResult.Failure(resultA.expected, input.position)
                is ParserResult.Success -> {

                    val resultB = sufix.parse(resultA.remainingInput)
                    when (resultB) {
                        is ParserResult.Failure -> ParserResult.Failure(resultB.expected, resultA.remainingInput.position)
                        is ParserResult.Success -> ParserResult.Success(
                                expected = "${this.name} followed by ${sufix.name}",
                                value = resultA.value,
                                remainingInput = resultB.remainingInput,
                                payload = resultA.payload,
                                position = resultA.remainingInput.position
                        )
                    }
                }
            }
        })