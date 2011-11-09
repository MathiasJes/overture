//
// THIS FILE IS AUTOMATICALLY GENERATED!!
//
// Generated at Mon 07-Jul-2008 by the VDM++ to JAVA Code Generator
// (v8.1.1b - Fri 06-Jun-2008 09:02:11)
//
// Supported compilers:
// jdk1.4
//

// ***** VDMTOOLS START Name=HeaderComment KEEP=NO
// ***** VDMTOOLS END Name=HeaderComment

// ***** VDMTOOLS START Name=package KEEP=NO
package org.overturetool.jml.ast.imp;

// ***** VDMTOOLS END Name=package

// ***** VDMTOOLS START Name=imports KEEP=YES

import jp.co.csk.vdm.toolbox.VDM.*;
import java.util.*;
import org.overturetool.jml.ast.itf.*;
@SuppressWarnings(all) 
// ***** VDMTOOLS END Name=imports



public class JmlUnaryExpression extends JmlExpression implements IJmlUnaryExpression {

// ***** VDMTOOLS START Name=vdmComp KEEP=NO
  static UTIL.VDMCompare vdmComp = new UTIL.VDMCompare();
// ***** VDMTOOLS END Name=vdmComp

// ***** VDMTOOLS START Name=ivOperator KEEP=NO
  private IJmlUnaryOperator ivOperator = null;
// ***** VDMTOOLS END Name=ivOperator

// ***** VDMTOOLS START Name=ivExpression KEEP=NO
  private IJmlExpression ivExpression = null;
// ***** VDMTOOLS END Name=ivExpression


// ***** VDMTOOLS START Name=JmlUnaryExpression KEEP=NO
  public JmlUnaryExpression () throws CGException {
    try {

      ivOperator = null;
      ivExpression = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
  }
// ***** VDMTOOLS END Name=JmlUnaryExpression


// ***** VDMTOOLS START Name=identity KEEP=NO
  public String identity () throws CGException {
    return new String("UnaryExpression");
  }
// ***** VDMTOOLS END Name=identity


// ***** VDMTOOLS START Name=accept KEEP=NO
  public void accept (final IJmlVisitor pVisitor) throws CGException {
    pVisitor.visitUnaryExpression((IJmlUnaryExpression) this);
  }
// ***** VDMTOOLS END Name=accept


// ***** VDMTOOLS START Name=JmlUnaryExpression KEEP=NO
  public JmlUnaryExpression (final IJmlUnaryOperator p1, final IJmlExpression p2) throws CGException {

    try {

      ivOperator = null;
      ivExpression = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
    {

      setOperator((IJmlUnaryOperator) p1);
      setExpression((IJmlExpression) p2);
    }
  }
// ***** VDMTOOLS END Name=JmlUnaryExpression


// ***** VDMTOOLS START Name=JmlUnaryExpression KEEP=NO
  public JmlUnaryExpression (final IJmlUnaryOperator p1, final IJmlExpression p2, final Long line, final Long column) throws CGException {

    try {

      ivOperator = null;
      ivExpression = null;
    }
    catch (Exception e){

      e.printStackTrace(System.out);
      System.out.println(e.getMessage());
    }
    {

      setOperator((IJmlUnaryOperator) p1);
      setExpression((IJmlExpression) p2);
      setPosition(line, column);
    }
  }
// ***** VDMTOOLS END Name=JmlUnaryExpression


// ***** VDMTOOLS START Name=init KEEP=NO
  public void init (final HashMap data) throws CGException {

    {

      String fname = new String("operator");
      Boolean cond_4 = null;
      cond_4 = new Boolean(data.containsKey(fname));
      if (cond_4.booleanValue()) 
        setOperator((IJmlUnaryOperator) data.get(fname));
    }
    {

      String fname = new String("expression");
      Boolean cond_13 = null;
      cond_13 = new Boolean(data.containsKey(fname));
      if (cond_13.booleanValue()) 
        setExpression((IJmlExpression) data.get(fname));
    }
  }
// ***** VDMTOOLS END Name=init


// ***** VDMTOOLS START Name=getOperator KEEP=NO
  public IJmlUnaryOperator getOperator () throws CGException {
    return (IJmlUnaryOperator) ivOperator;
  }
// ***** VDMTOOLS END Name=getOperator


// ***** VDMTOOLS START Name=setOperator KEEP=NO
  public void setOperator (final IJmlUnaryOperator parg) throws CGException {
    ivOperator = (IJmlUnaryOperator) UTIL.clone(parg);
  }
// ***** VDMTOOLS END Name=setOperator


// ***** VDMTOOLS START Name=getExpression KEEP=NO
  public IJmlExpression getExpression () throws CGException {
    return (IJmlExpression) ivExpression;
  }
// ***** VDMTOOLS END Name=getExpression


// ***** VDMTOOLS START Name=setExpression KEEP=NO
  public void setExpression (final IJmlExpression parg) throws CGException {
    ivExpression = (IJmlExpression) UTIL.clone(parg);
  }
// ***** VDMTOOLS END Name=setExpression
  
  public String toString() {
	  
	  String expr1 = this.ivExpression.toString();
	  String op = this.ivOperator.toString();
	  
	  if(this.ivOperator.hasArgs()) {
		  
		  return op + "(" + expr1 + ")";
	  }
	  else {
		  
		  return expr1 + "." + op + "()";
	  }
	  
	  
  }

}
;
