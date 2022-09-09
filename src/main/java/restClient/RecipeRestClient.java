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

/**
 * Classe per generare ricette di "cocktail" utilizzando
 * il servizio REST fornito da <a href="https://www.themealdb.com">themealdb</a>
 */
public class RecipeRestClient
{
    /** Client per eseguire chiamate REST. */
    private static final Client CLIENT = ClientBuilder.newClient();

    /** Indirizzo per la chiamata REST per ottenere le informazioni sui "cocktail". */
    private static final String URL = "https://www.themealdb.com/api/json/v1/1/random.php";

    /** Numero massimo di ingredienti del "cocktail". */
    private static final int MAX_INGREDIENTS = 6;

    /** Ingredienti di DEFAULT (utilizzati in caso di errore o di mancata connessione). */
    private static final String[] DEFAULT_INGREDIENTS = {"Onion - 10 cloves",
                                                        "Garlic - 40 cloves",
                                                        "Basil - 25 leaves",
                                                        "Parsley - a piacere",
                                                        "Cheese - too much",
                                                        "Tomato sauce - 20 cups"};
    /** Categoria di DEFAULT (utilizzati in caso di errore o di mancata connessione). */
    private static final String DEFAULT_CATEGORY = "Italian";

    /**
     *  Classe che rappresenta una ricetta per "cocktail", costituita
     *  dalla categoria di appartenenza nonché dalla lista d'ingredienti.
     */
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

    /**
     * Genera una ricetta casuale (tramite chiamata
     * REST all'indirizzo {@link RecipeRestClient#URL}).
     *
     * @return ricetta casuale generata
     */
    public static Recipe generateRecipe()
    {
        WebTarget target = CLIENT.target(URL);
        Response resp;
        try
        {
            resp = target.request(MediaType.APPLICATION_JSON).get();
        } catch (ProcessingException e) // in caso di errore nella richiesta
        {
            return new Recipe(DEFAULT_CATEGORY, Arrays.asList(DEFAULT_INGREDIENTS));
        }

        int status = resp.getStatus();

        // se la richiesta è anaata a buon fine
        if (status == 200)
        {

            JSONTokener jt = new JSONTokener(resp.readEntity(String.class));
            JSONObject mealJson = (JSONObject) new JSONObject(jt).getJSONArray("meals").get(0);

            // prendi categoria
            String category = mealJson.getString("strCategory");

            // caso: categoria assente
            if(category.equals(""))
                category = DEFAULT_CATEGORY;

            // prendi ingredienti
            List<String> ingredients = getIngredients(mealJson);

            return new Recipe(category, ingredients);
        }
        else
        {
            return new Recipe(DEFAULT_CATEGORY, Arrays.asList(DEFAULT_INGREDIENTS));
        }
    }

    /**
     * Recupera una lista d'ingredienti (con dosi) per il cocktail dal
     * {@code mealJson}.
     *
     * Il numero massimo d'ingredienti è  {@link RecipeRestClient#MAX_INGREDIENTS}.
     *
     * @param mealJson json della ricetta (dev'essere stato restituito dalla chiamata rest)
     * @return una lista contenente stringhe della forma "[ingrediente] - [quantità]"
     */
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
