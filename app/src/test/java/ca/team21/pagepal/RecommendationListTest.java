package ca.team21.pagepal;

import android.location.Location;
import android.location.LocationManager;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RecommendationListTest {

    @Test
    public void generateListTest() {
        BookHistoryList history = new BookHistoryList();
        Location location = new Location(LocationManager.NETWORK_PROVIDER);

        RecommendationList recommendations = new RecommendationList(history, location);
        recommendations.generateList();
        assertNotEquals(null, recommendations.get());
    }
}