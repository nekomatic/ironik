package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

infix fun <T : Any, TStreamItem : Any> Parser<T, TStreamItem>.otherwise(second: Parser<T, TStreamItem>): Parser<T, TStreamItem> {
    val p1 = this
    return Parser("${this.name} otherwise ${second.name} ",
            fun(input: IInput<TStreamItem>): ParserResult<T, TStreamItem> {

                val resultA = p1.parse(input)

                return when (resultA) {
                    is ParserResult.Success -> resultA
                    is ParserResult.Failure -> {
                        val resultB = second.parse(input)
                        when (resultB) {
                            is ParserResult.Success -> resultB
                            is ParserResult.Failure -> ParserResult.Failure(
                                    expected = resultA.expected + " orElse " + resultB.expected,
                                    position = input.position
                            )
                        }
                    }
                }
            })
}