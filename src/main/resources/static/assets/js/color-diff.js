(function(){
		var Comparator = function(){};

		Comparator.prototype.deltaE1976 = function(lab1, lab2){
			var delL = lab1.L - lab2.L;
			var dela = lab1.a - lab2.a;
			var delb = lab1.b - lab2.b;
			return Math.sqrt(delL * delL + dela * dela + delb * delb);
		};

		Comparator.prototype.deltaE1994 = function(lab1, lab2, graphicArts){
			var k1 = graphicArts ? 0.045 : 0.048;
			var k2 = graphicArts ? 0.015 : 0.014;
			var kL = graphicArts ? 1.0 : 2.0;
			var kC = 1.0;
			var kH = 1.0;

			var C1 = Math.sqrt(lab1.a * lab1.a + lab1.b * lab1.b);
			var C2 = Math.sqrt(lab2.a * lab2.a + lab2.b * lab2.b);

			var delA = lab1.a - lab2.a;
			var delB = lab1.b - lab2.b;
			var delC = C1 - C2;
			var delH2 = delA * delA + delB * delB - delC * delC;
			var delH = (delH2 > 0.0) ? Math.sqrt(delH2) : 0.0;
			var delL = lab1.L - lab2.L;

			var sL = 1.0;
			var sC = 1.0 + k1 * C1;
			var sH = 1.0 + k2 * C1;

			var vL = delL / (kL * sL);
			var vC = delC / (kC * sC);
			var vH = delH / (kH * sH);

			return Math.sqrt(vL * vL + vC * vC + vH * vH);
		};

		Comparator.prototype.deltaE2000 = function(lab1, lab2){
			var kL = 1.0;
			var kC = 1.0;
			var kH = 1.0;
			var lBarPrime = 0.5 * (lab1.L + lab2.L);
			var c1 = Math.sqrt(lab1.a * lab1.a + lab1.b * lab1.b);
			var c2 = Math.sqrt(lab2.a * lab2.a + lab2.b * lab2.b);
			var cBar = 0.5 * (c1 + c2);
			var cBar7 = cBar * cBar * cBar * cBar * cBar * cBar * cBar;
			var g = 0.5 * (1.0 - Math.sqrt(cBar7 / (cBar7 + 6103515625.0)));	/* 6103515625 = 25^7 */
			var a1Prime = lab1.a * (1.0 + g);
			var a2Prime = lab2.a * (1.0 + g);
			var c1Prime = Math.sqrt(a1Prime * a1Prime + lab1.b * lab1.b);
			var c2Prime = Math.sqrt(a2Prime * a2Prime + lab2.b * lab2.b);
			var cBarPrime = 0.5 * (c1Prime + c2Prime);
			var h1Prime = (Math.atan2(lab1.b, a1Prime) * 180.0) / Math.PI;
			if (h1Prime < 0.0)
				h1Prime += 360.0;
				var h2Prime = (Math.atan2(lab2.b, a2Prime) * 180.0) / Math.PI;
				if (h2Prime < 0.0)
					h2Prime += 360.0;
					var hBarPrime = (Math.abs(h1Prime - h2Prime) > 180.0) ? (0.5 * (h1Prime + h2Prime + 360.0)) : (0.5 * (h1Prime + h2Prime));
					var t = 1.0 -
					0.17 * Math.cos(Math.PI * (      hBarPrime - 30.0) / 180.0) +
					0.24 * Math.cos(Math.PI * (2.0 * hBarPrime       ) / 180.0) +
					0.32 * Math.cos(Math.PI * (3.0 * hBarPrime +  6.0) / 180.0) -
					0.20 * Math.cos(Math.PI * (4.0 * hBarPrime - 63.0) / 180.0);
					if (Math.abs(h2Prime - h1Prime) <= 180.0)
						dhPrime = h2Prime - h1Prime;
						else
							dhPrime = (h2Prime <= h1Prime) ? (h2Prime - h1Prime + 360.0) : (h2Prime - h1Prime - 360.0);
							var dLPrime = lab2.L - lab1.L;
							var dCPrime = c2Prime - c1Prime;
							var dHPrime = 2.0 * Math.sqrt(c1Prime * c2Prime) * Math.sin(Math.PI * (0.5 * dhPrime) / 180.0);
							var sL = 1.0 + ((0.015 * (lBarPrime - 50.0) * (lBarPrime - 50.0)) / Math.sqrt(20.0 + (lBarPrime - 50.0) * (lBarPrime - 50.0)));
							var sC = 1.0 + 0.045 * cBarPrime;
							var sH = 1.0 + 0.015 * cBarPrime * t;
							var dTheta = 30.0 * Math.exp(-((hBarPrime - 275.0) / 25.0) * ((hBarPrime - 275.0) / 25.0));
							var cBarPrime7 = cBarPrime * cBarPrime * cBarPrime * cBarPrime * cBarPrime * cBarPrime * cBarPrime;
							var rC = Math.sqrt(cBarPrime7 / (cBarPrime7 + 6103515625.0));
							var rT = -2.0 * rC * Math.sin(Math.PI * (2.0 * dTheta) / 180.0);
							return Math.sqrt(
								(dLPrime / (kL * sL)) * (dLPrime / (kL * sL)) +
								(dCPrime / (kC * sC)) * (dCPrime / (kC * sC)) +
								(dHPrime / (kH * sH)) * (dHPrime / (kH * sH)) +
								(dCPrime / (kC * sC)) * (dHPrime / (kH * sH)) * rT);
		};

		Comparator.prototype.deltaECMC = function(lab1, lab2, L, C){
			var c1 = Math.sqrt(lab1.a * lab1.a + lab1.b * lab1.b);
			var c2 = Math.sqrt(lab2.a * lab2.a + lab2.b * lab2.b);
			var sl = (lab1.L < 16.0) ? (0.511) : ((0.040975 * lab1.L) / (1.0 + 0.01765 * lab1.L));
			var sc = (0.0638 * c1) / (1.0 + 0.0131 * c1) + 0.638;
			var h1 = (c1 < 0.000001) ? 0.0 : ((Math.atan2(lab1.b, lab1.a) * 180.0) / Math.PI);
			while (h1 < 0.0)
				h1 += 360.0;
				while (h1 >= 360.0)
					h1 -= 360.0;
					var t = ((h1 >= 164.0) && (h1 <= 345.0)) ? (0.56 + Math.abs(0.2 * Math.cos((Math.PI * (h1 + 168.0)) / 180.0))) : (0.36 + Math.abs(0.4 * Math.cos((Math.PI * (h1 + 35.0)) / 180.0)));
					var c4 = c1 * c1 * c1 * c1;
					var f = Math.sqrt(c4 / (c4 + 1900.0));
					var sh = sc * (f * t + 1.0 - f);
					var delL = lab1.L - lab2.L;
					var delC = c1 - c2;
					var delA = lab1.a - lab2.a;
					var delB = lab1.b - lab2.b;
					var dH2 = delA * delA + delB * delB - delC * delC;
					var v1 = delL / (L * sl);
					var v2 = delC / (C * sc);
					var v3 = sh;
					return Math.sqrt(v1 * v1 + v2 * v2 + (dH2 / (v3 * v3)));
		};

		window.colorComparator = new Comparator();
})();
