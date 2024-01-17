import React from 'react'

const TaskCell = (ctx) => {
    return (
        <div className="taskcellwrapper">
            <div className="">Type: markdown</div>
            <div className="">{`Metadata: {
                "task_id": "Branches_IfElseVariants",
                "deletable": false,
                "editable": false,
                "language": "en"
            }`}</div>
            <div className="">Source: <br />Create a program in two different variants that calculates the shipping costs of an online shop.<br />

For this, read the payment method, either "credit card" or "invoice", from the user, as well as the amount to be paid.<br />

Furthermore, the user should be asked whether he or she is a Prime customer note: do not use a Boolean variable for yes/no but a string!.<br />

The shipping costs are calculated as follows:<br />

If the user is a Prime customer, he or she pays no shipping costs.<br />
If the user is not a Prime customer and pays by credit card, no shipping costs have to be paid for amounts of 29 Euro or above, otherwise the customer has to pay 4.99 Euro shipping costs.<br />
If the user pays by invoice, the shipping costs are 4.99 Euro for amounts below 29 Euro and 2.50 Euro for amounts above 29 Euro.<br />
Create 2 different variants of a program that calculates and displays the shipping costs.<br />

In the first variant, use if-else or if-elif-else branches with and or or conditions and in the second variant use nested if-else or if-elif-else branches.<br />

Document your considerations briefly as a comment with the respective conditions.<br />

You do not have to check for wrong inputs.<br />
        </div>
        </div>
    )
}

export default TaskCell