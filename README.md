jibenakka
=============

This is a set of basic Java examples for [akka](http://akka.io/).

## Getting Started

You will need to install [Apache Maven](http://maven.apache.org/). If you're using
Eclipse, it is recommended that you install the [m2e plugin](http://www.eclipse.org/m2e/). 
Once you've properly installed and configured Maven, you can then
use it to easily download all of jibenakka's needed dependencies.

The actual sample code can be found in the `sample` package, while supporting classes 
are located in the adjoining packages. Currently there are the following samples:

Word Count Map Reduce
-----------
This sample app peforms map reduce to count words in files using a combination 
of akka Actors and Futures. It can be found under the `mapreduce` package within
the `sample` package.


Supervisor Hierarchy Fault Tolerance
-----------
This sample app demonstrates creating a hierarchy of Actors. This is currently a 
work in progress. It can be found under the `fault` package within
the `sample` package.



