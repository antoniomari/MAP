package restClient;

import org.json.JSONObject;
import org.json.JSONTokener;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

public class RecipeRestClient
{
    private static final Client CLIENT = ClientBuilder.newClient();
    private static final String URL = "https://www.themealdb.com/api/json/v1/1/random.php";
    private static final WebTarget TARGET = CLIENT.target(URL);

    private static final String[] DEFAULT_INGREDIENTS = {"Onion: 10 cloves",
                                                        "Garlic: 40 cloves",
                                                        "Basil: 25 leaves",
                                                        "Parsley: a piacere",
                                                        "Cheese: too much",
                                                        "Tomato sauce 20 cups"};

    /*
    private String getNameRecipe()
    {
        if (STATUS == 200)
            return MEAL.getString("strMeal");
        else
        {
            return "NomeDiDefault";
        }
    }

    private String getProcedure()
    {
        if (STATUS == 200)
            return MEAL.getString("strInstructions");
        else
        {
            return "ProceduraDiDefault";
        }
    }

     */

    private static void generateRecipe()
    {
        final Response resp = TARGET.request(MediaType.APPLICATION_JSON).get();
        final int status = resp.getStatus();
        JSONTokener jt = new JSONTokener(resp.readEntity(String.class));

        JSONObject mealJson = (JSONObject) new JSONObject(jt).getJSONArray("meals").get(0);

        System.out.println(mealJson);
    }


    private List<String> getIngredients()
    {
        if (STATUS == 200)
        {
            String[] ingredient = new String[10];

            for (int i=0; i < Math.min(10, MEAL.length()); i++)
            {
                ingredient[i] = (MEAL.getString("strIngredient"+(i+1))+ ": " + MEAL.getString("strMeasure" + (i+1)));
            }
            return ingredient;
        }
        else
        {
            return new String[]{"Frigo","vuoto"};
        }
    }

    public static void main (String[] args)
    {

        generateRecipe();
        /*
        RecipeRestClient recipe = new RecipeRestClient();
        System.out.println(recipe.getNameRecipe());
        System.out.println(recipe.getProcedure());
        System.out.println(Arrays.toString(recipe.getIngredients()));
        System.out.println("STATUS: " + STATUS);

         */
    }

}
