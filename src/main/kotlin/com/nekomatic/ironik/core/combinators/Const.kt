package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser

infix fun <TA : Any, TB : Any, TStreamItem : Any> IParser<TA, TStreamItem>.toConst(const: TB): IParser<TB, TStreamItem> {
    return Parser(
            name = this.name,
            parseFunction = { input: IInput<TStreamItem> ->
                val resultA = this.parse(input)
                when (resultA) {
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
}