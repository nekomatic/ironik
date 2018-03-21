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

package com.nekomatic.ironik.core.fragment

import com.nekomatic.ironik.core.InputFactory
import com.nekomatic.ironik.core.InputBase
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.fragmentParser
import com.nekomatic.types.PositiveInt

fun <TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> sequenceOf(vararg tokenParsers: fragmentParser<TItem, TIn, TStr, TF>): fragmentParser<TItem, TIn, TStr, TF> =

        fun(input: TIn): ParserResult<TItem, TIn, TStr, TF> {
            val iterator = tokenParsers.iterator()
            tailrec fun parseNext(currentInput: TIn, accumulatorList: List<ParserResult.Success<TItem, TIn, TStr, TF>> = listOf()): ParserResult<TItem, TIn, TStr, TF> {
                return if (iterator.hasNext()) {
                    val result = iterator.next()(currentInput)
                    when (result) {
                        is ParserResult.Failure -> result
                        is ParserResult.Success -> parseNext(result.remainingInput, accumulatorList + result)
                    }
                } else
                    ParserResult.Success(
                            remainingInput = currentInput,
                            payload = accumulatorList.map { r -> r.payload }.reduce({ a, b -> a + b }),
                            position = input.position,
                            column = input.column,
                            line = input.line
                    )
            }
            return parseNext(input)
        }


fun <TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> numberOf(parser: fragmentParser<TItem, TIn, TStr, TF>, count: PositiveInt) =
        sequenceOf(*Array(count.value, { parser }))

fun <TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> fragmentParser<TItem, TIn, TStr, TF>.count(count: PositiveInt) =
        numberOf(this, count)

fun <TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> twoOf(parser: fragmentParser<TItem, TIn, TStr, TF>) =
        sequenceOf(*Array(2, { parser }))

fun <TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> threeOf(parser: fragmentParser<TItem, TIn, TStr, TF>) =
        sequenceOf(*Array(3, { parser }))

fun <TItem : Any, TIn : InputBase<TItem, TIn, TStr, TF>, TStr : Any, TF : InputFactory<TItem, TIn, TStr, TF>> fourOf(parser: fragmentParser<TItem, TIn, TStr, TF>) =
        sequenceOf(*Array(4, { parser }))