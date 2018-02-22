package org.overture.codegen.vdm2cpp;

import java.util.List;

import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.printer.MsgPrinter;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class CppGenMain {
	
	public static void main(String[] args) {
		
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
		
		String simpleModel = "module A definitions functions add: nat * nat +> nat add(m,n) == m+n; end A";
		
		try {
			TypeCheckResult<List<AModuleModules>> res = TypeCheckerUtil.typeCheckSl(simpleModel);
			
			if(!res.parserResult.errors.isEmpty())
			{
				System.out.println("Got the following parse errors:\n " + res.parserResult.errors);
				return;
			}
			
			if(!res.errors.isEmpty())
			{
				System.out.println("Got the following type errors:\n " + res.errors);
				return;
			}
			
			List<AModuleModules> ast = res.result;
			
			CppCodeGen vdm2cpp = new CppCodeGen();
			GeneratedData genModules = vdm2cpp.generate(CodeGenBase.getNodes(ast));
			
			for(GeneratedModule generated : genModules.getClasses())
			{
				if (generated.hasMergeErrors())
				{
					MsgPrinter.getPrinter().println(String.format("VDM expression '%s' could not be merged. Following merge errors were found:", simpleModel));
					GeneralCodeGenUtils.printMergeErrors(generated.getMergeErrors());
				} else if (!generated.canBeGenerated())
				{
					MsgPrinter.getPrinter().println("Could not generate VDM expression: "
							+ simpleModel);

					if (generated.hasUnsupportedIrNodes())
					{
						GeneralCodeGenUtils.printUnsupportedIrNodes(generated.getUnsupportedInIr());
					}

					if (generated.hasUnsupportedTargLangNodes())
					{
						GeneralCodeGenUtils.printUnsupportedNodes(generated.getUnsupportedInTargLang());
					}

				} else
				{
					MsgPrinter.getPrinter().println("Code generated expression: "
							+ generated.getContent().trim());
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
