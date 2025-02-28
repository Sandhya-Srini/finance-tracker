
# FINANCE TRACKER SERVICE
A Service to manage financial transactions


# Assumptions on the requirements

* Each Transaction is associated to a user. User table is not managed in the project
* Current Balance is calculated based on the user Id after a transaction is added / modified. 
* Current Balance based on date can be derived from the api end point 
* This project is designed as a REST API project. But a kafka consumer can be  added to also enable it for event driver service. 
* either way the service can work. 

# Input Restrictions for the  endpoints
* The View Transactions end point can take multiple filters with any valid field from the Transaction object. It can also sort by any field.
* filter needs to be in a format field.eq(value) and sort needs to be in format +/- fieldName

# Security Implementation
* Simple api-key authentication is implemented in this project . But for financial applications we could do OAUTH or JWT based authentication.


