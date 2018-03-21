package com.nekomatic.ironik.charparser

import com.nekomatic.ironik.core.ITokenParser
import com.nekomatic.ironik.core.ParserResult
import org.junit.jupiter.api.Test

class JsonParserTests {

    fun <T : Any> parse(str: String, tokenParser: ITokenParser<T, Char, CharInput>): ParserResult<T, Char, CharInput> {
        val input = CharInput.create(str)
        val result = tokenParser.parse(input)
        return result
    }

    fun <T : Any> assertMany(m: Map<String, Boolean>, tokenParser: ITokenParser<T, Char, CharInput>) {
        assert(m.map { (parse(it.key, tokenParser) is ParserResult.Success) == it.value }.reduce { a, b -> a && b })
    }




    val validJsonSample01: String = """
    {"widget": {
        "debug": "on\u0020",
        "window": {
            "title": "Sample \nKonfabulator Widget",
            "parserName": "\u0021main_window",
            "width": 500,
            "height": 500
        },
        "image": {
            "src": "Images/Sun.png",
            "parserName": "sun1",
            "hOffset": 250,
            "vOffset": 250,
            "alignment": "cent\u0020er"
        },
        "text": {
            "data": "Click \tHere",
            "size": 36,
            "style": "bold",
            "parserName": "\u0020text1\u0020",
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

    val escapedUtfChar = """\u0020"""
    val stringWithEscapedUtfChar = """ "a\u0020b" """


    @Test
    fun jsonTest01_simpleNumber() {
        val json = JsonParser()
        val r = parse("2", json)
        assert(r is ParserResult.Success && r.value is Json.Number)
    }

    @Test
    fun jsonTest02_negativeNumber() {
        val json = JsonParser()
        val r = parse("-2", json)
        assert(r is ParserResult.Success && r.value is Json.Number)

    }

    @Test
    fun jsonTest03_negativeFractionalNumber() {
        val json = JsonParser()
        val r = parse("-2.17", json)
        assert(r is ParserResult.Success && r.value is Json.Number)
    }

    @Test
    fun jsonTest04_negativeFractionalNumberExp() {
        val json = JsonParser()
        val r = parse("-2.17e+6", json)
        assert(r is ParserResult.Success && r.value is Json.Number)
    }

    @Test
    fun jsonTest05_simpleBoolTrue() {
        val json = JsonParser()
        val r = parse("true", json)
        assert(r is ParserResult.Success && r.value is Json.Bool.True)
    }

    @Test
    fun jsonTest06_simpleBoolFalse() {
        val json = JsonParser()
        val r = parse("false", json)
        assert(r is ParserResult.Success && r.value is Json.Bool.False)
    }

    @Test
    fun jsonTest07_simpleNull() {
        val json = JsonParser()
        val r = parse("null", json)
        assert(r is ParserResult.Success && r.value is Json.Null)
    }

    @Test
    fun jsonTest08_simpleString() {
        val json = JsonParser()
        val r = parse("\"something\"", json)
        assert(r is ParserResult.Success && r.value is Json.String)
    }

    @Test
    fun jsonTest09_simpleStringEmpty() {
        val json = JsonParser()
        val r = parse("\"\"", json)
        assert(r is ParserResult.Success && r.value is Json.String)
    }

    @Test
    fun jsonTest10_simpleEmptyArray() {
        val json = JsonParser()
        val r = parse("[]", json)
        assert(r is ParserResult.Success && r.value is Json.Array)
    }

    @Test
    fun jsonTest11_simpleEmptyArrayToken() {
        val json = JsonParser()
        val r = parse("[ ]", json)
        assert(r is ParserResult.Success && r.value is Json.Array)
    }

    @Test
    fun jsonTest12_simpleArraySingle() {
        val json = JsonParser()
        val r = parse("[ 5 ]", json)
        assert(r is ParserResult.Success && r.value is Json.Array)
    }


    @Test
    fun jsonTest13_simpleArraySingleNested() {
        val json = JsonParser()
        val r = parse("[ [ 5 ] ]", json)
        assert(r is ParserResult.Success && r.value is Json.Array)
    }

    @Test
    fun jsonTest01_simpleArraySingleNestedEmpty() {
        val json = JsonParser()
        val r = parse("[ [ ] ]", json)
        assert(r is ParserResult.Success && r.value is Json.Array)
    }

    @Test
    fun jsonTest14_simpleArrayMany() {
        val json = JsonParser()
        val r = parse("[ 28, [5], \"\", [null],  true ]", json)
        assert(r is ParserResult.Success && r.value is Json.Array && (r.value as Json.Array).values[0] is Json.Number)
    }

    @Test
    fun jsonTest15_simpleEmptyObject() {
        val json = JsonParser()
        val r = parse("{}", json)
        assert(r is ParserResult.Success && r.value is Json.Object)
    }

    @Test
    fun jsonTest16_simpleEmptyObjectToken() {
        val json = JsonParser()
        val r = parse("{ }", json)
        assert(r is ParserResult.Success && r.value is Json.Object)
    }

    @Test
    fun jsonTest17_simpleObjectSingle() {
        val json = JsonParser()
        val r = parse("{ \"a\" : 5 }", json)
        assert(r is ParserResult.Success && r.value is Json.Object && (r.value as Json.Object).values[0].value is Json.Number)
    }

    @Test
    fun jsonTest18_simpleObjectMany() {
        val json = JsonParser()
        val r = parse("{ \"a\" : 5, \"b\" : {\"b1\" : [1,2,3]} , \"c\" : {} }", json)
        assert(r is ParserResult.Success && r.value is Json.Object && (r.value as Json.Object).values[1].value is Json.Object)
    }

    @Test
    fun jsonTest19_validComplex01() {
        val json = JsonParser()
        val r = parse(validJsonSample01, json)
        assert(r is ParserResult.Success)
    }

    @Test
    fun jsonTest20_invalidComplex01() {
        val json = JsonParser()
        val r = parse(invalidJsonSample01, json)
        assert(r is ParserResult.Failure)
    }

    @Test
    fun jsonTest21_escapedUtfChar() {
        val jp = JsonParsers()

        val r = parse(escapedUtfChar, jp.strUTF)
        assert(r is ParserResult.Success)
    }

    @Test
    fun jsonTest21_stringWithEscapedUtfChar() {
        val jp = JsonParsers()
        val r = parse(stringWithEscapedUtfChar, jp.jString.token())
        assert(r is ParserResult.Success)
    }
}