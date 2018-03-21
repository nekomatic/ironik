package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.InputFactory
import com.nekomatic.ironik.core.InputBase
import com.nekomatic.ironik.core.fragmentParser

infix fun <TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> fragmentParser<TItem, TIn, TStr, TF>.surroundedBy(that: fragmentParser<TItem, TIn, TStr, TF>): fragmentParser<TItem, TIn, TStr, TF> =
        that consumeThen this thenConsume that

fun <TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> fragmentParser<TItem, TIn, TStr, TF>.surroundedBy(left: fragmentParser<TItem, TIn, TStr, TF>, right: fragmentParser<TItem, TIn, TStr, TF>): fragmentParser<TItem, TIn, TStr, TF> =
        left consumeThen this thenConsume right