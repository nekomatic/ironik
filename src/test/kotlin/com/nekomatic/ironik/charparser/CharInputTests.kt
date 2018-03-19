package com.nekomatic.ironik.charparser

import com.nekomatic.ironik.core.combinators.then
import org.junit.jupiter.api.Test

class CharInputTests {

//    val input1 = CharInput.create("a\nb")
//
//    val a = createCharParser("a", { c -> c == 'a' })
//    val b = createCharParser("a", { c -> c == 'b' })
//    val n = createCharParser("a", { c -> c == '\n' })
//    val r = createCharParser("a", { c -> c == '\r' })
//    val rn = r then n

    @Test
    fun test01_NLineBreakInMiddle() {
        val input1 = CharInput.create("a\nb")
        val input2 = input1.next()
        val input3 = input2.next()

        assert(input1.line == 1)
        assert(input2.line == 1)
        assert(input3.line == 2)

        assert(input1.column == 1)
        assert(input2.column == 2)
        assert(input3.column == 1)
    }

    @Test
    fun test01_NLineBreakAtTheBeginning() {
        val input1 = CharInput.create("\nab")
        val input2 = input1.next()
        val input3 = input2.next()

        assert(input1.line == 1)
        assert(input2.line == 2)
        assert(input3.line == 2)

        assert(input1.column == 1)
        assert(input2.column == 1)
        assert(input3.column == 2)
    }

    @Test
    fun test01_RLineBreakInMiddle() {
        val input1 = CharInput.create("a\rb")
        val input2 = input1.next()
        val input3 = input2.next()

        assert(input1.line == 1)
        assert(input2.line == 1)
        assert(input3.line == 2)

        assert(input1.column == 1)
        assert(input2.column == 2)
        assert(input3.column == 1)
    }

    @Test
    fun test01_RLineBreakAtTheBeginning() {
        val input1 = CharInput.create("\rab")
        val input2 = input1.next()
        val input3 = input2.next()

        assert(input1.line == 1)
        assert(input2.line == 2)
        assert(input3.line == 2)

        assert(input1.column == 1)
        assert(input2.column == 1)
        assert(input3.column == 2)
    }
    @Test
    fun test01_RNLineBreakInMiddle() {
        val input1 = CharInput.create("a\r\nb")
        val input2 = input1.next()
        val input3 = input2.next()
        val input4 = input3.next()

        assert(input1.line == 1)
        assert(input2.line == 1)
        assert(input3.line == 1)
        assert(input4.line == 2)

        assert(input1.column == 1)
        assert(input2.column == 2)
        assert(input3.column == 3)
        assert(input4.column == 1)
    }

    @Test
    fun test01_RNLineBreakAtTheBeginning() {
        val input1 = CharInput.create("\r\nab")
        val input2 = input1.next()
        val input3 = input2.next()
        val input4 = input3.next()

        assert(input1.line == 1)
        assert(input2.line == 1)
        assert(input3.line == 2)
        assert(input4.line == 2)

        assert(input1.column == 1)
        assert(input2.column == 2)
        assert(input3.column == 1)
        assert(input4.column == 2)
    }

}