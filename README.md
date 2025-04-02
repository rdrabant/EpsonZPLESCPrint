# EpsonZPLESCPrint
A set of Java Files to print to Epson ZPLII/ESC for the ColorWorks Series (tested with C4000u)

Use exanmples for sending a print job to a printer. You send a Array of java.awt.image.BufferedImage, with each image being a lable/name badge. 

This assumes the printer has an IP of 192.168.1.50

**To initialize the object, do the followwing. 
<code>
EpsonZplPrintJob pj = new EpsonZplPrintJob("192.168.1.50", 9100);
Boolean sucessfullyCompleted = false;
</code>**


**To calibrate the sensor, now just call the following**. 
<code>
EpsonZplPrinterResponse response = pj.calibrate();
if(response.getSuccess() != null) {
  sucessfullyCompleted = response.getSuccess();
}
</code>


**To print....**
<code>
List<BufferedImage> images = new ArrayList<BufferedImage>();
BufferedImage yourBufferedImage = ......an image you created
images.add(yourBufferedImage);
pj.setImages(images);
EpsonZplPrinterResponse response = pj.print();

if(response.getSuccess() != null) {
  sucessfullyCompleted = response.getSuccess();
}
</code>


**To update settings, set what you need, and call pj.updatePrinterSettings();**
<code>
pj.setFeedAndCutMode(EpsonZplPrintJob.FEED_AND_CUT_MODE.AUTOCUT);
pj.setLabelEdgeDetection(EpsonZplPrintJob.LABEL_EDGE_DETECTION.GAP);
pj.setPrintQuality(EpsonZplPrintJob.PRINT_QUALITY.NORMAL);
pj.setFeedAndCutMode(EpsonZplPrintJob.FEED_AND_CUT_MODE.AUTOCUT);
pj.setDpi(EpsonZplPrintJob.DPI.DPI_600);
//+/-36 dots
pj.setLeftEdgeAdj(Float.valueOf(25));
//+/- 256
pj.setLeadingEdgeAdjustment(Float.valueOf(100));
pj.updatePrinterSettings();
</code>


**As for the EpsonZplPrinterResponse response object** returned by most calls, we also ask the printer ink and maintenance kit levels, so you can report that back.
<code>
int blackPercentLevel = EpsonZplPrinterResponse.getPercentForCode(response.getBlack());
int cyanPercentLevel = EpsonZplPrinterResponse.getPercentForCode(response.getCyan());
int magentaPercentLevel = EpsonZplPrinterResponse.getPercentForCode(response.getMagenta());
int yellowPercentLevel = EpsonZplPrinterResponse.getPercentForCode(response.getYellow());
int maintenancePercentLevel = EpsonZplPrinterResponse.getPercentForCode(response.getMaintenance());
String message = response.getMessage();
</code>


The following files helped me in my journey. <code>
https://files.support.epson.com/pdf/pos/bulk/cw-c4000_esclabel_crg_en_revc.pdf
https://files.support.epson.com/pdf/pos/bulk/esclabel_cl_cw-c_en_06.pdf
https://files.support.epson.com/pdf/pos/bulk/esclabel_crg_en_07.pdf
https://files.support.epson.com/pdf/pos/bulk/esclabel_apg_en_forcw-c6000series_reve.pdf
</code>
