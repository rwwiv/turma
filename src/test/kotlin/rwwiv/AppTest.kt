package rwwiv

import org.junit.Test
import rwwiv.parser.Init
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.fail

class AppTest {
    val testInitString = """
        -- Initialization:
        {states: Q0,Q1,Q2,Q3,Q4,Q5,Q6,Q7,A,R}
        {start: Q0}
        {accept: A}
        {reject: R}
        {alpha: 0,1,#}
        {tape-alpha: 0,1,#,x}
    """.trimIndent()
    val states = "{states: Q0,Q1,Q2,Q3,Q4,Q5,Q6,Q7,A,R}"
    val startState = "{start: Q0}"
    val acceptState = "{accept: A}"
    val rejectState = "{reject: R}"
    val alpha = "{alpha: 0,1,#}"
    val tapeAlpha = "{tape-alpha: 0,1,#,x}"

    val testCommand = "rwRt Q0 0 x Q1;"

    @Test
    fun testSplit() {
        val scanner = Scanner(testInitString)
        var lineCount = 0
        val lineList = mutableListOf<String>()
        while (scanner.hasNext()) {
            val line = scanner.nextLine()
            if (line.startsWith("--")) continue
            if (line.isBlank()) continue
            when (lineCount) {
                in 0..5 -> {
                    val cleanString: String
                    if (line.contains("--")) {
                        cleanString = line.substring(0, line.indexOf("--"))
                    }
                    else cleanString = line
                    if (cleanString.isNotEmpty()) {
                        lineList.add(cleanString)
                    }
                    lineCount++
                }
                else -> {
                    fail()
                }
            }
        }
        assertEquals(6, lineCount)
        assertEquals("{states: Q0,Q1,Q2,Q3,Q4,Q5,Q6,Q7,A,R}", lineList[0])
        assertEquals("{start: Q0}", lineList[1])
        assertEquals("{accept: A}", lineList[2])
        assertEquals("{reject: R}", lineList[3])
        assertEquals("{alpha: 0,1,#}", lineList[4])
        assertEquals("{tape-alpha: 0,1,#,x}", lineList[5])

    }

    @Test
    fun testStatesParse() {
        val parseResult = Init.stateListParser.parse(states)
        val stateList = mutableListOf<String>()
        parseResult.get<ArrayList<ArrayList<Any>>>()[1].forEachIndexed { index, any ->
            if (index%2 == 0) {
                var tempString = ""
                @Suppress("UNCHECKED_CAST")
                (any as ArrayList<Char>).forEach {
                    tempString += it
                }
                stateList.add(tempString)
            }
        }
        val actualStateList: List<String> = listOf("Q0", "Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "A", "R")
        assertEquals(actualStateList, stateList)
    }
}


