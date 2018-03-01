package org.overture.codegen.vdm2cpp;

import java.util.LinkedList;
import java.util.List;
import java.util.Collection;
import java.util.HashSet;

import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.ir.*;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.IREventCoordinator;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.declarations.AModuleDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.trans.ModuleToClassTransformation;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;

public class CppCodeGen extends CodeGenBase implements IREventCoordinator {

	public static final String CPP_TEMPLATES_ROOT_FOLDER = "CppTemplates";
	private CppFormat cppFormat;
	private CppTransSeries transSeries;

	private List<String> headers = new LinkedList<>();
	
	public CppCodeGen() {
		
		this.cppFormat = new CppFormat(CPP_TEMPLATES_ROOT_FOLDER,generator.getIRInfo());
		this.transSeries = new CppTransSeries(this);

	}

	public CppTransSeries getTransSeries()
	{
		return this.transSeries;
	}
	
	private List<AModuleDeclIR> getModuleDecls(
			List<IRStatus<AModuleDeclIR>> statuses)
	{
		List<AModuleDeclIR> modules = new LinkedList<AModuleDeclIR>();

		for (IRStatus<AModuleDeclIR> status : statuses)
		{
			modules.add(status.getIrNode());
		}

		return modules;
	}
	
	@Override
	protected GeneratedData genVdmToTargetLang(List<IRStatus<PIR>> statuses) throws AnalysisException {
		
		List<IRStatus<AModuleDeclIR>> moduleStatuses = IRStatus.extract(statuses, AModuleDeclIR.class);
		List<GeneratedModule> genModules = new LinkedList<GeneratedModule>();

		List<IRStatus<PIR>> modulesAsNodes = IRStatus.extract(moduleStatuses);
		ModuleToClassTransformation moduleTransformation = new ModuleToClassTransformation(getInfo(), transAssistant, getModuleDecls(moduleStatuses));

		for (IRStatus<PIR> status : modulesAsNodes)
		{
			try
			{
				generator.applyTotalTransformation(status, moduleTransformation);

			} catch (org.overture.codegen.ir.analysis.AnalysisException e)
			{
				log.error("Error when generating code for module "
						+ status.getIrNodeName() + ": " + e.getMessage());
				log.error("Skipping module..");
				e.printStackTrace();
			}
		}
		
		List<IRStatus<SClassDeclIR>> classStatuses = IRStatus.extract(modulesAsNodes, SClassDeclIR.class);
		//classStatuses.addAll(IRStatus.extract(statuses, SClassDeclIR.class));
		
		List<IRStatus<PIR>> canBeGenerated = new LinkedList<IRStatus<PIR>>();

		for (IRStatus<PIR> status : IRStatus.extract(classStatuses))
		{
			if (status.canBeGenerated())
			{
				canBeGenerated.add(status);
			} 
			/*else
			{
				genModules.add(new GeneratedModule(status.getIrNodeName(), status.getUnsupportedInIr(), new HashSet<IrNodeInfo>(), isTestCase(status)));
			}*/
		}

		for (DepthFirstAnalysisAdaptor trans : transSeries.getSeries())
		{
			for (IRStatus<PIR> status : canBeGenerated)
			{
				try
				{
					if (!getInfo().getDeclAssistant().isLibraryName(status.getIrNodeName()))
					{
						generator.applyPartialTransformation(status, trans);
					}

				} catch (org.overture.codegen.ir.analysis.AnalysisException e)
				{
					log.error("Error when generating code for class "
							+ status.getIrNodeName() + ": " + e.getMessage());
					log.error("Skipping class..");
					e.printStackTrace();
				}
			}
		}
		
		generateClassHeaders(canBeGenerated);
		
		MergeVisitor mergeVisitor = cppFormat.getMergeVisitor();

		for (IRStatus<PIR> status : canBeGenerated)
		{
			try {
				
				genModules.add(genIrModule(mergeVisitor, status));
				
			} catch (org.overture.codegen.ir.analysis.AnalysisException e) {
				e.printStackTrace();
			}
		}
		
		
		GeneratedData data = new GeneratedData();
		data.setClasses(genModules);
		
		return data;
	}

	protected void generateClassHeaders(final List<IRStatus<PIR>> statuses)
	{
		try
		{
			Collection<? extends IRStatus<PIR>> classHeaders = new ClassHeaderGenerator().generateClassHeaders(IRStatus.extract(statuses, ADefaultClassDeclIR.class));

			for (IRStatus<PIR> h : classHeaders) {
				headers.add(h.getIrNodeName());
			}

			statuses.addAll(classHeaders);
		} catch (org.overture.codegen.ir.analysis.AnalysisException e)
		{
			log.error("Could not generate class headers: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	protected void preProcessAst(List<org.overture.ast.node.INode> ast) throws AnalysisException
	{
		generator.computeDefTable(getUserModules(ast));
		removeUnreachableStms(ast);
		handleOldNames(ast);

		for (INode node : ast)
		{
			if (getInfo().getAssistantManager().getDeclAssistant().isLibrary(node))
			{
				simplifyLibrary(node);
			} else
			{
				preProcessVdmUserClass(node);
			}
		}
	}

}
