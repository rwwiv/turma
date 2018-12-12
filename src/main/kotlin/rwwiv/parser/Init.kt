package rwwiv.parser

import org.petitparser.parser.Parser
import org.petitparser.parser.primitive.CharacterParser.*
import org.petitparser.parser.primitive.StringParser.of


object Init {
	val stateListParser: Parser = of("{states: ")
		.seq((word().star()).separatedBy(of(',')))
		.seq(of('}'))
	val stateParser: Parser = (of("{start: ").or(of("{accept: ")).or(of("{reject: ")))
		.seq(word().star())
		.seq(of('}'))
	val alphaParser: Parser = of("{alpha: ").or(of("{tape-alpha: "))
		.seq(any().separatedBy(of(',')))
		.seq(of('}'))
}