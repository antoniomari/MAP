package restClient;

import org.json.JSONObject;
import org.json.JSONTokener;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class RecipeRestClient
{
    private static final Client client = ClientBuilder.newClient();
    private static final String url = "https://www.themealdb.com/api/json/v1/1/random.php";
    private static final WebTarget target = client.target(url);
    private static final Response resp = target.request(MediaType.APPLICATION_JSON).get();
    private static final JSONTokener jt = new JSONTokener(resp.readEntity(String.class));
    private static final JSONObject jo = new JSONObject(jt);
    private static final JSONObject meal = (JSONObject) jo.getJSONArray("meals").get(0);
    private String getNameRecipe()
    {
        return meal.getString("strMeal");
    }

    private String getProcedure()
    {
        return meal.getString("strInstructions");
    }

    private String[] getIngredients()
    {
        String[] ingredient = new String[10];

        for (int i=0; i<10; i++)
        {
            ingredient[i] = (meal.getString("strIngredient"+(i+1))+ ": " + meal.getString("strMeasure" + (i+1)));
        }
        return ingredient;
    }

    public static void main (String[] args){
        RecipeRestClient recipe = new RecipeRestClient();
        System.out.println(recipe.getNameRecipe());
        System.out.println(recipe.getProcedure());
        System.out.println(Arrays.toString(recipe.getIngredients()));
    }

}
