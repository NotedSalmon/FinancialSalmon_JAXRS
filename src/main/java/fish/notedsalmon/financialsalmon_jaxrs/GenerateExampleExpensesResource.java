package fish.notedsalmon.financialsalmon_jaxrs;

import fish.notedsalmon.entities.Category;
import fish.notedsalmon.entities.Expenses;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Path("/generateExampleExpenses")
@RequestScoped
public class GenerateExampleExpensesResource {

    @Context
    private UriInfo uriInfo;

    @Inject
    private EntityManager em;

    private static final String[] SAMPLE_DESCRIPTIONS = {
            "Restaurant", "Rent", "Electricity bill",
            "Groceries", "Cinema", "Gym",
            "Clothes shopping", "Internet", "UBER",
            "Coffee", "Games", "Health Supplements",
            "Netflix"
    };

    private static final Random RANDOM = new Random();

    public GenerateExampleExpensesResource() {}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public String generateExampleExpenses() {
        List<Category> categories = em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
        if (categories.isEmpty()) {
            return "Error: No categories found";
        }

        for (int i=0; i < RANDOM.nextInt(51) + 50; i++) {
            Expenses expense = new Expenses();
            expense.setAmount(generateRandomAmount());
            expense.setCategory(randomCategory(categories));
            expense.setDescription(randomDescription());
            expense.setExpense_date(generateRandomDate());

            em.persist(expense);
        }

        return "Successful generated Expenses";
    }

    private BigDecimal generateRandomAmount() {
        double min = 1.00;
        double max = 500.00;
        double amount = min + (max - min) * RANDOM.nextDouble();
        return BigDecimal.valueOf(amount);
    }

    private Category randomCategory(List<Category> categories) {
        return categories.get(RANDOM.nextInt(categories.size()));
    }

    private String randomDescription() {
        return SAMPLE_DESCRIPTIONS[RANDOM.nextInt(SAMPLE_DESCRIPTIONS.length)];
    }

    private LocalDateTime generateRandomDate() {
        int year = LocalDateTime.now().getYear();
        int month = RANDOM.nextInt(12) + 1;
        int day = RANDOM.nextInt(28) + 1;
        return LocalDateTime.of(year, month, day, RANDOM.nextInt(24), RANDOM.nextInt(60));
    }
}