import java.util.List;

/**
 * Created by Katerina on 9/24/15.
 */
public class Towns {
    public Towns(){}
    List<CitiesTown> cities;
    String errorcode;

    public List<CitiesTown> getCities() {
        return cities;
    }

    public void setCities(List<CitiesTown> cities) {
        this.cities = cities;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }
}
