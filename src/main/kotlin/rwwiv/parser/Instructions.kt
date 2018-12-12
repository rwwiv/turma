package rwwiv.parser

import org.petitparser.parser.Parser
import org.petitparser.parser.primitive.CharacterParser.*
import org.petitparser.parser.primitive.StringParser.of

object Instructions {
	val commandParser: Parser = (of("rwRt")
		.or(of("rwLt"))
		.or(of("rRl"))
		.or(of("rLl"))
		.or(of("rRt"))
		.or(of("rLt")))
		.seq(any().star())
	val rwDtParser: Parser = (of("rwRt").or(of("rwLt")))
		.seq(of(' '))
		.seq(word().star())
		.seq(of(' '))
		.seq(any())
		.seq(of(' '))
		.seq(any())
		.seq(of(' '))
		.seq(word().star())
		.seq(of(';'))
	val rDlParser: Parser = (of("rRl").or(of("rLl")))
		.seq(of(' '))
		.seq(word().star())
		.seq(of(' '))
		.seq(any())
		.seq(of(';'))
	val rDtParser: Parser = (of("rRt").or(of("rLt")))
		.seq(of(' '))
		.seq(word().star())
		.seq(of(' '))
		.seq(any())
		.seq(of(' '))
		.seq(word().star())
		.seq(of(';'))
}