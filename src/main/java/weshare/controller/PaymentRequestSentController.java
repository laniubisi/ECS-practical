package weshare.controller;

import io.javalin.http.Handler;
import weshare.model.Expense;
import weshare.model.PaymentRequest;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.MonetaryAmount;
import javax.money.NumberValue;
import java.util.Collection;
import java.util.Map;

public class PaymentRequestSentController {

    // private static Expense correctExpense;

    public static final Handler view = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<PaymentRequest> paymentRequestsSent = expensesDAO.findPaymentRequestsSent(personLoggedIn);


        double grandTotal = 0;
        for (PaymentRequest paymentRequest : paymentRequestsSent) {
            MonetaryAmount paymentRequestdAmount = paymentRequest.getAmountToPay();
            double re = paymentRequestdAmount.getNumber().doubleValue();
            grandTotal += re;
        }
        String fomattedGrandTotal1 = String.format("ZAR %.2f", grandTotal);
        String grand_total = fomattedGrandTotal1.split(",")[0];


        Map<String, Object> viewModel = Map.of("paymentRequestsSent", paymentRequestsSent, "grand_total", grand_total);
        context.render("paymentRequestSent.html", viewModel);
    };

}
