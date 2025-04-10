/*
 * Copyright 2025 Robert James Drabant II, ThinkREG
 * https://www.linkedin.com/in/robert-drabant/
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

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.thinkreg.boot.util.WRPUtils;


public class EpsonZplPrintJob{
	
	public static final byte[] COMMAND_SAVE_TO_NON_VOLATILE_MEMORY = "^JU".getBytes();
	
	
	private DPI dpi = null;
	private LABEL_EDGE_DETECTION labelEdgeDetection = null;
	private FEED_AND_CUT_MODE feedAndCutMode = null;
	private PRINT_QUALITY printQuality = null;
	private UNITS_OF_MEASUREMENT unitsOfMeasurement = null;
	
	private List<BufferedImage> images = new ArrayList<BufferedImage>();
	private String ip;
	private int port;
	private Float leadingEdgeAdjustment;
	private Float leftEdgeAdj;
	private BigDecimal labelHeight = null;
	private BigDecimal labelWidth = null;
	private Integer cloggedNozzles;
	private Integer nozzleCheckLabel;
	
	
	public EpsonZplPrintJob(String ip, int port){
		this.ip = ip;
		this.port = port;
	}
	
	public EpsonZplPrintJob(String ip, int port, List<BufferedImage> images){
		this.ip = ip;
		this.port = port;
		this.images = images;
	}
	

	

	/**
	 * If you have buffered images set, this will send them to the printer. 
	 * @return 
	 * @throws IOException
	 */
	public EpsonZplPrinterResponse print() throws IOException {
		//long start = System.currentTimeMillis();
		
		EpsonZplPrinterResponse response = null;
        
		for(BufferedImage image: images) {
			//int width = image.getWidth();
			//int height = image.getHeight();
			
			//height = height - 100;
			
			ByteArrayOutputStream pngBaos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", pngBaos);//write the image as a PNG to memory
			pngBaos.flush();
		        
			byte[] pngBytes = pngBaos.toByteArray();
	        pngBaos.close();

	        //System.out.println("IMAGE WIDTH: " + width + " HEIGHT: " + height);
	        
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        
	        baos.writeBytes("^XA^IDR:*.*^FS^XZ".getBytes()); //1. Delete the files from the printer in case we have stuff left over.
	        baos.writeBytes("\r".getBytes());
	        
	        //set paper size, cutter, dpi, save it
	        baos.writeBytes("^XA".getBytes());
//	        baos.writeBytes("^S(CLR, R, 600".getBytes()); //Sets the rendering resolution at 600 [dpi].
//	        baos.writeBytes("^S(CLR, P, 600".getBytes()); //Sets the printing resolution at 600 [dpi]
	        //baos.writeBytes(("^MU I, " + DPI + ", " + DPI).getBytes()); //Sets units to inches, rendering res, printing res
	        
	        
    		
	        //baos.writeBytes("^S(CLS, P, 2400".getBytes()); //Sets the label width at 4 inches.
    		baos.writeBytes("^XZ".getBytes());
	        
	        baos.writeBytes("\r".getBytes());
	        baos.writeBytes("~DYR:BADGE,B,P,".getBytes());//send the image to the printer memory
	        baos.writeBytes(String.valueOf(pngBytes.length).getBytes());
	        baos.writeBytes(",0,".getBytes());
	        baos.writeBytes(pngBytes);
	        
	        
	        baos.writeBytes("\r".getBytes());
	        baos.writeBytes("^XA".getBytes());
	       // baos.writeBytes("^MMC".getBytes()); //Media command to cut label after print
	//	    baos.writeBytes("\r".getBytes());
	        
	        baos.writeBytes("^FO0,0^IMR:BADGE.PNG^FS".getBytes());	// 3. Arrange the graphic in the position (0,0).
	        baos.writeBytes("^XZ".getBytes());
	        baos.writeBytes("\r".getBytes());

	        baos.writeBytes("^XA ^IDR:*.*^FS^XZ".getBytes()); 		//4. Delete the files from the printer.
	        
	        
	        
//	        File badgeAsFileZpl = new File("D:\\BadgeAsFile.zpl");
	        
	        		
	        try /*(BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(badgeAsFileZpl));
    		)*/{
							
	        	byte[] zplBytes = baos.toByteArray();
	        	//fos.write(zplBytes);
//					long currentTime = System.currentTimeMillis();
				
	        	return sendZpl(zplBytes, this.getIp(), 9100);
	        }catch(Exception ex) {
	        	response = new EpsonZplPrinterResponse();
	        	response.setSuccess(false);
	        	response.setMessage(ex.getMessage());
	        	ex.printStackTrace();
	        }
			
	        baos = null;
	        
		}
		
		if(response == null) {
			response = new EpsonZplPrinterResponse();
			response.setMessage("unknown error");
		}
		
		return response;
	}
	
	
	public EpsonZplPrinterResponse calibrate() throws IOException {
		    
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        
		baos.writeBytes("^XA~JC^XZ".getBytes()); 
		baos.writeBytes("\r".getBytes());
	   
		EpsonZplPrinterResponse response = null;
	    
		try {
							
			byte[] zplBytes = baos.toByteArray();
				
			response = sendZpl(zplBytes, this.ip, this.port);
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
			
	        
		baos = null;
		
		if(response == null) {
			response = new EpsonZplPrinterResponse();
			response.setMessage("unknown error");
		}
		
		return response;

	}
	
	
	/**
	 * Pause the printer
	 * @throws IOException
	 */
	public void pausePrinter() throws IOException {
		//long start = System.currentTimeMillis();
		    
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        
		baos.writeBytes("^XA^PP^XZ".getBytes()); 
		baos.writeBytes("\r".getBytes());
	        
		try {
							
			byte[] zplBytes = baos.toByteArray();
				
			sendZpl(zplBytes, this.ip, this.port);
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
			
		baos = null;

	}
	
	/**
	 * UnPause the printer
	 * @throws IOException
	 */
	public void cancelPausePrinter() throws IOException {
		//long start = System.currentTimeMillis();
		    
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        
		baos.writeBytes("^XA~PS^XZ".getBytes()); 
		baos.writeBytes("\r".getBytes());
	        
		try {
							
			byte[] zplBytes = baos.toByteArray();
				
			sendZpl(zplBytes, this.ip, this.port);
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
			
		baos = null;

	}
	
	
	/**
	 * pushes mark type, left edge, top edge adjustments to printer
	 * @return 
	 * @throws Exception 
	 */
	public EpsonZplPrinterResponse updatePrinterSettings() throws Exception {
		//long start = System.currentTimeMillis();
		    
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		baos.writeBytes("^XA\r".getBytes()); 
		
		//https://files.support.epson.com/pdf/pos/bulk/cw-c4000_esclabel_crg_en_revc.pdf
		//~H(CLE,b
//		b=L: Logical label left edge position adjustment [dot]
//		b=M: Physical label left edge position adjustment [dot]
//		b=T: Physical label leading edge
//		position adjustment [dot]
		//baos.writeBytes("^S(CMP,U,I \r".getBytes()); //1. Sets units to inches
		baos.writeBytes(UNITS_OF_MEASUREMENT.DOTS.getZPL(dpi).getBytes()); //1. Sets units to dots

		//https://files.support.epson.com/pdf/pos/bulk/cw-c4000_esclabel_crg_en_revc.pdf
		if(this.getLeadingEdgeAdjustment() != null/*&& 
				floatVal.isValid(job.getLeadingEdgeAdjustemnet()*/) {
			
			//int dots = AWTUtils.inchesToPoints(Float.valueOf(job.getLeadingEdgeAdjustemnet()), DPI);
//			baos.writeBytes(("~H(CLE,T" + dots +  "\r").getBytes()); //2. top edge adjustment
			//baos.writeBytes(("~H(CLE,T" + 10 +  "\r").getBytes()); //2. top edge adjustment
			baos.writeBytes(("^S(CLE,T," + this.getLeadingEdgeAdjustment() +  "\r").getBytes()); //T: Physical label leading edge	position adjustment [dot]
			//+/-258
		}
		
		if(this.getLeftEdgeAdj() != null) {
			
			baos.writeBytes(("^S(CLE,M," + this.getLeftEdgeAdj() +  "\r").getBytes()); //M: Physical label left edge position adjustment [dot]
			//+/-36
		}
		
		if(this.getNozzleCheckLabel() != null && this.getNozzleCheckLabel() > 0) {
			baos.writeBytes(("^S(CMV,I," + this.getNozzleCheckLabel()  +  "\r").getBytes()); //M: Physical label left edge position adjustment [dot]
		}

		if(this.getCloggedNozzles() != null && this.getCloggedNozzles() > 0) {
			baos.writeBytes(("^S(CMV,C," + this.getCloggedNozzles()  +  "\r").getBytes()); //M: Physical label left edge position adjustment [dot]
		}
		
		if(this.getFeedAndCutMode() != null) {
			
			baos.writeBytes(this.getFeedAndCutMode().getZPL().getBytes()); 
		}
		
		if(this.getDpi() != null) {
			baos.writeBytes(this.getDpi().getZPL().getBytes());
		}
		
		if(this.getPrintQuality() !=null) {
			baos.writeBytes(this.getPrintQuality().getZPL().getBytes()); //Q: Print quality
		}
		
		if(this.getLabelEdgeDetection() != null) {
			baos.writeBytes(this.getLabelEdgeDetection().getZPL().getBytes()); //1. Set label detection to gap, blackmark, or none
		}
		
		if(this.getUnitsOfMeasurement() != null) {
			baos.writeBytes(this.getUnitsOfMeasurement().getZPL(dpi).getBytes()); 
		}

//		if(this.getUnitsOfMeasurement() == null) {
//			System.out.println("Unit's of measurement: null");
//		}else {
//			System.out.println("Unit's of measurement: " + this.getUnitsOfMeasurement());
//		}
//		System.out.println("getLabelHeight: " + this.getLabelHeight());
//		System.out.println("getLabelWidth: " + this.getLabelWidth());

		if(this.getLabelHeight() != null &&
        		this.getUnitsOfMeasurement() != null) {
        	
        	//baos.writeBytes(this.getUnitsOfMeasurement().getZPL()).getBytes()); 
        	baos.writeBytes(UNITS_OF_MEASUREMENT.DOTS.getZPL().getBytes());
			//b=L: Label width [dot]
        	
        	if(this.getUnitsOfMeasurement() == UNITS_OF_MEASUREMENT.INCHES && this.getDpi() != null) {
        		int dots = new BigDecimal(this.getDpi().getResolution()).multiply(this.getLabelHeight()).intValue();
        		baos.writeBytes(("^S(CLS,L," + dots + "\r").getBytes()); //Sets label length in inches
            }else if(this.getUnitsOfMeasurement() == UNITS_OF_MEASUREMENT.MILLIMETERS && this.getDpi() != null) {
        		
            	BigDecimal dots = new BigDecimal(this.getDpi().getResolution()).multiply(this.getLabelHeight());
            	dots = dots.divide(new BigDecimal(25.4));
        		baos.writeBytes(("^S(CLS,L," + dots + "\r").getBytes()); //Sets label length in inches
            }else{
            	baos.writeBytes(("^S(CLS,L," + this.getLabelHeight() + "\r").getBytes()); //Sets label length in inches
            }
        }
		
		if(this.getLabelWidth() != null &&
        		this.getUnitsOfMeasurement() != null) {
        	
        	//baos.writeBytes(this.getUnitsOfMeasurement().getZPL().getBytes()); 
        	baos.writeBytes(UNITS_OF_MEASUREMENT.DOTS.getZPL().getBytes());
        	//b=P: Label width [dot]
        	
        	if(this.getUnitsOfMeasurement() == UNITS_OF_MEASUREMENT.INCHES && this.getDpi() != null) {
        		int dots = new BigDecimal(this.getDpi().getResolution()).multiply(this.getLabelWidth()).intValue();
        		baos.writeBytes(("^S(CLS,P," + dots + "\r").getBytes()); //Sets label length in inches
            }else if(this.getUnitsOfMeasurement() == UNITS_OF_MEASUREMENT.MILLIMETERS && this.getDpi() != null) {
        		
            	BigDecimal dots = new BigDecimal(this.getDpi().getResolution()).multiply(this.getLabelWidth());
            	dots = dots.divide(new BigDecimal(25.4));
        		baos.writeBytes(("^S(CLS,P," + dots + "\r").getBytes()); //Sets label length in inches
            }else{
            	baos.writeBytes(("^S(CLS,P," + this.getLabelWidth() + "\r").getBytes()); //Sets label length in inches
            }
        	
        }
		
		
		baos.writeBytes("^JUS\r".getBytes());
		
		baos.writeBytes("^XZ\r".getBytes()); 

		baos.writeBytes("\r".getBytes());
	        
//	    File badgeAsFileZpl = new File("D:\\BadgeAsFile.zpl");
	        
		EpsonZplPrinterResponse response = null;
		
		try /*(BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(badgeAsFileZpl));
	        		)*/{
							
			byte[] zplBytes = baos.toByteArray();
			//fos.write(zplBytes);
//			long currentTime = System.currentTimeMillis();
//			long progress = 0;
				
			response = sendZpl(zplBytes, this.getIp(), 9100);
//	        		printerDAO.doSave(printer);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
		if(response == null) {
			response = new EpsonZplPrinterResponse();
			response.setMessage("unknown error");
		}
		
		return response;
		
	}
	
	
	
	private EpsonZplPrinterResponse sendZpl(byte[] zpl, String ip, int port) throws Exception {
		
		System.out.println("ABOUT TO TRY TO SEND ZPL: " + ip +":" + port);
		
//		String str = new String(zpl, StandardCharsets.UTF_8); 
//		System.out.println("ZPL BEING SENT " + ip +":" + port + "\r" + str);
		
		//^PP Pause the printer
		//~PS Cancel Pause
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		if(!WRPUtils.isEmpty(zpl)) {
			baos.write(zpl);
		}
	        
		baos.writeBytes("^XA\r".getBytes()); //1. 
		baos.writeBytes("~H(SEA,E\r".getBytes()); //Sends the printer error status
		
		//baos.writeBytes("~H(SMA,S\r".getBytes()); //Sends the printer operation status
//		2-character ASCII string
//		 ER: Error state
//		 SP: Self printing
//		 PR: Print operation state
//		 WT: Waiting
//		 IL: Idling
//		 PS: Pause
//		 CL: Head maintenance
//		 FC: Factory shipment state
//		 UP: Firmware updating
		baos.writeBytes("~H(QIQ\r".getBytes()); //sends the remaining ink for all colors in the printer.
		baos.writeBytes("~H(QMN\r".getBytes()); //sends the remaining Maintenance kit life.
		
//		baos.writeBytes("~H(S".getBytes()); //(Get printer operation status) command to get the printer error status.
//		baos.writeBytes("~H(Q".getBytes()); //(Get printer status) command to get the printer warning status
//		baos.writeBytes("^HH".getBytes()); //(Get printer status) command to get the printer warning status
//		baos.writeBytes("~H(CLM,D".getBytes()); //label edge detection
//		baos.writeBytes("~H(CLP,O".getBytes()); //cut position adjustment
//		baos.writeBytes("~H(CLP,T".getBytes()); //leading edge adjustment
//		baos.writeBytes("~H(CMV,C".getBytes()); //permitted clogged nozzles
//		baos.writeBytes("~H(CMV,A".getBytes()); //cleaning after self test. 
		
		
		
		
		// Use the "~H(S" (Get printer operation status) command to get the printer error status.
		// Use the "~H(Q" (Get printer status) command to get the printer warning status
		//~JC recalibrate sensors
		
		//baos.writeBytes("~H(IMM".getBytes()); //ink models
		
		//baos.writeBytes("~H(SMA,S".getBytes()); //ER (error status) is returned

		baos.writeBytes("^XZ\r".getBytes()); //1. Delete the files from the printer.
		
		zpl = baos.toByteArray();
		
		
		 
        File badgeAsFileZpl = new File("D:\\BadgeAsFile" + this.getIp() + ".zpl");
        
        try (BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(badgeAsFileZpl));){
        				
        	fos.write(zpl);
		
        }catch(Exception ex) {
        	ex.printStackTrace();
        }
		
		

//		long start = System.currentTimeMillis();
		EpsonZplPrinterResponse response = new EpsonZplPrinterResponse();

		try (Socket clientSocket = new Socket(ip, port);
				BufferedReader in =  new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				DataOutputStream outToServer =  new DataOutputStream(clientSocket.getOutputStream());
				/*BufferedWriter out = new BufferedWriter(clientSocket.getOutputStream()));*/){

			clientSocket.setSoTimeout(10000);
			
			System.out.println("SOCKET IS CONNECTED: " + clientSocket.isConnected() + " " + ip + ":" + port);

			if(!isEmpty(zpl)) {
				outToServer.write(zpl);
			}
			
			
			String fromPrinter = null;

		    //System.out.println("Read Start");

		    //Thread.sleep(100);
		    
	        char STX = (char) Integer.parseInt("02", 16);//STX
	        char ETX = (char) Integer.parseInt("03", 16);//ETX
	        
	        //start = System.currentTimeMillis();
	        
	        //boolean readIq = false;
	        //laast command is Mn
	        boolean readMn = false;
	        //boolean printerStatus = 
	        boolean done = false;
	        
	        //while (((System.currentTimeMillis() - continueListinging) < 250) || (fromPrinter = in.readLine()) != null ) {
	        while (!done && (fromPrinter = in.readLine()) != null ) {
			    //System.out.println("echo: " + fromPrinter);
//			    if(fromPrinter == null) {
//			    	break;
//			    }
		    	
			    fromPrinter = fromPrinter.replace(String.valueOf(STX), "");
			    fromPrinter = fromPrinter.replace(String.valueOf(ETX), "");
			    
			    //System.out.println("fromPrinter: " + fromPrinter);
			    
			    if(fromPrinter.startsWith("^S(SEA,E,")) {//It's ink levels
			    	fromPrinter = fromPrinter.replace("^S(SEA,E,", "");
			    	response.setErrorStatus(fromPrinter);
			    	
			    }else if(fromPrinter.startsWith("IQ,")) {//It's ink levels
			    	fromPrinter = fromPrinter.replace("IQ,", "");
			    	
			    	String[] inkLevels = fromPrinter.split(",");
			    	//<STX> IQ, <remaining black ink>, <remaining cyan ink>,<remaining magenta ink>, <remaining
			    	//yellow ink><ETX><CR><LF>
//			    	RH Enough ink in cartridge
//			    	RM Moderate ink in cartridge
//			    	RL Small ink in cartridge
//			    	RN Ink cartridge low
//			    	RR Replace Ink cartridge
//			    	NA Ink cartridge not installed
//			    	CI Ink cartridge installed

		    		
			    	if(!isEmpty(inkLevels)) {
			    		//black = inkLevels[0];
			    		response.setBlack(inkLevels[0]);
			    		
			    		if(inkLevels.length > 1) {
//			    			cyan = inkLevels[1];
			    			response.setCyan(inkLevels[1]);
				    	}
			    		
			    		if(inkLevels.length > 2) {
			    			//magenenta = inkLevels[2];
			    			response.setMagenta(inkLevels[2]);
			    		}
			    		
			    		if(inkLevels.length > 3) {
			    			//yellow = inkLevels[3];
			    			response.setYellow(inkLevels[3]);
			    		}
			    	}
			    	
//			    	System.out.println("BLACK: " + black + " Cyan: " + cyan + " Magenta: " + magenenta + 
//			    			" Yellow: " + yellow);
			    	
			    	//readIq = true;
			    }else if(fromPrinter.startsWith("MN,")) {//It's ink levels
			    	fromPrinter = fromPrinter.replace("MN,", "");
			    	
			    	//<STX> IQ, <remaining black ink>, <remaining cyan ink>,<remaining magenta ink>, <remaining
			    	//yellow ink><ETX><CR><LF>
//			    	RH Enough ink in cartridge
//			    	RM Moderate ink in cartridge
//			    	RL Small ink in cartridge
//			    	RN Ink cartridge low
//			    	RR Replace Ink cartridge
//			    	NA Ink cartridge not installed
//			    	CI Ink cartridge installed
			    	
		    		if(!isEmpty(fromPrinter)) {
		    			response.setMaintenance(fromPrinter);
			    	}
		    		
//			    	if(!WRPUtils.isEmpty(fromPrinter)) {
//				    	System.out.println("Maintinance Box: " + fromPrinter);
//
//			    	}
			    	
			    	readMn = true;
			    }
			    
//			    for(char theChar: fromPrinter.toCharArray()) {
//			    // byte[] to string
////			    String s = new String(bytes, StandardCharsets.UTF_8);
////			    System.out.println("echo: " + in.readLine());
//			    	String hexString1 = Integer.toHexString(theChar);
//			    	System.out.println(hexString1);
//			    }
			    
//			    System.out.println("(System.currentTimeMillis() - continueListinging)" + (System.currentTimeMillis() - continueListinging));
//			    if((System.currentTimeMillis() - continueListinging) > 500){
//			    	System.out.println("MORE THAN 500 millis, break");
//			    	break;
//			    }
			    
//			    continueListinging = System.currentTimeMillis();
			    //Thread.sleep(250);
			    
				
//		    	System.out.println("readIq: " +readIq + " readMn: " + readMn);
			    
			    if(/*readIq &&*/ readMn) {
			    	done = true;
//			    	System.out.println("Read Complete in " + (System.currentTimeMillis() - start));
			    }
			    	
			    
			    	
			}
		
			
	        //response.setMessage((System.currentTimeMillis() - start) + " millis to connect to printer and transmits data.");
	        
	        response.setSuccess(true);
		}catch(ConnectException c1) {
			response.setMessage("Cannot Connect To: " + ip);
		}catch (SocketTimeoutException s1) {
			response.setMessage("SOCKET TIMED OUT");
//			throw new Exception("Cannot print label on this printer : " + ip + ":" + port, e1);
		}catch (Exception e1) {
			response.setMessage(e1.getMessage());
			throw new Exception("Cannot print label on this printer : " + ip + ":" + port, e1);
		}
		
		return response;
		//System.out.println((System.currentTimeMillis() - start) + " millis to connect to printer and transmits data.");
	}


	public List<BufferedImage> getImages() {return images;}
	public void setImages(List<BufferedImage> images) {
		this.images = images;
	}

	public String getIp() {return ip;}
	public void setIp(String ip) {this.ip = ip;}

	public int getPort() {return port;}
	public void setPort(int port) {this.port = port;}

	public Float getLeadingEdgeAdjustment() {return leadingEdgeAdjustment;}
	public void setLeadingEdgeAdjustment(Float leadingEdgeAdjustment) {
		this.leadingEdgeAdjustment = leadingEdgeAdjustment;
	}

	public Float getLeftEdgeAdj() {return leftEdgeAdj;}
	public void setLeftEdgeAdj(Float leftEdgeAdj) {
		this.leftEdgeAdj = leftEdgeAdj;
	}

	public LABEL_EDGE_DETECTION getLabelEdgeDetection() {
		return labelEdgeDetection;
	}

	public void setLabelEdgeDetection(LABEL_EDGE_DETECTION labelEdgeDetection) {
		this.labelEdgeDetection = labelEdgeDetection;
	}
	
	

	public FEED_AND_CUT_MODE getFeedAndCutMode() {
		return feedAndCutMode;
	}

	public void setFeedAndCutMode(FEED_AND_CUT_MODE feedAndCutMode) {
		this.feedAndCutMode = feedAndCutMode;
	}

	public DPI getDpi() {return dpi;}

	public void setDpi(DPI dpi) {
		this.dpi = dpi;
	}

	public PRINT_QUALITY getPrintQuality() {
		return printQuality;
	}

	public void setPrintQuality(PRINT_QUALITY printQuality) {
		this.printQuality = printQuality;
	}

	public BigDecimal getLabelHeight() {
		return labelHeight;
	}

	public void setLabelHeight(BigDecimal labelHeight) {
		this.labelHeight = labelHeight;
	}

	public BigDecimal getLabelWidth() {
		return labelWidth;
	}

	public void setLabelWidth(BigDecimal labelWidth) {
		this.labelWidth = labelWidth;
	}

	public UNITS_OF_MEASUREMENT getUnitsOfMeasurement() {
		return unitsOfMeasurement;
	}

	public void setUnitsOfMeasurement(UNITS_OF_MEASUREMENT unitsOfMeasurement) {
		this.unitsOfMeasurement = unitsOfMeasurement;
	}






	public enum DPI {
		//^S(CLR,b,c
		//b = P for print resolution, so ^S(CLR,P,c
		//c = 200/300/600
		//200 [dpi] is to be used only when 200 [dpi]
		//	was specified for ^S(CLR,Z: print resolution of	replaced printer.
		
	    DPI_200 (200),
	    DPI_300 (300),
	    DPI_600 (600);
		
		private final int resolution;   // in kilograms
	    
	    DPI(int resolution) {
	        this.resolution = resolution;
	    }
	    
	    public int getResolution() { return resolution; }
	  
	    public String getZPL() {
	    	return "^S(CLR,P," + this.getResolution() + "\r";
	    }
	}
	
	public enum LABEL_EDGE_DETECTION {
		//^S(CLM,b,c
		//b=D: Label edge detection
		//c=M/W/N
		// M: Black mark detection
		// W: Gap detection
		// N: No detection
		//Selects media type (label
		//edge detection, media form,
		//media source, media shape,
		//or media coating type).
		
		BLACK_MARK ("M"),
		GAP ("W"),
		NONE ("D");
		
		private final String type;   // in kilograms
	    
		LABEL_EDGE_DETECTION(String type) {
	        this.type = type;
	    }
	    
		public String getType() {return type;}

		public String getZPL() {
	    	return "^S(CLM,B," + this.getType() + "\r";
	    }
	}
	
	/**
	 * From https://files.support.epson.com/pdf/pos/bulk/esclabel_crg_en_07.pdf
	 * Under the command ^MM
	 * Save to non volatile memory with ^JU command
	 */
	public enum FEED_AND_CUT_MODE {
		//^S(CLR,b,c
		//m = T/P/R/A/C
		//T: No cutting
		//P: Manual peeling and application
		//R: Rewind
		//A: Automatic peeling and application
		//C: Cutting performed
		
		NO_CUTTING("T"),
		MANUAL_PEEL_AND_APPLICATION ("P"),
		REWIND ("R"),
		AUTOMATIC_PEEL_AND_APPLICATION ("A"), //not suppored on c4000s
		AUTOCUT ("C");
		
		private final String feedmode;   
	    
		FEED_AND_CUT_MODE(String feedmode) {
	        this.feedmode = feedmode;
	    }
	    
		public String getFeedmode() {
			return feedmode;
		}


		public String getZPL() {
	    	return "^MM" + this.getFeedmode() + "\r";
	    }
	}
	
	/**
	 * Set units of Printer setting measurement
	 * ^S(CMP,b,c
	 * b=U: Basic printer unit system
	 * c = D/I/M
	 * 	D: Dots
	 * 	I: Inches
	 *  M: Millimeters
	 * 
	 */
	public enum UNITS_OF_MEASUREMENT {

		 
		DOTS ("D"),
		INCHES ("I"),
		MILLIMETERS ("M");
		
		private final String units;   // in kilograms
	    
		UNITS_OF_MEASUREMENT(String units) {
	        this.units = units;
	    }
	    
		public String getUnits() {
			return units;
		}

		/**
		 * Gets units of Printer setting measurement
		 * ^S(CMP,b,c
		 * b=U: Basic printer unit system
		 * c = D/I/M
		 * 	D: Dots
		 * 	I: Inches
		 *  M: Millimeters
		 * 
		 */
		private String getZPL() {
	    	return "^S(CMP,U," + this.getUnits() + "\r";
	    }
		public String getZPL(DPI dpi)throws Exception {
			if(dpi == null) {
				return "";
			}
	    	return "^MU" + this.getUnits() + "," + dpi.getResolution() + "," + dpi.getResolution() + "\r";
	    }
		
		@Override
		public String toString(){
			return this.getZPL();
		}
	}
	
	
	public enum PRINT_QUALITY {
		//^S(CPC,Q,
//		 D: Max Speed
//		 S: Speed
//		 N: Normal
//		 Q: Quality
//		 M: Max Quality
		 
		MAX_SPEED ("D"),
		SPEED ("S"),
		NORMAL ("N"),
		QUALITY ("Q"),
		MAX_QUALITY ("M");
		
		private final String quality;   // in kilograms
	    
		PRINT_QUALITY(String quality) {
	        this.quality = quality;
	    }
	    
	    
	  
	    public String getQuality() {
			return quality;
		}



		public String getZPL() {
	    	return "^S(CPC,Q," + this.getQuality() + "\r";
	    }
	}
	
	
	//~H(CLP,b    Sends paper feed amount, or cut position adjustment.
	
	private boolean isEmpty(byte[] s) {
		return (s == null || s.length == 0);
	}
	
	private boolean isEmpty(String s) {
		return (s == null || s.trim().length() == 0);
	}
	
	private boolean isEmpty(String[] arr) {
		if (arr == null || arr.length == 0) {
			return true;
		}
		if (arr.length == 1 && isEmpty(arr[0])) {
			return true;
		}
		return false;
	}

	public Integer getCloggedNozzles() {return cloggedNozzles;}
	/**
	 * ^S(CMV,b,c
	 * b=C: Permitted clogged nozzle
	 * c=0 ≤ c ≤ 16
	 * @param nozzleCheckLabel
	 */
	public void setCloggedNozzles(Integer cloggedNozzles) {this.cloggedNozzles = cloggedNozzles;}

	public Integer getNozzleCheckLabel() {return nozzleCheckLabel;}
	/**
	 * ^S(CMV,b,c
	 * b=I: Self-test interval in printing (number of labels)
	 * c=1 ≤ c ≤ 9999
	 * @param nozzleCheckLabel
	 */
	public void setNozzleCheckLabel(Integer nozzleCheckLabel) {this.nozzleCheckLabel = nozzleCheckLabel;}
	
	

}
