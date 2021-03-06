package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any> IParser<T, TStreamItem>.otherwise(name: String, that: IParser<T, TStreamItem>): IParser<T, TStreamItem> =
        Parser(
                fun(input: IInput<TStreamItem>): ParserResult<T, TStreamItem> {
                    val thisResult = this.parse(input)
                    return when (thisResult) {
                        is ParserResult.Success -> thisResult
                        is ParserResult.Failure -> {
                            val thatResult = that.parse(input)
                            when (thatResult) {
                                is ParserResult.Success -> thatResult
                                is ParserResult.Failure -> ParserResult.Failure(
                                        expected = name,
                                        position = input.position
                                )
                            }
                        }
                    }
                }
        )