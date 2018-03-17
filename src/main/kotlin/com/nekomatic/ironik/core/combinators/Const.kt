package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

infix fun <TA : Any, TB : Any, TStreamItem : Any> IParser<TA, TStreamItem>.toConst(const: TB): IParser<TB, TStreamItem> =
        Parser(
                { input: IInput<TStreamItem> ->
                    val thisResult = this.parse(input)
                    when (thisResult) {
                        is ParserResult.Success -> ParserResult.Success(
                                value = const,
                                remainingInput = thisResult.remainingInput,
                                payload = thisResult.payload,
                                position = input.position
                        )
                        is ParserResult.Failure -> thisResult
                    }
                }
        )