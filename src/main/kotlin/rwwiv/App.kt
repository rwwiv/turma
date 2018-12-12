package rwwiv

import rwwiv.parser.Init.alphaParser
import java.io.File
import java.util.*
import kotlin.system.exitProcess
import rwwiv.parser.Init.stateListParser
import rwwiv.parser.Init.stateParser
import rwwiv.parser.Instructions
import rwwiv.turing.*
import kotlin.collections.ArrayList

fun main(args: Array<String>) {
//    val inputFile = File("test01.tm")
    val inputFile: File
//    val inputTape = "01#01".toCharArray().toList()
    val inputTape: List<Char>

    val initStates = mutableListOf<String>()
    var initStart = ""
    var initAccept = ""
    var initReject = ""
    val initAlpha = mutableListOf<Char>()
    val initTapeAlpha = mutableListOf('_')

    val instructions = mutableListOf<Instruction>()

    if (args.isEmpty() || args.size != 2) {
        println("Please input a file name and a string for the tape")
        exitProcess(-1)
    }
    if (args[0].isNotEmpty()
        && args[0].toLowerCase().endsWith(".tm")
    ) {
        inputFile = File(args[0])
    } else {
        println("Please input a valid file name")
        exitProcess(-1)
    }
    if (args[1].isNotEmpty()) {
        inputTape = args[1].toCharArray().toList()
    } else {
        println("Please input a string for the tape")
        exitProcess(-1)
    }
    val scanner = Scanner(inputFile)
    var lineCount = 0
    val lineList = mutableListOf<String>()
    val fileLineList = mutableListOf<String>()
    while (scanner.hasNext()) {
        val line = scanner.nextLine()
        if (line.startsWith("--")) {
            fileLineList.add("")
        }
        if (line.isBlank()) {
            fileLineList.add("")
        }
        val cleanString =
            if (line.contains("--")) line.substring(0, line.indexOf("--"))
            else line
        if (cleanString.isNotEmpty()) {
            fileLineList.add(cleanString)
            lineList.add(cleanString)
        }
        lineCount++
    }

    for (index in 0..(lineList.count() - 1)) {
        if (!lineList[index].isBlank()) {
            when (index) {
                0 -> {
                    val parseResult = stateListParser.parse(lineList[index])
                    if (stateListParser.accept(lineList[index])) {
                        parseResult.get<ArrayList<ArrayList<Any>>>()[1].forEachIndexed { resultIndex, any ->
                            if (resultIndex % 2 == 0) {
                                var tempString = ""
                                @Suppress("UNCHECKED_CAST")
                                (any as ArrayList<Char>).forEach {
                                    tempString += it
                                }
                                initStates.add(tempString)
                            }
                        }
                    }
                    else {
                        println("State list line invalid.")
                        exitProcess(-1)
                    }
                }
                1, 2, 3 -> {
                    val parseResult = stateParser.parse(lineList[index])
                    if (stateParser.accept(lineList[index])) {
                        var tempString = ""
                        parseResult.get<ArrayList<ArrayList<Any>>>()[1].forEach { any ->
                            tempString += any
                        }
                        when (index) {
                            1 -> initStart = tempString
                            2 -> initAccept = tempString
                            3 -> initReject = tempString
                        }
                    }
                    else {
                        when (index) {
                            1 -> {
                                println("Start state line invalid.")
                                exitProcess(-1)
                            }
                            2 -> {
                                println("Accept state line invalid.")
                                exitProcess(-1)
                            }
                            3 -> {
                                println("Reject state line invalid.")
                                exitProcess(-1)
                            }
                        }
                        exitProcess(-1)
                    }
                }
                4, 5 -> {
                    val parseResult = alphaParser.parse(lineList[index])
                    if (alphaParser.accept(lineList[index])) {
                        parseResult.get<ArrayList<ArrayList<Any>>>()[1].forEachIndexed { resultIndex, any ->
                            if (resultIndex%2 == 0) {
                                when (index) {
                                    4 -> initAlpha.add(any as Char)
                                    5 -> initTapeAlpha.add(any as Char)
                                }
                            }
                        }
                    }
                    else {
                        when (index) {
                            4 -> {
                                println("Alphabet line invalid.")
                                exitProcess(-1)
                            }
                            5 -> {
                                println("Tape alphabet line invalid.")
                                exitProcess(-1)
                            }
                        }
                    }
                }
                else -> {
                    if (Instructions.rwDtParser.accept(lineList[index]) ||
                            Instructions.rDlParser.accept(lineList[index]) ||
                            Instructions.rDtParser.accept(lineList[index])) {
                        val direction: Direction
                        val tupleList = mutableListOf("", ' ', ' ', "")
                        if (lineList[index].toLowerCase().startsWith("rwrt") || lineList[index].toLowerCase().startsWith("rwlt")) {
                            direction = Direction.valueOf(lineList[index][2].toString())
                            if (Instructions.rwDtParser.accept(lineList[index])) {
                                val parseResult = Instructions.rwDtParser.parse(lineList[index])
                                parseResult.get<ArrayList<Any>>().forEachIndexed { resultIndex, any ->
                                    when (resultIndex) {
                                        2 -> {
                                            var tempString = ""
                                            @Suppress("UNCHECKED_CAST")
                                            (any as ArrayList<Char>).forEach {
                                                tempString += it
                                            }
                                            tupleList[0] = tempString
                                        }
                                        4 -> tupleList[1] = any
                                        6 -> tupleList[2] = any
                                        8 -> {
                                            var tempString = ""
                                            @Suppress("UNCHECKED_CAST")
                                            (any as ArrayList<Char>).forEach {
                                                tempString += it
                                            }
                                            tupleList[3] = tempString
                                        }
                                    }
                                }
                                val tuple = Tuple(
                                    tupleList[0] as String,
                                    tupleList[1] as Char,
                                    tupleList[2] as Char,
                                    tupleList[3] as String
                                )
                                val instruction = Instruction(direction, tuple)
                                instructions.add(instruction)
                            }
                        } else if (lineList[index].toLowerCase().startsWith("rrl") || lineList[index].toLowerCase().startsWith("rll")) {
                            direction = Direction.valueOf(lineList[index][1].toString())
                            if (Instructions.rDlParser.accept(lineList[index])) {
                                val parseResult = Instructions.rDlParser.parse(lineList[index])
                                parseResult.get<ArrayList<Any>>().forEachIndexed { resultIndex, any ->
                                    when (resultIndex) {
                                        2 -> {
                                            var tempString = ""
                                            @Suppress("UNCHECKED_CAST")
                                            (any as ArrayList<Char>).forEach {
                                                tempString += it
                                            }
                                            tupleList[0] = tempString
                                        }
                                        4 -> tupleList[1] = any
                                    }
                                }
                                val tuple = Tuple(
                                    tupleList[0] as String,
                                    tupleList[1] as Char,
                                    tupleList[1] as Char,
                                    tupleList[0] as String
                                )
                                val instruction = Instruction(direction, tuple)
                                instructions.add(instruction)
                            }
                        } else if (lineList[index].toLowerCase().startsWith("rrt") || lineList[index].toLowerCase().startsWith("rlt")) {
                            direction = Direction.valueOf(lineList[index][1].toString())
                            if (Instructions.rDtParser.accept(lineList[index])) {
                                val parseResult = Instructions.rDtParser.parse(lineList[index])
                                parseResult.get<ArrayList<Any>>().forEachIndexed { resultIndex, any ->
                                    when (resultIndex) {
                                        2 -> {
                                            var tempString = ""
                                            @Suppress("UNCHECKED_CAST")
                                            (any as ArrayList<Char>).forEach {
                                                tempString += it
                                            }
                                            tupleList[0] = tempString
                                        }
                                        4 -> tupleList[1] = any
                                        6 -> {
                                            var tempString = ""
                                            @Suppress("UNCHECKED_CAST")
                                            (any as ArrayList<Char>).forEach {
                                                tempString += it
                                            }
                                            tupleList[3] = tempString
                                        }
                                    }
                                }
                                val tuple = Tuple(
                                    tupleList[0] as String,
                                    tupleList[1] as Char,
                                    tupleList[1] as Char,
                                    tupleList[3] as String
                                )
                                val instruction = Instruction(direction, tuple)
                                instructions.add(instruction)
                            }
                        }
                    } else {
                        for (i in 0..(fileLineList.count() - 1)) {
                            if (fileLineList[i] == lineList[index]) {
                                println("Invalid instruction format at line ${i+1}.")
                                exitProcess(-1)
                            }
                        }
                    }
                }
            }
        }
    }
/*    println("States: $initStates")
    println("Start: $initStart")
    println("Accept: $initAccept")
    println("Reject: $initReject")
    println("Alpha: $initAlpha")
    println("Tape alpha: $initTapeAlpha")
    println("---------")
    instructions.forEach {
        println(it)
    }
    println("---------")*/
    try {
        val machine = Machine(
            Init(initStates, initStart, initAccept, initReject, initAlpha, initTapeAlpha),
            instructions.distinct(),
            inputTape
        )
        machine.run()
    } catch (ex: Exception) {
//        println("!!!")
        println(ex.message)
        exitProcess(-1)
    }
}