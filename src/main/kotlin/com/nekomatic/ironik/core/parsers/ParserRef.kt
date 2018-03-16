package com.nekomatic.ironik.core.parsers

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult


class ParserRef<T : Any, TStreamItem : Any>() : IParser<T, TStreamItem> {

    val dummyParser = Parser<T, TStreamItem>(
            name = "invalid parser",
            parseFunction = { input ->
                ParserResult.Failure("invalid parser", input.position)
            })

    override fun parse(input: IInput<TStreamItem>): ParserResult<T, TStreamItem> = p.parse(input)
    override val name: String by lazy { p.name }
    private var p: Parser<T, TStreamItem> = dummyParser
    fun setParser(parser: Parser<T, TStreamItem>) {
        p = parser
    }
}