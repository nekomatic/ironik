package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser
import com.nekomatic.types.Option

fun <T : Any, TStreamItem : Any> option(first: Parser<T, TStreamItem>): Parser<Option<T>, TStreamItem> =
        Parser("${first.name} option",
                fun(input: IInput<TStreamItem>): ParserResult<Option<T>, TStreamItem> {

                    val resultA = first.parse(input)

                    return when (resultA) {
                        is ParserResult.Success -> ParserResult.Success(
                                expected = "${first.name} option",
                                value = Option.Some(resultA.value),
                                remainingInput = resultA.remainingInput,
                                payload = resultA.payload,
                                position = input.position
                        )
                        is ParserResult.Failure -> ParserResult.Success(
                                expected = "${first.name} option",
                                value = Option.None,
                                remainingInput = input,
                                payload = listOf(),
                                position = input.position
                        )
                    }
                })