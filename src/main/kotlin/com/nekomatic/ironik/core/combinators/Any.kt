package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.createParser
import com.nekomatic.ironik.core.parsers.Parser

fun <TStreamItem : Any> any(): Parser<TStreamItem, TStreamItem> = createParser("Any") { _: TStreamItem -> true }
