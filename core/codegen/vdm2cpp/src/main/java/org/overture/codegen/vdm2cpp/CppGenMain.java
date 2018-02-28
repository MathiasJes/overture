package org.overture.codegen.vdm2cpp;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.printer.MsgPrinter;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class CppGenMain {
	
	public static final String FOLDER_ARG = "-folder";
	public static final String PRINT_ARG = "-print";
	public static final String OUTPUT_ARG = "-output";
	public static final String VDM_ENTRY_EXP = "-entry";
	public static final String NO_CODE_FORMAT = "-nocodeformat";
	public static final String GEN_SYS_CLASS = "-gensysclass";

	public static void main(String[] args) {
		
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
		boolean printClasses = false;
		CppSettings cppSettings = new CppSettings();

		//String simpleModel = "module A definitions functions add: int * int +> char | int add(m,n) == m+n; ex: char | int +> int ex(u) == if is_(u, int) then u else 1; end A";
		String simpleModel = "module A definitions functions add: int * int +> char | int add(m,n) == m+n; ex: char | int +> int ex(u) == if is_(u, int) then u else 1; end A";
		
		List<File> files = new LinkedList<File>();
		File outputDir = null;

		List<String> listArgs = Arrays.asList(args);
		for (Iterator<String> i = listArgs.iterator(); i.hasNext();)
		{
			String arg = i.next();

			
			if (arg.equals(PRINT_ARG))
			{
				printClasses = true;
			} else if (arg.equals(FOLDER_ARG))
			{
				if (i.hasNext())
				{
					File path = new File(i.next());

					if (path.isDirectory())
					{
						files.addAll(filterFiles(GeneralUtils.getFiles(path)));
					} else
					{
						usage("Could not find path: " + path);
					}
				} else
				{
					usage(FOLDER_ARG + " requires a directory");
				}
			} else if (arg.equals(OUTPUT_ARG))
			{
				if (i.hasNext())
				{
					outputDir = new File(i.next());
					outputDir.mkdirs();

					if (!outputDir.isDirectory())
					{
						usage(outputDir + " is not a directory");
					}

				} else
				{
					usage(OUTPUT_ARG + " requires a directory");
				}
			} else if (arg.equals(VDM_ENTRY_EXP))
			{
				if (i.hasNext())
				{
					cppSettings.setVdmEntryExp(i.next());
				}
			} else if (arg.equals(NO_CODE_FORMAT))
			{
				cppSettings.setFormatCode(false);
			} else if(arg.equals(GEN_SYS_CLASS))
			{
				cppSettings.setGenSystemClass(true);
			}
			else
			{
				// It's a file or a directory
				File file = new File(arg);

				if (file.isFile())
				{
					if (GeneralCodeGenUtils.isVdmSourceFile(file))
					{
						files.add(file);
					}
				} else
				{
					usage("Not a file: " + file);
				}
			}
		}

		if (outputDir == null && !printClasses)
		{
			MsgPrinter.getPrinter().println("No output directory specified - printing code generated classes instead..\n");
			printClasses = true;
		}


		handleSl(files, cppSettings, printClasses, outputDir, simpleModel);
	}

	public static void handleSl(List<File> files,
	CppSettings javaSettings, boolean printCode, File outputDir, String model)
	{
		try {
			TypeCheckResult<List<AModuleModules>> res;
			if (!files.isEmpty())
			{
				res = TypeCheckerUtil.typeCheckSl(files);
			}
			else
			{
				res = TypeCheckerUtil.typeCheckSl(model);
			}
			
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
					MsgPrinter.getPrinter().println(String.format("VDM expression '%s' could not be merged. Following merge errors were found:"));
					GeneralCodeGenUtils.printMergeErrors(generated.getMergeErrors());
				} else if (!generated.canBeGenerated())
				{
					MsgPrinter.getPrinter().println("Could not generate VDM expression: ");

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


	public static List<File> filterFiles(List<File> files)
	{
		List<File> filtered = new LinkedList<File>();

		for (File f : files)
		{
			if (GeneralCodeGenUtils.isVdmSourceFile(f))
			{
				filtered.add(f);
			}
		}

		return filtered;
	}

	public static void usage(String msg)
	{
		MsgPrinter.getPrinter().errorln("VDM-to-Java Code Generator: " + msg
				+ "\n");
		MsgPrinter.getPrinter().errorln(FOLDER_ARG
				+ " <folder path>: a folder containing input vdm source files");
		MsgPrinter.getPrinter().errorln(PRINT_ARG
				+ ": print the generated code to the console");
		MsgPrinter.getPrinter().errorln(OUTPUT_ARG
				+ " <folder path>: the output folder of the generated code");
		MsgPrinter.getPrinter().errorln(VDM_ENTRY_EXP
				+ " <vdm entry point expression>: generate a Java main method based on the specified entry point");
		MsgPrinter.getPrinter().errorln(NO_CODE_FORMAT
				+ ": to NOT format the generated Java code");

		// Terminate
		System.exit(1);
	}

}
