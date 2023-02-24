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
public class SpeedDialGroup {
 public String dispId;
 public String shiftId;
 public String groupType;
 public String groupId;
 public String groupName;

 public SpeedDialGroup(String gpId, String dsId, String shftId, String gpType, String gpName)
 {
   groupId=gpId;
   dispId=dsId;
   shiftId=shftId;
   groupType=gpType;
   groupName=gpName;
 }

@Override
public String toString() {
	return "SpeedDialGroup [dispId=" + dispId + ", shiftId=" + shiftId
			+ ", groupType=" + groupType + ", groupId=" + groupId
			+ ", groupName=" + groupName + "]";
}
 
 
}
