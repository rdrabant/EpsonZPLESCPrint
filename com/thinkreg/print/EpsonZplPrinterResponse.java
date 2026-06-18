/*
 * Copyright 2025 Robert James Drabant II, ThinkREG
 * https://www.linkedin.com/in/robert-drabant/
 * 
 * This code was independently developed by ThinkREG for use with Epson printer ColorWorks CW-C4000. 
 * Neither ThinkREG or Epson America, Inc. provide any warranties or support for this code. 
 * “Epson” is the registered trademark of Epson America, Inc. and its affiliates in the 
 * United States and other countries. Epson reserves all rights to its trademarks. 
 * Epson and ThinkREG are independent companies.
 *
 * From the author, this was created because I wanted a cleaner way to print to our Epsons we use in 
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


public class EpsonZplPrinterResponse{
	
	private String black;
	private String cyan;
	private String magenta;
	private String yellow;
	private String maintenance;
	private String message;
	private ERROR_CODE errorCode = null;
	private Boolean success = false;
	//private boolean errorBlocksPrinting = false;
	private boolean successfulConnection = false;
	

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
	
	public boolean isSuccessfulConnection() {return successfulConnection;}
	public void setSuccessfulConnection(boolean successfulConnection) throws Exception {
		if(!successfulConnection) {
			throw new Exception("SET TO FALSE? Why");
		}
		this.successfulConnection = successfulConnection;
	}
	
	/**
	 * Returns the error code, or null if unble to connect
	 * @return
	 */
	public ERROR_CODE getErrorCode() {return errorCode;}
	public void setErrorCode(ERROR_CODE errorCode) {
		this.errorCode = errorCode;
	}
	
	public void setErrorCode(String errorCode) {
//		if(errorCode == null || errorCode.trim().length() == 0) {
//			this.errorCode = ERROR_CODE.NE;
//			return;
//		}
		
		if(errorCode != null) {
			for(ERROR_CODE code: ERROR_CODE.values()) {
				if(code.getCode().equals(errorCode)) {
					this.errorCode = code;
				}
			}
		}
		
		if(this.errorCode == null) {
			this.errorCode = ERROR_CODE.UNKNOWN;
		}
		
	}
	
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
		if(code == null || code.trim().length() == 0){
			return null;
		}else if(code.equals("RH")){
			return 100;
		}else if(code.equals("RM")){
			return 60;
		}else if(code.equals("RL")){
			return 30;
		}else if(code.equals("RN")){
			return 10;
		}else if(code.equals("RR")){
			return 00;
		}
		
		return null;
	}
	
	
//	public String getErrorMessage() {
//				
//		//esc printer language guide
////		NE: No error
////		FE: Fatal error
////		NI: Interface not selected error
////		CO: Cover open error (Roll cover)
////		IE: Replace Ink cartridge, or No Ink cartridge error
////		SJ: Paper jam error
////		SN: Paper out error
////		MF: Replace maintenance box error
////		SS: Media size error
////		ST: Media source error
////		SR: Paper recognition error
////		CI: Ink cartridge cover open error
////		MN: No maintenance box error
////		CM: Maintenance box cover open error
////		SE: Paper removal error
////		LT: Maintenance error (tube life)
////		CS: Paper cover open error
////		CF: Front cover open error
////		CR: Release lever open error
////		CG: Guide unit open error
////		SC: Sensor calibration error
////		IC: Cleaning not available due to
////		low remaining ink
////		MC: Cleaning not available due to insufficient waste ink capacity
//		
//		//4000 specific
//		//NE No error
////		FE Fatal error
////		CO Cover open error (paper cover) *1
////		IE Replace Ink cartridge, or No Ink cartridge error
////		SJ Paper jam error
////		SN Paper out error
////		MF Replace maintenance box error
////		SS Media size error
////		ST Media source error
////		SR Paper recognition error
////		CI Ink cartridge cover open error
////		MN No maintenance box error
////		CM Maintenance box cover open error
////		SE Paper removal error
////		LT Maintenance error (tube life)
////		SC Sensor calibration error
////		IC Cleaning not available due to low remaining ink
////		MC Cleaning not available due to insufficient waste ink capacity
//		
//		if(!this.isSuccessfulConnection()) {
//			return "Unable to connect to printer";
//		}
//		
//			if(this.getErrorCode() == null || this.getErrorCode().trim().length() == 0){
//				return null;
//			}else if(this.getErrorCode().equals("NE")){
//				return "No error";
//			}else if(this.getErrorCode().equals("FE")){
//				return "Fatal error";
//			}else if(this.getErrorCode().equals("NI")){
//				return "Interface not selected error";
//			}else if(this.getErrorCode().equals("CO")){
//				return "Cover open error (Roll cover)";
//			}else if(this.getErrorCode().equals("IE")){
//				return "Replace Ink cartridge, or No Ink cartridge error";
//			}else if(this.getErrorCode().equals("SN")){
//				return "Paper out error";
//			}else if(this.getErrorCode().equals("MF")){
//				return "Replace maintenance box error";
//			}else if(this.getErrorCode().equals("SS")){
//				return "Media size error";
//			}else if(this.getErrorCode().equals("ST")){
//				return "Media source error";
//			}else if(this.getErrorCode().equals("SR")){
//				return "Paper recognition error";
//			}else if(this.getErrorCode().equals("CI")){
//				return "Ink cartridge cover open error";
//			}else if(this.getErrorCode().equals("MN")){
//				return "No maintenance box error";
//			}else if(this.getErrorCode().equals("CM")){
//				return "Maintenance box cover open error";
//			}else if(this.getErrorCode().equals("SE")){
//				return "Paper removal error";
//			}else if(this.getErrorCode().equals("LT")){
//				return "Maintenance error (tube life)";
//			}else if(this.getErrorCode().equals("CS")){
//				return "Paper cover open error";
//			}else if(this.getErrorCode().equals("CF")){
//				return "Front cover open error";
//			}else if(this.getErrorCode().equals("CR")){
//				return "Release lever open error";
//			}else if(this.getErrorCode().equals("CG")){
//				return "Guide unit open error";
//			}else if(this.getErrorCode().equals("SC")){
//				return "Sensor calibration error";
//			}else if(this.getErrorCode().equals("IC")){
//				return "Cleaning not available due to low remaining ink";
//			}else if(this.getErrorCode().equals("MC")){
//				return "Cleaning not available due to insufficient waste ink capacity";
//			}
//			
//			return null;
//		
//		}
	
	public enum ERROR_CODE{
	
		
		//esc printer language guide
//		NE: No error
//		FE: Fatal error
//		NI: Interface not selected error
//		CO: Cover open error (Roll cover)
//		IE: Replace Ink cartridge, or No Ink cartridge error
//		SJ: Paper jam error
//		SN: Paper out error
//		MF: Replace maintenance box error
//		SS: Media size error
//		ST: Media source error
//		SR: Paper recognition error
//		CI: Ink cartridge cover open error
//		MN: No maintenance box error
//		CM: Maintenance box cover open error
//		SE: Paper removal error
//		LT: Maintenance error (tube life)
//		CS: Paper cover open error
//		CF: Front cover open error
//		CR: Release lever open error
//		CG: Guide unit open error
//		SC: Sensor calibration error
//		IC: Cleaning not available due to
//		low remaining ink
//		MC: Cleaning not available due to insufficient waste ink capacity
		
		//4000 specific
		//NE No error
//		FE Fatal error
//		CO Cover open error (paper cover) *1
//		IE Replace Ink cartridge, or No Ink cartridge error
//		SJ Paper jam error
//		SN Paper out error
//		MF Replace maintenance box error
//		SS Media size error
//		ST Media source error
//		SR Paper recognition error
//		CI Ink cartridge cover open error
//		MN No maintenance box error
//		CM Maintenance box cover open error
//		SE Paper removal error
//		LT Maintenance error (tube life)
//		SC Sensor calibration error
//		IC Cleaning not available due to low remaining ink
//		MC Cleaning not available due to insufficient waste ink capacity
		
		NE ("NE", false, "No error"),
		FE ("FE", true, "Fatal error"),
		NI ("NI", true, "Interface not selected error"),
		CO ("CO", true, "Cover open error (paper cover)"),
		IE ("IE", true, "Replace Ink cartridge, or No Ink cartridge error"),
		SJ ("SJ", true, "Paper jam error"),
		SN ("SN", true, "Paper out error"),
		MF ("MF", true, "Replace maintenance box error"),
		SS ("SS", true, "Media size error"),
		ST ("ST", true, "Media source error"),
		SR ("SR", true, "Paper recognition error"),
		CI ("CI", true, "Ink cartridge cover open error"),
		MN ("MN", true, "No maintenance box error"),
		CM ("CM", true, "Maintenance box cover open error"),
		SE ("SE", true, "Paper removal error"),
		LT ("LT", true, "Maintenance error (tube life)"),
		CS ("CS", true, "Paper cover open error"),
		CF ("CF", true, "Front cover open error"),
		CR ("CR", true, "Release lever open error"),
		CG ("CG", true, "Guide unit open error"),
		SC ("SC", true, "Sensor calibration error"),
		IC ("IC", false, "Cleaning not available due to low remaining ink"),
		MC ("MC", false, "Cleaning not available due to insufficient waste ink capacity"),
		UNKNOWN ("UNKNOWN", true, "UNKNOWN ERROR");
		
		
		private final String code;
		private final boolean blocksPrinting;   
		private final String message;  
		
		
	    
		ERROR_CODE(String code, boolean blocksPrinting, String message) {
	       this.code = code;
	       this.blocksPrinting = blocksPrinting;
	       this.message = message;
	    }

		public String getCode() {return code;}

		public boolean isBlocksPrinting() {return blocksPrinting;}

		public String getMessage() {return message;}
	    
		
	}
}
