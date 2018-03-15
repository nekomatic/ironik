package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.Input
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

infix fun <T : Any, TStreamItem : Any> Parser<T, TStreamItem>.onlyIf(condition: Parser<T, TStreamItem>): Parser<T, TStreamItem> {
    val name = "${this.name} only if ${condition.name}"
    return Parser(
            name = name,
            parseFunction = fun(input: IInput<TStreamItem>): ParserResult<T, TStreamItem> {
                val resultA = this.parse(input)
                return when (resultA) {
                    is ParserResult.Failure -> ParserResult.Failure(
                            expected = name,
                            position = resultA.position
                    )
                    is ParserResult.Success -> {
                        val newInput = Input(resultA.payload, 0)

                        val resultB = condition.parse(newInput)
                        when (resultB) {
                            is ParserResult.Success -> resultA
                            is ParserResult.Failure -> ParserResult.Failure(
                                    expected = name,
                                    position = input.position
                            )
                        }
                    }
                }
            })
}