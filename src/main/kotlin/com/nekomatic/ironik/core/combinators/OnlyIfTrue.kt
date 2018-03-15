package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

infix fun <T : Any, TStreamElement : Any> Parser<T, TStreamElement>.onlyIfTrue(predicate: (T) -> Boolean) =
        Parser("${this.name} only if Match",
                fun(input: IInput<TStreamElement>): ParserResult<T, TStreamElement> {

                    val resultA = this.parse(input)
                    return when (resultA) {
                        is ParserResult.Failure -> resultA
                        is ParserResult.Success -> {

                            when (predicate(resultA.value)) {
                                true -> resultA
                                false -> ParserResult.Failure(
                                        expected = "${this.name} only if Match",
                                        position = input.position
                                )
                            }
                        }
                    }
                })