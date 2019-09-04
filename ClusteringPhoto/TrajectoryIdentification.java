/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clusteringphoto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import java.util.Date;


/**
 *
 * @author Majid
 */
public class TrajectoryIdentification {


//  private List<SemanticLocation> locationDB=new ArrayList(); //Each photo taken treated as location visited with a name of that location.
//  private List<UserLocation> userLocationDB=new ArrayList(); //Each photo taken treated as location visited with a name of that location and a user ... Places that a user visited......
//  private List<Trajectory> TrajectoryDB=new ArrayList();
//  private List<UserTrajectory> userTrajectoryDB=new ArrayList();
  private Places placeList;
//  public List<UserLocationTemporalContext> userLocationTempralContextList=new ArrayList();




  private List<ContextAwareLocation> ContextAwareLocationDB=new ArrayList();
  private Places mergPlaceList;
    private String folder_path;
//  public List<UserLocationTemporalContext> userLocationTempralContextListMerged=new ArrayList();

 



    public void FindLocationsCluster(List<UserPhoto> photoCollectionRaw) throws IOException, ParseException, InterruptedException{

        Places places=ClusterLocation(photoCollectionRaw);
       

    }

   

    


     //Need to do testing for this method.... Debugging....


     


    //*********

//    long oldtime=firstfoto.timeTaken.getTime();
//            long newtime=ufoto.timeTaken.getTime();
//            long diff=newtime-oldtime;
////            System.out.println(diff);
//            int fpm=GetPMAM(firstfoto.timeTaken);
////            System.out.println(fpm);
//            int npm=GetPMAM(ufoto.timeTaken);
////            System.out.println(npm);
//            if(diff>(3600000*6)||GetPMAM(firstfoto.timeTaken)!=GetPMAM(ufoto.timeTaken)){
    //**********



  public Places ClusterLocation(List<UserPhoto> photoCollectionRaw) throws IOException, ParseException {
//        DBSCAN111 dbscan=new DBSCAN111();
        PDBSCAN dbscan=new PDBSCAN();
        System.out.println("Clustering photoes to extract place...");
        List<UserPhoto> clusteredPhotoList=dbscan.BuildCluster(photoCollectionRaw);
        System.out.println("Clustering finished");
        Places places=ExtractPlaces(clusteredPhotoList);
        System.out.println("Generating Place Name...");
        FinalizePlaceNameOption(places);
        Printstatistics(places);
        printlocationPhoto(places);


    return places;

    }

 

 public void Printstatistics(Places places) throws IOException{
        Tools.DumpLocationCoordinatestoKMLRevised(places);
        Tools.DumpLocationCoordinatesCentroid(places);
 }


 
 public void printlocationPhoto(Places places) throws IOException{
     
        String filen = "J:\\photo\\cluster\\nanjing\\photolocationInformation.txt";
        File file = new File(filen);
         if (!file.exists()) {
            file.createNewFile();
        }
       BufferedWriter output = new BufferedWriter(new FileWriter(file));

       for(int i=0;i<places.places.size();i++){
         LocationCluster locationcls=places.places.get(i);
         output.write(locationcls.placeID);
         output.write("~");

          for (int j=0; j<locationcls.locationcluster.size(); j++){
              
              output.write(locationcls.locationcluster.get(j).photoId);
              output.write("|");
          }
         
         output.write("\n");




       }
output.flush();
     
     
     
     
 }




  public void FinalizePlaceNameOption( Places places){


      for(int i=0;i<places.places.size();i++){
           places.places.get(i).placeID=Integer.toString(i);

       }
    }


 
    public Places ExtractPlaces(List<UserPhoto> clusteredLoctions){
        Collections.sort(clusteredLoctions);
        Places places=new Places();
        int previousLocation=0;
        LocationCluster cluster=new LocationCluster();

      for(int i=0; i<clusteredLoctions.size();i++)
      {
        if(clusteredLoctions.get(i).ClusterID==-1){
            previousLocation++;
        }
        else
        {
            if(clusteredLoctions.get(i).ClusterID==clusteredLoctions.get(previousLocation).ClusterID){
            cluster.addLocationToCluster(clusteredLoctions.get(i));
         }
            else{
            places.AddPlace(cluster);
            cluster=new LocationCluster();
            cluster.addLocationToCluster(clusteredLoctions.get(i));
            previousLocation=i;

            }
        }
      }
           places.AddPlace(cluster);

           return places;

    }


 public double earthDistance(double startLng, double startLat, double endLng, double endLat) {
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




 public void BuildContextAwareLocationDB(String filein) throws FileNotFoundException, IOException{
        File file = new File(filein);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        while (br.ready()) {
            line = br.readLine();

            ContextAwareLocation loc=new ContextAwareLocation();

            String values[] = line.split("\\|");
            loc.locationID=values[0];

            //perception features

            String percF[]=values[1].split(",");
            loc.totalvisit=Integer.parseInt(percF[0]);
            loc.noOfUniqueVisits=Integer.parseInt(percF[1]);
            loc.maxNoVisitsByOne=Integer.parseInt(percF[2]);
            loc.minNoVisitsbyOne=Integer.parseInt(percF[3]);
            loc.entropyVisit=Double.parseDouble(percF[4]);

            //temporal features

            String tdf[]=values[2].split(",");
            for(int i=0;i<tdf.length;i++){
               loc.tdf[i]=Double.parseDouble(tdf[i]);
            }


            String twf[]=values[3].split(",");
            for(int j=0;j<twf.length;j++){
               loc.twf[j]=Double.parseDouble(twf[j]);
            }

            //weather features
            String wf[]=values[4].split(",");
            for(int k=0;k<wf.length;k++){
               loc.wf[k]=Double.parseDouble(wf[k]);
            }


            //orientation features
            String pf[]=values[5].split(",");
            for(int n=0;n<pf.length;n++){
               loc.pf[n]=Double.parseDouble(pf[n]);
            }
            loc.avgSeqLength=Double.parseDouble(pf[0]);
            loc.entropySequence=Double.parseDouble(pf[1]);


            //Adding locations
            this.ContextAwareLocationDB.add(loc);

            }
}


 public LocationCluster GetRespectiveCluster(Places places, String id){
     LocationCluster cl=new LocationCluster();

      for(int j=0; j<places.places.size();j++){

             LocationCluster placeL=places.places.get(j);
             if((placeL.placeID.equals(id))){
                 return placeL;
             }
      }
     return cl;
 }




 public Places ExtractPlacesMerged(List<ContextAwareLocation> clusteredLoctions){
        Collections.sort(clusteredLoctions);
        Places places=new Places();
        int previousLocation=0;
        LocationCluster cluster=new LocationCluster();

         for(int i=0; i<clusteredLoctions.size();i++)
    {
        if(clusteredLoctions.get(i).ClusterID==-1){
            cluster.locationcluster.addAll(clusteredLoctions.get(i).locCluster.locationcluster); // not including which are not included in cluster
            cluster.placeID=cluster.placeID+" "+clusteredLoctions.get(i).locationID;
            places.AddPlace(cluster);
            cluster=new LocationCluster();
            previousLocation++;
        }
        else
        {
            if(clusteredLoctions.get(i).ClusterID==clusteredLoctions.get(previousLocation).ClusterID){
            cluster.locationcluster.addAll(clusteredLoctions.get(i).locCluster.locationcluster);
            cluster.placeID=cluster.placeID+" "+clusteredLoctions.get(i).locationID;
             //need to complete implementation
            }
            else{
            places.AddPlace(cluster);
            cluster=new LocationCluster();
            cluster.locationcluster.addAll(clusteredLoctions.get(i).locCluster.locationcluster);
            previousLocation=i;

            }
        }
    }
   places.AddPlace(cluster);

       return places;

  }




}
