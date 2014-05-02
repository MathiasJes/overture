package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.ATupleExp;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;

public class ATupleExpAssistantInterpreter // extends ATupleExpAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ATupleExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static ValueList getValues(ATupleExp exp, ObjectContext ctxt)
//	{
//		return PExpAssistantInterpreter.getValues(exp.getArgs(), ctxt);
//	}

	public static PExp findExpression(ATupleExp exp, int lineno)
	{
		PExp found = PExpAssistantInterpreter.findExpressionBaseCase(exp, lineno);
		if (found != null)
			return found;

		return PExpAssistantInterpreter.findExpression(exp.getArgs(), lineno);
	}

}
