package rwwiv.turing

data class Tuple(
	val currentState: String,
	val tapeRead: Char,
	val tapeWrite: Char,
	val nextState: String
)