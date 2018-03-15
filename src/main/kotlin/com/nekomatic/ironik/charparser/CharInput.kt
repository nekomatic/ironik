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

import com.nekomatic.ironik.core.IInput
import com.nekomatic.types.Option


class CharInput(private val input: CharSequence, private val index: Int = 0, private val l: Int = 0, private val c: Int = 0) : IInput<Char> {
    companion object {
        fun create(input: CharSequence) = CharInput(input)
    }

    override fun hasNext(): Boolean = index < input.length

    override val item: Option<Char>
        get() =
            if (index >= input.length)
                Option.None
            else
                Option.Some(input[index])

    override fun next(): CharInput =
            if (index >= input.length)
                this
            else
                CharInput(this.input, index + 1, line, column + 1)

    override fun nextLine(): CharInput =
            if (index >= input.length)
                this
            else
                CharInput(this.input, index + 1, line + 1, 0)

    override val position: Int
        get() = index

    override val line: Int
        get() = l

    override val column: Int
        get() = c
}