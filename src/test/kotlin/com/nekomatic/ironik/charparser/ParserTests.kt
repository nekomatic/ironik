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

internal class ParserTests {

    val validJsonSample01: String = """
    {"widget": {
        "debug": "on",
        "window": {
            "title": "Sample Konfabulator Widget",
            "parserName": "main_window",
            "width": 500,
            "height": 500
        },
        "image": {
            "src": "Images/Sun.png",
            "parserName": "sun1",
            "hOffset": 250,
            "vOffset": 250,
            "alignment": "center"
        },
        "text": {
            "data": "Click \tHere",
            "size": 36,
            "style": "bold",
            "parserName": "text1",
            "hOffset": 250,
            "vOffset": 100,
            "alignment": "center",
            "onMouseUp": "sun1.opacity = (sun1.opacity / 100) * 90;"
        }
    }}
    """.trimIndent()

    val invalidJsonSample01: String = """
    {"widget": {
        "debug": "on",
        "window": {
            "title": "Sample Konfabulator Widget",
            "parserName": "main_window",
            "width": 500,
            "height": 500
        },
        "image": {
            "src": "Images/Sun.png",
            "parserName": "sun1",
            "hOffset": 250,
            "vOffset": 250,
            "alignment": "center"
        },
        "text": {
            "data": "Click \Here",
            "size": 36,
            "style": "bold",
            "parserName": "text1",
            "hOffset": 250,
            "vOffset": 100,
            "alignment": "center",
            "onMouseUp": "sun1.opacity = (sun1.opacity / 100) * 90;"
        }
    }}
    """.trimIndent()

    fun <T : Any> parse(str: String, parser: IParser<T, Char, CharInput>): ParserResult<T, Char, CharInput> {
        val input = CharInput.create(str)
        val result = parser.parse(input)
        return result
    }

    fun <T : Any> assertMany(m: Map<String, Boolean>, parser: IParser<T, Char, CharInput>) {
        assert(m.map { (parse(it.key, parser) is ParserResult.Success) == it.value }.reduce { a, b -> a && b })
    }

    val json = JsonParser()

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
        assertMany(tests, lSqBr)
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
        assertMany(tests, rSqBr)
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
        val parser = oneOf("square bracket or bool", lSqBr, rSqBr, jBool)
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
        val parser = lSqBr then rSqBr
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
        val parser = lSqBr.otherwise("[|]", rSqBr)
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
                        .otherwise("[|true|false", rSqBr)
                        .otherwise("[|true|false|]", jBool)
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
        val parser1 = lSqBr then rSqBr
        val parser2 = lSqBr then jBool then rSqBr
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
        val parser1 = lSqBr.otherwise("[|]", rSqBr).otherwise("[|true|false|]", jBool)
        val separator = coma
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
        val parser1 = lSqBr.otherwise("[|]", rSqBr).otherwise("[|true|false|]", jBool)
        val separator = coma
        val parser = lCrBr then parser1.listOfSeparatedBy(separator) then rCrBr
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
        val parser1 = lSqBr then rSqBr
        val parser2 = jBool.listOfSeparatedBy(coma) prefixedBy lSqBr suffixedBy rSqBr
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

//    @Test
//    fun test13_eolTest() {
//        val tests = mapOf(
//                "[\n[]" to true
////                "[\r\n []" to true,
////                "[\r [ ]" to true,
////                "\r\n [ ] " to false,
////                "] " to false,
////                "[ ] " to false
//        )
//        val parser = lSqBr then eol
//        assertMany(tests, parser)
//    }

    @Test
    fun jsonTest01_simpleNumber() {
        val r = parse("2", json)
        assert(r is ParserResult.Success && r.value is Json.Number)
    }

    @Test
    fun jsonTest02_negativeNumber() {
        val r = parse("-2", json)
        assert(r is ParserResult.Success && r.value is Json.Number)

    }

    @Test
    fun jsonTest03_negativeFractionalNumber() {
        val r = parse("-2.17", json)
        assert(r is ParserResult.Success && r.value is Json.Number)
    }

    @Test
    fun jsonTest04_negativeFractionalNumberExp() {
        val r = parse("-2.17e+6", json)
        assert(r is ParserResult.Success && r.value is Json.Number)
    }

    @Test
    fun jsonTest05_simpleBoolTrue() {
        val r = parse("true", json)
        assert(r is ParserResult.Success && r.value is Json.Bool.True)
    }

    @Test
    fun jsonTest06_simpleBoolFalse() {
        val r = parse("false", json)
        assert(r is ParserResult.Success && r.value is Json.Bool.False)
    }

    @Test
    fun jsonTest07_simpleNull() {
        val r = parse("null", json)
        assert(r is ParserResult.Success && r.value is Json.Null)
    }

    @Test
    fun jsonTest08_simpleString() {
        val r = parse("\"something\"", json)
        assert(r is ParserResult.Success && r.value is Json.String)
    }

    @Test
    fun jsonTest09_simpleStringEmpty() {
        val r = parse("\"\"", json)
        assert(r is ParserResult.Success && r.value is Json.String)
    }

    @Test
    fun jsonTest10_simpleEmptyArray() {
        val r = parse("[]", json)
        assert(r is ParserResult.Success && r.value is Json.Array)
    }

    @Test
    fun jsonTest11_simpleEmptyArrayToken() {
        val r = parse("[ ]", json)
        assert(r is ParserResult.Success && r.value is Json.Array)
    }

    @Test
    fun jsonTest12_simpleArraySingle() {
        val r = parse("[ 5 ]", json)
        assert(r is ParserResult.Success && r.value is Json.Array)
    }


    @Test
    fun jsonTest13_simpleArraySingleNested() {
        val r = parse("[ [ 5 ] ]", json)
        assert(r is ParserResult.Success && r.value is Json.Array)
    }

    @Test
    fun jsonTest01_simpleArraySingleNestedEmpty() {
        val r = parse("[ [ ] ]", json)
        assert(r is ParserResult.Success && r.value is Json.Array)
    }

    @Test
    fun jsonTest14_simpleArrayMany() {
        val r = parse("[ 28, [5], \"\", [null],  true ]", json)
        assert(r is ParserResult.Success && r.value is Json.Array && (r.value as Json.Array).values[0] is Json.Number)
    }

    @Test
    fun jsonTest15_simpleEmptyObject() {
        val r = parse("{}", json)
        assert(r is ParserResult.Success && r.value is Json.Object)
    }

    @Test
    fun jsonTest16_simpleEmptyObjectToken() {
        val r = parse("{ }", json)
        assert(r is ParserResult.Success && r.value is Json.Object)
    }

    @Test
    fun jsonTest17_simpleObjectSingle() {
        val r = parse("{ \"a\" : 5 }", json)
        assert(r is ParserResult.Success && r.value is Json.Object && (r.value as Json.Object).values[0].value is Json.Number)
    }

    @Test
    fun jsonTest18_simpleObjectMany() {
        val r = parse("{ \"a\" : 5, \"b\" : {\"b1\" : [1,2,3]} , \"c\" : {} }", json)
        assert(r is ParserResult.Success && r.value is Json.Object && (r.value as Json.Object).values[1].value is Json.Object)
    }

    @Test
    fun jsonTest19_validComplex01() {
        val r = parse(validJsonSample01, json)
        assert(r is ParserResult.Success)
    }

    @Test
    fun jsonTest20_invalidComplex01() {
        val r = parse(invalidJsonSample01, json)
        assert(r is ParserResult.Failure)
    }
}