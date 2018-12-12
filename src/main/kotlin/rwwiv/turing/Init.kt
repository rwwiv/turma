package rwwiv.turing

data class Init(
	val states: List<String>,
	val startState: String,
	val acceptState: String,
	val rejectState: String,
	val alpha: List<Char>,
	val tapeAlpha: List<Char>
)