package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.createParser
import com.nekomatic.ironik.core.fragmentParser

fun <TStreamItem : Any, TInput : IInput<TStreamItem>> any(): fragmentParser<TStreamItem, TInput> = createParser() { _: TStreamItem -> true }
