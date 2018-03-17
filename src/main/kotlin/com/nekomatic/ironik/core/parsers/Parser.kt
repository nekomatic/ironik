package com.nekomatic.ironik.core.parsers

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.genericParser

open class Parser<out T : Any, TStreamItem : Any>(val parseFunction: genericParser<T, TStreamItem>) : IParser<T, TStreamItem> {
    override fun parse(input: IInput<TStreamItem>): ParserResult<T, TStreamItem> = parseFunction(input)
}