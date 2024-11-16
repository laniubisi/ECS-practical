package weshare.controller;

import io.javalin.http.Handler;
import weshare.model.Expense;
import weshare.model.PaymentRequest;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.persistence.PersonDAO;
import weshare.server.Routes;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static weshare.model.DateHelper.TOMORROW;
import static weshare.model.MoneyHelper.amountOf;

public class NewPaymentRequestFormController {

    private static String idGlo;
//    private static Collection<PaymentRequest> listofpaymentrequests;

    public static final Handler view = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);

        String s = context.req.toString();
        String id = s.split("[=)]")[1];
        idGlo = id;

        Expense correctExpense = null;
        for (Expense expense : expenses) {
            String expenseId = String.valueOf(expense.getId());
            if (expenseId.equals(id)) {
                correctExpense = expense;
            }
        }

        Map<String, Object> viewModel = Map.of("paymentrequest", expenses, "correctExpense", correctExpense);
        context.render("paymentrequest.html", viewModel);
    };
    
    public static final Handler submit = context -> {
        ExpenseDAO expenseDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        String id = idGlo;

        String email = context.formParamAsClass("email", String.class).get();
        long amount = context.formParamAsClass("amount", long.class).get();
        String dateString = context.formParamAsClass("due_date", String.class).get();

        String[] datesplit = dateString.split("-");
        int day = Integer.parseInt(datesplit[2]);
        int month = Integer.parseInt(datesplit[1]);
        int year = Integer.parseInt(datesplit[0]);

        LocalDate date = LocalDate.of(year, month, day);
        Person personLoggedin = WeShareServer.getPersonLoggedIn(context);


        Collection<Expense> expenses = expenseDAO.findExpensesForPerson(personLoggedin);

        for (Expense expense : expenses) {
            String expenseId = String.valueOf(expense.getId());
            if (expenseId.equals(id)) {
                expense.requestPayment(new Person(email), amountOf(amount), date);
                expenseDAO.save(expense);
            }
        }

        context.redirect("/paymentrequest?expenseId=" + idGlo);


    };

}