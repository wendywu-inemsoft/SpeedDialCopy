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
public class RadioSpeedDial {
  public String dispId;
  public String shiftId;
  public String radioId;
  public String tabId;
  public int rowNumber;
  public int columnNumber;

  public RadioSpeedDial(String iDispId, String iShiftId, String iRadioId, String iTabId, int iRowNumber, int iColumnNumber)
  {
    dispId=iDispId;
    shiftId=iShiftId;
    radioId=iRadioId;
    tabId=iTabId;
    rowNumber=iRowNumber;
    columnNumber=iColumnNumber;
  }
}
