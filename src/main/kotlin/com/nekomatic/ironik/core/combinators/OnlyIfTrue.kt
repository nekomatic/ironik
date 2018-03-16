package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

infix fun <T : Any, TStreamItem : Any> IParser<T, TStreamItem>.onlyIfTrue(predicate: (T) -> Boolean): IParser<T, TStreamItem> {
    val name = "${this.name} only if Match"
    return Parser(
            name = name,
            parseFunction = fun(input: IInput<TStreamItem>): ParserResult<T, TStreamItem> {
                val resultA = this.parse(input)
                return when (resultA) {
                    is ParserResult.Failure -> resultA
                    is ParserResult.Success -> {
                        when (predicate(resultA.value)) {
                            true -> resultA
                            false -> ParserResult.Failure(
                                    expected = name,
                                    position = input.position
                            )
                        }
                    }
                }
            })
}