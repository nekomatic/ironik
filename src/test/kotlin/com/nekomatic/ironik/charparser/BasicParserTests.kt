package com.nekomatic.ironik.charparser


import com.nekomatic.ironik.charparser.JsonParsers.Companion.coma
import com.nekomatic.ironik.charparser.JsonParsers.Companion.emptyArray
import com.nekomatic.ironik.charparser.JsonParsers.Companion.jBool
import com.nekomatic.ironik.charparser.JsonParsers.Companion.lCrBr
import com.nekomatic.ironik.charparser.JsonParsers.Companion.lSqBr
import com.nekomatic.ironik.charparser.JsonParsers.Companion.rCrBr
import com.nekomatic.ironik.charparser.JsonParsers.Companion.rSqBr
import com.nekomatic.ironik.core.IParser
import com.nekomatic.ironik.core.ParserResult
import com.nekomatic.ironik.core.combinators.*
import org.junit.jupiter.api.Test

internal class BasicParserTests {

    fun <T : Any> parse(str: String, parser: IParser<T, Char, CharInput>): ParserResult<T, Char, CharInput> {
        val input = CharInput.create(str)
        val result = parser.parse(input)
        return result
    }

    fun <T : Any> assertMany(m: Map<String, Boolean>, parser: IParser<T, Char, CharInput>) {
        assert(m.map { (parse(it.key, parser) is ParserResult.Success) == it.value }.reduce { a, b -> a && b })
    }


    @Test
    fun test01_lSqBrTokenTest() {
        val tests = mapOf(
                "[" to true,
                " [" to true,
                "[ " to true,
                " [ " to true,
                "  [  " to true,
                "[a" to true,
                "[ a" to true,
                "[ [" to true,
                "[ {" to true,
                "{" to false,
                " { " to false
        )
        assertMany(tests,
                lSqBr)
    }

    @Test
    fun test02_rSqBrTokenTest() {
        val tests = mapOf(
                "]" to true,
                " ]" to true,
                "] " to true,
                " ] " to true,
                "  ]  " to true,
                "]a" to true,
                "] a" to true,
                "] ]" to true,
                "] {" to true,
                "{" to false,
                " { " to false
        )
        assertMany(tests,
                rSqBr)
    }

    @Test
    fun test03_emptyArrayTest() {
        val tests = mapOf(
                "[]" to true,
                "[ ]" to true,
                " [ ] " to true,
                " [] " to true,
                "  []  " to true,
                "[]a" to true,
                "[] a" to true,
                "[] ]" to true,
                "[ ] {" to true,
                "[{]" to false,
                "[ { ]" to false
        )
        assertMany(tests, emptyArray)
    }

    @Test
    fun test04_oneOfTest() {
        val tests = mapOf(
                "[" to true,
                "]" to true,
                " [ " to true,
                " ] " to true,
                " [] " to true,
                "  []  " to true,
                "[]a" to true,
                "[] a" to true,
                "[] ]" to true,
                "true[ ] {" to true,
                "false[ ] {" to true,
                "true[ ] {" to true,
                "{]" to false,
                " { ]" to false
        )
        val parser = oneOf("square bracket or bool",
                lSqBr,
                rSqBr,
                jBool)
        assertMany(tests, parser)
    }

    @Test
    fun test05_thenTest() {
        val tests = mapOf(
                "[]" to true,
                " []" to true,
                " [ ]" to true,
                " [ ] " to true,
                "[ ] " to true,
                "[] " to true,
                " [ ] " to true,
                " [] " to true,
                "[]  " to true,
                "  []" to true,
                "  [  ]  " to true,
                "[]a" to true,
                "[] a" to true,
                "[] ]" to true,
                "[ ] {" to true,
                "{]" to false,
                " { ]" to false
        )
        val parser =
                lSqBr then
                        rSqBr
        assertMany(tests, parser)
    }

    @Test
    fun test06_otherwiseSimpleTest() {
        val tests = mapOf(
                "[" to true,
                "]" to true,
                " [ " to true,
                " ] " to true,
                " [] " to true,
                "  []  " to true,
                "[]a" to true,
                "[] a" to true,
                "[] ]" to true,
                "true[ ] {" to false,
                "false[ ] {" to false,
                "true[ ] {" to false,
                "{]" to false,
                " { ]" to false
        )
        val parser =
                lSqBr.otherwise("[|]",
                        rSqBr)
        assertMany(tests, parser)
    }

    @Test
    fun test07_otherwiseComplexTest() {
        val tests = mapOf(
                "[" to true,
                "]" to true,
                " [ " to true,
                " ] " to true,
                " [] " to true,
                "  []  " to true,
                "[]a" to true,
                "[] a" to true,
                "[] ]" to true,
                "true[ ] {" to true,
                "false[ ] {" to true,
                "true[ ] {" to true,
                "{]" to false,
                " { ]" to false
        )
        val parser =

                lSqBr
                        .otherwise("[|true|false",
                                rSqBr)
                        .otherwise("[|true|false|]",
                                jBool)
        assertMany(tests, parser)
    }

    @Test
    fun test08_otherwiseMoreComplexTest() {
        val tests = mapOf(
                "[]" to true,
                " []" to true,
                " [ ]" to true,
                " [ ] " to true,
                "[ ] " to true,
                "[] " to true,
                " [ ] " to true,
                " [] " to true,
                "[]  " to true,
                "  []" to true,
                "  [  ]  " to true,
                "[]a" to true,
                "[] a" to true,
                "[] ]" to true,
                "[ ] {" to true,
                "{]" to false,
                " { ]" to false,
                "[]" to true,
                " [true]" to true,
                " [ true]" to true,
                " [true ] " to true,
                "[ false] " to true,
                "[false] " to true,
                " [ true ] " to true,
                " [true] " to true,
                "[false]  " to true,
                "  [flase]" to false,
                "  [  true]  " to true,
                "[true]a" to true,
                "[true] a" to true,
                "[true] ]" to true,
                "[ true] {" to true,
                "{true]" to false,
                "true { ]" to false
        )
        val parser1 =
                lSqBr then
                        rSqBr
        val parser2 =
                lSqBr then
                        jBool then
                        rSqBr
        val parser = parser1.otherwise("[]|[bool]", parser2)
        assertMany(tests, parser)
    }

    @Test
    fun test09_listOfSeparatedByTest() {
        val tests = mapOf(
                "[" to true,
                "]" to true,
                " [ " to true,
                " ] " to true,
                " [] " to true,
                "  []  " to true,
                "[,true" to true,
                "[], true" to true,
                "[false ]" to true,
                "true,[ ,] {" to true,
                "false[ ] {" to true,
                "true[ ] {" to true,
                "true,[ ,false , ] , ] {" to true,
                "   ]    ,true,[ ,false , ] , ] {" to true,
                "{]" to false,
                " { ]" to false
        )
        val parser1 =
                lSqBr.otherwise("[|]",
                        rSqBr).otherwise("[|true|false|]",
                        jBool)
        val separator =
                coma
        val parser = parser1.listOfSeparatedBy(separator)
        assertMany(tests, parser)
    }

    @Test
    fun test10_enclosedListOfSeparatedByTest() {
        val tests = mapOf(
                "{[}" to true,
                "{]}" to true,
                "{ [ }" to true,
                "{ ]} " to true,
                "{ [,]} " to true,
                "{  [,] } " to true,
                "{[,true}" to true,
                "{[,], true}" to true,
                "{[,], true" to false,
                "{[}false ]}" to true,
                "{ true,[ ,] }{" to true,
                "{false[ ] }{" to false,
                "{ true[ ] {" to false,
                "{, true,[ ,false , ] , ] }" to false,
                "{   ]    ,true,[ ,false , ] , ] }" to true,
                "{]" to false,
                " { ]," to false
        )
        val parser1 =
                lSqBr.otherwise("[|]",
                        rSqBr).otherwise("[|true|false|]",
                        jBool)
        val separator =
                coma
        val parser =
                lCrBr then parser1.listOfSeparatedBy(separator) then rCrBr
        assertMany(tests, parser)
    }

    @Test
    fun test11_otherwiseEvenMoreComplexTest() {
        val tests = mapOf(
                "[]" to true,
                " []" to true,
                " [ ]" to true,
                " \n [ ] " to true,
                "[ ] " to true,
                "[] " to true,
                " [ ] " to true,
                " [] " to true,
                "[]  " to true,
                "  []" to true,
                "  [  ]  " to true,
                "[]a" to true,
                "[] a" to true,
                "[] ]" to true,
                "[ ] {" to true,
                "{]" to false,
                " { ]" to false,
                "[]" to true,
                " [true]" to true,
                " [ true]" to true,
                " [true ] " to true,
                "[ false,false] " to true,
                "[false , true ] " to true,
                " [ true ] " to true,
                " [true] " to true,
                "[false]  " to true,
                "  [flase]" to false,
                "  [  true]  " to true,
                "[true]a" to true,
                "[true] a" to true,
                "[true] ]" to true,
                "[ true] {" to true,
                "{true]" to false,
                "true { ]" to false
        )

        val parser1 =
                lSqBr then
                        rSqBr
        val parser2 =
                jBool.listOfSeparatedBy(
                        coma) prefixedBy
                        lSqBr suffixedBy
                        rSqBr
        val parser = parser1.otherwise("[]|[list]", parser2)
        assertMany(tests, parser)
    }

    @Test
    fun test12_eolSimpleTest() {
        val tests = mapOf(
                "\n []" to true
        )
        val parser = eol
        assertMany(tests, parser)
    }

    @Test
    fun test13_SimpleLineColumnTest() {

        val parser1 = string("l1").token()
        val parser2 = string("l2").token()

        val input1 = CharInput.create("l1\nl2")
        val r1 = parser1.parse(input1)
        assert(r1 is ParserResult.Success)

        val input2 = (r1 as ParserResult.Success).remainingInput
        val r2 = parser2.parse(input2)
        assert(r2 is ParserResult.Success)

        assert(r1.line == 1)
        assert(r1.column == 1)
        assert((r2 as ParserResult.Success).line == 2)
        assert((r2 as ParserResult.Success).column == 1)
    }

    @Test
    fun test13_ComplexLineColumnTest() {

        val parser1 = string("l1").token()
        val parser2 = string("l2").token()

        val input1 = CharInput.create("\r\n l1  \n  \rl2  ")
        val r1 = parser1.parse(input1)
        assert(r1 is ParserResult.Success)

        val input2 = (r1 as ParserResult.Success).remainingInput
        val r2 = parser2.parse(input2)
        assert(r2 is ParserResult.Success)

        assert(r1.line == 1)
        assert(r1.column == 1)
        assert((r2 as ParserResult.Success).line == 4)
        assert((r2 as ParserResult.Success).column == 1)
    }
}