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
public class SpeedDialTab {
  public String tabId;
  public String tabName;
  public int tabIndex;
  public String tabsetId;

  public SpeedDialTab(String iTabId, String iTabName, int iTabIndex, String iTabsetId)
  {
    tabId=iTabId;
    tabName=iTabName;
    tabIndex=iTabIndex;
    tabsetId=iTabsetId;
  }
}
