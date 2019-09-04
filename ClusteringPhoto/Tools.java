/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clusteringphoto;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Majid
 */
public class Tools {
    
    public static String folder_path="J:\\photo\\cluster\\nanjing\\";
//    public static String folder_path_read="G:\\BaiduYunDownload\\data\\clusternew\\sf";





    public static void DumpLocationCoordinatestoKMLRevised(Places places) throws IOException{

        String filen = folder_path+"loc2kmlRevised.txt";
        File file = new File(filen);
         if (!file.exists()) {
            file.createNewFile();
        }
       BufferedWriter output = new BufferedWriter(new FileWriter(file));

       for(int i=0;i<places.places.size();i++){
         LocationCluster locationcls=places.places.get(i);
         output.write(locationcls.placeID+"~");

         List<UserPhoto> sublist=locationcls.getIdenticalsubLocations();
           if(sublist.size()>2){

                 for(int j=0;j<sublist.size()-1;j++){

                     output.write(sublist.get(j).geotag.lat+","+sublist.get(j).geotag.lng);
                          if(j<sublist.size()-2){
                             output.write("|");
                          }
                 }

            }
          else{

             GeoTag center =locationcls.GetCentriod();
             output.write(center.lat+","+center.lng);
          }


          output.write("\n");




       }
output.flush();

   }


    public static void DumpLocationCoordinatesCentroid(Places places) throws IOException{

        String filen = folder_path+"locCentriod.txt";
        File file = new File(filen);
         if (!file.exists()) {
            file.createNewFile();
        }
       BufferedWriter output = new BufferedWriter(new FileWriter(file));

      //  List<GeoTag> listoftags=new ArrayList();

       for(int i=0;i<places.places.size();i++){
             LocationCluster locationcls=places.places.get(i);
             output.write(locationcls.placeID +",");
             GeoTag center =locationcls.GetCentriod();
             output.write(center.lat+","+center.lng);
             output.write("\n");
       }
   output.flush();

   }

}
