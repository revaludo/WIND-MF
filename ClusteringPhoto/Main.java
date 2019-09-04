/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clusteringphoto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Majid
 */
public class Main {

     List<UserPhoto> photoCollectionRaw=new ArrayList();



    /**
     * @param args the command line arguments
     */
    private void readDir(String dirPath) throws IOException, ParseException {
        File dir = new File(dirPath);
        File files[] = dir.listFiles();
        for (int i=0; i<files.length; i++) {
        readFileNew(files[i]);   //filtered data
        }
    }


    private void readFile(String file) throws IOException, ParseException {

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        int i=0;
        while (br.ready()) {

            line = br.readLine();
            UserPhoto photo=new UserPhoto();

            String values[] = line.split("\\|");
            if(values.length<9)// skip if it contains | within some value; skip whole photo
            {
            photo.photoId= values[0];
//            photo.photoTitle= values[1];
//            photo.ownerName=values[1];
            photo.ownerId=values[2];


          if( values.length>6){
            String annotationString=values[6];

            String AnnotationSet[]=annotationString.split("~");

            for(int t=0;t<AnnotationSet.length;t++){
                photo.annotations.photoAnnotationElement.add(AnnotationSet[t]);

            }
                }
           else{
                photo.annotations.photoAnnotationElement.add("No tag");
              }

            photo.ID=i;
            String slng=values[5];
            
            try{
            photo.geotag.lng=Double.parseDouble(slng);
            String slat=values[4];
            

           
            photo.geotag.lat=Double.parseDouble(slat);
                }
            catch(Exception ex){
                continue;
            }
   
//            long timeStamp=Long.parseLong(values[3]);
//            Date date=new Date(timeStamp);
//            photo.timeTaken=date;
      //      photo.dateTaken=timeStamp;
            this.photoCollectionRaw.add(photo);
            i++;
            System.out.println(i);
            }

        }

     System.out.println("Reading finished.. now filtering");
    }
    


    private void readFileNew(File file) throws IOException, ParseException {

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        int i=0;
        while (br.ready()) {

            line = br.readLine();
            UserPhoto photo=new UserPhoto();

            String values[] = line.split("\\|");
            if(values.length<9)// skip if it contains | within some value; skip whole photo
            {
            photo.photoId= values[0];
//            photo.photoTitle= values[1];
//            photo.ownerName=values[1];
            photo.ownerId=values[2];


          if( values.length>6){
            String annotationString=values[6];

            String AnnotationSet[]=annotationString.split("~");

            for(int t=0;t<AnnotationSet.length;t++){
                photo.annotations.photoAnnotationElement.add(AnnotationSet[t]);

            }
                }
           else{
                photo.annotations.photoAnnotationElement.add("No tag");
              }

            photo.ID=i;
            String slng=values[5];
            
            try{
            photo.geotag.lng=Double.parseDouble(slng);
            String slat=values[4];
            

           
            photo.geotag.lat=Double.parseDouble(slat);
                }
            catch(Exception ex){
                continue;
            }
   
            long timeStamp=Long.parseLong(values[3]);
            Date date=new Date(timeStamp);
            photo.timeTaken=date;
      //      photo.dateTaken=timeStamp;
            this.photoCollectionRaw.add(photo);
            i++;
            System.out.println(i);
            }

        }

     System.out.println("Reading finished.. now filtering");
    }



    public static void main(String[] args) throws ParseException, InterruptedException {


        Main tester = new Main();

        try
        {
              System.out.println("Reading Data...");
//              tester.readDir(Tools.folder_path_read);
              tester.readFile("J:\\photo\\metadata\\nanjing.txt");
              System.out.println("Total photos from data\t"+tester.photoCollectionRaw.size());
              TrajectoryIdentification trajIdent=new TrajectoryIdentification();
              trajIdent.FindLocationsCluster(tester.photoCollectionRaw);
        }catch(IOException ex) {
           System.out.println(ex);
        }

    }






}
