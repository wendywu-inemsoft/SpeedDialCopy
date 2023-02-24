import java.util.ArrayList;


public class DispatcherSpeedDialData {
	public String dispId;
	public String shiftId;
	public  ArrayList speedDialGroupList;
	public ArrayList speedDialSubGroupList;
	public ArrayList speedDialTabsetList;
	public ArrayList speedDialTabList;
	public ArrayList speedDialSelectedTabset;
	public  ArrayList phoneSpeedDialList;
	public ArrayList radioSpeedDialList;
	
	public DispatcherSpeedDialData(String dsId, String shId, ArrayList speedDialGroupList,
			ArrayList speedDialSubGroupList, ArrayList speedDialTabsetList,
			ArrayList speedDialTabList, ArrayList speedDialSelectedTabset,
			ArrayList phoneSpeedDialList, ArrayList radioSpeedDialList) {
		super();
		this.dispId=dsId;
		this.shiftId=shId;
		this.speedDialGroupList = new ArrayList(speedDialGroupList);
		this.speedDialSubGroupList = new ArrayList(speedDialSubGroupList);
		this.speedDialTabsetList = new ArrayList(speedDialTabsetList);
		this.speedDialTabList = new ArrayList(speedDialTabList);
		this.speedDialSelectedTabset = new ArrayList(speedDialSelectedTabset);
		this.phoneSpeedDialList = new ArrayList(phoneSpeedDialList);
		this.radioSpeedDialList = new ArrayList(radioSpeedDialList);
	}
	
	

}
