# Money Transfer API # 

A simple RESTful API for account operations including deposit, withdraw, transfer b/w accounts,
balance inquiry, transaction and transfer history.

## Technology Stack Used:

- Javalin - A lightweight web framework
- Google Guice - A lightweight dependency injection framework
- H2 - In-memory relational database
- JDBI - A library built on top of JDBC that provides convenient access to relational data
- Junit - A unit testing framework
- Mockito - A mocking framework for unit test
- Rest Assured - A library for testing RESTful APIs


## How to compile and start ##
`Java 8` and `Maven` will be required for compiling and running the project.

Update the `application.properties` file as per your need. The file contains the following properties

    ## Port on which application will be started
    server.port=7001
    ## Database Properties
    jdbc.url=jdbc:h2:mem:MoneyTransferDB;DB_CLOSE_DELAY=-1
    jdbc.username=
    jdbc.password=

##### Steps to run the application #####
- Run `mvn clean install` to compile application and run tests.
- Goto `/target` folder and run `java -jar money-transfer-api-1.0-SNAPSHOT-shaded.jar`
- Application will be started in the `server.port`.
- Open browser and hit http://localhost:7001/accounts, where `7001` is your server.port. You will see a list of default accounts.
- Now you can perform other operations using the provided APIs.

  
## APIs ##
Response of every API will be in the following format:
```json
{
    "statusCode": "0000",
    "description": "Amount 10 deposited successfully",
    "data": {
        "accountId": 1,
        "accountTitle": "Aliko",
        "balance": 50.00,
        "lastUpdated": "2019-09-15 12:56:04 PM"
    }
}
```

- statusCode - This tells what actually happened with the request. A list of possible status codes is mentioned below.
- description -  The description tells the status of request in more detailed way.
- data - This contains the data that is to be used by the end user. This can contains simple object or list. This will be null or missing when `statusCode` is not `0000`

##### List of possible status codes #####
<table>
<thead>
<tr>
    <th>Code</th>
    <th>Status</th>
    <th>Description</th>
</tr>
</thead>

<tbody>
    <tr>
        <td>0000</td>
        <td>Operation Successful</td>
        <td>
            Following descriptions can be assiciated with <b>0000</b>  
            <ul>
                <li>Operation Successful</li>
                <li>Account created successfully with Account # {}</li>
                <li>Account balance retrieved successfully</li>
                <li>Account transaction history retrieved successfully</li>
                <li>Account transfer history retrieved successfully</li>
                <li>Amount {} deposited successfully</li>
                <li>Amount {} withdrawn successfully</li>
                <li>Amount {} has been successfully transferred to Account # {}</li>
            </ul> 
        </td>
    </tr>
    <tr>
        <td>0001</td>
        <td>Failed</td>
        <td>
        Account with ID: {} doesn't exists
        </td>
    </tr>
    <tr>
        <td>0002</td>
        <td>Failed</td>
        <td>
        Account doesn't have sufficient balance to perform operation
        </td>
    </tr>
    <tr>
        <td>0003</td>
        <td>Failed</td>
        <td>
        Invalid value for amount provided
        </td>
    </tr>
    <tr>
        <td>0004</td>
        <td>Failed</td>
        <td>
        Failed to create account, please check if all the details are provided
        </td>
    </tr>
</tbody>

</table>


##### List of API Endpoints #####
                                                        
<table>
<thead>
<tr>
<th>Endpoint</th>
<th>Request</th>
<th>Response</th>
</tr>
</thead>
<tbody>
<tr>
	<td><code>POST /accounts</code></td>
	<td>Creates new account<br/><br/>
	    Body:
	    <pre>
{
    "accountTitle": "Hammad Naeem",
    "balance": "10"
}
	    </pre>
	</td>
	<td>
      <pre>
{
    "statusCode": "0000",
    "description": "Account created successfully with Account # 10001",
    "data": {
        "accountId": 10001,
        "accountTitle": "Hammad Naeem",
        "balance": 10.00,
        "lastUpdated": "2019-09-15 12:37:02 PM"
    }
}
	  </pre>
</tr>

<tr>
	<td><code>GET /accounts/{id}</code></td>
	<td>Retrieves an account<br/><br/>
	Path Params:<br/>
	<b>id</b>: Account ID
	<td>
      <pre>
{
  "statusCode": "0000",
  "description": "Operation Successful",
  "data": {
    "accountId": 1,
    "accountTitle": "Aliko",
    "balance": 10.00,
    "lastUpdated": "2019-09-16 06:52:35 AM"
  }
}
	  </pre>
    </td>
</tr>

<tr>
	<td><code>GET /accounts/{id}/balance-inquiry</code></td>
	<td>Retrieves account balance<br/><br/>
	Path Params:<br/>
	<b>id</b>: Account ID
	<td>
      <pre>
{
    "statusCode": "0000",
    "description": "Account balance retrieved successfully",
    "data": {
        "balance": 10.00
    }
}
	  </pre>
    </td>
</tr>

<tr>
	<td><code>POST /accounts/{id}/deposit</code></td>
	<td>Deposit Account<br/><br/>
	Path Params:<br/>
	<b>id</b>: Account ID<br/><br/>
	Body:
    	    <pre>
{
    "amount": "10"
}
    	    </pre>
	<td>
      <pre>
{
    "statusCode": "0000",
    "description": "Amount 10 deposited successfully",
    "data": {
        "accountId": 1,
        "accountTitle": "Aliko",
        "balance": 20.00,
        "lastUpdated": "2019-09-16 07:07:53 AM"
    }
}
	  </pre>
    </td>
</tr>

<tr>
	<td><code>POST /accounts/{id}/withdraw</code></td>
	<td>Withdraw from Account<br/><br/>
	Path Params:<br/>
	<b>id</b>: Account ID<br/><br/>
	Body:
    	    <pre>
{
    "amount": "10"
}
    	    </pre>
	<td>
      <pre>
{
    "statusCode": "0000",
    "description": "Amount 10 withdrawn successfully",
    "data": {
        "accountId": 1,
        "accountTitle": "Aliko",
        "balance": 10.00,
        "lastUpdated": "2019-09-16 07:08:59 AM"
    }
}
	  </pre>
    </td>
</tr>

<tr>
	<td><code>POST /accounts/{id}/transfer/{toAccountId}</code></td>
	<td>Transfer from one account to another<br/><br/>
	Path Params:<br/>
	<b>id</b>: Account ID<br/>
	<b>toAccountId</b>: Account ID to transfer amount<br/><br/>
	Body:
    	    <pre>
{
    "amount": "10"
}
    	    </pre>
	<td>
      <pre>
{
    "statusCode": "0000",
    "description": "Amount 10 has been successfully transferred to Account # 2",
    "data": {
        "accountId": 1,
        "accountTitle": "Aliko",
        "balance": 0.00,
        "lastUpdated": "2019-09-16 07:12:42 AM"
    }
}
	  </pre>
    </td>
</tr>

<tr>
 	<td><code>GET /accounts/{id}/transaction-history</code></td>
 	<td>Retrieves transaction history<br/><br/>
 	Path Params:<br/>
 	<b>id</b>: Account ID
 	<td>
       <pre>
{
    "statusCode": "0000",
    "description": "Account transaction history retrieved successfully",
    "data": [
        {
            "transactionHistoryId": 3,
            "accountId": 1,
            "transactionSummary": "Amount 10 has been transferred to Account # 2.",
            "transactionAmount": 10.00,
            "type": "WITHDRAW",
            "closingBalance": 0.00,
            "transactionDate": "2019-09-16 07:12:42 AM"
        },
        {
            "transactionHistoryId": 1,
            "accountId": 1,
            "transactionSummary": "Amount 10 deposited by self.",
            "transactionAmount": 10.00,
            "type": "DEPOSIT",
            "closingBalance": 20.00,
            "transactionDate": "2019-09-16 07:07:53 AM"
        }
    ]
}
 	  </pre>
     </td>
 </tr>
 
 <tr>
  	<td><code>GET /accounts/{id}/transfer-history</code></td>
  	<td>Retrieves transfer history<br/><br/>
  	Path Params:<br/>
  	<b>id</b>: Account ID
  	<td>
        <pre>
{
    "statusCode": "0000",
    "description": "Account transfer history retrieved successfully",
    "data": [
        {
            "transferHistoryId": 1,
            "fromAccountId": 1,
            "toAccountId": 2,
            "amount": 10.00,
            "transactionHistoryId": 3,
            "transferDate": "2019-09-16 07:12:42 AM"
        }
    ]
}
  	  </pre>
      </td>
  </tr>

</tbody></table>
