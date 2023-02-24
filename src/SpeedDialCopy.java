import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;

import javax.swing.JOptionPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException; /**

 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2010</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SpeedDialCopy {
  public static Properties appProperties;
  private static Logger log = Logger.getLogger(SpeedDialCopy.class);
  private DBHandler dbHandler;
  public SpeedDialCopy(String iniFileName) {
    try {
      log.info("SpeedDialCopy Started");
      appProperties = new Properties();
      FileInputStream isStream = new FileInputStream("etc" + File.separator + iniFileName);
      appProperties.load(isStream);
      isStream.close();
      dbHandler = new DBHandler();
    } catch (FileNotFoundException g) {
      JOptionPane.showMessageDialog(null, "INI file not found",
                                    "Error", JOptionPane.ERROR_MESSAGE);
      System.exit(1);

    } catch (Exception e) {
      log.error("Exception", e);
      JOptionPane.showMessageDialog(null,
                                    "Exception when processing " + e,
                                    "Error", JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }
  }

  public void doSpeedDialCopy() {
	  
	  String sourceDispId=appProperties.getProperty("SourceDispId");
	    if(sourceDispId==null)
	    	sourceDispId="";
    // get the source dispatcher Id
    String srcDSId = (String) JOptionPane.showInputDialog(
        null,
        "Enter the source dispatcher Id:\n", sourceDispId);
    
    if (srcDSId == null || srcDSId.trim().length() == 0) {
      JOptionPane.showMessageDialog(null, "Invalid source dispatcher Id",
                                    "Error", JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }
    srcDSId = srcDSId.trim();
    log.info("Source dispatcher id=" + srcDSId);
    
    String sourceShiftId=appProperties.getProperty("SourceShiftId");
    if(sourceShiftId==null)
    	sourceShiftId="";
    
    String srcShiftId = (String) JOptionPane.showInputDialog(
    		null,
    		"Enter the source shift Id:\n", sourceShiftId);
    if (srcShiftId == null || srcShiftId.trim().length() == 0) {
    	JOptionPane.showMessageDialog(null, "Invalid source shift Id",
    			"Error", JOptionPane.ERROR_MESSAGE);
    	System.exit(1);
    }
    
    srcShiftId = srcShiftId.trim();
    
    if (srcShiftId.trim().length() >1) {
    	JOptionPane.showMessageDialog(null, "Invalid source shift Id",
    			"Error", JOptionPane.ERROR_MESSAGE);
    	System.exit(1);
    }
    log.info("Source shift id=" + srcShiftId);
    
    String destDispList=appProperties.getProperty("DestDispIdList");
    if(destDispList==null)
    	destDispList="";

    // get the destination dispatcher Id
    String destDSIds = (String) JOptionPane.showInputDialog(
        null,
        "Enter the destination dispatcher Id list (seperate by ',':\n", destDispList);

    if (destDSIds == null || destDSIds.trim().length() == 0) {
      JOptionPane.showMessageDialog(null, "Invalid destination dispatcher Id list",
                                    "Error", JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }

    destDSIds=destDSIds.trim();
    
    String[] ids=destDSIds.split(",");
    ArrayList<String> dispIdList=new ArrayList<String>();
    for(int i=0; i<ids.length; i++)
    {
    	if(ids[i].indexOf("~")>0)
    	{
    		
    	}
    	else
    		dispIdList.add(ids[i].trim());
    }
    
    log.info("destination dispatcher id list=" + dispIdList);
    
    String destinationShiftId=appProperties.getProperty("DestShiftId");
    if(destinationShiftId==null)
    	destinationShiftId="";
    
    String destShiftId = (String) JOptionPane.showInputDialog(
            null,
            "Enter the destination shift Id:\n", destinationShiftId);

    if (destShiftId == null || destShiftId.trim().length() == 0) {
    	JOptionPane.showMessageDialog(null, "Invalid destination shift Id",
    			"Error", JOptionPane.ERROR_MESSAGE);
    	System.exit(1);
    }
    
    if (destShiftId.trim().length() >1 ) {
    	JOptionPane.showMessageDialog(null, "Invalid destination shift Id",
    			"Error", JOptionPane.ERROR_MESSAGE);
    	System.exit(1);
    }

    destShiftId=destShiftId.trim();
    log.info("destination shift id=" + destShiftId);
    
    String convertPhoneNumberStr=appProperties.getProperty("ConvertPhoneNumber");
    boolean convert=false;
    if(convertPhoneNumberStr!=null)
    	convert=Boolean.parseBoolean(convertPhoneNumberStr);
    	
    // get if need to convert speed dial number to 10 digit format
   /* Object[] options = {"Yes", "No"};
    Object defaultOpt=options[1];
    if(convertPhoneNumber)
    	defaultOpt=options[0];
    int n = JOptionPane.showOptionDialog(null,
                                         "Would you like to convert speed dial number to 1+10 digits format?",
                                         "Question",
                                         JOptionPane.YES_NO_OPTION,
                                         JOptionPane.QUESTION_MESSAGE,
                                         null,
                                         options,
                                         defaultOpt);
    boolean convert = false;
    if (n == JOptionPane.YES_OPTION) {
      convert = true;
    } else {
      convert = false;
    }   */
    
    if(dispIdList.contains(srcDSId) && srcShiftId.equals(destShiftId)
    		&& dbHandler.sameSourceAndDestDB())    
    {
    	JOptionPane.showMessageDialog(null, "Destination dispatcher Ids contain the source dispatcher id, cannot copy speed dial data to itself.",
    			"Error", JOptionPane.ERROR_MESSAGE);
    	System.exit(1);
    }

    if(convert)
      log.info("User would like to convert speed dial number to 1 + 10 digits format");
    else
      log.info("User Do not like to convert speed dial number to 1 + 10 digits format");
    dbHandler.doSpeedDialCopy(srcDSId, srcShiftId, dispIdList, destShiftId, convert);
    System.exit(1);
  }

  public static void main(String[] args) {
	  String fileName="ini.txt";
	  if(args != null && args.length>0)
		  fileName=args[0];
    SpeedDialCopy speeddialcopy = new SpeedDialCopy(fileName);
    speeddialcopy.doSpeedDialCopy();
  }
}
