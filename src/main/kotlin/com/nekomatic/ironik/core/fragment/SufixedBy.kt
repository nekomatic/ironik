package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.fragmentParser

infix fun <TStreamItem : Any, TInput : IInput<TStreamItem>> fragmentParser<TStreamItem, TInput>.suffixedBy(suffix: fragmentParser<TStreamItem, TInput>): fragmentParser<TStreamItem, TInput> =
        this thenConsume suffix