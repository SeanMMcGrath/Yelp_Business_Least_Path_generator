# csc365_final
Final result from csc365 Data Structures &amp; Algorithms class

Process:

Parses information from an XML file of a Yelp dataset listing a set of businses and their information.(not including this in repo)

That data is put into a Btree that uses Disk writing and reading to quickly save and read the data for use.

Program runs K-means clustering as preprocessing on the data using location distances.

All disjointed sets are connected into a single set by creating an edge connecting them. 

Then through user imput a point is decided and Dijkstra's algorithm is used to find the shortest path from that point to its closest cluster center.

My first GUI so yes, I know its ugly.

Examples:

![graph1](images/Graph1.png)

![graph2](images/Graph2.png)
