package com.sap.amd.bcpandicp;

import java.io.Serializable;
import java.util.Arrays;

public class Region implements Entity<Region>, Serializable
{
	private static final long serialVersionUID = -6717112666130004338L;

	public static final Region Unknown = new Region("Unknown", new String[]{});
	public static final Region AMER = new Region("AMER", "AI,AG,AW,BS,BB,BQ,VG,KY,CU,CW,DM,DO,GD,GP,HT,JM,MQ,MS,PR,BL,KN,LC,MF,VC,SX,TT,TC,VI,BZ,CR,SV,GT,HN,MX,NI,PA,AR,BO,BR,CL,CO,EC,FR,GF,GY,PY,PE,SR,UY,VE,BM,CA,GL,SP,US".split(","));
	public static final Region EMEA = new Region("EMEA", "PK,AL,AD,BY,BA,HR,FO,GI,GG,IS,JE,KV,LI,MK,MD,MC,ME,NO,RU,SM,RS,SJ,CH,TR,UA,VA,AT,BE,BG,CY,CZ,DK,EE,FI,FR,DE,GR,HU,IE,IT,LV,LT,LU,MT,NL,PL,PT,RO,SK,SL,ES,SE,UK,GB,BH,IQ,IR,IL,JO,KW,LB,OM,PL,QA,SA,SY,AE,YE,BI,KM,DJ,ER,ET,KE,MB,MW,MU,YT,MZ,RE,RW,SC,SO,TZ,UG,ZM,ZW,AO,CM,CF,TD,CG,CD,GQ,GA,ST,DZ,EG,LY,MA,SS,SD,TN,ST,BW,LS,NA,ZA,SZ,BJ,BF,CV,CI,GM,GH,GN,LR,ML,MR,NE,NG,SH,SN,SL,TG".split(","));
	public static final Region APJ = new Region("APJ", "AF,AM,AZ,BD,BT,BN,KH,CN,GE,HK,IN,ID,JP,KZ,KP,KR,KG,LA,MO,MY,MV,MN,MM,NP,PR,PH,SG,LK,TW,TJ,TH,TL,TM,UZ,VN,AU,FJ,PF,GU,KI,MH,FM,NC,NZ,PG,WS,AS,SB,TO,VU".split(","));
	
	private String name;
	private String[] countries;
	
	public Region(String name, String[] countries)
	{
		this.name = name;
		this.countries = countries;
	}

	public String getName()
	{
		return name;
	}

	public String[] getCountries()
	{
		return countries;
	}
	
	public boolean contains(String country)
	{
		for (int i = 0; i < countries.length; i++)
		{
			if (countries[i].equalsIgnoreCase(country))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isEqualTo(Region region)
	{
		return (this.getName().equals(region.getName()) && Arrays.equals(this.getCountries(), region.getCountries()));
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
