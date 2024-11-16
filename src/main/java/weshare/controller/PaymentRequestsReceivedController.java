package weshare.controller;

import io.javalin.http.Handler;
import weshare.model.Expense;
import weshare.model.PaymentRequest;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.Routes;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

import static weshare.model.MoneyHelper.amountOf;

public class PaymentRequestsReceivedController {
    
    public static final Handler view = context -> {

        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<PaymentRequest> paymentRequestsReceived = expensesDAO.findPaymentRequestsReceived(personLoggedIn);

        double grandTotal = 0;
        for (PaymentRequest paymentRequest : paymentRequestsReceived) {
            MonetaryAmount paymentRequestdAmount = paymentRequest.getAmountToPay();
            double re = paymentRequestdAmount.getNumber().doubleValue();
            grandTotal += re;
        }
        String fomattedGrandTotal1 = String.format("ZAR %.2f", grandTotal);
        String grand_total = fomattedGrandTotal1.split(",")[0];


        Map<String, Object> viewModel = Map.of("paymentRequestsReceived", paymentRequestsReceived, "grand_total", grand_total);
        context.render("paymentRequestsReceived.html", viewModel);
    };

    public static final Handler submit = context -> {
        ExpenseDAO expenseDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedin = WeShareServer.getPersonLoggedIn(context);
        Collection<PaymentRequest> paymentRequestsReceived = expenseDAO.findPaymentRequestsReceived(personLoggedin);

        String paymentid = context.formParamAsClass("payment", String.class).get();

        for (PaymentRequest paymentRequest : paymentRequestsReceived) {
            if (String.valueOf(paymentRequest.getId()).equals(paymentid)) {
                paymentRequest.pay(personLoggedin, LocalDate.now());
                Expense paymentExpense = new Expense(personLoggedin, paymentRequest.getDescription(), paymentRequest.getAmountToPay(), LocalDate.now());
                expenseDAO.save(paymentExpense);
            }
        }


        context.redirect(Routes.paymentRequestReceived);
    };

}
