package org.overture.codegen.vdm2java;

import java.util.List;

import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.trans.IPostCheckCreator;
import org.overture.codegen.trans.IsExpTransformation;
import org.overture.codegen.trans.PostCheckTransformation;
import org.overture.codegen.trans.PreCheckTransformation;
import org.overture.codegen.trans.PrePostTransformation;
import org.overture.codegen.trans.SeqConversionTransformation;
import org.overture.codegen.trans.TransformationVisitor;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.conc.MainClassConcTransformation;
import org.overture.codegen.trans.conc.MutexDeclTransformation;
import org.overture.codegen.trans.conc.SentinelTransformation;
import org.overture.codegen.trans.funcvalues.FunctionValueAssistant;
import org.overture.codegen.trans.funcvalues.FunctionValueTransformation;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.iterator.JavaLanguageIterator;
import org.overture.codegen.trans.letexps.FuncTransformation;
import org.overture.codegen.trans.letexps.IfExpTransformation;
import org.overture.codegen.trans.patterns.PatternMatchConfig;
import org.overture.codegen.trans.patterns.PatternTransformation;
import org.overture.codegen.trans.quantifier.Exists1CounterData;
import org.overture.codegen.trans.uniontypes.UnionTypeTransformation;

import static org.overture.codegen.ir.CodeGenBase.*;

public class JavaTransSeries
{
	private JavaCodeGen codeGen;

	public JavaTransSeries(JavaCodeGen codeGen)
	{
		this.codeGen = codeGen;
	}

	public DepthFirstAnalysisAdaptor[] consAnalyses(List<AClassDeclCG> classes,
			FunctionValueAssistant functionValueAssistant)
	{
		IPostCheckCreator postCheckCreator = new JavaPostCheckCreator(POST_CHECK_METHOD_NAME);

		TransAssistantCG transAssistant = codeGen.getTransformationAssistant();

		FuncTransformation funcTransformation = new FuncTransformation(transAssistant);
		IRInfo irInfo = codeGen.getIRGenerator().getIRInfo();

		PrePostTransformation prePostTransformation = new PrePostTransformation(irInfo);
		IfExpTransformation ifExpTransformation = new IfExpTransformation(transAssistant);
		FunctionValueTransformation funcValueTransformation = new FunctionValueTransformation(irInfo, transAssistant, functionValueAssistant, INTERFACE_NAME_PREFIX, TEMPLATE_TYPE_PREFIX, EVAL_METHOD_PREFIX, PARAM_NAME_PREFIX);
		ILanguageIterator langIterator = new JavaLanguageIterator(transAssistant, irInfo.getTempVarNameGen(), codeGen.getTempVarPrefixes());
		TransformationVisitor transVisitor = new TransformationVisitor(irInfo, classes, codeGen.getTempVarPrefixes(), transAssistant, consExists1CounterData(), langIterator, TERNARY_IF_EXP_NAME_PREFIX, CASES_EXP_RESULT_NAME_PREFIX, AND_EXP_NAME_PREFIX, OR_EXP_NAME_PREFIX, WHILE_COND_NAME_PREFIX, REC_MODIFIER_NAME_PREFIX);
		PatternTransformation patternTransformation = new PatternTransformation(classes, codeGen.getTempVarPrefixes(), irInfo, transAssistant, new PatternMatchConfig());
		PreCheckTransformation preCheckTransformation = new PreCheckTransformation(irInfo, transAssistant, new JavaValueSemanticsTag(false));
		PostCheckTransformation postCheckTransformation = new PostCheckTransformation(postCheckCreator, irInfo, transAssistant, FUNC_RESULT_NAME_PREFIX, new JavaValueSemanticsTag(false));
		IsExpTransformation isExpTransformation = new IsExpTransformation(irInfo, transAssistant, IS_EXP_SUBJECT_NAME_PREFIX);
		SeqConversionTransformation seqConversionTransformation = new SeqConversionTransformation(transAssistant);

		// Concurrency related transformations
		SentinelTransformation concurrencytransform = new SentinelTransformation(irInfo, classes);
		MainClassConcTransformation mainclassTransform = new MainClassConcTransformation(irInfo, classes);
		MutexDeclTransformation mutexTransform = new MutexDeclTransformation(irInfo, classes);

		UnionTypeTransformation unionTypeTransformation = new UnionTypeTransformation(transAssistant, irInfo, classes, APPLY_EXP_NAME_PREFIX, OBJ_EXP_NAME_PREFIX, CALL_STM_OBJ_NAME_PREFIX, MISSING_OP_MEMBER, MISSING_MEMBER);
		JavaClassToStringTrans javaToStringTransformation = new JavaClassToStringTrans(irInfo);

		DepthFirstAnalysisAdaptor[] analyses = new DepthFirstAnalysisAdaptor[] {
				funcTransformation, prePostTransformation, ifExpTransformation,
				funcValueTransformation, transVisitor, patternTransformation,
				preCheckTransformation, postCheckTransformation,
				isExpTransformation, unionTypeTransformation,
				javaToStringTransformation, concurrencytransform,
				mutexTransform, mainclassTransform, seqConversionTransformation };

		return analyses;
	}

	private Exists1CounterData consExists1CounterData()
	{
		AExternalTypeCG type = new AExternalTypeCG();
		type.setName("Long");

		IRInfo irInfo = codeGen.getIRGenerator().getIRInfo();
		AIntLiteralExpCG initExp = irInfo.getExpAssistant().consIntLiteral(0);

		return new Exists1CounterData(type, initExp);
	}
}
