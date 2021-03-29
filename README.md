# scala-payments
## End points
The end points are:

Method | Url         | Description
------ | ----------- | -----------
GET    | /payment      | Returns all payments.
GET    | /payment/{id} | Returns the payment for the specified id, 404 when no payment present with this id.
POST   | /payment      | Creates a payment, give as body JSON with the sender, amount and receiver, returns a 201 with the created payment. Returns 406 Not Acceptable when amount is less than 0.
PUT    | /payment/{id} | Updates an existing payment, give as body JSON with the new amount, returns a 200 with the updated payment when a payment is present with the specified id, 404 otherwise.
DELETE | /payment/{id} | Deletes the payment with the specified id, 404 when no payment present with this id.