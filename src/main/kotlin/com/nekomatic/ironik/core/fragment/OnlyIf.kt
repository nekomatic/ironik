//package com.nekomatic.ironik.core.fragment
//
//import com.nekomatic.ironik.core.IInput
//import com.nekomatic.ironik.core.Input
//import com.nekomatic.ironik.core.ParserResult
//import com.nekomatic.ironik.core.fragmentParser
//
//infix fun <TStreamItem : Any, TInput : IInput<TStreamItem>> fragmentParser<TStreamItem, TInput>.onlyIf(condition: fragmentParser<TStreamItem, TInput>): fragmentParser<TStreamItem, TInput> =
//
//        fun(input: TInput): ParserResult<TStreamItem, TInput> {
//            val thisResult = this(input)
//            return when (thisResult) {
//                is ParserResult.Failure -> ParserResult.Failure(
//                        position = thisResult.position,
//                        column = input.column,
//                        line = input.line
//                )
//                is ParserResult.Success -> {
//                    val newInput: TInput = Input(thisResult.payload, 0)
//                    val resultB: ParserResult<TStreamItem, TInput> = condition(newInput)
//                    when (resultB) {
//                        is ParserResult.Success -> thisResult
//                        is ParserResult.Failure -> ParserResult.Failure(
//                                position = input.position,
//                                column = input.column,
//                                line = input.line
//                        )
//                    }
//                }
//            }
//        }
