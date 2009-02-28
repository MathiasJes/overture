/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overturetool.vdmj.pog;

import java.util.Vector;

@SuppressWarnings("serial")
public class ProofObligationList extends Vector<ProofObligation>
{
	// Convenience class to hold lists of POs.

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		int n = 1;

		for (ProofObligation po: this)
		{
			sb.append("Proof Obligation ");
			sb.append(n++);
			sb.append(":\n");
			sb.append(po);
			sb.append("\n");
		}

		return sb.toString();
	}
}
