package com.revoult.moneytransferapi.controller;

import com.revoult.moneytransferapi.ApplicationMain;
import com.revoult.moneytransferapi.config.JavalinConfig;
import com.revoult.moneytransferapi.model.Account;
import com.revoult.moneytransferapi.utils.PropertyUtils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;


public class AccountControllerTest {
    @BeforeAll
    public static void setup() {
        ApplicationMain.startServer();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = PropertyUtils.getPropertyAsInteger("server.port");
    }

    @AfterAll
    public static void shutdown() {
        JavalinConfig.stop();
    }

    @Test
    public void testPing() {
        given()
                .contentType("application/json")
                .when().get("/accounts")
                .then().statusCode(200);
    }

    @Test
    public void testAccountCreation() {
        Account account = new Account();
        account.setAccountTitle("Test 1");
        account.setBalance(BigDecimal.TEN);

        Response response = given()
                .contentType("application/json")
                .body(account)
                .when().post("/accounts")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"))
                .body("description", startsWith("Account created successfully with Account #"))
                .body("data.accountTitle", equalTo(account.getAccountTitle()))
                .extract()
                .response();

        Integer accountId = response.path("data.accountId");
        BigDecimal balance = new BigDecimal(response.path("data.balance").toString());

        Assertions.assertEquals(balance.compareTo(BigDecimal.TEN), 0, "balance returned from api is same as used in account creation");
        Assertions.assertNotNull(accountId, "account has some account Id");
    }

    @Test
    public void testAccountCreationInvalidAccountTitle() {
        Account account = new Account();
        account.setBalance(BigDecimal.TEN);

        given()
                .contentType("application/json")
                .body(account)
                .when().post("/accounts")
                .then().statusCode(200)
                .body("statusCode", equalTo("0004"))
                .body("description", startsWith("Failed to create account, please check if all the details are provided"));
    }

    @Test
    public void testFindOneAccount() {
        Account account = new Account();
        account.setAccountTitle("Test 1");
        account.setBalance(BigDecimal.TEN);

        Response response = given()
                .contentType("application/json")
                .body(account)
                .when().post("/accounts")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"))
                .extract()
                .response();

        Integer accountId = response.path("data.accountId");


        //check found one returns the same account details as created above
        given()
                .contentType("application/json")
                .pathParam("accountId", accountId)
                .when().get("/accounts/{accountId}")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"))
                .body("data.accountId", equalTo(accountId))
                .body("data.accountTitle", equalTo(account.getAccountTitle()));

    }

    @Test
    public void testFindOneInvalidAccountAccount() {

        Integer accountId = Integer.MAX_VALUE;

        given()
                .contentType("application/json")
                .pathParam("accountId", accountId)
                .when().get("/accounts/{accountId}")
                .then().statusCode(200)
                .body("statusCode", equalTo("0001"));

    }

    @Test
    public void testDepositAccount() {
        Account account = new Account();
        account.setAccountTitle("Test 1");
        account.setBalance(BigDecimal.TEN);

        Response response = given()
                .contentType("application/json")
                .body(account)
                .when().post("/accounts")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"))
                .extract()
                .response();

        Integer accountId = response.path("data.accountId");
        BigDecimal balance = new BigDecimal(response.path("data.balance").toString());

        Assertions.assertEquals(balance.compareTo(new BigDecimal(10)), 0);

        response = given()
                .contentType("application/json")
                .pathParam("accountId", accountId)
                .body("{\"amount\":10}")
                .when().post("/accounts/{accountId}/deposit")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"))
                .body("data.accountId", equalTo(accountId))
                .extract()
                .response();

        balance = new BigDecimal(response.path("data.balance").toString());

        Assertions.assertEquals(balance.compareTo(new BigDecimal(20)), 0, "checks the new balance after deposit");
    }

    @Test
    public void testDepositAccountInvalidAmount() {
        Account account = new Account();
        account.setAccountTitle("Test 1");
        account.setBalance(BigDecimal.TEN);

        Response response = given()
                .contentType("application/json")
                .body(account)
                .when().post("/accounts")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"))
                .extract()
                .response();

        Integer accountId = response.path("data.accountId");

        given()
                .contentType("application/json")
                .pathParam("accountId", accountId)
                .body("{\"amount\":-10}")
                .when().post("/accounts/{accountId}/deposit")
                .then().statusCode(200)
                .body("statusCode", equalTo("0003"));
    }

    @Test
    public void testWithdrawAccount() {
        Account account = new Account();
        account.setAccountTitle("Test 1");
        account.setBalance(new BigDecimal(20));

        Response response = given()
                .contentType("application/json")
                .body(account)
                .when().post("/accounts")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"))
                .extract()
                .response();

        Integer accountId = response.path("data.accountId");
        BigDecimal balance = new BigDecimal(response.path("data.balance").toString());

        Assertions.assertEquals(balance.compareTo(new BigDecimal(20)), 0, "checks the balance before withdrawal");

        response = given()
                .contentType("application/json")
                .pathParam("accountId", accountId)
                .body("{\"amount\":10}")
                .when().post("/accounts/{accountId}/withdraw")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"))
                .body("data.accountId", equalTo(accountId))
                .extract()
                .response();

        balance = new BigDecimal(response.path("data.balance").toString());

        Assertions.assertEquals(balance.compareTo(new BigDecimal(10)), 0, "checks the balance after withdrawal");
    }

    @Test
    public void testWithdrawAccountInsufficientBalance() {
        Account account = new Account();
        account.setAccountTitle("Test 1");
        account.setBalance(new BigDecimal(20));

        Response response = given()
                .contentType("application/json")
                .body(account)
                .when().post("/accounts")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"))
                .extract()
                .response();

        Integer accountId = response.path("data.accountId");
        BigDecimal balance = new BigDecimal(response.path("data.balance").toString());

        Assertions.assertEquals(balance.compareTo(new BigDecimal(20)), 0);

        given()
                .contentType("application/json")
                .pathParam("accountId", accountId)
                .body("{\"amount\":100}")
                .when().post("/accounts/{accountId}/withdraw")
                .then().statusCode(200)
                .body("statusCode", equalTo("0002"))
                .body("description", equalTo("Account doesn't have sufficient balance to perform operation"));

    }

    @Test
    public void testTransferBetweenAccounts() {
        Account fromAccount = new Account();
        fromAccount.setAccountTitle("Test 1");
        fromAccount.setBalance(new BigDecimal(20));

        Response response = given()
                .contentType("application/json")
                .body(fromAccount)
                .when().post("/accounts")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"))
                .extract()
                .response();

        Integer fromAccountId = response.path("data.accountId");
        BigDecimal balance = new BigDecimal(response.path("data.balance").toString());
        Assertions.assertEquals(balance.compareTo(new BigDecimal(20)), 0, "check from account balance before transfer");

        Account toAccount = new Account();
        toAccount.setAccountTitle("Test 2");
        toAccount.setBalance(new BigDecimal(30));

        response = given()
                .contentType("application/json")
                .body(toAccount)
                .when().post("/accounts")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"))
                .extract()
                .response();

        Integer toAccountId = response.path("data.accountId");
        balance = new BigDecimal(response.path("data.balance").toString());
        Assertions.assertEquals(balance.compareTo(new BigDecimal(30)), 0, "check to account balance before transfer");


        given()
                .contentType("application/json")
                .pathParam("accountId", fromAccountId)
                .pathParam("toAccountId", toAccountId)
                .body("{\"amount\":5}")
                .when().post("/accounts/{accountId}/transfer/{toAccountId}")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"));


        response = given()
                .contentType("application/json")
                .pathParam("accountId", fromAccountId)
                .when().get("/accounts/{accountId}/balance-inquiry")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"))
                .extract()
                .response();
        balance = new BigDecimal(response.path("data.balance").toString());
        Assertions.assertEquals(balance.compareTo(new BigDecimal(15)), 0, "from account balance is decreased after transfer");


        response = given()
                .contentType("application/json")
                .pathParam("accountId", toAccountId)
                .when().get("/accounts/{accountId}/balance-inquiry")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"))
                .extract()
                .response();
        balance = new BigDecimal(response.path("data.balance").toString());
        Assertions.assertEquals(balance.compareTo(new BigDecimal(35)), 0, "to account balance is increased after transfer");

        //Check transaction history of from account
        response = given()
                .contentType("application/json")
                .pathParam("accountId", fromAccountId)
                .when().get("/accounts/{accountId}/transaction-history")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"))
                .body("data[0].type", equalTo("WITHDRAW"))
                .body("data[0].accountId", equalTo(fromAccountId))
                .extract()
                .response();

        balance = new BigDecimal(response.path("data[0].transactionAmount").toString());
        Assertions.assertEquals(balance.compareTo(new BigDecimal(5)), 0, "amount of transaction history is same as of transaction amount");

        balance = new BigDecimal(response.path("data[0].closingBalance").toString());
        Assertions.assertEquals(balance.compareTo(new BigDecimal(15)), 0, "closing balance is same as of balance inquiry of to account");


        //Check transaction history of to account
        response = given()
                .contentType("application/json")
                .pathParam("accountId", toAccountId)
                .when().get("/accounts/{accountId}/transaction-history")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"))
                .body("data[0].type", equalTo("DEPOSIT"))
                .body("data[0].accountId", equalTo(toAccountId))
                .extract()
                .response();

        balance = new BigDecimal(response.path("data[0].transactionAmount").toString());
        Assertions.assertEquals(balance.compareTo(new BigDecimal(5)), 0, "amount of transaction history is same as transaction amount");

        balance = new BigDecimal(response.path("data[0].closingBalance").toString());
        Assertions.assertEquals(balance.compareTo(new BigDecimal(35)), 0, "closing balance is same as of balance inquiry of from account");


        //check the transfer history of from account
        response = given()
                .contentType("application/json")
                .pathParam("accountId", fromAccountId)
                .when().get("/accounts/{accountId}/transfer-history")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"))
                .body("data[0].fromAccountId", equalTo(fromAccountId))
                .body("data[0].toAccountId", equalTo(toAccountId))
                .extract()
                .response();

        balance = new BigDecimal(response.path("data[0].amount").toString());
        Assertions.assertEquals(balance.compareTo(new BigDecimal(5)), 0, "transfer history has same amount as of transaction");
    }

    @Test
    public void testTransferBetweenAccountsInSufficientBalance() {
        Account fromAccount = new Account();
        fromAccount.setAccountTitle("Test 1");
        fromAccount.setBalance(new BigDecimal(20));

        Response response = given()
                .contentType("application/json")
                .body(fromAccount)
                .when().post("/accounts")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"))
                .extract()
                .response();

        Integer fromAccountId = response.path("data.accountId");
        BigDecimal balance = new BigDecimal(response.path("data.balance").toString());
        Assertions.assertEquals(balance.compareTo(new BigDecimal(20)), 0);

        Account toAccount = new Account();
        toAccount.setAccountTitle("Test 2");
        toAccount.setBalance(new BigDecimal(30));

        response = given()
                .contentType("application/json")
                .body(toAccount)
                .when().post("/accounts")
                .then().statusCode(200)
                .body("statusCode", equalTo("0000"))
                .extract()
                .response();

        Integer toAccountId = response.path("data.accountId");
        balance = new BigDecimal(response.path("data.balance").toString());
        Assertions.assertEquals(balance.compareTo(new BigDecimal(30)), 0);

        given()
                .contentType("application/json")
                .pathParam("accountId", fromAccountId)
                .pathParam("toAccountId", toAccountId)
                .body("{\"amount\":50}")
                .when().post("/accounts/{accountId}/transfer/{toAccountId}")
                .then().statusCode(200)
                .body("statusCode", equalTo("0002"));
    }

}
