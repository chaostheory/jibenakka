jibenakka
=============

This is a set of basic Java examples for [akka](http://akka.io/).

## Getting Started

You will need to install [Apache Maven](http://maven.apache.org/). If you're using
Eclipse, it is recommended that you install the [m2e plugin](http://www.eclipse.org/m2e/). 
Once you've properly installed and configured Maven, you can then use it to easily download 
all of jibenakka's needed dependencies.

The actual sample code can be found in the `sample` package, while supporting classes 
are located in the adjoining packages. Currently there are the following samples:

Word Count Map Reduce
-----------
This sample app peforms map reduce to count words in files using a combination 
of akka Actors and Futures. It can be found under the `sample>mapreduce.wordcount` package.

Word Count's core class is WCMapReduceApp. This class is both the main supervisor Actor as
well as the main partitioner for the map reduce work, specifically it is at the top of the 
supervisor hierarchy (restarting any failed supervisors under it) as well dividing and 
assigning map reduce work at the highest level. WCMapReduceApp starts off by loading the 
files the read. Once it has references to the files, it puts each file into a work unit, which is 
really just a message, and passes them to one or more instances of the Mapper class.

What does the Mapper class do? Contrary to its name, it doesn't actually map anything. 
When a Mapper class instance recieves work from WCMapReduceApp reads each file, creating a work 
unit for each line of the files. A Mapper class instance

Supervisor Hierarchy Fault Tolerance
-----------
This sample app demonstrates creating a hierarchy of Actors. This is currently an unfinished 
work in progress. It can be found under the `fault` package within the `sample` package.



