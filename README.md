# Tool Rentals Service

This service represents a tool rentals service where customers can rent large tools for a specified number of days. When a customer checks out a tool, a Rental Agreement is produced, specifying important details such as the daily charge rate, number of charge days, discount amount and final charge. The daily charge rate is different for each tool, and some tools are free of charge on weekend or holidays. Clerks may give customers a discount that reduces the final charge of the rental.

### The Data
The in memory H2 database is initially loaded with the following tools specified in the TOOL table:
| Tool Code | Tool Type  | Brand   |
| --------- | ---------- | ------- |
| CHNS      | Chainsaw   | Stihl   |
| LADW      | Ladder     | Werner  |
| JAKD      | Jackhammer | DeWalt  |
| JAKR      | Jackhammer | Ridgid  |

Each TOOL is related to a PRICE entry in a many-to-one relationship on the Tool Type column. The H2 database is initially loaded with the following Price data:
| Tool Type | Daily Charge | Weekday Charge | Weekend Charge | Holiday Charge |
| --------- | ------------ | -------------- | -------------- | -------------- |
| Ladder    | $1.99        | Yes            | Yes            | No             |
| Chainsaw  | $1.49        | Yes            | No             | Yes            |
| Jackhammer| $2.99        | Yes            | No             | No             |

*Note:* See the data.sql init script located under `src/main/resources`

### Holidays
The holidays observed are Independence day (July 4) and Labor Day (first Monday of September). If July 4th falls on a weekend, is is observed on the closest weekday (If Saturday, observed on Friday; if Sunday, observed on Monday).

### Checkout
The service contains the following endpoint which runs the checkout logic and procudes rental agreement data:
`GET /toolRentals/checkout`

The required request parameters are as follows:
* toolCode - The unique Tool Code which specifies the tool to be rented
* rentalDays - The number of days the tool will be rented for
* discount - The discount applied to the final charge as a whole number
* checkoutDate - The date the tool will be checked out, in yyyyMMdd format

Example checkout request: `http://localhost:8080/toolRentals/checkout?toolCode=CHNS&rentalDays=4&discount=10&checkoutDate=20240724`

This will produce the following rental agreement response:
```
{
    "rentalAgreement": {
        "tool": {
            "price": {
                "dailyCharge": 1.49,
                "weekdayCharge": true,
                "weekendCharge": false,
                "holidayCharge": true
            },
            "toolCode": "CHNS",
            "toolType": "Chainsaw",
            "brand": "Stihl"
        },
        "rentalDays": 4,
        "checkoutDate": "2024-07-24",
        "dueDate": "2024-07-28",
        "chargeDays": 2,
        "preDiscountCharge": 2.98,
        "discountPercent": 10,
        "discountAmount": 0.3,
        "finalCharge": 2.68
    }
}
```

### Running the application
After cloning the repository, you can run the application either using maven or java.

**Using maven:** In the root of the project, simply run `mvn spring-boot:run`

**Using java:** First compile the project using `mvn clean install`. Then, cd to the target directory and run `java -jar tool-rentals-service.jar`

The service is set to listen on port 8080. Also, the h2 database console is available at `http://localhost:8080/h2-console` and can be accessed using the default username `sa` and no password.
