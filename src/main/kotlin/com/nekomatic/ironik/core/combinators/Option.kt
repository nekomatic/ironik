package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser
import com.nekomatic.types.Option

fun <T : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> option(parser: IParser<T, TStreamItem, TInput>): IParser<Option<T>, TStreamItem, TInput> =
        Parser(
                fun(input: IInput<TStreamItem>): ParserResult<Option<T>, TStreamItem, TInput> {
                    val parserResult = parser.parse(input)
                    return when (parserResult) {
                        is ParserResult.Success -> ParserResult.Success(
                                value = Option.Some(parserResult.value),
                                remainingInput = parserResult.remainingInput,
                                payload = parserResult.payload,
                                position = input.position
                        )
                        is ParserResult.Failure -> ParserResult.Success(
                                value = Option.None,
                                remainingInput = input,
                                payload = listOf(),
                                position = input.position
                        )
                    }
                }
        )