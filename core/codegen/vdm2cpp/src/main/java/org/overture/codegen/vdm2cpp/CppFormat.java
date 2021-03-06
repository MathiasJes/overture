package org.overture.codegen.vdm2cpp;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.codegen.assistant.LocationAssistantIR;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.types.AUnionTypeIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateManager;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.STypeIR;

public class CppFormat {
	
	public static final String CPP_FORMAT_KEY = CppFormat.class.getSimpleName();
	
	protected IRInfo info;

	private MergeVisitor mergeVisitor;
	
	public CppFormat(String templateRoot, IRInfo info)
	{
		TemplateCallable[] templateCallables = new TemplateCallable[] {new TemplateCallable(CPP_FORMAT_KEY, this)};
		this.mergeVisitor = new MergeVisitor(new TemplateManager(templateRoot), templateCallables);
		this.info = info;
	}

	public String format(INode node) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		node.apply(mergeVisitor, writer);
		return writer.toString();
	}

	public String formattedTypes(List<STypeIR> types, String typePostFix)
	throws AnalysisException
	{
	STypeIR firstType = types.get(0);

	StringWriter writer = new StringWriter();
	writer.append(format(firstType) + typePostFix);

	for (int i = 1; i < types.size(); i++)
	{
		STypeIR currentType = types.get(i);

		writer.append(", " + format(currentType) + typePostFix);
	}

	String result = writer.toString();

	return result;
	}

	public String formatTypeArg(STypeIR type) throws AnalysisException
	{
		if (type == null)
		{
			return null;
		} else
		{
			List<STypeIR> types = new LinkedList<STypeIR>();
			types.add(type);

			return formattedTypes(types, "");
		}
	}

	public static boolean isScoped(ABlockStmIR block)
	{
		return block != null && block.getScoped() != null && block.getScoped();
	}

	public String format(List<AFormalParamLocalParamIR> params)
			throws AnalysisException
	{
		StringWriter writer = new StringWriter();

		if (params.size() <= 0)
		{
			return "";
		}

		AFormalParamLocalParamIR firstParam = params.get(0);
		writer.append(format(firstParam));

		for (int i = 1; i < params.size(); i++)
		{
			AFormalParamLocalParamIR param = params.get(i);
			writer.append(", ");
			writer.append(format(param));
		}
		return writer.toString();
	}

	public String formatVdmSource(PIR irNode)
	{
		if ( irNode != null)
		{
			org.overture.ast.node.INode vdmNode = LocationAssistantIR.getVdmNode(irNode);

			if (vdmNode != null)
			{
				ILexLocation loc = info.getLocationAssistant().findLocation(vdmNode);

				if (loc != null)
				{
					return String.format("/* %s %d:%d */\n", loc.getFile().getName(), loc.getStartLine(), loc.getStartPos());
				}
			}

		}

		return "";
	}


	public List<AFieldDeclIR> getPrivateFields(List<AFieldDeclIR> toFilter)
	{
		return toFilter.stream().filter(f -> !f.getAccess().equals(IRConstants.PRIVATE)).collect(Collectors.toList());
	}
	
	public MergeVisitor getMergeVisitor() {
		return mergeVisitor;
	}

	public boolean isNull(INode node)
	{
		return node == null;
	}

	public boolean isUnionType(INode node)
	{
		return node instanceof AUnionTypeIR;
	}

	public boolean isVoidType(STypeIR node)
	{
		return node instanceof AVoidTypeIR;
	}
}
