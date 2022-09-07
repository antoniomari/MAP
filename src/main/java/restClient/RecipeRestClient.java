package restClient;

import org.json.JSONObject;
import org.json.JSONTokener;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;

public class RecipeRestClient
{
    private static final Client CLIENT = ClientBuilder.newClient();
    private static final String URL = "https://www.themealdb.com/api/json/v1/1/random.php";
    private static final WebTarget TARGET = CLIENT.target(URL);
    private static final Response RESP = TARGET.request(MediaType.APPLICATION_JSON).get();
    private static final int STATUS = 404; //RESP.getStatus();
    private static final JSONTokener JT = new JSONTokener(RESP.readEntity(String.class));
    private static final JSONObject JO = new JSONObject(JT);
    private static final JSONObject MEAL = (JSONObject) JO.getJSONArray("meals").get(0);
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

    private String[] getIngredients()
    {
        if (STATUS == 200)
        {
            String[] ingredient = new String[10];

            for (int i=0; i<10; i++)
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

    public static void main (String[] args){
        RecipeRestClient recipe = new RecipeRestClient();
        System.out.println(recipe.getNameRecipe());
        System.out.println(recipe.getProcedure());
        System.out.println(Arrays.toString(recipe.getIngredients()));
        System.out.println("STATUS: " + STATUS);
    }

}
