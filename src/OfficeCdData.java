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
public class OfficeCdData
{
        int pbxSite = -1;
        String officeCd="";
        String areaCd = "";

        public OfficeCdData(int inSite, String inOfficeCd, String inAreaCd)
        {
                pbxSite = inSite;
                officeCd = inOfficeCd;
                areaCd = inAreaCd;
        }

        public int getPBXSite()
        {
                return pbxSite;
        }
        public String getOfficeCd()
        {
                return officeCd;
        }
        public String getAreaCd()
        {
                return areaCd;
        }
}
