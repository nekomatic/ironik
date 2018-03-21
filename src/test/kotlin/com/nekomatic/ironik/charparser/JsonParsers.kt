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
import com.nekomatic.ironik.core.ITokenParser
import com.nekomatic.ironik.core.fragment.*
import com.nekomatic.types.Option

class JsonParser() : ITokenParser<Json, Char, CharInput> {
    companion object {
        private val jsonParsers = JsonParsers()
        private val jTokenParser = oneOf("json value",
                jsonParsers.jArray,
                jsonParsers.jObject,
                jsonParsers.jString,
                jsonParsers.jBool,
                jsonParsers.jNumber,
                jsonParsers.jNull
        )
    }
    override fun parse(input: IInput<Char>) = jTokenParser.parse(input)
}

class JsonParsers() {

    internal val lSqBr by lazy { char('[').token() }
    internal val rSqBr by lazy { char(']').token() }
    internal val lCrBr by lazy { char('{').token() }
    internal val rCrBr by lazy { char('}').token() }
    internal val coma by lazy { char(',').token() }
    internal val colon by lazy { char(':').token() }
    internal val quote by lazy { char('"') }
    internal val backSl by lazy { char('\\') }

    //TODO: change to direct Char from Hex value convestion using built in java
    internal val fourDigitHex = fourOf(HEX_DIGIT_AS_INT) mapValue { it[3] + it[2] * 16 + (it[1] + it[0] * 16) * 256 }

    internal val strUTF = fourDigitHex mapValue { it.toChar() } prefixedBy string("\\u")
    internal val strQ = quote prefixedBy char('\\') toConst ('"')
    internal val strBS = char('\\') prefixedBy char('\\') toConst ('\\')
    internal val strFS = char('/') prefixedBy char('\\') toConst ('/')
    internal val strBSP = char('b') prefixedBy char('\\') toConst ('\b')
    internal val strFF = char('f') prefixedBy char('\\') toConst (0x000C.toChar())
    internal val strNL = char('n') prefixedBy char('\\') toConst ('\n')
    internal val strRT = char('r') prefixedBy char('\\') toConst ('\r')
    internal val strT = char('t') prefixedBy char('\\') toConst ('\t')
    internal val zero = char('0') toConst listOf('0')
    internal val nonZero = DIGIT.onlyIfTrue("[1-9]") { it != '0' } mapValue { listOf(it) } then zeroOrMore(DIGIT) mapValue { it.item01 + it.item02 }

    internal val escaped = oneOf("escaped string character",
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

    internal val jStringChar = escaped.otherwise("string character", anyCharExcluding(quote.otherwise("unescaped string character", backSl)))
    internal val jNumDiscreete: ITokenParser<List<Char>, Char, CharInput> = zero.otherwise("0|[1-9][0-9]*", nonZero)
    internal val jNumDotpart: ITokenParser<List<Char>, Char, CharInput> = oneOrMore(DIGIT).prefixedBy(char('.')) mapValue { listOf('.') + it }
    internal val jNumEPart = option(char('+').otherwise("+|-", char('-'))) then (char('e').otherwise("e|E", char('E'))) then oneOrMore(DIGIT) mapValue { it.flatten() } mapValue
            {
                (if (it.item01 is Option.Some) listOf((it.item01 as Option.Some<Char>).value)
                else listOf()) +
                        listOf(it.item02) + it.item03
            }

    internal val doubleOption by lazy {
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

    val jString: ITokenParser<Json.String, Char, CharInput> =
            (string("\"\"")
                    .toConst(Json.String("")))
                    .otherwise("any sctring character", oneOrMore(jStringChar)
                            .surroundedBy(quote)
                            .mapValue { Json.String(it.joinToString("")) })
    val jBool: ITokenParser<Json.Bool, Char, CharInput> =
            string("true")
                    .otherwise("true|false", string("false"))
                    .mapValue { if (it == "true") Json.Bool.True else Json.Bool.False }

    val jNumber = doubleOption.onlyIfSome("valid number") mapValue { Json.Number(it) }
    val jNull: ITokenParser<Json.Null, Char, CharInput> = string("null") mapValue { Json.Null }

    internal val emptyArray: ITokenParser<Json.Array, Char, CharInput> = (lSqBr then rSqBr).toConst(Json.Array(listOf()))
    internal val nonEmptyArray: ITokenParser<Json.Array, Char, CharInput> = (JsonParser() listOfSeparatedBy coma) suffixedBy rSqBr prefixedBy lSqBr mapValue { Json.Array(it) }

    val jArray: ITokenParser<Json.Array, Char, CharInput> = nonEmptyArray.otherwise("json array", emptyArray)

    internal val jProperty: ITokenParser<Json.Property, Char, CharInput> = (jString suffixedBy colon) then JsonParser() mapValue { Json.Property(name = it.item01, value = it.item02) }
    internal val emptyObject: ITokenParser<Json.Object, Char, CharInput> = (lCrBr then rCrBr).toConst(Json.Object(listOf()))
    internal val nonEmptyObject: ITokenParser<Json.Object, Char, CharInput> = ((jProperty.listOfSeparatedBy(coma)).suffixedBy(rCrBr) prefixedBy lCrBr).mapValue { Json.Object(it) }

    val jObject: ITokenParser<Json.Object, Char, CharInput> = emptyObject.otherwise("json object", nonEmptyObject)

}