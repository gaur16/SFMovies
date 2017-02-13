# SF Movies

### Problem Statement
Create an app that shows on a map where movies have been filmed in San Francisco. The user should be able to filter the view using autocompletion search.
The data is available on DataSF: [Film Locations](https://data.sfgov.org/Arts-Culture-and-Recreation-/Film-Locations-in-San-Francisco/yitu-d5am).

### Solution
A light-weight Android app that facilitates autocomplete search and smooth UI for navigation on the map. The implementation includes:
  - **Map** : To display top view of a Map marking all the places of interest
  - **Detail** : To allow a user to tap and view details of the place on the map
  - **Autocomplete** : To provide suggestions to user while searching the map (through Auto-complete)
  - **UX** : To ensure clean material design implementation, and a smooth UX

#### Implementation & Challenges
###### Map
The location information provided in the data was in street address format. For marking on the map, each instance of location had to be converted to ```LatLng``` object. 
Android provides a ```android.location.Geocoder``` class for handling geocoding and reverse geocoding, but it performs poorly in case of handling address to geocode conversion (barely 50% success rate). 
The app therefore, directly fetches Geocoded values for a given address string by hitting Google Maps API for Geocoded data.

```sh
https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=YOUR_API_KEY
```
This happens in the background, where ```SFGetLocationIntentService``` implements a Callback (using [retrofit2](https://square.github.io/retrofit/) library) and after parsing the response, launches a broadcast intent with the geo-coordinates.
A broadcast receiver in the ```SFMap``` fragment listens to the intent and updates the ```Googlemap``` user-interface by adding new ```Markers```.

###### Detail
Following the material design principles, the app incorportes a ```BottomSheet``` in the main Fragment itself. User can launch the bottom sheet, containing data of the movie shot at that location (title, release year, production house, director etc.) by tapping on the ```InfoWindow``` of the Marker. 

###### Autocomplete
The [Socrata Open Data API (SODA)](https://dev.socrata.com/foundry/data.sfgov.org/wwmu-gmzc) provides programmatic access to the Film Locations dataset. The API also allows the app to query its dataset. Using ```$q``` parameter in the query one can perform a special full-text search within the dataset.
```sh
https://soda.demo.socrata.com/resource/4tka-6guv.json?$q=Islands
```
The App uses ```SearchCompleteAdapter``` to populate the list of suggestions. The SearchCompleteAdapter, in its implementation of ```performFiltering``` method, creates a ```Controller``` object and populates the ```filterResults``` in its Callback. Next it calls ```publishResults``` method to populate the list of suggested results, which the user sees after a small delay.
In order to prevent sending of multiple requests when user is typing a query, a ```DEFAULT_AUTOCOMPLETE_DELAY``` of 750ms is added in the SFAutoComplete class. This prevents repition of similar requests to the server as user inputs a string.

###### UX
The App handles this un-indexed dataset by first fetching the list of all possible addresses. After filtering out the redundancy (duplicates and empty fields), we launch a background service that fetches the encoded value of each address and displays on the map. 
Since the geocoding runs in the background, it becomes feasible to handle events of the main thread, i.e., AutoCompleteSearch. The user therefore doesn't have to wait for the entire Map to load before he can begin exploring. 
Also, the app doesn't shut down when pressed back button from the main screen, hence enhancing ease of navigation.

#### Further Enhancements
###### Marker Clustering
After loading 700 markers on the MapView, the UI tends to become slow. Hence, marker clustering needs to be implemented. 
As of now, the App loads 350 locations to begin with. However, any location searched through Autocomplete Search can be viewed.
###### Refined Autocomplete
The SODA API provides an SoQL function "like", that searches for substring for a given column. This could enable partial word searching. As of now, Autocomplete only works when user enters full/stemmed word present in DB. 
A filter also needs to be implemented to prevent occurence of empty fields in the autocomplete suggestions.
###### Additional Features
Feature to automatically embed wikipedia/IMDB link in the movie title, adding Google Maps Static Image with each Marker etc. are some additional features yet to be implemented.
