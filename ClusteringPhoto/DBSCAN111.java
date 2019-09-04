/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clusteringphoto;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author majid
 */
public class DBSCAN111 {

 private   double epsilon=100;
 private   long minPts=50;
 private int numberOfGeneratedClusters=0;
  private   double maxepsilon=400;
 private int clusterID;
 List<UserPhoto> database;
 int NOISE=-1;

 int adaptive_density_threshold=300;
 int current_density;
 int neighbourhood_density;
 double drop_density_threshold=0.5;

 Map<UserPhoto, Integer>map=new HashMap<UserPhoto, Integer>();

 public DBSCAN111()
 {
    

 }

 public List<UserPhoto> BuildCluster(List<UserPhoto> data){
    this.database=data;

     numberOfGeneratedClusters = 0;
     clusterID = 1;

     for(int i=0;i<database.size();i++){

         current_density=adaptive_density_threshold;
         UserPhoto dataobject=database.get(i);
         if(dataobject.ClusterID==0){

             if(expandCluster(dataobject)){
                 clusterID++;
                 numberOfGeneratedClusters++;
             }
         }
         
     }

     return this.database;
 }

 private boolean expandCluster(UserPhoto dataobject){

     List<UserPhoto> seedList=epsilonRangeQuery(dataobject, dataobject);

     //adptve
     neighbourhood_density=seedList.size();
     double ratio= neighbourhood_density/(double)current_density;
    //adptve


     if(seedList.size()<this.minPts&&ratio<drop_density_threshold){
       //  dataobject.SETClassfied("NOISE");
         SetClassification(dataobject, NOISE);
         return false;
     }


     ////adptve
     current_density=seedList.size();
     //


    for (int i = 0; i < seedList.size(); i++) {

        UserPhoto  seedListDataObject =seedList.get(i);
        SetclusterID(seedListDataObject, clusterID);
       // seedListDataObject.SetClusterID(clusterID);
        if (seedListDataObject.checkequal(dataobject)) {
            seedList.remove(i);
            i--;
        }
    }

for (int j = 0; j < seedList.size(); j++) {
    
   UserPhoto  seedListDataObject =seedList.get(j);
   List<UserPhoto>seedListDataObject_Neighbourhood=epsilonRangeQuery(seedListDataObject, dataobject);



   //adaptive density
   neighbourhood_density=seedListDataObject_Neighbourhood.size();
   ratio=neighbourhood_density/(double)current_density;
//   System.out.println("current:\t"+current_density);
//   System.out.println("neighbourhood:\t"+neighbourhood_density);
//   System.out.println("ratio:\t"+ratio);
   //



   if (seedListDataObject_Neighbourhood.size() >= this.minPts && ratio>drop_density_threshold ) {
      
       //adptve
       current_density=neighbourhood_density;
       //adptve

       for (int i = 0; i < seedListDataObject_Neighbourhood.size(); i++) {

           UserPhoto p=seedListDataObject_Neighbourhood.get(i);
           if(p.ClusterID==0||p.ClusterID==NOISE){
               if(p.ClusterID==0){
                   seedList.add(p);
           
               }
                SetclusterID(p, clusterID);
               // p.SetClusterID(clusterID);
           }
       }

   }
   
  seedList.remove(j);
  j--;
}



    return true;



     
 }

 public List<UserPhoto> epsilonRangeQuery(UserPhoto dataobject, UserPhoto firstObject){


     List<UserPhoto> SeedList=new ArrayList();

     for(int i=0;i<database.size();i++){

         UserPhoto ctrPoint=database.get(i);
         double Dis=Checkdistance(ctrPoint, dataobject);
         if(Dis<epsilon){
             SeedList.add(ctrPoint);

         }

     }

     return SeedList;
 }

public double Checkdistance(UserPhoto dataobject, UserPhoto ctrPoint)
    {
       double dis=earthDistance(ctrPoint.geotag.lng, ctrPoint.geotag.lat, dataobject.geotag.lng, dataobject.geotag.lat);
     //  System.out.println(dis);
       return dis;
    }




public void SetclusterID(UserPhoto place, int ID){

    for(int i=0; i<database.size();i++)
    {
        UserPhoto dplace=database.get(i);
        if(place.checkequal(dplace)){
            database.get(i).ClusterID=ID;
        }
    }

}
public void SetClassification(UserPhoto place, int c){

    for(int i=0; i<database.size();i++)
    {
        UserPhoto dplace=database.get(i);
        if(place.checkequal(dplace)){
            database.get(i).ClusterID=c;
        }
    }

}


public void printcluster()
{
   for(int i=0; i<database.size();i++)
    {
        System.out.println(""+database.get(i).geotag.lat+","+database.get(i).geotag.lng+","+database.get(i).ClusterID);
    }
}

public  double earthDistance(double startLng, double startLat, double endLng, double endLat) {
        double dis = 0.0;
        double radLat1 = startLat * Math.PI / 180.0;
        double radLat2 = endLat * Math.PI / 180.0;
        double a = radLat1 - radLat2;
        double radLng1 = startLng * Math.PI / 180.0;
        double radLng2 = endLng * Math.PI / 180.0;
        double b = radLng1 - radLng2;
        dis = 2 * Math.asin(Math.sqrt(Math.sin(a / 2) * Math.sin(a / 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.sin(b / 2) * Math.sin(b / 2)));
        dis = dis * 6378137.0;
        return dis;
    }


}
