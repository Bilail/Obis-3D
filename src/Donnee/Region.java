package Donnee;

import java.math.BigDecimal;

public class Region {
	
	private BigDecimal[] p1;
	private BigDecimal[] p2;
	private BigDecimal[] p3;
	private BigDecimal[] p4;
	
	public Region(BigDecimal[] p1, BigDecimal[] p2, BigDecimal[] p3, BigDecimal[] p4) {
		this.p1=p1;
		this.p2=p2;
		this.p3=p3;
		this.p4=p4;
	}
	
	public BigDecimal[][] getPoints() {
		BigDecimal[][] points = {p1, p2, p3, p4};
		return points;
	}
}
