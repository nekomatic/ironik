package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

infix fun <TA : Any, TB : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> IParser<TA, TStreamItem, TInput>.toConst(const: TB): IParser<TB, TStreamItem, TInput> =
        Parser(
                fun(input: IInput<TStreamItem>): ParserResult<TB, TStreamItem, TInput> {
                    val thisResult = this.parse(input)
                    return when (thisResult) {
                        is ParserResult.Success -> ParserResult.Success(
                                value = const,
                                remainingInput = thisResult.remainingInput,
                                payload = thisResult.payload,
                                position = input.position,
                                column = input.column,
                                line = input.line
                        )
                        is ParserResult.Failure -> thisResult
                    }
                }
        )