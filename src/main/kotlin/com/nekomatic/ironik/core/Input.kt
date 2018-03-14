/**
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
 */

package com.nekomatic.ironik.core

class Input<T : Any>(private val input: List<T>, private val index: Int = 0, private val l: Int = 0, private val c: Int = 0) : IInput<T> {
    companion object {
        fun <T : Any> create(input: List<T>) = Input(input)
    }

    override fun hasNext(): Boolean = index < input.size

    override val item: InputItem<T>
        get() =
            if (index >= input.size)
                InputItem.EOF
            else
                InputItem.Some(input[index])

    override fun next(): Input<T> =
            if (index >= input.size)
                this
            else
                Input(this.input, index + 1, line, column + 1)

    override fun nextLine(): Input<T> =
            if (index >= input.size)
                this
            else
                Input(this.input, index + 1, line + 1, 0)

    override val position: Int
        get() = index

    override val line: Int
        get() = l

    override val column: Int
        get() = c
}