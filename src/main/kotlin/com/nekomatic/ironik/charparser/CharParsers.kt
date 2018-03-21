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

import com.nekomatic.ironik.core.createParser
import com.nekomatic.ironik.core.fragment.*
import com.nekomatic.ironik.core.fragmentParser

fun createCharParser(match: (Char) -> Boolean): fragmentParser<Char, CharInput> = createParser(match)
fun createCharParser(match: Char): fragmentParser<Char, CharInput> = createParser(match)

fun char(char: Char): fragmentParser<Char, CharInput> = createCharParser(char)
fun anyCharExcluding(parser: fragmentParser<Char, CharInput>): fragmentParser<Char, CharInput> = not(parser)

fun string(value: String) =
        value.toList()
                .map { char(it) }
                .sequence()

val DIGIT = createCharParser({ it.isDigit() })
val LOWERCASE_CHAR = createCharParser({ it.isLowerCase() })
val UPPERCASE_CHAR = createCharParser({ it.isUpperCase() })
val LETTER = createCharParser({ it.isLetter() })
val LETTER_OR_DIGIT = createCharParser({ it.isLetterOrDigit() })
val WHITESPACE = createCharParser { it.isWhitespace() }
val WHITESPACES = oneOrMore(WHITESPACE)
val OPTIONAL_WHITESPACES: fragmentParser<Char, CharInput> = zeroOrMore(WHITESPACE)
val HEX_DIGIT = oneOf(*("0123456789abcdefABCDEF".toList().map { char(it) }.toTypedArray()))
val EOL = oneOf(
        (char('\r') join (char('\n'))),
        char('\n'),
        char('\r')
)
fun fragmentParser<Char, CharInput>.token(): fragmentParser<Char, CharInput> = this.surroundedBy(OPTIONAL_WHITESPACES)