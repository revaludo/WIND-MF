/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clusteringphoto;

import java.util.ArrayList;
import java.util.List;



/**
 *
 * @author Majid
 */
public class ContextAwareLocation implements Comparable<ContextAwareLocation> {

    public int ClusterID=0;


     public int compareTo(ContextAwareLocation o) {
        return this.ClusterID-o.ClusterID;
    }

   public LocationCluster locCluster=new LocationCluster();


   public String locationID="";
   public double lat=0;
   public double lng=0;
 

   //temporal distribution

   public double tdf[]=new double[24];
   public double tdpd[]=new double[24];

   //
   public double twf[]=new double[7];
   public double twpd[]=new double[7];



    //Weather features
    public int Warmsunny=0;
    public int warmraining=0;
    public int hotsunny=0;
    public int hotraining=0;
    public int coldsunny=0;
    public int coldraining=0;

    public double wf[]=new double[6];


   // Temporal Features
    public int weekdayAM=0;
    public int weekdayPM=0;
    public int weekendAM=0;
    public int weekendPM=0;

    //Population Features
    
    public int totalvisit=0;
    public int noOfUniqueVisits=0;
    public int maxNoVisitsByOne=0;
    public int minNoVisitsbyOne=0;
    public double entropyVisit=0.0;

    //perception features
    public double avgSeqLength=0.0;
    public double  entropySequence=0.0;

    public double pf[]=new double[2];

    //public ranks
    public double RankPF=0.0;
    public double RankTF=0.0;
    public double RankWF=0.0;
    public double RankOF=0.0;
    public double RankHF=0.0;
    public double RankPLF=0.0;
    public double RankAggregated=0.0;

    public String getPopularDayCategory(){
        double Weekday=0.0;
        double weekend=0.0;
        for(int i=0; i<twf.length; i++){

            if(i==0||i==6){
                weekend=weekend+twf[i];
            }
            else{
                Weekday=Weekday+twf[i];
            }
         }

        if(Weekday>weekend){
            return "WeekDay";
        }
        return "WeekEnd";
    }

       
        public String getPopularTimeCategoryCategory(){    
        double AM=0.0;
        double PM=0.0;
        for(int i=0; i<twf.length; i++){
            
            if(i<12){
                AM=AM+twf[i];
            }
            else{
                PM=PM+twf[i];
            } 
         }
        
        if(AM>PM){
            return "AM";
        }
        return "PM"; 
    }




}
