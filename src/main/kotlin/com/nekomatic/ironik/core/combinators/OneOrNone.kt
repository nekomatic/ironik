package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

fun <T : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> oneOrNone(parser: IParser<T, TStreamItem, TInput>, defaultValue: T): IParser<T, TStreamItem, TInput> =
        Parser(
                fun(input: IInput<TStreamItem>): ParserResult<T, TStreamItem, TInput> {
                    val parserRresult: ParserResult<T, TStreamItem, TInput> = parser.parse(input)
                    return when (parserRresult) {
                        is ParserResult.Failure -> ParserResult.Success<T, TStreamItem, TInput>(
                                value = defaultValue,
                                remainingInput = input,
                                payload = listOf(),
                                position = input.position,
                                column = input.column,
                                line = input.line
                        )
                        is ParserResult.Success -> parserRresult
                    }
                }
        )