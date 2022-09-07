package restClient;

import org.json.JSONObject;
import org.json.JSONTokener;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeRestClient
{
    private static final Client CLIENT = ClientBuilder.newClient();
    private static final String URL = "https://www.themealdb.com/api/json/v1/1/random.php";
    private static final int MAX_INGREDIENTS = 6;

    private static final String[] DEFAULT_INGREDIENTS = {"Onion - 10 cloves",
                                                        "Garlic - 40 cloves",
                                                        "Basil - 25 leaves",
                                                        "Parsley - a piacere",
                                                        "Cheese - too much",
                                                        "Tomato sauce - 20 cups"};

    private static final String DEFAULT_CATEGORY = "Italian";

    public static class Recipe
    {
        String category;
        List<String> ingredients;

        Recipe(final String category, final List<String> ingredients)
        {
            this.category = category;
            this.ingredients = ingredients;
        }

        public String getCategory()
        {
            return category;
        }

        public List<String> getIngredients()
        {
            return ingredients;
        }
    }

    public static Recipe generateRecipe()
    {

        WebTarget target = CLIENT.target(URL);
        Response resp;
        try
        {
            resp = target.request(MediaType.APPLICATION_JSON).get();
        } catch (ProcessingException e)
        {
            return new Recipe(DEFAULT_CATEGORY, Arrays.asList(DEFAULT_INGREDIENTS));
        }

        int status = resp.getStatus();

        if (status == 200)
        {

            JSONTokener jt = new JSONTokener(resp.readEntity(String.class));

            JSONObject mealJson = (JSONObject) new JSONObject(jt).getJSONArray("meals").get(0);

            // prendi categoria
            String category = mealJson.getString("strCategory");

            if(category.equals(""))
                category = DEFAULT_CATEGORY;

            List<String> ingredients = getIngredients(mealJson);

            return new Recipe(category, ingredients);
        }
        else
        {
            return new Recipe(DEFAULT_CATEGORY, Arrays.asList(DEFAULT_INGREDIENTS));
        }
    }


    private static List<String> getIngredients(JSONObject mealJson)
    {
        List<String> ingredients = new ArrayList<>(MAX_INGREDIENTS);

        for (int i = 1; i <= MAX_INGREDIENTS; i++)
        {
            String name = mealJson.getString("strIngredient" + (i));
            String measure = mealJson.getString("strMeasure" + (i));

            // se hai finito gli ingredienti
            if(name.equals(""))
                break;
            // aggiungi stampa ingrediente
            ingredients.add(name + " - " + measure);
        }

        return ingredients;
    }

}
