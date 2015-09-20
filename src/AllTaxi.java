import java.util.List;

/**
 * Created by Katerina on 9/20/15.
 */
public class AllTaxi {
    public AllTaxi (){}
    String errorcode;
    List<AllTaxiServices> services;

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public List<AllTaxiServices> getServices() {
        return services;
    }

    public void setServices(List<AllTaxiServices> services) {
        this.services = services;
    }
}
