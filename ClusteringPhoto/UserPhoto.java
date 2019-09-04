/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clusteringphoto;

/**
 *
 * @author Majid
 */

import java.util.Date;
public class UserPhoto implements Comparable<UserPhoto> {


    public String photoId;
    public int ID;
    public String ownerName;
    public String ownerId;
    public GeoTag geotag;
    public String photoTitle;
  //  public long dateTaken;
    public int ClusterID=0;
    public GeoTag location;
    public PhotoAnnotation annotations;

    public Date timeTaken;


     public int compareTo(UserPhoto o) {
        return this.ClusterID-o.ClusterID;
    }

     public UserPhoto(){
         geotag=new GeoTag();
         annotations=new PhotoAnnotation();
     }


     public boolean checkequal(UserPhoto obc)
    {
        if(this.geotag.lat==obc.geotag.lat&&this.geotag.lng==obc.geotag.lng)
        {
            return true;
        }
        return false;
    }

}
