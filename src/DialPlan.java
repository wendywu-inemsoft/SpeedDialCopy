import java.util.*;
import javax.swing.JOptionPane;

/**
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
public class DialPlan {
  private int siteID = -1;
  private Hashtable officeCodeHash = new Hashtable();
  private ArrayList localAreaCodeList = new ArrayList();
  private DBHandler dbHandler;

  public DialPlan(int iSiteId, DBHandler dbhandler) {
    siteID = iSiteId;
    dbHandler = dbhandler;
    localAreaCodeList = dbHandler.getAreaCodeList(siteID);
    if (localAreaCodeList == null || localAreaCodeList.size() == 0) {
      JOptionPane.showMessageDialog(null,
                                    "No Area Code defined in the database",
                                    "Error", JOptionPane.ERROR_MESSAGE);
      System.exit(1);

    }
    officeCodeHash = dbHandler.getOfficeCodeList(siteID);
    if (officeCodeHash == null || officeCodeHash.size() == 0) {
      JOptionPane.showMessageDialog(null,
                                    "No Office Code defined in the database",
                                    "Error", JOptionPane.ERROR_MESSAGE);
      System.exit(1);

    }
  }

  //Standard format is 10 digits: "areaCode + officeCode + extension" (i.e. "8173527000")
  public String standardNumFormat(String inNum) {
    if (inNum == null)
      return "";

    inNum = inNum.trim();
    int inNumLength = inNum.length();
    String stdNum = inNum; //default value
    String officeCode = null;

    //international dial:
    if (inNumLength > 4 && inNum.substring(0, 4).equals("8011")) { //i.e. 8-011-52-5555596762
      stdNum = inNum.substring(1);
    } else {
      //USA or Canada dial:
      switch (inNumLength) {
      case 12: //i.e. "81" or "91" + "8173527000"
      case 11: //i.e. 8 or 9 + "8173527000"
      case 10: //i.e. "8173527000"
        stdNum = inNum.substring(inNumLength - 10);
        break;
      case 7: //3527000
      case 8: //i.e. "8" + "3527000"
        officeCode = inNum.substring(inNumLength - 7, inNumLength - 4);
        if (officeCodeHash.containsKey(officeCode)) {
          /**
           * get the area code that associated with office code
           * assume office code is unique across all PBX sites in the system
           */
          String areaCode = ((OfficeCdData) officeCodeHash.get(officeCode)).
                            getAreaCd();
          if (areaCode == null)
            areaCode = "";
          stdNum = areaCode + inNum.substring(inNumLength - 7);
        }
        break;
      case 5: //i.e. 27000
        officeCode = getMyOfficeCode(inNum.charAt(0));
        if (officeCode != null) {
          /**
           * get the area code that associated with this office code
           */
          String areaCode = ((OfficeCdData) officeCodeHash.get(officeCode)).
                            getAreaCd();
          if (areaCode == null)
            areaCode = "";
          stdNum = areaCode + officeCode + inNum.substring(inNumLength - 4);
        }
        break;
      default:
        break;
      }
    }
    return stdNum; //in format like "8173527000" or "4444" ...
  }

  private String getMyOfficeCode(char code) {
    Enumeration e = officeCodeHash.elements();
    for (; e.hasMoreElements(); ) {
      OfficeCdData data = (OfficeCdData) e.nextElement();
      String officeCd = data.getOfficeCd();
      int pbxSite = data.getPBXSite();
      if (siteID == pbxSite && officeCd != null && officeCd.length() >= 1) {
        if (officeCd.charAt(officeCd.length() - 1) == code) {
          return officeCd;
        }
      }
    }
    return null;
  }


}
