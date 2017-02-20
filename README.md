# SF Movies

## Problem Statement
Create an app that shows on a map where movies have been filmed in San Francisco. The user should be able to filter the view using autocompletion search.
The data is available on DataSF: [Film Locations](https://data.sfgov.org/Arts-Culture-and-Recreation-/Film-Locations-in-San-Francisco/yitu-d5am).

## Solution
A light-weight Android app that facilitates autocomplete search and smooth UI for navigation on the map. The implementation includes:
  - **Map** : To display top view of a Map marking all the places of interest
  - **Detail** : To allow a user to tap and view details of the place on the map
  - **Autocomplete** : To provide suggestions to user while searching the map (through Auto-complete)
  - **UX** : To ensure clean material design implementation, and a smooth UX

### Application Architecture
The App is built on a simple MVC design. The Model package consists of the ```SFLocation``` and ```SFGoogleResponse``` objects. In addition to this, a single fragment class ```SFMap``` inflates the ```MapView``` and the ```AutoCompleteSearchView```. ```Controller```, ```SFGeocodeGoogle``` and ```SFGetLocationIntentService``` together constitute the controller.

### Implementation & Challenges
##### Map
The location information provided in the data was in street address format. For marking on the map, each instance of location had to be converted to ```LatLng``` object. <br />
<br />
Android provides a ```android.location.Geocoder``` class for handling geocoding and reverse geocoding, but it performs poorly in case of handling address to geocode conversion (barely 50% success rate). <br />
<br />
The app therefore, directly fetches Geocoded values for a given address string by hitting Google Maps API for Geocoded data.

```sh
https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=YOUR_API_KEY
```
This happens in the background, where ```SFGetLocationIntentService``` implements a Callback (using [retrofit2](https://square.github.io/retrofit/) library) and after parsing the response, launches a broadcast intent with the geo-coordinates.
A broadcast receiver in the ```SFMap``` fragment listens to the intent and updates the ```Googlemap``` user-interface by adding new ```Markers```.

##### Detail
Following the material design principles, the app incorportes a ```BottomSheet``` in the main Fragment itself. User can launch the bottom sheet, containing data of the movie shot at that location (title, release year, production house, director etc.) by tapping on the ```InfoWindow``` of the Marker. (Can be removed by swiping down)
<br />

##### Autocomplete
The [Socrata Open Data API (SODA)](https://dev.socrata.com/foundry/data.sfgov.org/wwmu-gmzc) provides programmatic access to the Film Locations dataset. The API also allows the app to query its dataset. Using ```$q``` parameter in the query one can perform a special full-text search within the dataset. <br /> But using the ```like``` parameter for querying, we can perform partial search over the data. 
```sh
https://data.cityofchicago.org/resource/tt4n-kn4t.json?$where=job_titles%20like%20%27%2525CHIEF%2525%27
```
The App uses ```SearchCompleteAdapter``` to populate the list of suggestions. The SearchCompleteAdapter, in its implementation of ```performFiltering``` method, creates a ```Controller``` object and populates the ```filterResults``` in its Callback. Next it calls ```publishResults``` method to populate the list of suggested results, which the user sees after a small delay. <br />
In order to prevent sending of multiple requests when user is typing a query, a ```DEFAULT_AUTOCOMPLETE_DELAY``` of 1second is added in the SFAutoComplete class. <br /> This prevents repition of similar requests to the server as user inputs a string.

##### UX
The App handles this un-indexed dataset by first fetching the list of all possible addresses. After filtering out the redundancy (duplicates and empty fields), we launch a background service that fetches the encoded value of each address and displays on the map. <br />
<br />
Since the geocoding runs in the background, it becomes feasible to handle events of the main thread, i.e., AutoCompleteSearch. The user therefore doesn't have to wait for the entire Map to load before he can begin exploring. <br/>
<br />
Also, the app doesn't shut down when pressed back button from the main screen, hence enhancing ease of navigation.

### Further Enhancements
##### Marker Clustering
After loading 700 markers on the MapView, the UI tends to become slow. Hence, marker clustering needs to be implemented. 
As of now, the App loads 200 locations to begin with. However, any location searched through Autocomplete Search can be viewed.
##### Refined Autocomplete
A title case filter needs to be implemented. Since SODA API has a limitation of searching in lower case, a title case filter can ensure proper partial search. As of now, user has to enter the substring that matches a part of the title.
##### Additional Features
Feature to automatically embed wikipedia/IMDB link in the movie title, adding Google Maps Static Image with each Marker etc. are some additional features yet to be implemented.
##### An SQLite local DB
I have added a handler class already for the SQLite DB implementation. By doing that, it would be unnecessary to make server calls. Further flags can be added to perform search in the local db. Apart form that, we can use ```Loaders``` to update as soon as there's a change in the db, accommodating real-time changes.
