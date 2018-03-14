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

import com.nekomatic.ironik.core.combinators.mapValue
import com.nekomatic.ironik.core.combinators.renameTo
import com.nekomatic.ironik.core.combinators.sequence
import com.nekomatic.ironik.core.createParser
import com.nekomatic.ironik.core.parsers.Parser


fun createCharParser(expected: String, match: (Char) -> Boolean) = createParser(expected, match)


fun char(char: Char) = createCharParser(char.toString()) { c -> c == char }
fun string(value: String): Parser<String, Char> =
        value.toList()
                .map { char(it) }
                .sequence()
                .renameTo(value)
                .mapValue { it.joinToString("") }

val digit by lazy { createCharParser("[0-9]", { it.isDigit() }) }
val lowercase by lazy { createCharParser("[a-z]", { it.isLowerCase() }) }
val uppercase by lazy { createCharParser("[A-Z]", { it.isUpperCase() }) }
val letter by lazy { createCharParser("[a-zA-Z]", { it.isLetter() }) }
val letterOrDigit by lazy { createCharParser("[a-zA-Z0-9]", { it.isLetterOrDigit() }) }
val whitespace by lazy { createCharParser("whitespace") { it.isWhitespace() } }