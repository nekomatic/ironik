package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any> oneOrNone(parser: IParser<T, TStreamItem>, defaultValue: T): IParser<T, TStreamItem> =
        Parser(
                { input: IInput<TStreamItem> ->
                    val parserRresult = parser.parse(input)
                    when (parserRresult) {
                        is ParserResult.Failure -> ParserResult.Success(
                                value = defaultValue,
                                remainingInput = input,
                                payload = listOf(),
                                position = input.position
                        )
                        is ParserResult.Success -> parserRresult
                    }
                }
        )