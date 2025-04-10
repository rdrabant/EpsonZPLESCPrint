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


public class EpsonZplPrinterResponse{
	
	private String black;
	private String cyan;
	private String magenta;
	private String yellow;
	private String maintenance;
	private String message;
	private String errorStatus;
	private Boolean success = false;
	private boolean errorBlocksPrinting = false;
	

	
	public boolean getErrorBlocksPrinting() {return errorBlocksPrinting;}
	public void setErrorBlocksPrinting(boolean errorBlocksPrinting) {
		this.errorBlocksPrinting = errorBlocksPrinting;
	}
	
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
	
	public String getErrorStatus() {return errorStatus;}
	public void setErrorStatus(String errorStatus) {
		
		this.errorStatus = errorStatus;
	
		if(errorStatus == null || errorStatus.trim().length() == 0){
			//no action
		}else if(errorStatus.equals("FE")){
			this.setErrorBlocksPrinting(true);
		}else if(errorStatus.equals("NI")){
			this.setErrorBlocksPrinting(true);
		}else if(errorStatus.equals("CO")){
			this.setErrorBlocksPrinting(true);
		}else if(errorStatus.equals("IE")){
			this.setErrorBlocksPrinting(true);
		}else if(errorStatus.equals("SN")){
			this.setErrorBlocksPrinting(true);
		}else if(errorStatus.equals("MF")){
			this.setErrorBlocksPrinting(true);
		}else if(errorStatus.equals("SS")){
			this.setErrorBlocksPrinting(true);
		}else if(errorStatus.equals("ST")){
			this.setErrorBlocksPrinting(true);
		}else if(errorStatus.equals("SR")){
			this.setErrorBlocksPrinting(true);
		}else if(errorStatus.equals("CI")){
			this.setErrorBlocksPrinting(true);
		}else if(errorStatus.equals("MN")){
			this.setErrorBlocksPrinting(true);
		}else if(errorStatus.equals("CM")){
			this.setErrorBlocksPrinting(true);
		}else if(errorStatus.equals("SE")){
			this.setErrorBlocksPrinting(true);
		}else if(errorStatus.equals("LT")){
			this.setErrorBlocksPrinting(true);
		}else if(errorStatus.equals("CS")){
			this.setErrorBlocksPrinting(true);
		}else if(errorStatus.equals("CF")){
			this.setErrorBlocksPrinting(true);
		}else if(errorStatus.equals("CR")){
			this.setErrorBlocksPrinting(true);
		}else if(errorStatus.equals("CG")){
			this.setErrorBlocksPrinting(true);
		}else if(errorStatus.equals("SC")){
			this.setErrorBlocksPrinting(true);
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
	
	
	public static String getErrorMessage(String code) {
		
//		2-character ASCII string
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
		
			if(code == null || code.trim().length() == 0){
				return null;
			}else if(code.equals("NE")){
				return "No error";
			}else if(code.equals("FE")){
				return "Fatal error";
			}else if(code.equals("NI")){
				return "Interface not selected error";
			}else if(code.equals("CO")){
				return "Cover open error (Roll cover)";
			}else if(code.equals("IE")){
				return "Replace Ink cartridge, or No Ink cartridge error";
			}else if(code.equals("SN")){
				return "Paper out error";
			}else if(code.equals("MF")){
				return "Replace maintenance box error";
			}else if(code.equals("SS")){
				return "Media size error";
			}else if(code.equals("ST")){
				return "Media source error";
			}else if(code.equals("SR")){
				return "Paper recognition error";
			}else if(code.equals("CI")){
				return "Ink cartridge cover open error";
			}else if(code.equals("MN")){
				return "No maintenance box error";
			}else if(code.equals("CM")){
				return "Maintenance box cover open error";
			}else if(code.equals("SE")){
				return "Paper removal error";
			}else if(code.equals("LT")){
				return "Maintenance error (tube life)";
			}else if(code.equals("CS")){
				return "Paper cover open error";
			}else if(code.equals("CF")){
				return "Front cover open error";
			}else if(code.equals("CR")){
				return "Release lever open error";
			}else if(code.equals("CG")){
				return "Guide unit open error";
			}else if(code.equals("SC")){
				return "Sensor calibration error";
			}else if(code.equals("IC")){
				return "Cleaning not available due to low remaining ink";
			}else if(code.equals("MC")){
				return "Cleaning not available due to insufficient waste ink capacity";
			}
			
			return null;
		
		}
}
