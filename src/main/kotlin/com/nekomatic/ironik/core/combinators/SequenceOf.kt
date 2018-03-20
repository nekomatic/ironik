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

package com.nekomatic.ironik.core.combinators

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.parsers.Parser
import com.nekomatic.types.PositiveInt

fun <T : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> sequenceOf(vararg parsers: IParser<T, TStreamItem, TInput>): IParser<List<T>, TStreamItem, TInput> =
        Parser(
                { input: IInput<TStreamItem> ->
                    val iterator = parsers.iterator()
                    tailrec fun parseNext(currentInput: IInput<TStreamItem>, accumulatorList: List<ParserResult.Success<T, TStreamItem, TInput>> = listOf()): ParserResult<List<T>, TStreamItem, TInput> {
                        return if (iterator.hasNext()) {
                            val result = iterator.next().parse(currentInput)
                            when (result) {
                                is ParserResult.Failure -> result
                                is ParserResult.Success -> parseNext(result.remainingInput, accumulatorList + result)
                            }
                        } else
                            ParserResult.Success(
                                    value = accumulatorList.map { r -> r.value },
                                    remainingInput = currentInput,
                                    payload = accumulatorList.map { r -> r.payload }.reduce({ a, b -> a + b }),
                                    position = input.position,
                                    column = input.column,
                                    line = input.line
                            )
                    }
                    parseNext(input)
                }
        )

fun <T : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> numberOf(parser: IParser<T, TStreamItem, TInput>, count: PositiveInt) =
        sequenceOf(*Array(count.value, { parser }))

fun <T : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> IParser<T, TStreamItem, TInput>.count(count: PositiveInt) =
        numberOf(this, count)

fun <T : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> twoOf(parser: IParser<T, TStreamItem, TInput>) =
        sequenceOf(*Array(2, { parser }))

fun <T : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> threeOf(parser: IParser<T, TStreamItem, TInput>) =
        sequenceOf(*Array(3, { parser }))

fun <T : Any, TStreamItem : Any, TInput : IInput<TStreamItem>> fourOf(parser: IParser<T, TStreamItem, TInput>) =
        sequenceOf(*Array(4, { parser }))