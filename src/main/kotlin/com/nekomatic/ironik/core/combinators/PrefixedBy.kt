package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.parsers.Parser

infix fun <T1 : Any, T2 : Any, TStreamItem : Any> IParser<T1, TStreamItem>.prefixedBy(prefix: IParser<T2, TStreamItem>): IParser<T1, TStreamItem> =
        Parser({ input: IInput<TStreamItem> -> (prefix then this mapValue { it.item02 }).parse(input) })