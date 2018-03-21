package com.nekomatic.ironik.core.parsers

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ITokenParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser



open class Parser<TStreamItem : Any, TInput : IInput<TStreamItem>>(val parseFunction: fragmentParser<TStreamItem, TInput>) : ITokenParser<TStreamItem, TInput> {
    override fun parse(input: IInput<TStreamItem>): ParserResult<TStreamItem, TInput> = parseFunction(input)
}