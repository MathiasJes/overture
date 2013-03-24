/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:12
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Line.java

package org.overture.ide.plugins.rttraceviewer.draw;

import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Point;

public class Line extends Polyline implements TraceFigure
{

    public Line(Long px1, Long py1, Long px2, Long py2)
    {
    	
        addPoint(new Point(px1.intValue(), py1.intValue()));
        addPoint(new Point(px2.intValue(), py2.intValue()));
    }

    public void setLineWidth(Long width)
    {
        setLineWidth(width.intValue());
    }

    public void setSolid()
    {
        setLineStyle(1);
    }

    public void setDot()
    {
        setLineStyle(3);
    }

    public void setDash()
    {
        setLineStyle(2);
    }

    public void setDashDot()
    {
        setLineStyle(4);
    }

    public void setDashDotDot()
    {
        setLineStyle(5);
    }

	@Override
	public void dispose()
	{
	}
}
