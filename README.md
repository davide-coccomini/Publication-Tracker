# Google Scholar Publication-Tracker

## Introduction
The scope of the application is to allow people to explore the information about authors and their publications, published on Google Scholar.

## Features
* The system allows the user and the admin to view the list of all authors and publications;
* The system allows the user and the admin to look for citations made and received in a certain publication;
* The system allows the user and the admin to look for authors publications and coauthors;
* The system allows the user and the admin to easily identify famous publications;
* The system allows the user and the admin to easily identify famous authors.
* The system allows the admin to add and delete a publication and modify its quantity;
* The system allows the admin to add and delete an author;
* The system allows the admin to update or delete users;


## Database structure
Considering the requirements, it is convenient to exploit the structure of a graph-database. Many of the statistics features will need to look at the database as a graph in order to find the relationship between the entities and the paths that connect them.

## Data retrieving
To achieve this goal, these data have to be retrieved from Google Scholar. The main challenge in this case is caused by the absence of official Google Scholar API. For that reason, it was needed to make some web scraping. To do that, we used the well-known Python library, Scholarly. The Python files in the "scraping" a fixed version of Scholarly and other libraries to collect the needed data.
The conversion.py file convert the obtaines JSON to Cypher code to obtain a Neo4j graph database. It is composed by two main entities, Authors (composed by name, email, heading and affiliation) and Publications (composed by name). The authors are related to publications by the relation "PUBLISHES", while the publications are related between them by the relation "CITES".
### How to run scraping
* Download and import the fixed version of Scholarly from: https://github.com/Davide57/scholarly
* Add the authors you prefer in the Python list and run scraping.py;
* When the scraping is completed, run convert.py to obtain the Cypher code for Neo4j.

## Main modules

### Entry Point
* MainApp: This class contains the stage shown to the user with all the elements that are manipulated by the controllers. 

### Controllers
* AuthorCreationController: This class manages the form to create an author;
* AuthorViewController: This class manages the page of authors' details filling tables with their information, publications and coauthors;
* AuthorsListController: This class manages the list of authors filling the table with their information;
* LoginController: This class manages the form to login;
* MenuController: This class manages the buttons in the main menu;
* PublicationCreationController: This class manages the form to create a publication;
* PublicationViewController: This class manages the page of publications' details filling tables with their information and citations made and received;
* PublicationsListController: This class manages the list of publications filling the table with their information;
* RegistrationController: This class manages the form for account creation;
* SessionController: This class manages the navigation through pages and implements the persistence of user session.
* StatisticsController: This class manages the page of statistics;
* TopController: This class makes the menu bar in the top of every page work. It is possible to select the page by clicking on the respective button;
* UserViewController: This class manages the page of users' update;
* UsersListController: This class manages the list of users filling the table with their information;

### Middleware
* DatabaseManager: This class manages the connection and operations with relational database for users management;
* GraphManager: This class manages the connection and the operations with Neo4j database for authors and publications management.

### Models
* Author: Represent the Author entity on Neo4j database;
* Publication: Represent the Publication entity on Neo4j database;
* User: Represent the User entity on relational database;

## How to run application
* Execute the Cypher code in Neo4j;
* Insert the SQL database into Mysql server;
* Run the application via Netbeans, Eclipse or other Java environments.

## Extra
The complete version of the documentation is available in the docs folder.
