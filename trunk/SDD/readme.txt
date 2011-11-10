How to Set-up RDF Converter for you local machine

The RDFConverter tool relies on the Jena API, at
(http://sourceforge.net/projects/jena/files/Jena/Jena-2.6.4/),
so all of the jars in the Jena distribution need to be on the build path
for this project (as does some version of a mysql-connector jar).

The packages of interest in the SDD project folder are conversion and dao.  
The conversion package holds RDFConverter.java and a sub-package test with 
a unit test demonstrating the use thereof.  The dao package contains a couple
of data-access java files for looking at two different databases: one database
contains a single table that maps singular names to plural names.  The other
maps filenames (the output files of Hong's algorithms) to taxa. These are the 
only databases used for the whole "SDD" project (the project in which I'm
doing all my work on SDD, RDF and matrices).

To set-up these databases on your machine:

First, there are sql scripts for loading these 
("fnav19_filename2taxon_4boston20110412 0909.sql" and
"singularpluralorgannames.sql").
Run these with mysql to get databases/tables installed.  

Next, make sure some mysql user account you have access to has the proper
privaleges for accessing these tables (select privelages are enough).
For instance, I created a user called "biosemantics" identified by 
password "stimpy" that has all privaleges for only these tables from any
location.  You could also just use your root mysql user.

Once those tables are set up, find the properties file called 
"database.properties" in the dao package.  Change the user and password 
properties to match your mysql user and password.  Save the file.

Input and Output:

You need to tell the object that parses annotations the path to where those 
annotations are located.  This can be done in "description.properties" in
the conversion package, by changing "input.path" to point to the path on
your file system where the annotation XML files reside. You don't need
to worry about the other properties in description.properties.

Very briefly, the way I begin generating either SDD or XML is by first creating
a DescriptionParser object for a given taxon name and rank (corresponding to
"cirsium" and TaxonRank.GENUS in TestRDFConverter.java).  This is where the 
filename2taxon database is used - a filename is looked up based on a taxon name
and rank.
This Parser object calls it's main method, "parseTaxon", and the result is used 
to create a TaxonHierarchy object.  This TaxonHierarchy object is used as the
argument to the constructor for an RDFConverter.

An RDFConverter uses the method "taxonToRDF", which takes the root element of 
the TaxonHierarchy and the name of an output path in which to place an 
RDF XML document as output. So, you can place either an absolute path or 
relative (to the project) path as an argument to this method.

Changing RDF Namespaces

The namespaces used for various predicates ("structure", "character", etc.)
can be changed in the conversion/rdf.properties file.  These are just
populated with toy names at the moment, it's safe to change any of them
(other than the "rdf" property).

Changing Database Names and Table Names

The filename fetching and singular->plural term processing rely on
the FilenameTaxonDao and SingularPluralDao objects, respectively. These
query certain databases containing tables holding relevant information.
The names of the databases and tables in which the data lies can be 
configured in the dao/database.properties files.  The properties to change
to make such configurations are:

- singular-plural-database
- singular-plural-table-name
- filename-database
- filename-table-name

Thus, when changes in versions occur, the table names can be easily updated.

