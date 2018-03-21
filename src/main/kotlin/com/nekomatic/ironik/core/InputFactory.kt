package com.nekomatic.ironik.core

abstract class InputFactory<TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> {
    abstract fun create(
            stream: TStr,
            nextEolIndexFinder: (stream: TStr, currentIndex: Int, lastKnown: Int) -> Int,
            itemAtIndex: (stream: TStr, currentIndex: Int) -> TItem,
            isPastEnd: (input: TStr, currentIndex: Int) -> Boolean,
            index: Int = 0,
            l: Int = 0,
            c: Int = 1,
            lastKnownEolIndex: Int = -1,
            factory: TF
    ): TIn

    abstract fun wrap(
            input: InputBase<TItem, TIn, TStr, TF>,
            factory: TF): TIn


}