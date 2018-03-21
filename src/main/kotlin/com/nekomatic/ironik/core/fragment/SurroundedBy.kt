package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.fragmentParser

infix fun <TStreamItem : Any, TInput : IInput<TStreamItem>> fragmentParser<TStreamItem, TInput>.surroundedBy(that: fragmentParser<TStreamItem, TInput>): fragmentParser<TStreamItem, TInput> =
        that consumeThen this thenConsume that

fun <TStreamItem : Any, TInput : IInput<TStreamItem>> fragmentParser<TStreamItem, TInput>.surroundedBy(left: fragmentParser<TStreamItem, TInput>, right: fragmentParser<TStreamItem, TInput>): fragmentParser<TStreamItem, TInput> =
    left consumeThen this thenConsume right