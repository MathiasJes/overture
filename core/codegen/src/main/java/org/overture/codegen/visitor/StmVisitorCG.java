package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.ADefLetDefStm;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ANotYetSpecifiedStm;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.statements.ASkipStm;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.statements.PStm;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallStmCG;
import org.overture.codegen.cgast.statements.AElseIfStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ALetDefStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.statements.ASkipStmCG;
import org.overture.codegen.cgast.statements.PStateDesignatorCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;


public class StmVisitorCG extends AbstractVisitorCG<CodeGenInfo, PStmCG>
{
	
	private static final long serialVersionUID = -602593891699169007L;

	public StmVisitorCG()
	{
	}
	
	@Override
	public PStmCG caseANotYetSpecifiedStm(ANotYetSpecifiedStm node,
			CodeGenInfo question) throws AnalysisException
	{
		System.out.println("NotYetSpecified opname: " + node.toString());
		
		AExplicitOperationDefinition x = node.getAncestor(AExplicitOperationDefinition.class);
		
		if(x != null) System.out.println(x.getName().getName()); else System.out.println("nothing!");
		
		return super.caseANotYetSpecifiedStm(node, question);
	}
	
	@Override
	public PStmCG defaultPExp(PExp node, CodeGenInfo question)
			throws AnalysisException
	{
		AReturnStmCG returnStm = new AReturnStmCG();
		
		PExpCG exp =  node.apply(question.getExpVisitor(), question);
		returnStm.setExp(exp);
		
		return returnStm;
	}	
	
	@Override
	public PStmCG caseABlockSimpleBlockStm(ABlockSimpleBlockStm node,
			CodeGenInfo question) throws AnalysisException
	{
		
		ABlockStmCG blockStm = new ABlockStmCG();
		
		LinkedList<PStm> stms = node.getStatements();
		
		for (PStm pStm : stms)
		{
			blockStm.getStatements().add(pStm.apply(question.getStatementVisitor(), question));
		}
		
		return blockStm;
	}
	
	@Override
	public PStmCG caseAAssignmentStm(AAssignmentStm node, CodeGenInfo question)
			throws AnalysisException
	{
		PStateDesignatorCG target = node.getTarget().apply(question.getStateDesignatorVisitor(), question);
		PExpCG exp = node.getExp().apply(question.getExpVisitor(), question);
		
		AAssignmentStmCG assignment = new AAssignmentStmCG();
		assignment.setTarget(target);
		assignment.setExp(exp);
		
		return assignment;
	}
	
	@Override
	public PStmCG caseADefLetDefStm(ADefLetDefStm node, CodeGenInfo question)
			throws AnalysisException
	{
		ALetDefStmCG localDefStm = new ALetDefStmCG();
		
		LinkedList<PDefinition> localNodeDefs = node.getLocalDefs();
		
		for (PDefinition def : localNodeDefs)
		{
			if(def instanceof AValueDefinition)
			{
				AValueDefinition valueDef = (AValueDefinition) def;
				
				PTypeCG type = valueDef.getType().apply(question.getTypeVisitor(), question);
				String name = valueDef.getPattern().toString();
				PExpCG exp = valueDef.getExpression().apply(question.getExpVisitor(), question);
				
				ALocalVarDeclCG localVarDecl = new ALocalVarDeclCG();
				localVarDecl.setType(type);
				localVarDecl.setName(name);
				localVarDecl.setExp(exp);
				
				localDefStm.getLocalDefs().add(localVarDecl);
			}
		}
		
		PStmCG stm = node.getStatement().apply(question.getStatementVisitor(), question);
		localDefStm.setStm(stm);
		
		return localDefStm;
	}
		
	@Override
	public PStmCG caseAReturnStm(AReturnStm node, CodeGenInfo question)
			throws AnalysisException
	{
		PExpCG exp = node.getExpression().apply(question.getExpVisitor(), question);
		
		AReturnStmCG returnStm = new AReturnStmCG();
		returnStm.setExp(exp);
		
		return returnStm;
	}
	
	@Override
	public PStmCG caseACallStm(ACallStm node, CodeGenInfo question)
			throws AnalysisException
	{
		String name = node.getName().getName();
		
		ACallStmCG call = new ACallStmCG();
		call.setName(name);
		
		LinkedList<PExp> applyArgs = node.getArgs();
		
		for (int i = 0; i < applyArgs.size(); i++)
		{
			PExpCG arg = applyArgs.get(i).apply(question.getExpVisitor(), question);
			call.getArgs().add(arg);
		}		
		
		return call;
	}
	
	@Override
	public PStmCG caseAElseIfStm(AElseIfStm node, CodeGenInfo question)
			throws AnalysisException
	{
		//Don't visit it but create it directly if needed in the ifStm in order to avoid casting
		return null;
	}
	
	@Override
	public PStmCG caseAIfExp(AIfExp node, CodeGenInfo question)
			throws AnalysisException
	{
		PExpCG ifExp = node.getTest().apply(question.getExpVisitor(), question);
		PStmCG then = node.getThen().apply(question.getStatementVisitor(), question);

		AIfStmCG ifStm = new AIfStmCG();

		ifStm.setIfExp(ifExp);
		ifStm.setThenStm(then);
		LinkedList<AElseIfExp> elseIfs = node.getElseList();	
		
		for (AElseIfExp exp : elseIfs)
		{
			ifExp = exp.getElseIf().apply(question.getExpVisitor(), question);
			then = exp.getThen().apply(question.getStatementVisitor(), question);
						
			AElseIfStmCG elseIfStm = new AElseIfStmCG();
			elseIfStm.setElseIf(ifExp);
			elseIfStm.setThenStm(then);
			
			ifStm.getElseIf().add(elseIfStm);
		}
		
		if(node.getElse() != null)
		{
			PStmCG elseStm = node.getElse().apply(question.getStatementVisitor(), question);
			ifStm.setElseStm(elseStm);
		}

		return ifStm;
	}
	
	@Override
	public PStmCG caseAIfStm(AIfStm node, CodeGenInfo question)
			throws AnalysisException
	{
		PExpCG ifExp = node.getIfExp().apply(question.getExpVisitor(), question);
		PStmCG thenStm = node.getThenStm().apply(question.getStatementVisitor(), question);
		
		
		AIfStmCG ifStm = new AIfStmCG();
		
		ifStm.setIfExp(ifExp);
		ifStm.setThenStm(thenStm);
		
		LinkedList<AElseIfStm> elseIfs = node.getElseIf();	
		
		for (AElseIfStm stm : elseIfs)
		{
			ifExp = stm.getElseIf().apply(question.getExpVisitor(), question);
			thenStm = stm.getThenStm().apply(question.getStatementVisitor(), question);
			
			AElseIfStmCG elseIfStm = new AElseIfStmCG();
			elseIfStm.setElseIf(ifExp);
			elseIfStm.setThenStm(thenStm);
			
			
			ifStm.getElseIf().add(elseIfStm);
		}
		
		if(node.getElseStm() != null)
		{
			PStmCG elseStm = node.getElseStm().apply(question.getStatementVisitor(), question);
			ifStm.setElseStm(elseStm);
		}
		
		return ifStm;
		
	}
	
	
	@Override
	public PStmCG caseASkipStm(ASkipStm node, CodeGenInfo question)
			throws AnalysisException
	{
		return new ASkipStmCG();
	}
	
	@Override
	public PStmCG caseASubclassResponsibilityStm(
			ASubclassResponsibilityStm node, CodeGenInfo question)
			throws AnalysisException
	{
		return null;//Indicates an abstract body
	}
	
}
