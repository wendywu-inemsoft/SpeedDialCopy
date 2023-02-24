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
public class PhoneSpeedDial {
  public String dispId;
  public String shiftId;
  public String speedDialNumber;
  public String speedDialDesc;
  public String speedDialType;
  public String toneNumber;
  public String tabId;
  public int rowNumber;
  public int columnNumber;
  public int cwpId;
  public String foreColorStr;
  public String bgColorStr;

  public PhoneSpeedDial(String iDispId, String iShiftId, String iSPDNumber, String iSPDDesc, String iSPDType, String iToneNumber,
                        String iTabId, int iRowNumber, int iColNumber, int iCWPId, String foreColor, String bgColor)
  {
    dispId=iDispId;
    shiftId=iShiftId;
    speedDialNumber=iSPDNumber;
    speedDialDesc=iSPDDesc;
    speedDialType=iSPDType;
    toneNumber=iToneNumber;
    tabId=iTabId;
    rowNumber=iRowNumber;
    columnNumber=iColNumber;
    cwpId=iCWPId;
    foreColorStr=foreColor;
    bgColorStr=bgColor;
  }

}
