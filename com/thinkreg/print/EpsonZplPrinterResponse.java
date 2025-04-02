/*
 * Copyright 2025 Robert James Drabant II, ThinkREG
 * 
 * Created because I wanted a cleaner way to print to our Epsons we use in 
 * conference and event management. We have mostly 4000s, but this should 
 * work with any Epson that supports ZPLII/ESC. I also want to thank team 
 * members at Epson for their support. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thinkreg.print;

import com.thinkreg.boot.util.WRPUtils;

public class EpsonZplPrinterResponse{
	
	private String black;
	private String cyan;
	private String magenta;
	private String yellow;
	private String maintenance;
	private String message;
	private Boolean success = false;
	

	

	public String getBlack() {return black;}
	public void setBlack(String black) {this.black = black;}

	public String getCyan() {return cyan;}
	public void setCyan(String cyan) {this.cyan = cyan;}

	public String getMagenta() {return magenta;}
	public void setMagenta(String magenta) {this.magenta = magenta;}

	public String getYellow() {return yellow;}
	public void setYellow(String yellow) {this.yellow = yellow;}

	public String getMaintenance() {return maintenance;}
	public void setMaintenance(String maintenance) {this.maintenance = maintenance;}

	public String getMessage() {return message;}
	public void setMessage(String message) {this.message = message;}

	public Boolean getSuccess() {return success;}
	public void setSuccess(Boolean success) {this.success = success;}

	/**
	 * 	Epson does not publish what these equate to in percent, so this is an educated guess 
	 * for ink level or maintenance kit 
	 *  <STX> IQ, <remaining black ink>, <remaining cyan ink>,<remaining magenta ink>, <remaining
		yellow ink><ETX><CR><LF>
		RH Enough ink in cartridge
    	RM Moderate ink in cartridge
    	RL Small ink in cartridge
    	RN Ink cartridge low
    	RR Replace Ink cartridge
    	NA Ink cartridge not installed
    	CI Ink cartridge installed
	 * @param code
	 * @return
	 */
	public static Integer getPercentForCode(String code) {
		
	//	RH Enough ink in cartridge
	//	RM Moderate ink in cartridge
	//	RL Small ink in cartridge
	//	RN Ink cartridge low
	//	RR Replace Ink cartridge
	//	NA Ink cartridge not installed
	//	CI Ink cartridge installed
		if(WRPUtils.isEmpty(code)){
			return null;
		}
		
		if(code.equals("RH")){
			return 100;
		}
	
		if(code.equals("RM")){
			return 60;
		}
		
		if(code.equals("RL")){
			return 30;
		}
	
		if(code.equals("RN")){
			return 10;
		}
	
		if(code.equals("RR")){
			return 00;
		}
		
		return null;
	
	}
	
	
	
}
