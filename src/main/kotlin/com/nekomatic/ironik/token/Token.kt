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

package com.nekomatic.ironik.token

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser
import com.nekomatic.types.Option


class TokenMatcher<TStreamItem : Any, TInput : IInput<TStreamItem>>() {
    fun <T:Token<TStreamItem,TInput>,TF : TokenFactory<TStreamItem, TInput,T>> match(tokenFactory: TF, input: TInput): Pair<Option<T>, TInput> {
        val r = tokenFactory.rule(input)
        return when (r) {
            is ParserResult.Success -> Pair(Option.Some(tokenFactory.createToken(r.payload)), r.remainingInput)
            is ParserResult.Failure -> Pair(Option.None, input)

        }

    }
}

abstract class TokenFactory<TStreamItem : Any, TInput : IInput<TStreamItem>, T:Token<TStreamItem,TInput>>(val name: String) {
    abstract val rule: fragmentParser<TStreamItem, TInput>
    abstract fun createToken(payload:List<TStreamItem>) :T
}

abstract class Token<TStreamItem : Any, TInput : IInput<TStreamItem>>(val name: String, val payload:List<TStreamItem>)

sealed class TokenResult<out T : Any, out TStreamItem : Any>(open val name: String) {

    data class Success<out T : Any, TStreamItem : Any>(
            override val name: String,
            val value: List<TStreamItem>,
            val remainingInput: IInput<TStreamItem>,
            val payload: List<TStreamItem>,
            val position: Position
    ) : TokenResult<T, TStreamItem>(name)

    data class Failure<out TStreamItem : Any>(
            override val name: String,
            val at: Int
    ) : TokenResult<Nothing, TStreamItem>(name)
}

class Position(
        val index: Int,
        val length: Int,
        val column: Int = 0,
        val line: Int = 0
)