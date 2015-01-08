package org.overture.codegen.vdm2java.rt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ARMIServerDeclCG;
import org.overture.codegen.cgast.declarations.ARemoteContractDeclCG;
import org.overture.codegen.cgast.declarations.ARemoteContractImplDeclCG;
import org.overture.codegen.cgast.declarations.ASynchTokenDeclCG;
import org.overture.codegen.cgast.declarations.ASynchTokenInterfaceDeclCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateStructure;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaFormat;

/*
 * In this the ARMIServerDeclCG node is set up
 * in order to code generate the global registration service.
 * In addition, the generate remote contracts and remote contract 
 * implementation are printed to files inside the relevant CPUs
 * in order to test the code. For this reason, the path is currently 
 * fix to a local path.
 */

public class RemoteContractDistribution {
	
	private Map<String, Set<String>> cpuToConnectedCPUs;
	private Set<ARemoteContractDeclCG> remoteContracts;
	private Map<String, Set<AClassClassDefinition>> cpuToDeployedClasses;
	private List<ARemoteContractImplDeclCG> remoteImpls;

	public RemoteContractDistribution(
			Set<ARemoteContractDeclCG> remoteContracts, Map<String, Set<AClassClassDefinition>> cpuToDeployedClasses, Map<String, Set<String>> cpuToConnectedCPUs, List<ARemoteContractImplDeclCG> remoteImpls) {
		this.cpuToDeployedClasses = cpuToDeployedClasses;
		this.remoteContracts = remoteContracts;
		this.cpuToConnectedCPUs = cpuToConnectedCPUs;
		this.remoteImpls = remoteImpls;
	}

	public void run() throws AnalysisException, IOException {

		IRInfo info = new IRInfo("cg_init");
		JavaFormat javaFormat = new JavaFormat(new TempVarPrefixes(), new TemplateStructure(JavaCodeGen.JAVA_TEMPLATES_ROOT_FOLDER), info);
		MergeVisitor printer = javaFormat.getMergeVisitor();

		//Create the RMI server
		
		ARMIServerDeclCG rmiServer = new ARMIServerDeclCG();

		int PortNumber = 1099;
		String RMI_ServerName = "RMI_Server";
		
		rmiServer.setPortNumber(PortNumber);
		rmiServer.setName(RMI_ServerName);
		
		StringWriter writer_s = new StringWriter();
		rmiServer.apply(printer, writer_s);

		File theDir_s = new File("/home/gkanos/Documents/vdmrtcodegen/" + RMI_ServerName);

		theDir_s.mkdir();
		
		File file_s = new File("/home/gkanos/Documents/vdmrtcodegen/" + RMI_ServerName + "/" + RMI_ServerName + ".java");
		BufferedWriter output_s = new BufferedWriter(new FileWriter(file_s));
		output_s.write(JavaCodeGenUtil.formatJavaCode(writer_s
				.toString()));
		output_s.close();
		
		// Print remote contracts to the RMI server
		for (ARemoteContractDeclCG contract : remoteContracts) {
				StringWriter writer_i = new StringWriter();
				contract.apply(printer, writer_i);

				File file_i = new File("/home/gkanos/Documents/vdmrtcodegen/" + RMI_ServerName + "/" + contract.getName() + ".java");
				BufferedWriter output_i = new BufferedWriter(new FileWriter(file_i));
				output_i.write(JavaCodeGenUtil.formatJavaCode(writer_i
						.toString()));
				output_i.close();
		}
		
		// Add SyncToken interface to the RMI server directory
		ASynchTokenInterfaceDeclCG synchToken_interface_RMI = new ASynchTokenInterfaceDeclCG();

		StringWriter writer_synch_i_RMI = new StringWriter();
		synchToken_interface_RMI.apply(printer, writer_synch_i_RMI);

		File file_synch_i_RMI = new File("/home/gkanos/Documents/vdmrtcodegen/" + RMI_ServerName + "/" + "SynchToken_interface.java");
		BufferedWriter output_synch_i_RMI = new BufferedWriter(new FileWriter(file_synch_i_RMI));
		output_synch_i_RMI.write(JavaCodeGenUtil.formatJavaCode(writer_synch_i_RMI
				.toString()));
		output_synch_i_RMI.close();
		
		//System.out.println("**********************Remote contracts**********************");

		// Create a directory for every CPU, and place relevant remote contracts and
		// remote contract implementations inside

		for(String cpu : cpuToDeployedClasses.keySet()){
			File theDir = new File("/home/gkanos/Documents/vdmrtcodegen/" + cpu);

			theDir.mkdir();
		}
		
		
		for(String cpu : cpuToDeployedClasses.keySet()){


			for(AClassClassDefinition clas : cpuToDeployedClasses.get(cpu)){
				
				// Generate a SynchToken and its interface for each CPU
				ASynchTokenDeclCG synchToken = new ASynchTokenDeclCG();

				StringWriter writer_synch = new StringWriter();
				synchToken.apply(printer, writer_synch);

				File file_synch = new File("/home/gkanos/Documents/vdmrtcodegen/" + cpu + "/" + "SynchToken.java");
				BufferedWriter output_synch = new BufferedWriter(new FileWriter(file_synch));
				output_synch.write(JavaCodeGenUtil.formatJavaCode(writer_synch
						.toString()));
				output_synch.close();
				
				
				ASynchTokenInterfaceDeclCG synchToken_interface = new ASynchTokenInterfaceDeclCG();

				StringWriter writer_synch_i = new StringWriter();
				synchToken_interface.apply(printer, writer_synch_i);

				File file_synch_i = new File("/home/gkanos/Documents/vdmrtcodegen/" + cpu + "/" + "SynchToken_interface.java");
				BufferedWriter output_synch_i = new BufferedWriter(new FileWriter(file_synch_i));
				output_synch_i.write(JavaCodeGenUtil.formatJavaCode(writer_synch_i
						.toString()));
				output_synch_i.close();
				
				for (ARemoteContractDeclCG contract : remoteContracts) { // print remote contract to files
						StringWriter writer = new StringWriter();
						contract.apply(printer, writer);

						File file = new File("/home/gkanos/Documents/vdmrtcodegen/" + cpu + "/" + contract.getName() + ".java");
						BufferedWriter output = new BufferedWriter(new FileWriter(file));
						output.write(JavaCodeGenUtil.formatJavaCode(writer
								.toString()));
						output.close();
				}

				for (ARemoteContractImplDeclCG impl : remoteImpls) { // print remote contract implementation to files
					
						StringWriter writer = new StringWriter();
						impl.apply(printer, writer);

						File file = new File("/home/gkanos/Documents/vdmrtcodegen/" + cpu + "/" + impl.getName() + ".java");
						BufferedWriter output = new BufferedWriter(new FileWriter(file));
						output.write(JavaCodeGenUtil.formatJavaCode(writer
								.toString()));
						output.close();
				}
			}
		}

	}

}
