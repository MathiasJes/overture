package org.overture.typechecker.assistant.pattern;

import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexQuoteToken;
import org.overture.ast.patterns.AQuotePattern;
import org.overture.ast.types.PType;

public class AQuotePatternAssistantTC {

	public static PType getPossibleTypes(AQuotePattern pattern) {
		return AstFactory.newAQuoteType(((AQuotePattern) pattern).getValue().clone());
	}
 
	public static PExp getMatchingExpression(AQuotePattern qp) {
		ILexQuoteToken v = qp.getValue();
		return AstFactory.newAQuoteLiteralExp(v.clone());
	}

}
