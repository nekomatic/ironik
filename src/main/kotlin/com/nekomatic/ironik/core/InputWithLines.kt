package com.nekomatic.ironik.core

import com.nekomatic.types.Option

//TODO: change creation
open class InputWithLines<TStreamItem : Any, TInput : InputWithLines<TStreamItem, TInput, TStream>, TStream : Any>(
        protected val stream: TStream,
        private val nextEolIndexFinder: (stream: TStream, currentIndex: Int, lastKnown: Int) -> Int,
        private val itemAtIndex: (stream: TStream, currentIndex: Int) -> TStreamItem,
        private val isPastEnd: (input: TStream, currentIndex: Int) -> Boolean,
        private val index: Int = 0,
        private val l: Int = 0,
        private val c: Int = 1,
        private val lastKnownEolIndex: Int = 0

) : IInput<TStreamItem> {

    companion object {

        fun <TStreamItem : Any, TInput : InputWithLines<TStreamItem, TInput, TStream>, TStream : Any> create(
                stream: TStream,
                nextEolIndexFinder: (stream: TStream, currentIndex: Int, lastKnown: Int) -> Int,
                itemAtIndex: (stream: TStream, currentIndex: Int) -> TStreamItem,
                isPastEnd: (input: TStream, currentIndex: Int) -> Boolean

        ) =
                InputWithLines<TStreamItem, TInput, TStream>(
                        stream = stream,
                        nextEolIndexFinder = nextEolIndexFinder,
                        itemAtIndex = itemAtIndex,
                        isPastEnd = isPastEnd
                )

        fun <TStreamItem : Any, TInput : InputWithLines<TStreamItem, TInput, TStream>, TStream : Any> create(
                stream: TStream,
                itemAtIndex: (stream: TStream, currentIndex: Int) -> TStreamItem,
                isPastEnd: (input: TStream, currentIndex: Int) -> Boolean

        ) = InputWithLines<TStreamItem, TInput, TStream>(
                stream = stream,
                nextEolIndexFinder = { _: TStream, _: Int, _: Int -> -1 },
                itemAtIndex = itemAtIndex,
                isPastEnd = isPastEnd
        )
    }

    override val item: Option<TStreamItem>
        get() {
//            val nextEolIndex: Int = findNextEolIndex()
            return if (isPastEnd.invoke(this.stream, this.position))
                Option.None
            else
                Option.Some(itemAtIndex.invoke(this.stream, index))
        }


    override fun next(): InputWithLines<TStreamItem, TInput, TStream> {
        val r = when {
            isPastEnd.invoke(this.stream, this.position) -> this
            index == lastKnownEolIndex -> this.nextItem(newLine = true)
            else -> this.nextItem(newLine = false)
        }
        return r
    }

    private fun nextItem(newLine: Boolean): InputWithLines<TStreamItem, TInput, TStream> {
        val nextEolIndex: Int = findNextEolIndex()
        return if (isPastEnd.invoke(this.stream, this.position))
            this
        else
            InputWithLines<TStreamItem, TInput, TStream>(
                    stream = this.stream,
                    nextEolIndexFinder = nextEolIndexFinder,
                    itemAtIndex = itemAtIndex,
                    isPastEnd = isPastEnd,
                    index = index + 1,
                    l = if (newLine) line + 1 else line,
                    c = if (newLine) 1 else c + 1,
                    lastKnownEolIndex = nextEolIndex
            )
    }

    private fun findNextEolIndex(): Int = nextEolIndexFinder(this.stream, this.position, this.lastKnownEolIndex)

    override val position: Int
        get() = index

    override val line: Int
        get() = l

    override val column: Int
        get() = c
}