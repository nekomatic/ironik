package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser
import com.nekomatic.types.Option

fun <T : Any, TStreamItem : Any> option(parser: Parser<T, TStreamItem>): Parser<Option<T>, TStreamItem> {
    val name = "${parser.name} option"
    return Parser(
            name = name,
            parseFunction = fun(input: IInput<TStreamItem>): ParserResult<Option<T>, TStreamItem> {
                val resultA = parser.parse(input)
                return when (resultA) {
                    is ParserResult.Success -> ParserResult.Success(
                            expected = name,
                            value = Option.Some(resultA.value),
                            remainingInput = resultA.remainingInput,
                            payload = resultA.payload,
                            position = input.position
                    )
                    is ParserResult.Failure -> ParserResult.Success(
                            expected = name,
                            value = Option.None,
                            remainingInput = input,
                            payload = listOf(),
                            position = input.position
                    )
                }
            })
}