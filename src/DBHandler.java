import javax.swing.JOptionPane;
import java.sql.DriverManager;
import java.sql.Connection;
import java.util.*;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.log4j.Logger;
import java.sql.PreparedStatement;

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
public class DBHandler {
  private static Logger log = Logger.getLogger(DBHandler.class);
  private Connection src_conn = null;
  private Connection dest_conn = null;
  private DialPlan dialPlan = null;
  private String src_databaseName=null;
  private String src_url=null;
  private String dest_url=null;
  
  private String dest_databaseName=null;

  public DBHandler() {
    try {

      String src_driverClassName = SpeedDialCopy.appProperties.getProperty(
          "SourceDB_JDBCDriverClassName");
      src_url = SpeedDialCopy.appProperties.getProperty("SourceDB_DBurl");
      if(src_url != null)
    	  src_url=src_url.trim();
      src_databaseName = SpeedDialCopy.appProperties.getProperty(
          "SourceDB_DatabaseName");
      if(src_databaseName != null)
    	  src_databaseName=src_databaseName.trim();
      String userNameTmp = SpeedDialCopy.appProperties.getProperty(
          "SourceDB_loginUserName");
      String passwordTmp = SpeedDialCopy.appProperties.getProperty(
          "SourceDB_loginPassword");
      String src_userName = "";
      String src_password = "";
      try {
        src_userName = CipherTxtBased.decrypt(userNameTmp);
        src_password = CipherTxtBased.decrypt(passwordTmp);
      } catch (Exception e) {
        log.error("Exception", e);
        JOptionPane.showMessageDialog(null,
                                      "Wrong database username or password!!!!",
                                      "Error",
                                      JOptionPane.ERROR_MESSAGE);
        System.exit(1);
      }

      Class.forName(src_driverClassName);
      String src_dbUrl = src_url + src_databaseName;
      src_conn = DriverManager.getConnection(src_dbUrl, src_userName,
                                             src_password);
      log.info("Get database connection to source DB: " + src_dbUrl);

      String dest_driverClassName = SpeedDialCopy.appProperties.getProperty(
          "DestDB_JDBCDriverClassName");
      dest_url = SpeedDialCopy.appProperties.getProperty("DestDB_DBurl");
      if(dest_url != null)
    	  dest_url=dest_url.trim();
      dest_databaseName = SpeedDialCopy.appProperties.getProperty(
          "DestDB_DatabaseName");
      if(dest_databaseName != null)
    	  dest_databaseName=dest_databaseName.trim();
      userNameTmp = SpeedDialCopy.appProperties.getProperty(
          "DestDB_loginUserName");
      passwordTmp = SpeedDialCopy.appProperties.getProperty(
          "DestDB_loginPassword");
      String dest_userName = "";
      String dest_password = "";
      try {
        dest_userName = CipherTxtBased.decrypt(userNameTmp);
        dest_password = CipherTxtBased.decrypt(passwordTmp);
      } catch (Exception e) {
        log.error("Exception", e);
        JOptionPane.showMessageDialog(null,
                                      "Wrong database username or password!!!!",
                                      "Error",
                                      JOptionPane.ERROR_MESSAGE);
        System.exit(1);
      }

      Class.forName(dest_driverClassName);
      String dest_dbUrl = dest_url + dest_databaseName;
      dest_conn = DriverManager.getConnection(dest_dbUrl, dest_userName,
                                              dest_password);
      log.info("Get database connection to destination DB: " + dest_dbUrl);

    } catch (Exception e) {
      log.error("Exception", e);
      JOptionPane.showMessageDialog(null,
                                    "Fail to get database connection!!!!",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      System.exit(1);
    }
  }

  public void doSpeedDialCopy(String sourceDispId, String sourceShiftId, ArrayList<String> destDispIdList, String destShiftId, boolean convert) {
    ArrayList speedDialGroupList = getSpeedDialGroup(sourceDispId, sourceShiftId);
    ArrayList speedDialSubGroupList = getSpeedDialSubGroup(sourceDispId, sourceShiftId);
    ArrayList speedDialTabsetList = getSpeedDialTabSet(sourceDispId, sourceShiftId);
    ArrayList speedDialTabList = getSpeedDialTabList(sourceDispId, sourceShiftId);
    ArrayList speedDialSelectedTabset = getSelectedSpeedDialTabSet(sourceDispId, sourceShiftId);
    ArrayList phoneSpeedDialList = getPhoneSpeedDialList(sourceDispId, sourceShiftId);
    ArrayList radioSpeedDialList = getRadioSpeedDialList(sourceDispId, sourceShiftId);
    
    // convert speed dial number to 1+10 digits
    if (convert) {
      if (dialPlan == null)
        dialPlan = new DialPlan(1, this);

      if (dialPlan != null) {
        for (int i = 0; i < phoneSpeedDialList.size(); i++) {
          PhoneSpeedDial phoneSpeedDial = (PhoneSpeedDial) phoneSpeedDialList.
                                          get(i);
          if (phoneSpeedDial.speedDialNumber.length() <= 10) {
            String standardNumber = dialPlan.standardNumFormat(phoneSpeedDial.
                speedDialNumber);
            if (standardNumber.length() == 10) {
              log.debug("Convert speed dial number " +
                        phoneSpeedDial.speedDialNumber + " to " +
                        "1" + standardNumber);
              phoneSpeedDial.speedDialNumber = "1" + standardNumber;
            }
          }
        }
      }
    }
    
    //
   /* Object[] options = {"Yes", "No"};
    int n = JOptionPane.showOptionDialog(null,
                                         "Current destination dispatcher " + destDispIdList + " shift " + destShiftId + " speed dial data will be deleted " +
                                         " from the database " +  dest_databaseName + ", do you want to continue?",
                                         "Question",
                                         JOptionPane.YES_NO_OPTION,
                                         JOptionPane.QUESTION_MESSAGE,
                                         null,
                                         options,
                                         options[0]);
    if (n == JOptionPane.YES_OPTION) {
     // deleteOldDataFromDestDB(destDispId, destShiftId);
    } else {
    	return;
    }*/
    
    ArrayList<String> goodList=new ArrayList<String>();
	ArrayList<String> badList=new ArrayList<String>();
    DispatcherSpeedDialData[] dispSpdialDataList=new DispatcherSpeedDialData[destDispIdList.size()];
    for(int i=0; i<dispSpdialDataList.length; i++)
    {
    	String destDispId=destDispIdList.get(i);
    	dispSpdialDataList[i]=new DispatcherSpeedDialData(destDispId, destShiftId, speedDialGroupList, speedDialSubGroupList, speedDialTabsetList,
    			speedDialTabList, speedDialSelectedTabset, phoneSpeedDialList, radioSpeedDialList);    	

    	convertGroupData(dispSpdialDataList[i].speedDialGroupList, sourceDispId, sourceShiftId, destDispId, destShiftId);
    	convertSubGroupData(dispSpdialDataList[i].speedDialSubGroupList, destDispId, destShiftId);
    	convertTabSetData(dispSpdialDataList[i].speedDialTabsetList, destDispId, destShiftId);
    	convertSelectedTabsetData(dispSpdialDataList[i].speedDialSelectedTabset, destDispId, destShiftId);
    	convertTabData(dispSpdialDataList[i].speedDialTabList, destDispId, destShiftId);
    	convertPhoneSpeedDialData(dispSpdialDataList[i].phoneSpeedDialList, destDispId, destShiftId);
    	convertRadioSpeedDialData(dispSpdialDataList[i].radioSpeedDialList, destDispId, destShiftId);
    	    	
    	if(copySpeedDialData(dispSpdialDataList[i]))
    		goodList.add(destDispId);
    	else
    		badList.add(destDispId);   	
    }       
    String str="";
	if(goodList.size()>0)
		str="Succeed to copy Disp" + sourceDispId + " speed dial data to dispatcher " + goodList + "\n";
	if(badList.size()>0)
		str=str+"Fail to copy Disp" + sourceDispId + " speed dial data to dispatcher " + goodList;
	JOptionPane.showMessageDialog(null, str, "Message", JOptionPane.INFORMATION_MESSAGE);
    
    return;
  }

  private void deleteOldDataFromDestDB(String destDispId, String destShiftId) {
    deletePhoneSpeedDial(destDispId, destShiftId);
    deleteRadioSpeedDial(destDispId, destShiftId);
    deleteTabData(destDispId, destShiftId);
    deleteSelectedTabSetData(destDispId, destShiftId);
    deleteTabSetData(destDispId, destShiftId);
    deleteSubGroupData(destDispId, destShiftId);
    deleteGroupData(destDispId, destShiftId);
  }

  private boolean insertDispShifData(ArrayList groupDataList, String destDispId)
  {
    ArrayList newShiftList=new ArrayList();
    for(int i=0; i<groupDataList.size(); i++)
    {
      SpeedDialGroup spdDialGroup = (SpeedDialGroup) groupDataList.get(i);
      if(!newShiftList.contains(spdDialGroup.shiftId))
        newShiftList.add(spdDialGroup.shiftId);
    }

    if(newShiftList.size() >0)
    {
      ArrayList currentShiftList=getDispShifId(destDispId);
      ArrayList shiftToAdd = new ArrayList();
      for(int i=0; i<newShiftList.size(); i++)
      {
        String shiftId = (String) newShiftList.get(i);
        if(!currentShiftList.contains(shiftId))
          shiftToAdd.add(shiftId);
      }

      if(shiftToAdd.size()>0)
      {
        log.info("Need to add shift " + shiftToAdd + " into " + dest_databaseName + " RT.TRTI_DSPR_SHIFT table");
        if(!addDispShift(destDispId, shiftToAdd))
          return false;
      }
    }
    return true;
  }

  private ArrayList getDispShifId(String destDispId)
  {
    String sql="SELECT RTI_SHIFT_ID FROM RT.TRTI_DSPR_SHIFT WHERE RTI_DSPR_ID=?";
     PreparedStatement stmt = null;
     ResultSet rs=null;
     ArrayList shiftList=new ArrayList();
     try{
       stmt = dest_conn.prepareStatement(sql);
       stmt.setString(1, destDispId);
       rs = stmt.executeQuery();
       while(rs.next())
       {
         String shiftId= rs.getString(1);
         if(shiftId != null)
         {
           shiftId = shiftId.trim();
           shiftList.add(shiftId);
         }
       }
     }catch(Exception e)
     {
       log.error("getDispShifId for Disp" + destDispId, e);
     }
     finally{
       cleanUp(stmt, rs);
     }
     return shiftList;
  }

  private boolean addDispShift(String destDispId, ArrayList shiftList)
  {
    String sql="INSERT INTO RT.TRTI_DSPR_SHIFT(RTI_DSPR_ID, RTI_SHIFT_ID) VALUES(?,?)";
    PreparedStatement stmt = null;
    try{
      stmt = dest_conn.prepareStatement(sql);
      for(int i=0; i<shiftList.size(); i++)
      {
        String shiftId = (String) shiftList.get(i);
        stmt.setString(1, destDispId);
        stmt.setString(2, shiftId);
        stmt.executeUpdate();
      }
      log.info("Done of add shift " + shiftList + " into database");
      return true;
    }catch(Exception e)
    {
      log.error("getDispShifId for Disp" + destDispId, e);
    }
    finally{
      cleanUp(stmt, null);
    }
    return false;

  }

  private boolean insertSpeedDialGroupData(ArrayList groupDataList) {
    log.info("Start to insert " + groupDataList.size() +
             " speed dial group data......");
    String sql = "INSERT INTO RT.TRTI_SDIAL_GROUP(SDIAL_GRP_ID, RTI_DSPR_ID, RTI_SHIFT_ID, SDIAL_GRP_TYP, SDIAL_GRP_NME) " +
                 " values(?, ?, ?, ?, ?)";
    PreparedStatement stmt = null;
    try {
      stmt = dest_conn.prepareStatement(sql);
      for (int i = 0; i < groupDataList.size(); i++) {
        SpeedDialGroup spdDialGroup = (SpeedDialGroup) groupDataList.get(i);
        stmt.setString(1, spdDialGroup.groupId);
        stmt.setString(2, spdDialGroup.dispId);
        stmt.setString(3, spdDialGroup.shiftId);
        stmt.setString(4, spdDialGroup.groupType);
        stmt.setString(5, spdDialGroup.groupName);        
        stmt.executeUpdate();
      }
      log.info("Done of insert " + groupDataList.size() +
               " speed dial group data.");
      return true;
    } catch (Exception e) {
      log.error("insertSpeedDialGroupData", e);
    } finally {
      cleanUp(stmt, null);
    }
    return false;
  }

  private boolean insertSpeedDialSubGroupData(ArrayList subGroupDataList) {
    log.info("Start to insert " + subGroupDataList.size() +
             " speed dial subgroup data ......");
    String sql = "INSERT INTO RT.TRTI_SDIAL_SUB_GRP(SDIAL_SUBG_ID, SDIAL_GRP_ID, SDIAL_SUBG_NME) " +
                 " values(?, ?, ?)";
    PreparedStatement stmt = null;
    try {
      stmt = dest_conn.prepareStatement(sql);
      for (int i = 0; i < subGroupDataList.size(); i++) {
        SpeedDialSubGroup spdDialSubGroup = (SpeedDialSubGroup)
                                            subGroupDataList.get(i);
        stmt.setString(1, spdDialSubGroup.subGroupId);
        stmt.setString(2, spdDialSubGroup.groupId);
        stmt.setString(3, spdDialSubGroup.subGroupName);
        stmt.executeUpdate();
      }
      log.info("Done of insert " + subGroupDataList.size() +
               " speed dial subgroup data");
      return true;
    } catch (Exception e) {
      log.error("insertSpeedDialSubGroupData", e);
    } finally {
      cleanUp(stmt, null);
    }
    return false;
  }

  private boolean insertSpeedDialTabSetData(ArrayList tabSetDataList) {
    log.info("Start to insert " + tabSetDataList.size() +
             " speed dial tabset data ......");
    String sql = "INSERT INTO RT.TRTI_SDIAL_TABSET(SDIAL_TBST_ID, SDIAL_SUBG_ID, SDIAL_TBST_NME) " +
                 " values(?, ?, ?)";
    PreparedStatement stmt = null;
    try {
      stmt = dest_conn.prepareStatement(sql);
      for (int i = 0; i < tabSetDataList.size(); i++) {
        SpeedDialTabset spdDialTabset = (SpeedDialTabset) tabSetDataList.get(i);
        stmt.setString(1, spdDialTabset.tabsetId);
        stmt.setString(2, spdDialTabset.subGroupId);
        stmt.setString(3, spdDialTabset.tabsetName);
        stmt.executeUpdate();
      }
      log.info("Done of insert " + tabSetDataList.size() +
               " speed dial tabset data");
      return true;
    } catch (Exception e) {
      log.error("insertSpeedDialTabSetData", e);
    } finally {
      cleanUp(stmt, null);
    }
    return false;
  }

  private boolean insertSpeedDialSelectedTabSetData(ArrayList
      selectedTabSetDataList) {
    log.info("Start to insert " + selectedTabSetDataList.size() +
             " selected speed dial tabset data ......");
    String sql = "INSERT INTO RT.TRTI_SEL_TABSET(RTI_DSPR_ID, RTI_SHIFT_ID, SDIAL_GRP_TYP, SDIAL_TBST_ID) " +
                 " values(?, ?, ?, ?)";
    PreparedStatement stmt = null;
    try {
      stmt = dest_conn.prepareStatement(sql);
      for (int i = 0; i < selectedTabSetDataList.size(); i++) {
        SpeedDialSelectedTabSet spdDialSelectedTabset = (
            SpeedDialSelectedTabSet) selectedTabSetDataList.get(i);
        stmt.setString(1, spdDialSelectedTabset.dispId);
        stmt.setString(2, spdDialSelectedTabset.shiftId);
        stmt.setString(3, spdDialSelectedTabset.groupType);
        stmt.setString(4, spdDialSelectedTabset.tabSetId);
        stmt.executeUpdate();
      }
      log.info("Done of insert " + selectedTabSetDataList.size() +
               " selected speed dial tabset data");
      return true;
    } catch (Exception e) {
      log.error("insertSpeedDialSelectedTabSetData", e);
    } finally {
      cleanUp(stmt, null);
    }
    return false;
  }

  private boolean insertSpeedDialTabData(ArrayList tabDataList) {
    log.info("Start to insert " + tabDataList.size() +
             " speed dial tab data ......");
    String sql = "INSERT INTO RT.TRTI_SDIAL_TAB(SDIAL_TAB_ID, SDIAL_TAB_NME, SDIAL_TAB_IDX, SDIAL_TBST_ID) " +
                 " values(?, ?, ?, ?)";
    PreparedStatement stmt = null;
    try {
      stmt = dest_conn.prepareStatement(sql);
      for (int i = 0; i < tabDataList.size(); i++) {
        SpeedDialTab spdDialTab = (SpeedDialTab) tabDataList.get(i);
        stmt.setString(1, spdDialTab.tabId);
        stmt.setString(2, spdDialTab.tabName);
        stmt.setInt(3, spdDialTab.tabIndex);
        stmt.setString(4, spdDialTab.tabsetId);
        stmt.executeUpdate();
      }
      log.info("Done of insert " + tabDataList.size() + " speed dial tab data.");
      return true;
    } catch (Exception e) {
      log.error("insertSpeedDialTabData", e);
    } finally {
      cleanUp(stmt, null);
    }
    return false;
  }

  private boolean insertPhoneSpeedDialData(ArrayList phoneSpeedDialDataList) {
    log.info("Start to insert " + phoneSpeedDialDataList.size() +
             " phone speed dial data......");
    String sql =
        "INSERT INTO RT.TRTI_DSPR_SPD_DIAL(RTI_DSPR_ID, RTI_SHIFT_ID, " +
        "RTI_SDIAL_NBR, RTI_SDIAL_DESC, RTI_SDIAL_TYP, RTI_TONE_NBR, " +
        "SDIAL_TAB_ID, SCRN_ROW_NBR, SCRN_COL_NBR, DSPR_CWP_ID) " +
        " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    PreparedStatement stmt = null;
    try {
      stmt = dest_conn.prepareStatement(sql);
      for (int i = 0; i < phoneSpeedDialDataList.size(); i++) {
        PhoneSpeedDial phoneSpeedDial = (PhoneSpeedDial) phoneSpeedDialDataList.
                                        get(i);
        stmt.setString(1, phoneSpeedDial.dispId);
        stmt.setString(2, phoneSpeedDial.shiftId);
        stmt.setString(3, phoneSpeedDial.speedDialNumber);
        stmt.setString(4, phoneSpeedDial.speedDialDesc);
        stmt.setString(5, phoneSpeedDial.speedDialType);
        stmt.setString(6, phoneSpeedDial.toneNumber);
        stmt.setString(7, phoneSpeedDial.tabId);
        stmt.setInt(8, phoneSpeedDial.rowNumber);
        stmt.setInt(9, phoneSpeedDial.columnNumber);
        stmt.setInt(10, phoneSpeedDial.cwpId);
        stmt.executeUpdate();
      }
      log.info("Done of insert " + phoneSpeedDialDataList.size() +
               " phone speed dial data.");
      return true;
    } catch (Exception e) {
      log.error("insertPhoneSpeedDialData", e);
    } finally {
      cleanUp(stmt, null);
    }
    return false;
  }

  private boolean insertRadioSpeedDialData(ArrayList radioSpeedDialDataList) {
    log.info("Start to insert " + radioSpeedDialDataList.size() +
             " radio speed dial data ......");
    String sql = "INSERT INTO RT.TRTI_DRAD_SDIAL(RTI_DSPR_ID, RTI_SHIFT_ID, " +
                 "RTI_RADIO_ID, " +
                 "SDIAL_TAB_ID, SCRN_ROW_NBR, SCRN_COL_NBR) " +
                 " values(?, ?, ?, ?, ?, ?)";
    PreparedStatement stmt = null;
    try {
      stmt = dest_conn.prepareStatement(sql);
      for (int i = 0; i < radioSpeedDialDataList.size(); i++) {
        RadioSpeedDial radioSpeedDial = (RadioSpeedDial) radioSpeedDialDataList.
                                        get(i);
        stmt.setString(1, radioSpeedDial.dispId);
        stmt.setString(2, radioSpeedDial.shiftId);
        stmt.setString(3, radioSpeedDial.radioId);
        stmt.setString(4, radioSpeedDial.tabId);
        stmt.setInt(5, radioSpeedDial.rowNumber);
        stmt.setInt(6, radioSpeedDial.columnNumber);
        stmt.executeUpdate();
      }
      log.info("Done of insert " + radioSpeedDialDataList.size() +
               " radio speed dial data.");
      return true;
    } catch (Exception e) {
      log.error("insertRadioSpeedDialData", e);
    } finally {
      cleanUp(stmt, null);
    }
    return false;
  }


  private void deletePhoneSpeedDial(String destDispId, String destShiftId) {
    String sql = "DELETE from RT.TRTI_DSPR_SPD_DIAL WHERE RTI_DSPR_ID=? and RTI_SHIFT_ID=?";
    PreparedStatement stmt = null;
    try {
      stmt = dest_conn.prepareStatement(sql);
      stmt.setString(1, destDispId);
      stmt.setString(2, destShiftId);
      int n = stmt.executeUpdate();
      log.debug("Delete " + n + " phone speed dial data");
    } catch (Exception e) {
      log.error("deletePhoneSpeedDial", e);
    } finally {
      cleanUp(stmt, null);
    }
  }

  private void deleteRadioSpeedDial(String destDispId, String destShiftId) {
    String sql = "DELETE from RT.TRTI_DRAD_SDIAL WHERE RTI_DSPR_ID=? and RTI_SHIFT_ID=?";
    PreparedStatement stmt = null;
    try {
      stmt = dest_conn.prepareStatement(sql);
      stmt.setString(1, destDispId);
      stmt.setString(2, destShiftId);
      int n = stmt.executeUpdate();
      log.debug("Delete " + n + " radio speed dial data");
    } catch (Exception e) {
      log.error("deleteRadioSpeedDial", e);
    } finally {
      cleanUp(stmt, null);
    }
  }

  private void deleteTabData(String destDispId, String destShiftId) {
    String sql = "DELETE from RT.TRTI_SDIAL_TAB WHERE SDIAL_TAB_ID like '" +
                 destDispId + "_" + destShiftId+ "_%'";
    PreparedStatement stmt = null;
    try {
      stmt = dest_conn.prepareStatement(sql);
      int n = stmt.executeUpdate();
      log.debug("Delete " + n + " speed dial tab data");
    } catch (Exception e) {
      log.error("deleteTabData", e);
    } finally {
      cleanUp(stmt, null);
    }
  }

  private void deleteSelectedTabSetData(String destDispId, String destShiftId) {
    String sql = "DELETE from RT.TRTI_SEL_TABSET WHERE RTI_DSPR_ID =? and RTI_SHIFT_ID=?";
    PreparedStatement stmt = null;
    try {
      stmt = dest_conn.prepareStatement(sql);
      stmt.setString(1, destDispId);
      stmt.setString(2, destShiftId);
      int n = stmt.executeUpdate();
      log.debug("Delete " + n + " selected speed dial tabset data");
    } catch (Exception e) {
      log.error("deleteSeletedTabSetData", e);
    } finally {
      cleanUp(stmt, null);
    }
  }

  private void deleteTabSetData(String destDispId, String destShiftId) {
    String sql = "DELETE from RT.TRTI_SDIAL_TABSET WHERE SDIAL_TBST_ID like '" +
                 destDispId + "_" + destShiftId+ "_%'";
    PreparedStatement stmt = null;
    try {
      stmt = dest_conn.prepareStatement(sql);
      int n = stmt.executeUpdate();
      log.debug("Delete " + n + " speed dial tabset data");
    } catch (Exception e) {
      log.error("deleteTabSetData", e);
    } finally {
      cleanUp(stmt, null);
    }
  }

  private void deleteSubGroupData(String destDispId, String destShiftId) {
    String sql = "DELETE from RT.TRTI_SDIAL_SUB_GRP WHERE SDIAL_SUBG_ID like '" +
                 destDispId + "_" + destShiftId + "_%'";
    PreparedStatement stmt = null;
    try {
      stmt = dest_conn.prepareStatement(sql);
      int n = stmt.executeUpdate();
      log.debug("Delete " + n + " speed dial subgroup data");
    } catch (Exception e) {
      log.error("deleteSubGroupData", e);
    } finally {
      cleanUp(stmt, null);
    }
  }

  private void deleteGroupData(String destDispId, String destShiftId) {
    String sql = "DELETE from RT.TRTI_SDIAL_GROUP WHERE RTI_DSPR_ID=? and RTI_SHIFT_ID=?";
    PreparedStatement stmt = null;
    try {
      stmt = dest_conn.prepareStatement(sql);
      stmt.setString(1, destDispId);
      stmt.setString(2, destShiftId);
      int n = stmt.executeUpdate();
      log.debug("Delete " + n + " speed dial group data");
    } catch (Exception e) {
      log.error("deleteGroupData", e);
    } finally {
      cleanUp(stmt, null);
    }
  }


  private void convertGroupData(ArrayList spdDialGroupList, String sourceDispId, String sourceShiftId,
                                String destDispId, String destShiftId) {
    for (int i = 0; i < spdDialGroupList.size(); i++) {
      SpeedDialGroup spdDialGroup = (SpeedDialGroup) spdDialGroupList.get(i);
      spdDialGroup.dispId = destDispId;
      spdDialGroup.shiftId = destShiftId;
      spdDialGroup.groupId = destDispId + "_" + destShiftId + spdDialGroup.groupId.substring(5);
      if (spdDialGroup.groupName.startsWith("Init (" + sourceDispId + "_")) {
        spdDialGroup.groupName = spdDialGroup.groupName.replaceFirst(
            sourceDispId + "_" + sourceShiftId, destDispId + "_" + destShiftId);
      }
      log.debug("Convert Speed Dial Group data to dispId=" +
                spdDialGroup.dispId +
                " group Id=" + spdDialGroup.groupId + ", group name=" +
                spdDialGroup.groupName);
    }
  }

  private void convertSubGroupData(ArrayList spdSubGroupList, String destDispId, String destShiftId) {
    for (int i = 0; i < spdSubGroupList.size(); i++) {
      SpeedDialSubGroup sdialSubGroup = (SpeedDialSubGroup) spdSubGroupList.get(
          i);
      sdialSubGroup.groupId = destDispId + "_" + destShiftId + sdialSubGroup.groupId.substring(5);
      sdialSubGroup.subGroupId = destDispId +  "_" + destShiftId +
                                 sdialSubGroup.subGroupId.substring(5);
      log.debug("Convert Speed Dial Subgroup data to group Id=" +
                sdialSubGroup.groupId +
                ", subGroup Id=" + sdialSubGroup.subGroupId);
    }
  }

  private void convertTabSetData(ArrayList tabsetList, String destDispId,String destShiftId) {
    for (int i = 0; i < tabsetList.size(); i++) {
      SpeedDialTabset spdDialTabset = (SpeedDialTabset) tabsetList.get(i);
      spdDialTabset.tabsetId = destDispId + "_" + destShiftId + spdDialTabset.tabsetId.substring(5);
      spdDialTabset.subGroupId = destDispId + "_" + destShiftId +
                                 spdDialTabset.subGroupId.substring(5);
      log.debug("Convert Speed Dial tabset data to tabset Id=" +
                spdDialTabset.tabsetId
                + ", subgroup Id=" + spdDialTabset.subGroupId);
    }
  }

  private void convertSelectedTabsetData(ArrayList selectedTabsetList,
                                         String destDispId, String destShiftId) {
    for (int i = 0; i < selectedTabsetList.size(); i++) {
      SpeedDialSelectedTabSet spdDialSelectedTabset = (SpeedDialSelectedTabSet)
          selectedTabsetList.get(i);
      spdDialSelectedTabset.dispId = destDispId;
      spdDialSelectedTabset.shiftId = destShiftId;
      spdDialSelectedTabset.tabSetId = destDispId + "_" + destShiftId +
                                       spdDialSelectedTabset.tabSetId.substring(5);
      log.debug("Convert selected Speed Dial tabset data to disp Id=" +
                spdDialSelectedTabset.dispId
                + ", tabset Id=" + spdDialSelectedTabset.tabSetId);
    }
  }

  private void convertTabData(ArrayList tabList, String destDispId, String destShiftId) {
    for (int i = 0; i < tabList.size(); i++) {
      SpeedDialTab spdDialTab = (SpeedDialTab) tabList.get(i);
      spdDialTab.tabId = destDispId + "_" + destShiftId +spdDialTab.tabId.substring(5);
      spdDialTab.tabsetId = destDispId + "_" + destShiftId + spdDialTab.tabsetId.substring(5);
      log.debug("Convert selected Speed Dial tab data to tab Id=" +
                spdDialTab.tabId +
                ", tabset Id=" + spdDialTab.tabsetId);
    }
  }

  private void convertRadioSpeedDialData(ArrayList radioSpeedDialList,
                                         String destDispId, String destShiftId) {
    for (int i = 0; i < radioSpeedDialList.size(); i++) {
      RadioSpeedDial radioSpeedDial = (RadioSpeedDial) radioSpeedDialList.get(i);
      radioSpeedDial.dispId = destDispId;
      radioSpeedDial.shiftId = destShiftId;
      radioSpeedDial.tabId = destDispId + "_" + destShiftId +radioSpeedDial.tabId.substring(5);
    }
  }

  private void convertPhoneSpeedDialData(ArrayList phoneSpeedDialList,
                                         String destDispId, String destShiftId) {
    for (int i = 0; i < phoneSpeedDialList.size(); i++) {
      PhoneSpeedDial phoneSpeedDial = (PhoneSpeedDial) phoneSpeedDialList.get(i);
      phoneSpeedDial.dispId = destDispId;
      phoneSpeedDial.shiftId = destShiftId;
      phoneSpeedDial.tabId = destDispId + "_" + destShiftId +phoneSpeedDial.tabId.substring(5);
    }
  }

  private ArrayList getSpeedDialGroup(String sourceDispId, String sourceShiftId) {
    String sql = "SELECT SDIAL_GRP_ID, RTI_DSPR_ID, RTI_SHIFT_ID, SDIAL_GRP_TYP, SDIAL_GRP_NME FROM RT.TRTI_SDIAL_GROUP WHERE RTI_DSPR_ID=? and RTI_SHIFT_ID=?";
    ArrayList groupList = new ArrayList();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      if (src_conn != null) {
        stmt = src_conn.prepareStatement(sql);
        stmt.setString(1, sourceDispId);
        stmt.setString(2, sourceShiftId);
        rs = stmt.executeQuery();
        while (rs.next()) {
          String groupId = rs.getString(1);
          if (groupId != null)
            groupId = groupId.trim();
          String dsId = rs.getString(2);
          if (dsId != null)
            dsId = dsId.trim();
          String shiftId = rs.getString(3);
          if (shiftId != null)
            shiftId = shiftId.trim();
          String groupType = rs.getString(4);
          if (groupType != null)
            groupType = groupType.trim();
          String groupName = rs.getString(5);
          if (groupName != null)
            groupName = groupName.trim();
          SpeedDialGroup spdialGroup = new SpeedDialGroup(groupId, dsId,
              shiftId, groupType, groupName);
          groupList.add(spdialGroup);
        }
      }
      log.info("Get Disp" + sourceDispId + " shift " + sourceShiftId + " speed dial group data, size = " + groupList.size());
    } catch (Exception e) {
      log.error("getSpeedDialGroup exception", e);
    } finally {
      cleanUp(stmt, rs);
    }

    return groupList;
  }

  private ArrayList getSpeedDialSubGroup(String sourceDispId, String sourceShiftId) {
    String sql = "SELECT SDIAL_SUBG_ID, SDIAL_GRP_ID, SDIAL_SUBG_NME FROM RT.TRTI_SDIAL_SUB_GRP WHERE SDIAL_SUBG_ID like '" +
                 sourceDispId + "_" + sourceShiftId + "_%'";
    ArrayList subGroupList = new ArrayList();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      if (src_conn != null) {
        stmt = src_conn.prepareStatement(sql);
        rs = stmt.executeQuery();
        while (rs.next()) {
          String subGroupId = rs.getString(1);
          if (subGroupId != null)
            subGroupId = subGroupId.trim();
          String groupId = rs.getString(2);
          if (groupId != null)
            groupId = groupId.trim();
          String subGroupName = rs.getString(3);
          if (subGroupName != null)
            subGroupName = subGroupName.trim();
          SpeedDialSubGroup spdialSubGroup = new SpeedDialSubGroup(subGroupId,
              groupId, subGroupName);
          subGroupList.add(spdialSubGroup);
        }
        log.info("Get Disp" + sourceDispId + " shift " + sourceShiftId + " speed dial subgroup data, size = " + subGroupList.size());
      }
    } catch (Exception e) {
      log.error("getSpeedDialSubGroup exception", e);
    } finally {
      cleanUp(stmt, rs);
    }
    return subGroupList;
  }

  private ArrayList getSpeedDialTabSet(String sourceDispId, String shiftId) {
    String sql = "SELECT SDIAL_TBST_ID, SDIAL_SUBG_ID, SDIAL_TBST_NME FROM RT.TRTI_SDIAL_TABSET WHERE SDIAL_TBST_ID like '" +
                 sourceDispId + "_" + shiftId+ "_%'";
    ArrayList tabsetList = new ArrayList();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      if (src_conn != null) {
        stmt = src_conn.prepareStatement(sql);
        rs = stmt.executeQuery();
        while (rs.next()) {
          String tabsetId = rs.getString(1);
          if (tabsetId != null)
            tabsetId = tabsetId.trim();
          String subGroupId = rs.getString(2);
          if (subGroupId != null)
            subGroupId = subGroupId.trim();
          String tabsetName = rs.getString(3);
          if (tabsetName != null)
            tabsetName = tabsetName.trim();
          SpeedDialTabset spdialTabset = new SpeedDialTabset(tabsetId,
              subGroupId, tabsetName);
          tabsetList.add(spdialTabset);
        }

        log.info("Get Disp" + sourceDispId + " shift " + shiftId + " speed dial tabset, size = " + tabsetList.size());
      }
    } catch (Exception e) {
      log.error("getSpeedDialTabSet exception", e);
    } finally {
      cleanUp(stmt, rs);
    }
    return tabsetList;
  }

  private ArrayList getSpeedDialTabList(String sourceDispId, String srcShiftId) {
    String sql = "SELECT SDIAL_TAB_ID, SDIAL_TAB_NME, SDIAL_TAB_IDX, SDIAL_TBST_ID FROM RT.TRTI_SDIAL_TAB WHERE SDIAL_TAB_ID like '" +
                 sourceDispId + "_" + srcShiftId+ "_%'";
    ArrayList tabList = new ArrayList();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      if (src_conn != null) {
        stmt = src_conn.prepareStatement(sql);
        rs = stmt.executeQuery();
        while (rs.next()) {
          String tabId = rs.getString(1);
          if (tabId != null)
            tabId = tabId.trim();

          String tabName = rs.getString(2);
          if (tabName != null)
            tabName = tabName.trim();

          int tabIndex = rs.getInt(3);
          String tabsetId = rs.getString(4);
          if (tabsetId != null)
            tabsetId = tabsetId.trim();
          SpeedDialTab spdialTab = new SpeedDialTab(tabId, tabName, tabIndex,
              tabsetId);
          tabList.add(spdialTab);
        }
        log.info("Get Disp" + sourceDispId + " shift " + srcShiftId + " speed dial tab data, size = " + tabList.size());
      }
    } catch (Exception e) {
      log.error("getSpeedDialTabList exception", e);
    } finally {
      cleanUp(stmt, rs);
    }
    return tabList;
  }

  private ArrayList getSelectedSpeedDialTabSet(String sourceDispId, String sourceShiftId) {
    String sql = "SELECT RTI_DSPR_ID, RTI_SHIFT_ID, SDIAL_GRP_TYP, SDIAL_TBST_ID FROM RT.TRTI_SEL_TABSET WHERE RTI_DSPR_ID=? and RTI_SHIFT_ID=?";
    ArrayList tabSetList = new ArrayList();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      if (src_conn != null) {
        stmt = src_conn.prepareStatement(sql);
        stmt.setString(1, sourceDispId);
        stmt.setString(2, sourceShiftId);
        rs = stmt.executeQuery();
        while (rs.next()) {
          String dsId = rs.getString(1);
          if (dsId != null)
            dsId = dsId.trim();
          String shiftId = rs.getString(2);
          if (shiftId != null)
            shiftId = shiftId.trim();
          String groupType = rs.getString(3);
          if (groupType != null)
            groupType = groupType.trim();
          String tabSetId = rs.getString(4);
          if (tabSetId != null)
            tabSetId = tabSetId.trim();

          SpeedDialSelectedTabSet spdialTabSet = new SpeedDialSelectedTabSet(
              dsId, shiftId, groupType, tabSetId);
          tabSetList.add(spdialTabSet);
        }
        log.info("Get Disp" + sourceDispId + " shift " + sourceShiftId + " speed dial selected tabset data, size = " + tabSetList.size());
      }
    } catch (Exception e) {
      log.error("getSelectedSpeedDialTabSet exception", e);
    } finally {
      cleanUp(stmt, rs);
    }
    return tabSetList;
  }

  private ArrayList getPhoneSpeedDialList(String sourceDispId, String sourceShiftId) {
    String sql =
        "SELECT RTI_DSPR_ID, RTI_SHIFT_ID, RTI_SDIAL_NBR, RTI_SDIAL_DESC, " +
        "RTI_SDIAL_TYP, RTI_TONE_NBR, SDIAL_TAB_ID, SCRN_ROW_NBR, SCRN_COL_NBR, DSPR_CWP_ID, FRGR_COLOR, BKGR_COLOR " +
        " FROM RT.TRTI_DSPR_SPD_DIAL WHERE RTI_DSPR_ID=? and RTI_SHIFT_ID=? and SDIAL_TAB_ID is not null";
    ArrayList spdialList = new ArrayList();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      if (src_conn != null) {
        stmt = src_conn.prepareStatement(sql);
        stmt.setString(1, sourceDispId);
        stmt.setString(2, sourceShiftId);
        rs = stmt.executeQuery();
        while (rs.next()) {
          String dsId = rs.getString(1);
          if (dsId != null)
            dsId = dsId.trim();
          String shiftId = rs.getString(2);
          if (shiftId != null)
            shiftId = shiftId.trim();
          String sDialNumber = rs.getString(3);
          if (sDialNumber != null)
            sDialNumber = sDialNumber.trim();
          String sDialDesc = rs.getString(4);
          if (sDialDesc != null)
            sDialDesc = sDialDesc.trim();
          String sDialType = rs.getString(5);
          if (sDialType != null)
            sDialType = sDialType.trim();
          String toneNumber = rs.getString(6);
          if (toneNumber != null)
            toneNumber = toneNumber.trim();
          String sDialTabId = rs.getString(7);
          if (sDialTabId != null)
            sDialTabId = sDialTabId.trim();
          int rowNumber = rs.getInt(8);
          int columnNumber = rs.getInt(9);
          int cwpId = rs.getInt(10);
          String foreColorStr=rs.getString(11);
          if(foreColorStr != null)
        	  foreColorStr=foreColorStr.trim();
          if(foreColorStr==null)
        	  foreColorStr="";
          String bgColorStr=rs.getString(12);
          if(bgColorStr != null)
        	  bgColorStr=bgColorStr.trim();
          if(bgColorStr==null)
        	  bgColorStr="";
          PhoneSpeedDial phoneSpeedDial = new PhoneSpeedDial(dsId, shiftId,
              sDialNumber, sDialDesc, sDialType,
              toneNumber, sDialTabId, rowNumber, columnNumber, cwpId, foreColorStr, bgColorStr);
          spdialList.add(phoneSpeedDial);
        }
        log.info("Get Disp" + sourceDispId + " shift " + sourceShiftId + " phone speed dial data, size = " + spdialList.size());
      }
    } catch (Exception e) {
      log.error("getPhoneSpeedDialList exception", e);
    } finally {
      cleanUp(stmt, rs);
    }
    return spdialList;
  }
  
  /*private ArrayList getPhoneSpeedDialList(String sourceDispId) {
    String sql =
        "SELECT RTI_DSPR_ID, RTI_SHIFT_ID, RTI_SDIAL_NBR, RTI_SDIAL_DESC, " +
        "RTI_SDIAL_TYP, RTI_TONE_NBR, SDIAL_TAB_ID, SCRN_ROW_NBR, SCRN_COL_NBR, DSPR_CWP_ID " +
        " FROM RT.TRTI_DSPR_SPD_DIAL WHERE RTI_DSPR_ID=? and SDIAL_TAB_ID is not null";
    ArrayList spdialList = new ArrayList();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      if (src_conn != null) {
        stmt = src_conn.prepareStatement(sql);
        stmt.setString(1, sourceDispId);
        rs = stmt.executeQuery();
        while (rs.next()) {
          String dsId = rs.getString(1);
          if (dsId != null)
            dsId = dsId.trim();
          String shiftId = rs.getString(2);
          if (shiftId != null)
            shiftId = shiftId.trim();
          String sDialNumber = rs.getString(3);
          if (sDialNumber != null)
            sDialNumber = sDialNumber.trim();
          String sDialDesc = rs.getString(4);
          if (sDialDesc != null)
            sDialDesc = sDialDesc.trim();
          String sDialType = rs.getString(5);
          if (sDialType != null)
            sDialType = sDialType.trim();
          String toneNumber = rs.getString(6);
          if (toneNumber != null)
            toneNumber = toneNumber.trim();
          String sDialTabId = rs.getString(7);
          if (sDialTabId != null)
            sDialTabId = sDialTabId.trim();
          int rowNumber = rs.getInt(8);
          int columnNumber = rs.getInt(9);
          int cwpId = rs.getInt(10);
          PhoneSpeedDial phoneSpeedDial = new PhoneSpeedDial(dsId, shiftId,
              sDialNumber, sDialDesc, sDialType,
              toneNumber, sDialTabId, rowNumber, columnNumber, cwpId);
          spdialList.add(phoneSpeedDial);
        }
        log.info("Get get PhoneSpeedDialList, size = " + spdialList.size());
      }
    } catch (Exception e) {
      log.error("getPhoneSpeedDialList exception", e);
    } finally {
      cleanUp(stmt, rs);
    }
    return spdialList;
  }*/
  private ArrayList getRadioSpeedDialList(String sourceDispId, String sourceShiftId) {
    String sql =
        "SELECT RTI_DSPR_ID, RTI_SHIFT_ID, RTI_RADIO_ID, SDIAL_TAB_ID, " +
        "SCRN_ROW_NBR, SCRN_COL_NBR " +
        " FROM RT.TRTI_DRAD_SDIAL WHERE RTI_DSPR_ID=? and RTI_SHIFT_ID=?";
    ArrayList spdialList = new ArrayList();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      if (src_conn != null) {
        stmt = src_conn.prepareStatement(sql);
        stmt.setString(1, sourceDispId);
        stmt.setString(2, sourceShiftId);
        rs = stmt.executeQuery();
        while (rs.next()) {
          String dsId = rs.getString(1);
          if (dsId != null)
            dsId = dsId.trim();
          String shiftId = rs.getString(2);
          if (shiftId != null)
            shiftId = shiftId.trim();
          String radioId = rs.getString(3);
          if (radioId != null)
            radioId = radioId.trim();
          String sDialTabId = rs.getString(4);
          if (sDialTabId != null)
            sDialTabId = sDialTabId.trim();

          int rowNumber = rs.getInt(5);
          int columnNumber = rs.getInt(6);

          RadioSpeedDial radioSpeedDial = new RadioSpeedDial(dsId, shiftId,
              radioId, sDialTabId, rowNumber, columnNumber);
          spdialList.add(radioSpeedDial);
        }
        log.info("Get Disp" + sourceDispId + " shift " + sourceShiftId + " radio speed dial data, size = " + spdialList.size());
      }
    } catch (Exception e) {
      log.error("getRadioSpeedDialList exception", e);
    } finally {
      cleanUp(stmt, rs);
    }
    return spdialList;
  }

  public ArrayList getAreaCodeList(int pbxSite) {
    ArrayList areaCodeList = new ArrayList();
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      String sql = "select * from RT.TRTI_LOCL_ACD where PBX_SITE_ID=?";
      stmt = src_conn.prepareStatement(sql);
      stmt.setInt(1, pbxSite);
      rs = stmt.executeQuery();
      while (rs.next()) {
        String areaCode = rs.getString(1);
        if (areaCode != null) {
          areaCode = areaCode.trim();
          areaCodeList.add(areaCode);
        }
      }
    } catch (Exception e) {
      log.error("getAreaCodeList exception", e);
    } finally {
      cleanUp(stmt, rs);
    }
    return areaCodeList;
  }

  public Hashtable getOfficeCodeList(int pbxSite) {
    String sql = "select LOCL_OCD, PBX_SITE_ID, LOCL_ACD from RT.TRTI_LOCL_OCD WHERE PBX_SITE_ID=?";
    PreparedStatement stmt = null;
    ResultSet rs = null;
    Hashtable offCodeList = new Hashtable();
    try {
      stmt = src_conn.prepareStatement(sql);
      stmt.setInt(1, pbxSite);
      rs = stmt.executeQuery();
      while (rs.next()) {
        String officeCD = rs.getString(1);
        int siteId = rs.getInt(2);
        String areaCD = rs.getString(3);
        if (officeCD != null && areaCD != null) {
          officeCD = officeCD.trim();
          areaCD = areaCD.trim();
          OfficeCdData data = new OfficeCdData(siteId, officeCD,
                                               areaCD);
          offCodeList.put(officeCD, data);
        }
      }
    } catch (Exception e) {
      log.error("getOfficeCodeList exception", e);
    } finally {
      cleanUp(stmt, rs);
    }
    return offCodeList;
  }

  protected void cleanUp(Statement stmt, ResultSet rs) {
    try {
      if (stmt != null)
        stmt.close();
      if (rs != null)
        rs.close();
    } catch (Exception e) {
      log.error("Exception", e);
    }
  }
  
  private boolean copySpeedDialData(DispatcherSpeedDialData dispSpdialData)
  {
	  boolean status=false;
	  String delete_phone_spdial_sql = "DELETE from RT.TRTI_DSPR_SPD_DIAL WHERE RTI_DSPR_ID=? and RTI_SHIFT_ID=?";
	  String delete_radio_spdial_sql = "DELETE from RT.TRTI_DRAD_SDIAL WHERE RTI_DSPR_ID=? and RTI_SHIFT_ID=?";
	  String delete_vsg_spdial_sql = "DELETE from RT.TRTI_VSG_SDIAL WHERE RTI_DSPR_ID=? and RTI_SHIFT_ID=?";
	  String delete_tab_sql = "DELETE from RT.TRTI_SDIAL_TAB WHERE SDIAL_TAB_ID like '" +
			  dispSpdialData.dispId + "_" + dispSpdialData.shiftId+ "_%'";
	  String delete_sel_tabset_sql = "DELETE from RT.TRTI_SEL_TABSET WHERE RTI_DSPR_ID =? and RTI_SHIFT_ID=?";
	  String delete_tabset_sql = "DELETE from RT.TRTI_SDIAL_TABSET WHERE SDIAL_TBST_ID like '" +
			  dispSpdialData.dispId + "_" + dispSpdialData.shiftId+ "_%'";
	  String delete_subgrp_sql = "DELETE from RT.TRTI_SDIAL_SUB_GRP WHERE SDIAL_SUBG_ID like '" +
			  dispSpdialData.dispId + "_" + dispSpdialData.shiftId + "_%'";
	  String delete_grp_sql = "DELETE from RT.TRTI_SDIAL_GROUP WHERE RTI_DSPR_ID=? and RTI_SHIFT_ID=?";
	  
	  String select_shift_sql="SELECT RTI_SHIFT_ID FROM RT.TRTI_DSPR_SHIFT WHERE RTI_DSPR_ID=?";
	  PreparedStatement select_shift_stmt = null;
	  ResultSet rs=null;
	  
	  String insert_shift_sql="INSERT INTO RT.TRTI_DSPR_SHIFT(RTI_DSPR_ID, RTI_SHIFT_ID) VALUES(?,?)";
	  PreparedStatement insert_shift_stmt = null;
	  
	  String insert_group_sql = "INSERT INTO RT.TRTI_SDIAL_GROUP(SDIAL_GRP_ID, RTI_DSPR_ID, RTI_SHIFT_ID, SDIAL_GRP_TYP, SDIAL_GRP_NME) " +
              " values(?, ?, ?, ?, ?)";
	  String insert_subGroup_sql = "INSERT INTO RT.TRTI_SDIAL_SUB_GRP(SDIAL_SUBG_ID, SDIAL_GRP_ID, SDIAL_SUBG_NME) " +
              " values(?, ?, ?)";	 
	  String insert_tabset_sql = "INSERT INTO RT.TRTI_SDIAL_TABSET(SDIAL_TBST_ID, SDIAL_SUBG_ID, SDIAL_TBST_NME) " +
              " values(?, ?, ?)";
	  String insert_sel_tabset_sql ="INSERT INTO RT.TRTI_SEL_TABSET(RTI_DSPR_ID, RTI_SHIFT_ID, SDIAL_GRP_TYP,SDIAL_TBST_ID) values(?,?,?,?)";
	  String insert_tab_sql = "INSERT INTO RT.TRTI_SDIAL_TAB(SDIAL_TAB_ID, SDIAL_TAB_NME, SDIAL_TAB_IDX, SDIAL_TBST_ID) " +
              " values(?, ?, ?, ?)";
	  String insert_phn_spdial_sql =
		        "INSERT INTO RT.TRTI_DSPR_SPD_DIAL(RTI_DSPR_ID, RTI_SHIFT_ID, " +
		        "RTI_SDIAL_NBR, RTI_SDIAL_DESC, RTI_SDIAL_TYP, RTI_TONE_NBR, " +
		        "SDIAL_TAB_ID, SCRN_ROW_NBR, SCRN_COL_NBR, DSPR_CWP_ID, FRGR_COLOR, BKGR_COLOR) " +
		        " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";	  
	  String insert_radio_spdial_sql = "INSERT INTO RT.TRTI_DRAD_SDIAL(RTI_DSPR_ID, RTI_SHIFT_ID, " +
              "RTI_RADIO_ID, " +
              "SDIAL_TAB_ID, SCRN_ROW_NBR, SCRN_COL_NBR) " +
              " values(?, ?, ?, ?, ?, ?)"; 
 
	  PreparedStatement delete_phone_spdial_stmt = null;
	  PreparedStatement delete_radio_spdial_stmt = null;
	  PreparedStatement delete_vsg_spdial_stmt = null;
	  PreparedStatement delete_tab_stmt = null;
	  PreparedStatement delete_sel_tabset_stmt = null;
	  PreparedStatement delete_tabset_stmt = null;
	  PreparedStatement delete_subgrp_stmt=null;
	  PreparedStatement delete_grp_stmt = null;
	  PreparedStatement insert_group_stmt = null;
	  PreparedStatement insert_subGroup_stmt = null;
	  PreparedStatement insert_tabset_stmt = null;
	  PreparedStatement insert_sel_tabset_stmt = null;
	  PreparedStatement insert_tab_stmt = null;
	  PreparedStatement insert_phn_spdial_stmt = null;
	  PreparedStatement insert_radio_spdial_stmt = null;
	  
	  String dispId=dispSpdialData.dispId;
	  String shiftId=dispSpdialData.shiftId;
	  try{		 
		  dest_conn.setAutoCommit(false);
		  
		  delete_phone_spdial_stmt = dest_conn.prepareStatement(delete_phone_spdial_sql);
		  delete_phone_spdial_stmt.setString(1, dispSpdialData.dispId);
		  delete_phone_spdial_stmt.setString(2, dispSpdialData.shiftId);
	      int n = delete_phone_spdial_stmt.executeUpdate();
	      log.debug("Delete " + n + " phone speed dial data for from Disp" + dispId + " of shift " + shiftId);
	      
	      delete_radio_spdial_stmt = dest_conn.prepareStatement(delete_radio_spdial_sql);
	      delete_radio_spdial_stmt.setString(1, dispSpdialData.dispId);
	      delete_radio_spdial_stmt.setString(2, dispSpdialData.shiftId);
	      n = delete_radio_spdial_stmt.executeUpdate();
	      log.debug("Delete " + n + " radio speed dial data from Disp" + dispId + " of shift " + shiftId);
	      
	      try{
	    	  delete_vsg_spdial_stmt = dest_conn.prepareStatement(delete_vsg_spdial_sql);
	    	  delete_vsg_spdial_stmt.setString(1, dispSpdialData.dispId);
	    	  delete_vsg_spdial_stmt.setString(2, dispSpdialData.shiftId);
	    	  n = delete_vsg_spdial_stmt.executeUpdate();
	    	  log.debug("Delete " + n + " vsg speed dial data from Disp" + dispId + " of shift " + shiftId);
	      }
	      catch(Exception e)
	      {
	    	  log.warn("delete VSG speed dial data exception", e);
	      }
	      
	      delete_tab_stmt = dest_conn.prepareStatement(delete_tab_sql);
	      n = delete_tab_stmt.executeUpdate();
	      log.debug("Delete " + n + " speed dial tab data from Disp" + dispId + " of shift " + shiftId);
	      
	      delete_sel_tabset_stmt = dest_conn.prepareStatement(delete_sel_tabset_sql);
	      delete_sel_tabset_stmt.setString(1, dispSpdialData.dispId);
	      delete_sel_tabset_stmt.setString(2, dispSpdialData.shiftId);
	      n = delete_sel_tabset_stmt.executeUpdate();
	      log.debug("Delete " + n + " selected speed dial tabset data from Disp" + dispId + " of shift " + shiftId);
	      
	      delete_tabset_stmt = dest_conn.prepareStatement(delete_tabset_sql);
	      n = delete_tabset_stmt.executeUpdate();
	      log.debug("Delete " + n + " speed dial tabset data from Disp" + dispId + " of shift " + shiftId);
	      
	      delete_subgrp_stmt = dest_conn.prepareStatement(delete_subgrp_sql);
	      n = delete_subgrp_stmt.executeUpdate();
	      log.debug("Delete " + n + " speed dial subgroup data from Disp" + dispId + " of shift " + shiftId);
	      
	      delete_grp_stmt = dest_conn.prepareStatement(delete_grp_sql);
	      delete_grp_stmt.setString(1, dispSpdialData.dispId);
	      delete_grp_stmt.setString(2, dispSpdialData.shiftId);
	      n = delete_grp_stmt.executeUpdate();
	      log.debug("Delete " + n + " speed dial group data from Disp" + dispId + " of shift " + shiftId);
	      
	      //insert shift Id
	      ArrayList<String> shiftList=new ArrayList<String>();
	      select_shift_stmt = dest_conn.prepareStatement(select_shift_sql);
	      select_shift_stmt.setString(1, dispId);
	      rs = select_shift_stmt.executeQuery();
	      while(rs.next())
	      {
	    	  String shId= rs.getString(1);
	    	  if(shId != null)
	    	  {
	    		  shId = shId.trim();
	    		  shiftList.add(shId);
	    	  }
	      }
	      
	      if(!shiftList.contains(shiftId))
	      {
	    	  insert_shift_stmt = dest_conn.prepareStatement(insert_shift_sql);	           
	    	  insert_shift_stmt.setString(1, dispId);
	    	  insert_shift_stmt.setString(2, shiftId);
	    	  insert_shift_stmt.executeUpdate();
	          
	          log.info("Inserted shift " + shiftId + " into database for Disp" + dispId + ".");
	      }
	      
	      //insert group
	      insert_group_stmt = dest_conn.prepareStatement(insert_group_sql);
	      for (int i = 0; i < dispSpdialData.speedDialGroupList.size(); i++) {
	        SpeedDialGroup spdDialGroup = (SpeedDialGroup) dispSpdialData.speedDialGroupList.get(i);
	        insert_group_stmt.setString(1, spdDialGroup.groupId);
	        insert_group_stmt.setString(2, spdDialGroup.dispId);
	        insert_group_stmt.setString(3, spdDialGroup.shiftId);
	        insert_group_stmt.setString(4, spdDialGroup.groupType);
	        insert_group_stmt.setString(5, spdDialGroup.groupName);        
	        insert_group_stmt.executeUpdate();
	      }
	      log.info("inserted " + dispSpdialData.speedDialGroupList.size() + " speed dial group data for Disp" + dispId + " of shift " + shiftId+ ".");
		  
	      insert_subGroup_stmt = dest_conn.prepareStatement(insert_subGroup_sql);
	      for (int i = 0; i < dispSpdialData.speedDialSubGroupList.size(); i++) {
	        SpeedDialSubGroup spdDialSubGroup = (SpeedDialSubGroup)dispSpdialData.speedDialSubGroupList.get(i);
	        insert_subGroup_stmt.setString(1, spdDialSubGroup.subGroupId);
	        insert_subGroup_stmt.setString(2, spdDialSubGroup.groupId);
	        insert_subGroup_stmt.setString(3, spdDialSubGroup.subGroupName);
	        insert_subGroup_stmt.executeUpdate();
	      }
	      log.info("Inserted " + dispSpdialData.speedDialSubGroupList.size() +
	               " speed dial subgroup data for Disp" + dispId + " of shift " + shiftId+ ".");
	      
	      insert_tabset_stmt = dest_conn.prepareStatement(insert_tabset_sql);
	      for (int i = 0; i < dispSpdialData.speedDialTabsetList.size(); i++) {
	        SpeedDialTabset spdDialTabset = (SpeedDialTabset) dispSpdialData.speedDialTabsetList.get(i);
	        insert_tabset_stmt.setString(1, spdDialTabset.tabsetId);
	        insert_tabset_stmt.setString(2, spdDialTabset.subGroupId);
	        insert_tabset_stmt.setString(3, spdDialTabset.tabsetName);
	        insert_tabset_stmt.executeUpdate();
	      }
	      log.info("Inserted " + dispSpdialData.speedDialTabsetList.size() + " speed dial tabset data for Disp" + dispId + " of shift " + shiftId+ ".");
	      
	      insert_sel_tabset_stmt = dest_conn.prepareStatement(insert_sel_tabset_sql);
	      for (int i = 0; i < dispSpdialData.speedDialSelectedTabset.size(); i++) {
	    	  SpeedDialSelectedTabSet selectedSpdDialTabset = (SpeedDialSelectedTabSet) dispSpdialData.speedDialSelectedTabset.get(i);
	    	insert_sel_tabset_stmt.setString(1, selectedSpdDialTabset.dispId);
	    	insert_sel_tabset_stmt.setString(2, selectedSpdDialTabset.shiftId);
	    	insert_sel_tabset_stmt.setString(3, selectedSpdDialTabset.groupType);
	    	insert_sel_tabset_stmt.setString(4, selectedSpdDialTabset.tabSetId);
	    	insert_sel_tabset_stmt.executeUpdate();
	      }	      
	      log.info("Inserted " + dispSpdialData.speedDialSelectedTabset.size() + " selected speed dial tabset data for Disp" + dispId + " of shift " + shiftId+ ".");
	      
	      insert_tab_stmt = dest_conn.prepareStatement(insert_tab_sql);
	      for (int i = 0; i < dispSpdialData.speedDialTabList.size(); i++) {
	        SpeedDialTab spdDialTab = (SpeedDialTab) dispSpdialData.speedDialTabList.get(i);
	        insert_tab_stmt.setString(1, spdDialTab.tabId);
	        insert_tab_stmt.setString(2, spdDialTab.tabName);
	        insert_tab_stmt.setInt(3, spdDialTab.tabIndex);
	        insert_tab_stmt.setString(4, spdDialTab.tabsetId);
	        insert_tab_stmt.executeUpdate();
	      }
	      log.info("Insert " + dispSpdialData.speedDialTabList.size() + " speed dial tab data for Disp" + dispId + " of shift " + shiftId+ ".");
	      
	      insert_phn_spdial_stmt = dest_conn.prepareStatement(insert_phn_spdial_sql);
	      for (int i = 0; i < dispSpdialData.phoneSpeedDialList.size(); i++) {
	        PhoneSpeedDial phoneSpeedDial = (PhoneSpeedDial) dispSpdialData.phoneSpeedDialList.get(i);
	        insert_phn_spdial_stmt.setString(1, phoneSpeedDial.dispId);
	        insert_phn_spdial_stmt.setString(2, phoneSpeedDial.shiftId);
	        insert_phn_spdial_stmt.setString(3, phoneSpeedDial.speedDialNumber);
	        insert_phn_spdial_stmt.setString(4, phoneSpeedDial.speedDialDesc);
	        insert_phn_spdial_stmt.setString(5, phoneSpeedDial.speedDialType);
	        insert_phn_spdial_stmt.setString(6, phoneSpeedDial.toneNumber);
	        insert_phn_spdial_stmt.setString(7, phoneSpeedDial.tabId);
	        insert_phn_spdial_stmt.setInt(8, phoneSpeedDial.rowNumber);
	        insert_phn_spdial_stmt.setInt(9, phoneSpeedDial.columnNumber);
	        insert_phn_spdial_stmt.setInt(10, phoneSpeedDial.cwpId);
	        insert_phn_spdial_stmt.setString(11, phoneSpeedDial.foreColorStr);
	        insert_phn_spdial_stmt.setString(12, phoneSpeedDial.bgColorStr);
	        insert_phn_spdial_stmt.addBatch();
	      }
	      int[] numUpdate=insert_phn_spdial_stmt.executeBatch();
	      int count=0;
	      for(int i=0; i<numUpdate.length; i++)
	    	  count=count + numUpdate[i];
	      log.info("Inserted " + count +
	               " phone speed dial data for Disp" + dispId + " of shift " + shiftId+ ".");
	      
	      insert_radio_spdial_stmt = dest_conn.prepareStatement(insert_radio_spdial_sql);
	      for (int i = 0; i < dispSpdialData.radioSpeedDialList.size(); i++) {
	        RadioSpeedDial radioSpeedDial = (RadioSpeedDial) dispSpdialData.radioSpeedDialList.get(i);
	        insert_radio_spdial_stmt.setString(1, radioSpeedDial.dispId);
	        insert_radio_spdial_stmt.setString(2, radioSpeedDial.shiftId);
	        insert_radio_spdial_stmt.setString(3, radioSpeedDial.radioId);
	        insert_radio_spdial_stmt.setString(4, radioSpeedDial.tabId);
	        insert_radio_spdial_stmt.setInt(5, radioSpeedDial.rowNumber);
	        insert_radio_spdial_stmt.setInt(6, radioSpeedDial.columnNumber);
	        insert_radio_spdial_stmt.addBatch();
	      }
	      numUpdate=insert_radio_spdial_stmt.executeBatch();
	      count=0;
	      for(int i=0; i<numUpdate.length; i++)
	    	  count=count + numUpdate[i];
	      log.info("Inserted " + count +
	               " radio speed dial data for Disp" + dispId + " of shift " + shiftId+ ".");
	      
		  dest_conn.commit();
		  status=true;
		  
	  }catch(Exception e)
	  {
		  log.error("Error happen when copy speed dial", e);
	  }
	  finally{
		  try{
			  if(select_shift_stmt != null)
				  select_shift_stmt.close();
			  if(rs != null)
				  rs.close();
			  if(delete_phone_spdial_stmt != null)
				  delete_phone_spdial_stmt.close();
			  if(delete_radio_spdial_stmt != null)
				  delete_radio_spdial_stmt.close();
			  if(delete_tab_stmt != null)
				  delete_tab_stmt.close();
			  if(delete_sel_tabset_stmt != null)
				  delete_sel_tabset_stmt.close();
			  if(delete_tabset_stmt != null)
				  delete_tabset_stmt.close();
			  if(delete_subgrp_stmt !=null)
				  delete_subgrp_stmt.close();
			  if(delete_grp_stmt != null)
				  delete_grp_stmt.close();
			  if(insert_group_stmt != null)
				  insert_group_stmt.close();
			  if(insert_subGroup_stmt != null)
				  insert_subGroup_stmt.close();
			  if(insert_tabset_stmt != null)
				  insert_tabset_stmt.close();
			  if(insert_tab_stmt != null)
				  insert_tab_stmt.close();
			  if(insert_phn_spdial_stmt != null)
				  insert_phn_spdial_stmt.close();
			  if(insert_radio_spdial_stmt != null)
				  insert_radio_spdial_stmt.close();
		  }
		  catch(Exception e)
		  {
			  log.error("exception when close statement");
		  }
		  if(!status)
		  {
			  log.error("Error happen when copy speed dial, roll back");
			  try{
				  if(dest_conn != null)
				  {
					  dest_conn.rollback();
				  }
			  }
			  catch(Exception e)
			  {
				  log.error("exception when roll back");
			  }
		  }
		  try{
			  dest_conn.setAutoCommit(true);
		  }
		  catch(Exception e)
		  {
			  log.error("exception when set auto commit.");
		  }
	  }
	  return status;
  }
  
  public boolean sameSourceAndDestDB()
  {
	  if(src_url != null && dest_url != null && src_url.equalsIgnoreCase(dest_url) &&
			  src_databaseName != null && dest_databaseName != null &&
			  src_databaseName.equalsIgnoreCase(dest_databaseName))
		  return true;
	  return false;				  
  }
}
