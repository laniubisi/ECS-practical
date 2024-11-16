package weshare.controller;

import io.javalin.http.Handler;
import org.javamoney.moneta.Money;
import weshare.model.Expense;
import weshare.model.PaymentRequest;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.persistence.PersonDAO;
import weshare.server.Routes;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import static weshare.model.DateHelper.TODAY;
import static weshare.model.MoneyHelper.amountOf;

public class AddExpenseActionController {




    public static final Handler submit = context -> {
        PersonDAO personDAO = ServiceRegistry.lookup(PersonDAO.class);
        ExpenseDAO expenseDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        context.redirect(Routes.EXPENSES);
        String description = context.formParamAsClass("Description", String.class).get();
        long amount = context.formParamAsClass("Amount", long.class).get();
        String dateString = context.formParamAsClass("Date", String.class).get();

        String[] datesplit = dateString.split("-");
        int day = Integer.parseInt(datesplit[2]);
        int month = Integer.parseInt(datesplit[1]);
        int year = Integer.parseInt(datesplit[0]);

        LocalDate date = LocalDate.of(year, month, day);
        Person personLoggedin = WeShareServer.getPersonLoggedIn(context);


        Expense expense1 = new Expense(personLoggedin, description, amountOf(amount), LocalDate.now());
        expenseDAO.save(expense1);

    };
}
