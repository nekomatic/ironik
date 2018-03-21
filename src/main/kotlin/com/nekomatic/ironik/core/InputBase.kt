package com.nekomatic.ironik.core

import com.nekomatic.types.Option


//TODO: change creation
open class InputBase<TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>>(
        protected val stream: TStr,
        private val eolTestParser: (stream: TStr, currentIndex: Int, lastKnown: Int) -> Int,
        private val itemAtIndex: (stream: TStr, currentIndex: Int) -> TItem,
        private val isPastEnd: (input: TStr, currentIndex: Int) -> Boolean,
        private val index: Int = 0,
        private val l: Int = 0,
        private val c: Int = 1,
        private val lastKnownEolIndex: Int = -1,
        private val inputFactory: TF
) : IInput<TItem, TIn, TStr, TF> {
    override val item: Option<TItem>
        get() {
            return if (isPastEnd.invoke(this.stream, this.position))
                Option.None
            else
                Option.Some(itemAtIndex.invoke(this.stream, index))
        }
    override fun next(): TIn {
        val nextEolIndex: Int = findNextEolIndex()
        val r = when {
            isPastEnd.invoke(this.stream, this.position) -> this
            index == nextEolIndex -> this.nextItem(newLine = true, lastKnownEolIndex = nextEolIndex)
            else -> this.nextItem(newLine = false, lastKnownEolIndex = lastKnownEolIndex)
        }
        return this.inputFactory.wrap(r, inputFactory)
    }

    private fun nextItem(newLine: Boolean, lastKnownEolIndex: Int): TIn {
        return if (isPastEnd.invoke(this.stream, this.position))
            this.inputFactory.wrap(this, inputFactory) // TODO:replace this with option.none
        else
        // TODO:replace this with option.some instantiated from generic TIn
            this.inputFactory.create(
                    stream = this.stream,
                    nextEolIndexFinder = eolTestParser,
                    itemAtIndex = itemAtIndex,
                    isPastEnd = isPastEnd,
                    index = index + 1,
                    l = if (newLine) line + 1 else line,
                    c = if (newLine) 1 else c + 1,
                    lastKnownEolIndex = lastKnownEolIndex,
                    factory = inputFactory
            )
    }

    private fun findNextEolIndex(): Int = eolTestParser(this.stream, this.position, this.lastKnownEolIndex)

    override val position: Int
        get() = index

    override val line: Int
        get() = l

    override val column: Int
        get() = c
}