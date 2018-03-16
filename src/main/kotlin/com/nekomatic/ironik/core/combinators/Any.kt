package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.createParser

fun <TStreamItem : Any> any(): IParser<TStreamItem, TStreamItem> = createParser("Any") { _: TStreamItem -> true }
