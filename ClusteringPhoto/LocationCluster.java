/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clusteringphoto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Date;


/**
 *
 * @author MajidGe
 */
public class LocationCluster {

    public int ClusterID=0;
    public List<UserPhoto> locationcluster=new ArrayList();

     public List<UserPhoto> sublocationcluster=new ArrayList();

    public void addLocationToCluster(UserPhoto location){
        this.locationcluster.add(location);
    }
    private double centroidLng;
    private double centroidLat;
    private GeoTag  centriodPoint=new GeoTag();

    //***************************************** Name for these collection of locations
    public List<String> tagCollection=new ArrayList(); //used to store collections of title
 //   public List<String> AnnotationCollection=new ArrayList();
 
   
    public String finalNameofPlace="";
    public String TypeofPlace="*******";
    public String placeID="";

    public int userCount=0;
    public int photoCount=0;

    //*************************************************final semantic data*************************************************
   
    public String MixtureResul="";
    public String MixtureMethod="";

    //************************************************************************
    



    //***********************************************************

   public int totalVisits=0;
   public int noOfUniqueVisitors=0;
   public int MaxNoofSingleVisitor=0;
   public int MinNoofSingleVisitor=0;
   public double disVisitDay=0.0;
   public double disVisitWeek=0.0;
   public double noOfStartTS=0;
   public double noofEndTS=0;
   public double entropy=0.0;
   public List<Integer>Vul=new ArrayList();
 
   //**********************************

     public GeoTag GetCentriod()
    {
        double sumLng = 0.0;
        double sumLat = 0.0;
        int posCnt = this.locationcluster.size();
        for (int i=0; i<this.locationcluster.size(); i++) {
          //  Point point = this.locationcluster.get(i).getCentriod();

            sumLng += this.locationcluster.get(i).geotag.lng;
            sumLat += this.locationcluster.get(i).geotag.lat;
        }
        this.centroidLng = sumLng / posCnt;
        this.centroidLat = sumLat / posCnt;
        this.centriodPoint.lng=centroidLng;
        this.centriodPoint.lat=centroidLat;
        return this.centriodPoint;

    }


     public int getUserCount(){


         int userCountx=0;
        Map<String, String>map=new HashMap<String, String>();

        for(int i=0;i<this.locationcluster.size();i++){


             String user=this.locationcluster.get(i).ownerId;


                if(map.keySet().contains(user))
                 {

                 }
                 else
                 {
                    map.put(user, "abc");
                    userCountx++;
                 }


         }

        return userCountx;
     }


     public int getPhotoCount(){

         return this.locationcluster.size();
     }

     public List<UserPhoto> getIdenticalsubLocations(){

         this.sublocationcluster.add(this.locationcluster.get(0));

         for(int i=0;i<this.locationcluster.size();i++){
             UserPhoto ufoto=this.locationcluster.get(i);
             if(Notcontainphoto(ufoto)){
                 this.sublocationcluster.add(ufoto);
             }

         }
       return this.sublocationcluster;
     }

     public boolean Notcontainphoto(UserPhoto userphoto){

         for(int i=0; i<sublocationcluster.size();i++){
            UserPhoto userphotoC=sublocationcluster.get(i);

            if((userphotoC.geotag.lat==userphoto.geotag.lat)&&( userphotoC.geotag.lng==userphoto.geotag.lng)){
                return false;
            }

         }

         return true;
     }

}
