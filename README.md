# Virtual Traveler

### IMPORTANT!!!!!!!
### TO RUN THIS APP YOU NEED A FLICKR API KEY, IN ORDER TO GET THIS ONE FOLLOW THIS LINK AND REGISTER
### https://www.flickr.com/services/apps/create/

Be the ultimate couch explorer. With the Virtual Traveler app, you can navigate the globe and view fantastic photos from Flickr users all over the world. Search for pictures from your current location, or search for that perfect destination. You can save your favorite pictures and view them from your home screen with the Virtual Traveler widget. Perfect for planning your photography tour, or getting the lay of the land before your next vacation. 

Go explore today!

The Virtual Traveler app will allow you to:
- Search the globe for a specific location or center on the user’s current location via Google Maps and the Location API
 - These locations are saved so users always have reference to them.
 - Search for pictures using the Flickr API
 - Save favorite pictures for display on the widget (and in app via FAB)
 - Share pictures to Facebook or Twitter or in a MMS message

### Map View
![alt text](https://github.com/jwright798/markdown-images/blob/master/MapsView.png "Map View")

This first page is the main screen users will see when they launch the app. It will be a full screen map view, and users can tap on a specific point on the map to drop a pin for that location. 

To remove the pin, start dragging to remove the pin (after confirming on an alert)

To view photos related to that pin, just tap on the pin

To view your favorite photo tap the star FAB

### Photos View
![alt text](https://github.com/jwright798/markdown-images/blob/master/PhotosView.png "Photos View")

In this view you can see photos for that area (from Flickr). Tap on a photo to save it to your favorites, and long press on a photo to bring up the sharing screen

### Favorites View
![alt text](https://github.com/jwright798/markdown-images/blob/master/FavoritesView.png "Favorites View")

In this view you can see all of your favorite photos that you’ve saved across all of your locations. You can tap a photo to remove it (after a dialog confirmation) and long press to share the photo

### Widget View
![alt text](https://github.com/jwright798/markdown-images/blob/master/WidgetView.png "Widget View")

This widget lets you view your favorited photos. Empty message appears if there are no favorited photos

### Key Considerations

**How will your app handle data persistence?** 

To handle storing the user’s favorite photos (just urls)  and saved locations, I will be creating 2 Content Providers, one for photos, one for locations

**Describe any corner cases in the UX.**

- Users will tap and hold on a map location to set pins 

 - Tap the pin to be transitioned to the Photos view

 - Drag an existing pin to delete it. (Confirmation dialog will appear)
 - Tap and hold on a photo to launch the sharing screen

 - Tap on a photo to save it to the favorites list.

 - Back button will take you from the photos view to the map screen

**Describe any libraries you’ll be using and share your reasoning for including them.**

Images will be retrieved via the Picasso library 

**Describe how you will implement Google Play Services.**

 - Location - This will be used to retrieve the currrent location in LatLong of the user. This will also be used for geocoding and reverse geocoding locations.
 - Google Maps - This is the key service since this will be used on the main screen to display a map users can navigate around (using the standard map view)

