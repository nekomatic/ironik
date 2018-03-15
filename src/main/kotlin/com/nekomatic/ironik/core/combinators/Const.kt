package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

infix fun <TA : Any, TB : Any, TStreamItem : Any> Parser<TA, TStreamItem>.toConst(const: TB) =
        Parser(this.name,
                fun(input: IInput<TStreamItem>): ParserResult<TB, TStreamItem> {

                    val resultA = this.parse(input)
                    return when (resultA) {
                        is ParserResult.Success -> ParserResult.Success(
                                expected = this.name,
                                value = const,
                                remainingInput = resultA.remainingInput,
                                payload = resultA.payload,
                                position = input.position
                        )
                        is ParserResult.Failure -> resultA
                    }
                })