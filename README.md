# Banking With Java

A simple banking application developed in Java. The project allows users to manage bank accounts and perform common banking operations such as deposits, withdrawals, transfers, and viewing transaction history.

## Technologies Used

* **Java**
* **JUnit 4 & 5** – for testing
* **Git & GitHub** – version control
* **IntelliJ IDEA** – development environment
* **Trello** – project planning and user stories
* **PlantUML** – UML class diagram

## Trello

The project planning, user stories, tasks, and development progress were managed using Trello.

[View the Trello Board](https://trello.com/invite/b/6a9fa7f554df059301ad37dc/ATTIa471c4a109241f2f960eb3248dabccf69026DFA8/mytodo)

## Additional Resources

* [Java Documentation](https://docs.oracle.com/en/java/)
* [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
* [PlantUML Documentation](https://plantuml.com/)

## Planning & Development Process

The project was developed using an incremental approach.

### 1. Planning

We started by identifying the main banking features and creating user stories in Trello. These user stories were then broken down into smaller development tasks.

### 2. Development

The banking system was implemented using Java classes. The main functionality includes:

* Creating and managing bank accounts
* Checking and savings accounts
* Depositing money
* Withdrawing money
* Transferring money between accounts
* Viewing transaction history
* Updating account status
* Applying savings account withdrawal limits

### 3. Testing & Debugging

JUnit tests were used to check that the main banking operations worked correctly.

During debugging, issues such as invalid transfers, incorrect transaction history, account status updates, and savings withdrawal limits were identified and fixed.


## Favorite Functions

### Withdraw

The withdraw function checks whether the account has enough funds before removing money from the balance.

For savings accounts, the withdrawal limit is also checked before allowing the transaction.

### Transfer

The transfer function allows money to be moved from one account to another.

It checks that the destination account exists before completing the transfer and prevents invalid transfers.

### Transaction History

The transaction history keeps track of operations performed on an account, such as deposits, withdrawals, and transfers.

This allows users to review previous banking activity.

## UML Diagram

The following UML diagram represents the structure and relationships between the main classes in the banking application.

<img width="2060" height="1286" alt="rLPHR-Cs37xthz0mOFFXsiEUXwsuDiMX1TgjQ7gunuAncLYe8XaKdI3h-jzdsLR2ocbo1VPoUOhuaP_uaPBepvw1k5cQx2UkS8bgPXgiILlE_9EsDH0iLM70-tDcqhE6yRfmTgh_GVLRblsg_dphrs9VFPBwHPqNh9rLDx11EXYHQSj8SoYm2xrt1jLpfjhVHqNj8l-WVbT51PIzfBx" src="https://github.com/user-attachments/assets/82a51341-828b-4192-98d8-c151041a0a8b" />




## Unresolved Issues & Future Improvements

Possible improvements for future versions include:

* Improving user authentication and login functionality.
* Improving input validation and error messages.
* Adding more detailed transaction information.
* Adding more automated tests for edge cases.
* Improving the overall user experience.

## Project Structure

The project is organized into Java source files, test files, and supporting data files.
AcmeBank
├── src
│   └── com
│       └── ga
│           └── acmebank
│               ├── account
│               │   ├── Account.java
│               │   ├── AccountFileHelper.java
│               │   ├── BankAccount.java
│               │   ├── CheckingAccount.java
│               │   ├── OverdraftActions.java
│               │   └── SavingsAccount.java
│               ├── cards
│               │   ├── Card.java
│               │   ├── MasterCard.java
│               │   ├── MasterCardPlatinum.java
│               │   └── MasterCardTitanium.java
│               ├── user
│               │   ├── Actions.java
│               │   ├── Banker.java
│               │   ├── LoginHandler.java
│               │   ├── UserAccountActions.java
│               │   └── UserRole.java
│               └── util
│                   ├── FileManager.java
│                   └── passwordHasher.java
├── test
│   └── com
│       └── ga
│           └── acmebank
│               └── AccountTest.java
├── data
│   └── users.txt
├── project1diagram.png
└── README.md
## Conclusion

This project demonstrates the use of Java and object-oriented programming concepts to create a basic banking system. It also demonstrates the importance of planning, testing, debugging, and version control during software development.
