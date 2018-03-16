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

package com.nekomatic.ironik.charparser

import com.nekomatic.ironik.core.IInput
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.combinators.*
import com.nekomatic.types.Option
import com.nekomatic.ironik.core.parsers.ParserRef
import com.nekomatic.types.flatten


class JsonParser(override val name:String) : IParser<Json, Char> {

    override fun parse(input: IInput<Char>): ParserResult<Json, Char> = jToken.parse(input)

    companion object {
        //TODO: make good use of touple types and create tests

        private val lSqBr by lazy { char('[').token() }
        private val rSqBr by lazy { char(']').token() }
        private val lCrBr by lazy { char('{').token() }
        private val rCrBr by lazy { char('}').token() }
        private val coma by lazy { char(',').token() }
        private val colon by lazy { char(':').token() }
        private val quote by lazy { char('"') }
        private val backSl by lazy { char('\\') }

        //TODO: change to direct Char from Hex value convestion using built in java
        val fourDigitHex = fourOf(hexDigitAsInt) mapValue { it[3] + it[2] * 16 + (it[1] + it[0] * 16) * 256 }

        val strUTF = fourDigitHex mapValue { it.toChar() } prefixedBy string("\\u")
        val strQ = quote prefixedBy char('\\') toConst ('"')
        val strBS = char('\\') prefixedBy char('\\') toConst ('\\')
        val strFS = char('/') prefixedBy char('\\') toConst ('/')
        val strBSP = char('b') prefixedBy char('\\') toConst ('\b')
        val strFF = char('f') prefixedBy char('\\') toConst (0x000C.toChar())
        val strNL = char('n') prefixedBy char('\\') toConst ('\n')
        val strRT = char('r') prefixedBy char('\\') toConst ('\r')
        val strT = char('t') prefixedBy char('\\') toConst ('\t')
        val zero = char('0') toConst listOf('0')
        val nonZero = digit onlyIfTrue { it != '0' } mapValue { listOf(it) } then zeroOrMore(digit) mapValue { it.item01 + it.item02 }

        val escaped = oneOf(
                strUTF,
                strQ,
                strBS,
                strFS,
                strBSP,
                strFF,
                strNL,
                strRT,
                strT
        )

        val jStringChar = escaped otherwise (anyCharExcluding(quote otherwise backSl))

        val jNumDiscreete: IParser<List<Char>, Char> = zero otherwise nonZero
        val jNumDotpart: IParser<List<Char>, Char> = oneOrMore(digit).prefixedBy(char('.')) mapValue { listOf('.') + it }
        val jNumEPart = option(char('+') otherwise char('-')) then (char('e') otherwise char('E')) then oneOrMore(digit) mapValue { it.flatten() } mapValue
                {
                    (if (it.item01 is Option.Some) listOf((it.item01 as Option.Some<Char>).value)
                    else listOf()) +
                            listOf(it.item02) + it.item03
                }

        val doubleOption by lazy {
            option(char('-')) then jNumDiscreete then option(jNumDotpart) then option(jNumEPart) mapValue
                    {
                        val f = it.flatten()
                        val sign = if (f.item01 is Option.Some) listOf((f.item01 as Option.Some<Char>).value) else listOf()
                        val disc = f.item02
                        val dec = if (f.item03 is Option.Some) (f.item03 as Option.Some<List<Char>>).value else listOf()
                        val e = if (f.item04 is Option.Some) (f.item04 as Option.Some<List<Char>>).value else listOf()
                        val value = (sign + disc + dec + e).joinToString("").toDoubleOrNull()
                        if (value == null) Option.None else Option.Some(value)
                    }
        }
        val jToken = ParserRef<Json, Char>()
        val jString: IParser<Json.String, Char> = (string("\"\"").toConst(Json.String(""))) otherwise
                oneOrMore(jStringChar).surroundedBy(quote).mapValue { Json.String(it.joinToString("")) }
        val jBool: IParser<Json.Bool, Char> = string("true").otherwise(string("false")) mapValue { if (it == "true") Json.Bool.True else Json.Bool.False }
        val jNumber = doubleOption.onlyIfSome() mapValue { Json.Number(it) }

        val jNull: IParser<Json.Null, Char> = string("null") mapValue { Json.Null }

        val jProperty: IParser<Json.Property, Char> = (jString sufixedBy colon) then jToken mapValue { Json.Property(name = it.item01, value = it.item02) }
        val jArray: IParser<Json.Array, Char> = (jToken listOfSeparatedBy coma) sufixedBy rSqBr prefixedBy lSqBr mapValue { Json.Array(it) }
        val jObject: IParser<Json.Object, Char> = (jProperty listOfSeparatedBy coma) sufixedBy rCrBr prefixedBy lCrBr mapValue { Json.Object(it) }
    }

    init {
        jToken.setParser(oneOf(
                jArray,
                jObject,
                jString,
                jBool,
                jNumber,
                jNull))
    }


}