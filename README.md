# FetchRewards Coding Exercise

## Short Summary 
This web application is developed as a part of Fetch Rewards Coding Exercise.

## Prerequisite Installation
Please install these before running the application
1. Java ( version used : 11 )
2. Maven ( version used : 3.8.6 )

## How to Run 
Note: I am running this application at port 8080. Please make sure that port 8080 is free before use, otherwise you may get error 
      at the last step when we run our application

### Steps to Run Application 

1. Open a terminal and change your cwd to the application folder.
2. Run './mvnw clean' on terminal. This will clear previous state files from previous versions if any. 
3. Run './mvnw install' on terminal. This uses our 'pom.xml' to fetch all dependencies.
4. Run './mvnw spring-boot:run' to start the application on port 8080. 

### Steps to make API calls 

You can either use PostMan or cURL to make API calls to the web application. Under "credit/src/main/resources', I have given 
'fetchrewards.postman_collection.json' file for directly import API calls in PostMan. For cURL, I have given terminal commands
in 'curl_commands.txt' for API calls. Please note that I have formed these API calls as per examples in the coding exercise document. 

#### cURL ( Easier )
1. After starting application, open a new terminal. 
2. Copy each cURL command from 'curl_commands.txt' file ( in "credit/src/main/resources' folder )in the terminal and run it. 
3. You should see the api response in the terminal itself just after running cURL command.


#### PostMan ( Preferred )

1. Please install PostManAgent on your local PC. 
2. Open PostManAgent on PC and PostMan Web Portal on your browser. 
3. On PostMan Web portal, import api calls using given 'fetchrewards.postman_collection.json' ( in "credit/src/main/resources' folder ) in your workspace. 
4. You can see the api responses in the PostMan Web portal too. 


## Algorithm and Design

__Validation__ 
1. A transaction is validated before adding in database. Payer should be a non-empty string, Points should be non-zero. Timestamp should be non-null. I am also throwing messages in reponse if a transaction request is invalid as per first point. 
3. Similar validation for request for Spend. Points should be greater than 0 i.e positive. 

__Adding the transactions__
1. Txns with Positive points : A valid positive txn is processed before getting added to database. By processing, I mean if we have any EARLIER txns with negative balance in the database with payer same as txn's payer, we will reduce our positive points ( positive txn points ) by that much. This process will done continuously for all EARLIER negative transaction until we exhausted all our positive points or reached the end of the list. After this, if we have any positive point left, we update our positive txn's points to that and add it to the database in the sorted way. 

2. Txns with Negative points : A valid negative txn is processed before getting added to database. By processing, I mean if we have any EARLIER txns with positive balance in the database with payer same as txn's payer, we will reduce our negative points ( negative txn points ) by that much. This process will done continuously for all EARLIER positive transaction until we exhausted all our negative points or reached the end of the list. After this, if we have any negative point left, we update our negative txn's points to that and add it to the database in the sorted way. 
      
__Spend Points__
 
For Spending points, I am just traversing the list from starting since our transaction database is sorted as per timestamp already. First, I am storing the indexes of the txns we are going to use. If even after using all transactions, we are still left with some points to spend, I am throwing an EXCEPTION with a message. Otherwise, I am removing the used transactions from the database and also returning a reponse containing points spent by each user. 
 
__GetBalance__

In my Dao, I also maintaining a balance map which contains the overall balance of each payer in the database. I am simply using this to send a reponse for fetching balance calls. 
      


## Assumptions 

1. Using in-memory data structures as database for storing transactions. 
2. All transactions are made for one user account. 
3. Points can only be integers. 
4. Transactions should always have non-zero points. 
5. Spend points should always be greater than 0 i.e positive.
6. Transaction are sorted as per timestamp. For same timestamp, insertion order is followed. 

## Improvements
1. Can use other data structures like LinkedList with some modifications for faster processing .  
2. Using persistent datastore to store transactions in order to achieve real world application. 
3. Proper Unit Testing can be done instead of manual testing. 










