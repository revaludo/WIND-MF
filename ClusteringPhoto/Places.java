/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clusteringphoto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author majid
 */
public class Places {


    public List<LocationCluster> places=new ArrayList();
    public void AddPlace(LocationCluster place)
    {
        this.places.add(place);

    }

}
