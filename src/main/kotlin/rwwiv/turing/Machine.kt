package rwwiv.turing

class Machine(init: Init, instructions: List<Instruction>, input: List<Char>) {
	
	private val _init = init
	private val _instructions = instructions
	private val _input = input
	
	
	private val validStart = init.states.contains(init.startState)
	private val validAccept = init.states.contains(init.acceptState)
	private val validReject = init.states.contains(init.rejectState)
	private val validAlpha = !init.alpha.contains('_')
	private val tapeAlphaSuper = init.tapeAlpha.containsAll(init.alpha)
	private val tapeAlphaUnderScore = init.tapeAlpha.contains('_')
	private val validTapeAlpha = tapeAlphaSuper && tapeAlphaUnderScore
	
	private val validInstructions = validInstructions(instructions, init)
	
	private val validInput = init.tapeAlpha.containsAll(input)
	
	private val invalidChars = inputInvalidChars(input, init.tapeAlpha)
	
	init {
		if (!validAlpha) throw Exception("Specified alphabet contains _ which is a reserved character.")
		if (!validStart) throw Exception("Start state not in set of states.")
		if (!validAccept) throw Exception("Accept state not in set of states.")
		if (!validReject) throw Exception("Reject state not in set of states.")
		if (!validTapeAlpha) {
			if (!tapeAlphaSuper) throw Exception("Tape alphabet is not superset of alphabet.")
			else if (!tapeAlphaUnderScore) throw Exception("Tape alphabet does not contain '_' character.")
		}
		if (!validInput) {
			when (invalidChars.count()) {
				1 -> throw Exception("Input contains invalid character ${invalidChars[0]}")
				else -> throw Exception("Input contains invalid characters $invalidChars")
			}
		}
		if (!validInstructions.first) throw Exception("Syntax error at instruction ${validInstructions.second + 1}: ${validInstructions.third}.")
	}
	
	fun run() {
		var head = 0
		var currentState = _init.startState
		val tape = mutableListOf<Char>()
		_input.forEach {
			tape.add(it)
		}
		tape.add('_')
		
		tape.forEachIndexed { index, c ->
			if (index == head) {
				print("[${_init.startState}]")
			}
			if (index < (tape.count() - 1)) {
				print(c)
			}
		}
		println()
		if (currentState == _init.acceptState) {
			println("Accept")
		}
		if (currentState == _init.rejectState) {
			println("Reject")
		}
		
		while (currentState != _init.acceptState || currentState != _init.rejectState) {
			var instructionFound = false
			for (i in 0..(_instructions.count() - 1)) {
				if (_instructions[i].tuple.currentState == currentState &&
					_instructions[i].tuple.tapeRead == tape[head]
				) {
					currentState = _instructions[i].tuple.nextState
					tape[head] = _instructions[i].tuple.tapeWrite
					if (currentState == _init.acceptState || currentState == _init.rejectState) {
						instructionFound = true
						break
					}
					if (_instructions[i].direction == Direction.R) {
						head++
						if (tape.getOrNull(head) == null) {
							tape.add('_')
						}
						instructionFound = true
						break
					} else {
						if (head != 0) {
							head--
						}
						instructionFound = true
						break
					}
				} else {
					instructionFound = false
				}
			}
			if (!instructionFound) {
				throw Exception("Halting state reached, exiting.")
			}
			tape.forEachIndexed { index, c ->
				if (index == head) {
					print("[$currentState]")
				}
				if (index < (tape.count() - 1)) {
					print(c)
				}
			}
			println()
			if (currentState == _init.acceptState) {
				println("Accept")
				break
			}
			if (currentState == _init.rejectState) {
				println("Reject")
				break
			}
		}
	}
	
	private fun inputInvalidChars(input: List<Char>, tapeAlpha: List<Char>): List<Char> {
		val outList = mutableListOf<Char>()
		val distinctInput = input.distinct()
		distinctInput.forEach {
			if (!(tapeAlpha.contains(it))) {
				outList.add(it)
			}
		}
		return outList
	}
	
	private fun validInstructions(instructions: List<Instruction>, init: Init): Triple<Boolean, Int, String> {
		var allGood = true
		var instructionLine = 0
		var synErrorType = ""
		
		for (i in 0..(instructions.count() - 1)) {
			val tuple = instructions[i].tuple
			val validRead: Boolean = init.tapeAlpha.contains(tuple.tapeRead)
			val validWrite: Boolean = init.tapeAlpha.contains(tuple.tapeWrite)
			val validCurrentState: Boolean = init.states.contains(tuple.currentState)
			val validNextState: Boolean = init.states.contains(tuple.nextState)
			val tranAcceptState = (tuple.currentState == init.acceptState)
			val tranRejectState = (tuple.currentState == init.rejectState)
			fun rejectLine(errorType: String) {
				allGood = false
				instructionLine = i
				synErrorType = errorType
			}
			if (!validRead) {
				rejectLine("Invalid tape read")
				break
			}
			if (!validWrite) {
				rejectLine("Invalid tape write")
				break
			}
			if (!validCurrentState) {
				rejectLine("Invalid current state")
				break
			}
			if (!validNextState) {
				rejectLine("Invalid transition state")
				break
			}
			if (tranAcceptState) {
				rejectLine("Transition from accept state")
				break
			}
			if (tranRejectState) {
				rejectLine("Transition from reject state")
				break
			}
		}
		return Triple(allGood, instructionLine, synErrorType)
	}
}