package com.nekomatic.ironik.core.parsers

import com.nekomatic.ironik.core.*

open class Parser<out T : Any, TStreamItem : Any>(override val name: String, val parseFunction: genericParser<T, TStreamItem>) : IParser<T, TStreamItem> {
    override fun parse(input: IInput<TStreamItem>): ParserResult<T, TStreamItem> = parseFunction(input )
}