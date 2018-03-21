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

import com.nekomatic.ironik.core.InputBase
import com.nekomatic.ironik.core.InputFactory
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser
import com.nekomatic.types.Option


class TokenMatcher<TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>>() {
    fun <T : Token<TItem, TIn, TStr, TF>, TF : TokenFactory<TItem, TIn, TStr, TF, T>> match(tokenFactory: TF, input: TIn): Pair<Option<T>, TIn> {
        val r = tokenFactory.rule(input)
        return when (r) {
            is ParserResult.Success -> Pair(
                    Option.Some(tokenFactory.createToken(
                            payload = r.payload,
                            location = Location(
                                    index = r.position,
                                    length = r.payload.count(),
                                    column = r.column,
                                    line = r.line
                            )
                    )),
                    r.remainingInput
            )
            is ParserResult.Failure -> Pair(Option.None, input)
        }
    }
}

abstract class TokenFactory<TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>, out T : Token<TItem, TIn, TStr, TF>>(val name: String) {
    abstract val rule: fragmentParser<TItem, TIn, TStr, TF>
    abstract fun createToken(payload: List<TItem>, location: Location): T

}

abstract class Token<TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>>(
        val name: String,
        val payload: List<TItem>,
        val location: Location
)

sealed class TokenResult<out T : Any, TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>>(open val name: String) {

    data class Success<out T : Any, TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>>(
            override val name: String,
            val value: List<TItem>,
            val remainingInput: TIn,
            val payload: List<TItem>,
            val location: Location
    ) : TokenResult<T, TItem, TIn, TStr, TF>(name)

    data class Failure<TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>>(
            override val name: String,
            val at: Int
    ) : TokenResult<Nothing, TItem, TIn, TStr, TF>(name)
}

class Location(
        val index: Int,
        val length: Int,
        val column: Int = 0,
        val line: Int = 0
)