package com.nekomatic.ironik.core.parsers

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.StreamItem
import com.nekomatic.types.Option

class EofParser<out T : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> : IParser<StreamItem.End.OfFile, TStreamItem, TInput> {
    val name: String by lazy { "eof" }
    override fun parse(input: IInput<TStreamItem>): ParserResult<StreamItem.End.OfFile, TStreamItem, TInput> =
            when (input.item) {
                is Option.Some<TStreamItem> -> ParserResult.Failure(
                        expected = name,
                        position = input.position,
                        column = input.column,
                        line = input.line
                )
                Option.None -> ParserResult.Success(
                        value = StreamItem.End.OfFile,
                        remainingInput = input,
                        payload = listOf(),
                        position = input.position,
                        column = input.column,
                        line = input.line
                )
            }
}