package com.nekomatic.ironik.core

import com.nekomatic.ironik.core.parsers.Parser
import com.nekomatic.types.Option

typealias genericParser<T, TStreamItem> = (IInput<TStreamItem>) -> ParserResult<T, TStreamItem>

fun <TStreamItem : Any> createParser(name: String, match: (TStreamItem) -> Boolean): Parser<TStreamItem, TStreamItem> {
    return Parser(name) { input: IInput<TStreamItem> ->
        val currentItem: Option<TStreamItem> = input.item
        when (currentItem) {
            Option.None -> ParserResult.Failure<TStreamItem>(
                    expected = name,
                    position = input.position
            )

            is Option.Some -> {
                val current = currentItem.value
                if (match(current)) {
                    val remaining = input.next()
                    ParserResult.Success<TStreamItem, TStreamItem>(
                            expected = name,
                            value = current,
                            remainingInput = remaining,
                            payload = listOf(current),
                            position = input.position
                    )
                } else
                    ParserResult.Failure<TStreamItem>(
                            expected = name,
                            position = input.position
                    )
            }
        }
    }
}