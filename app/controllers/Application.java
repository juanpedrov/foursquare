package controllers;

import java.util.Iterator;
import java.util.List;

import models.FoursquareCredentials;
import models.Venue;
import play.mvc.Controller;
import play.mvc.Util;
import foursquare.*;


public class Application extends Controller {

	
    public static void index() {
    	
    	render();
    }

	public static void credentials(String clientId, String clientSecret) {
    	FoursquareCredentials credentials = new FoursquareCredentials(clientId, clientSecret);
    	credentials.save();    	
    	List<CategoryResponse> categories = getCategoriesUtil(credentials);

    	render("Application/categories.html", categories);
    }
    
    public static void categories() {    	
    	List<CategoryResponse> categories = getCategoriesUtil(null);
    	
        render(categories);    	
    }

	public static void fillVenuesDb(String categoryId, String ll, String city) {
    	List<VenueResponse> venues;
    	
    	venues = getVenuesUtil(null, categoryId, ll, city);
        for (Iterator<VenueResponse> iterator = venues.iterator(); iterator.hasNext();) {
        	VenueResponse venue = iterator.next();

        	Venue dbVenue = Venue.find("name = ?", venue.getName()).first();
        	if (dbVenue == null) {
        		
        		dbVenue = new Venue(venue.getName(), venue.getLocation().getLng(), venue.getLocation().getLat());
        		dbVenue.save();
        	}
        }   
        
    	render(venues);
    }
	    
	@Util
    private static List<CategoryResponse> getCategoriesUtil(FoursquareCredentials credentials) {
    	if (credentials == null)
    		credentials = (FoursquareCredentials) FoursquareCredentials.findAll().get(0);
    	
    	FoursquareConnectionManager FsqrConnector = new FoursquareConnectionManager(credentials.getClientId(), credentials.getClientSecret());
    	List<CategoryResponse> categories = FsqrConnector.getPlainCategories();
    	
		return categories;
	}	

	@Util
    private static List<VenueResponse> getVenuesUtil(FoursquareCredentials credentials, String categoryId, String ll, String city) {
    	List<VenueResponse> venues;
    	FoursquareConnectionManager FsqrConnector;
    	
    	if (credentials == null)
    		credentials = (FoursquareCredentials) FoursquareCredentials.findAll().get(0);    	
    	FsqrConnector = new FoursquareConnectionManager(credentials.getClientId(), credentials.getClientSecret());
    	venues = FsqrConnector.getVenues(categoryId, ll, city);
    	
		return venues;
	}	
}