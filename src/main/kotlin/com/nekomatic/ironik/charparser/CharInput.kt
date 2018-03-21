/*
 * MIT License
 *
 * Copyright (c) 2018 nekomatic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.nekomatic.ironik.charparser

import com.nekomatic.ironik.core.InputFactory
import com.nekomatic.ironik.core.InputBase


class CharInputFactory() : InputFactory<Char, CharInput, CharSequence, CharInputFactory>() {

    override fun wrap(input: InputBase<Char, CharInput, CharSequence, CharInputFactory>, factory: CharInputFactory): CharInput {
        return input as CharInput
    }

    override fun create(
            stream: CharSequence,
            eolTestParser: (stream: CharSequence, currentIndex: Int, lastKnown: Int) -> Int,
            itemAtIndex: (stream: CharSequence, currentIndex: Int) -> Char,
            isPastEnd: (input: CharSequence, currentIndex: Int) -> Boolean,
            index: Int,
            l: Int,
            c: Int,
            lastKnownEolIndex: Int,
            factory: CharInputFactory
    ): CharInput =
            CharInput(
                    charStream = stream,
                    eolTestParser = eolTestParser,
                    itemAtIndex = itemAtIndex,
                    isPastEnd = isPastEnd,
                    index = index,
                    l = l,
                    c = c,
                    lastKnownEolIndex = lastKnownEolIndex,
                    factory = factory
            )
}

class CharInput(
        private val charStream: CharSequence,
        private val eolTestParser: (stream: CharSequence, currentIndex: Int, lastKnown: Int) -> Int,
        private val itemAtIndex: (stream: CharSequence, currentIndex: Int) -> Char,
        private val isPastEnd: (input: CharSequence, currentIndex: Int) -> Boolean,
        private val index: Int = 0,
        private val l: Int = 1,
        private val c: Int = 1,
        private val lastKnownEolIndex: Int = -1,
        private val factory: CharInputFactory
) : InputBase<Char, CharInput, CharSequence, CharInputFactory>(
        stream = charStream,
        eolTestParser = eolTestParser,
        itemAtIndex = itemAtIndex,
        isPastEnd = isPastEnd,
        index = index,
        l = l,
        c = c,
        lastKnownEolIndex = lastKnownEolIndex,
        inputFactory = factory
) {
    companion object {

        private fun eolIndexFinder(stream: CharSequence, currentIndex: Int, lastKnownEolIndex: Int, itemAtIndex: (CharSequence, Int) -> Char, pastEnd: (CharSequence, Int) -> Boolean): Int {

            return if (pastEnd.invoke(stream, currentIndex)) lastKnownEolIndex
            else {
                val currentItem = itemAtIndex.invoke(stream, currentIndex)
                return when {
                    isCurrentRN(stream, currentIndex, currentItem, itemAtIndex, pastEnd) -> currentIndex + 1
                    currentItem == '\n' -> currentIndex
                    currentItem == '\r' -> currentIndex
                    else -> lastKnownEolIndex
                }
            }
        }

        private fun isCurrentRN(stream: CharSequence, currentIndex: Int, item: Char, itemAtIndex: (CharSequence, Int) -> Char, pastEnd: (CharSequence, Int) -> Boolean): Boolean {
            return when (item) {
                '\r' -> when {
                    pastEnd(stream, currentIndex + 1) -> false
                    itemAtIndex.invoke(stream, currentIndex + 1) == '\n' -> true
                    else -> false
                }
                else -> false
            }
        }

        fun create(input: CharSequence): CharInput {
            val itemAtIndex = { s: CharSequence, i: Int -> s[i] }
            val isPastEnd = { s: CharSequence, i: Int -> i >= s.length }
            val eolTestParser = { s: CharSequence, i: Int, lastKnown: Int -> eolIndexFinder(s, i, lastKnown, itemAtIndex, isPastEnd) }
            return CharInput(
                    charStream = input,
                    eolTestParser = eolTestParser,
                    itemAtIndex = itemAtIndex,
                    isPastEnd = isPastEnd,
                    factory = CharInputFactory()
            )
        }
    }
}



