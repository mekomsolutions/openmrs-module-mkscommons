/**
 * 
 */
package org.openmrs.module.mkscommons;

import java.util.Date;

/**
 * @author Kamonyo
 *
 *         Helps with rendering coordinates of Concepts composing Vital Signs
 *         with last 24 hours values:
 *         Where <code>x</code> is the Obs Datetime
 *         and <code>y</code> is the Numeric value at that date & time
 */
public class ChartPoint {

	public Date x;
	public Double y;
	
	public ChartPoint(Date x, Double y) {
		this.x = x;
		this.y = y;
	}

	public Date getX() {
		return x;
	}

	public void setX(Date x) {
		this.x = x;
	}

	public Double getY() {
		return y;
	}

	public void setY(Double y) {
		this.y = y;
	}
	
}
