The micro-service which calculates the distance between two cities. The micro-service contains two endpoints: 
one endpoint allows users to define a distance between two arbitrary cities, and the second endpoint allows the user to retrieve 
the distance between two cities.

The endpoint which calculates distance between cities is able to work with the cities which are not directly connected: 
i.e. if the user wants the distance between A and D, there may be a definition for A - B (5 miles), and B - C (10 miles), 
and C - D (1 mile).
The endpoint will produce an output for the above inputs of 16 (5 + 10 + 1).
The endpoint also includes the path for the specified distance.

If there are MULTIPLE paths from A to D in the example above, the endpoint will return ALL of the paths from 
A to D along with their computed distance.

If there is no connection between cities A and D (e.g. San Francisco to Tokyo), the service will return an error.

Service uses embedded database H2, so it requires no special configuration. 
