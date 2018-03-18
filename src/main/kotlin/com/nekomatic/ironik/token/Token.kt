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
import com.nekomatic.ironik.core.IParser


//class Token<TStreamItem : Any>(val name: String, val parser: IParser<TStreamItem, TStreamItem>) {
//
//}
//
//
//sealed class TokenResult<out T : Any, out TStreamItem : Any>(open val name: String) {
//
//    data class Success<out T : Any, TStreamItem : Any>(
//            override val name: String,
//            val value: List<TStreamItem>,
//            val remainingInput: IInput<TStreamItem>,
//            val payload: List<TStreamItem>,
//            val position: Position
//    ) : TokenResult<T, TStreamItem>(name)
//
//    data class Failure<out TStreamItem : Any>(
//            override val name: String,
//            val at: Int
//    ) : TokenResult<Nothing, TStreamItem>(name)
//}
//
//class Position(
//        val index: Int,
//        val length: Int,
//        val column: Int = 0,
//        val line: Int = 0
//)