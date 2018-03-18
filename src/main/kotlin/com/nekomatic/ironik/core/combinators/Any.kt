package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.createParser

fun <TStreamItem : Any, TInput : IInput<TStreamItem>> any(): IParser<TStreamItem, TStreamItem, TInput> = createParser("Any") { _: TStreamItem -> true }
