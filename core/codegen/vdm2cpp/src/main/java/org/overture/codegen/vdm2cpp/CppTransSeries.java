package org.overture.codegen.vdm2cpp;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.traces.TraceNames;
import org.overture.codegen.traces.TracesTrans;
import org.overture.codegen.trans.*;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.conc.EvalPermPredTrans;
import org.overture.codegen.trans.conc.MainClassConcTrans;
import org.overture.codegen.trans.conc.MutexDeclTrans;
import org.overture.codegen.trans.conc.SentinelTrans;
import org.overture.codegen.trans.funcvalues.FuncValAssistant;
import org.overture.codegen.trans.funcvalues.FuncValPrefixes;
import org.overture.codegen.trans.funcvalues.FuncValTrans;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.iterator.JavaLanguageIterator;
import org.overture.codegen.trans.letexps.FuncTrans;
import org.overture.codegen.trans.letexps.IfExpTrans;
import org.overture.codegen.trans.patterns.PatternTrans;
import org.overture.codegen.trans.patterns.PatternVarPrefixes;
import org.overture.codegen.trans.quantifier.CounterData;
import org.overture.codegen.trans.uniontypes.NonDetStmTrans;
import org.overture.codegen.trans.uniontypes.UnionTypeTrans;
import org.overture.codegen.trans.uniontypes.UnionTypeVarPrefixes;

public class CppTransSeries
{
	private static final String OBJ_INIT_CALL_NAME_PREFIX = "cg_init_";

	private CppCodeGen codeGen;
	private List<DepthFirstAnalysisAdaptor> series;
	private FuncValAssistant funcValAssist;

	public CppTransSeries(CppCodeGen codeGen)
	{
		this.codeGen = codeGen;
		this.series = new LinkedList<>();
		this.funcValAssist = new FuncValAssistant();
		setupAnalysis();
	}

	public FuncValAssistant getFuncValAssist()
	{
		return funcValAssist;
	}

	public List<DepthFirstAnalysisAdaptor> getSeries()
	{
		return series;
	}

	private List<DepthFirstAnalysisAdaptor> setupAnalysis()
	{
		// Data and functionality to support the transformations
		TransAssistantIR transAssist = codeGen.getTransAssistant();

		// Construct the transformations
		FuncTrans funcTr = new FuncTrans(transAssist);

		// Set up order of transformations
		
		series.add(funcTr);

		return series;
	}

	private CounterData consCounterData()
	{
		AExternalTypeIR type = new AExternalTypeIR();
		type.setName("Long");

		IRInfo irInfo = codeGen.getIRGenerator().getIRInfo();
		AIntLiteralExpIR initExp = irInfo.getExpAssistant().consIntLiteral(0);

		return new CounterData(type, initExp);
	}

	public void clear()
	{
		funcValAssist.getFuncValInterfaces().clear();
	}
}
