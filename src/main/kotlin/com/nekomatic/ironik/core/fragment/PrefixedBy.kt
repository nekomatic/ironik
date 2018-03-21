package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.fragmentParser

infix fun <TStreamItem : Any, TInput : IInput<TStreamItem>> fragmentParser<TStreamItem, TInput>.prefixedBy(prefix: fragmentParser<TStreamItem, TInput>): fragmentParser<TStreamItem, TInput> =
        prefix consumeThen this
