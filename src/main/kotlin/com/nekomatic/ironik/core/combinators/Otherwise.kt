package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

infix fun <T : Any, TStreamItem : Any> IParser<T, TStreamItem>.otherwise(that: IParser<T, TStreamItem>): IParser<T, TStreamItem> {
    val name = "${this.name} otherwise ${that.name}"
    return Parser(
            name = name,
            parseFunction = fun(input: IInput<TStreamItem>): ParserResult<T, TStreamItem> {
                val resultA = this.parse(input)
                return when (resultA) {
                    is ParserResult.Success -> resultA
                    is ParserResult.Failure -> {
                        val resultB = that.parse(input)
                        when (resultB) {
                            is ParserResult.Success -> resultB
                            is ParserResult.Failure -> ParserResult.Failure(
                                    expected = name,
                                    position = input.position
                            )
                        }
                    }
                }
            })
}