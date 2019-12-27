package javashowcase;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class RESTAssureEmployeeTest {

    private int empId;

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 4000;
        empId = 8;
    }

    public Response getEmployeeList() {
        Response response = RestAssured.get("/employees/list");
        return response;
    }

    @Test
    public void onCallingList_ResturnEmployeeList() {
        Response response=getEmployeeList();
        System.out.println("AT FIRST:"+response.asString());
        response.then().body("id", Matchers.hasItems(1,3,4));
        response.then().body("name",Matchers.hasItems("priya"));
    }

    @Test
    public void givenEmployee_OnPost_ShouldReturnAddedEmployee() {
        Response response=RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"name\":\"Priyanka\",\"salary\":\"20000\"}")
                 .when()
                 .post("/employees/create");
        String respAsStr=response.asString();
        JsonObject jsonObject=new Gson().fromJson(respAsStr,JsonObject.class);
        int id=jsonObject.get("id").getAsInt();
        response.then().body("id",Matchers.any(Integer.class));
        response.then().body("name",Matchers.is("Priyanka"));
    }

    @Test
    public void givenEmployee_OnUpdate_ShouldReturnUpdatedEmployee(){
        Response response=RestAssured.given().
                contentType(ContentType.JSON).accept(ContentType.JSON)
                .body("{\"name\":\"Lisa Tanaki\",\"salary\":ad\"20000\"}")
                .when()
                .put("/employees/update/"+empId);
              String respAsStr=response.asString();
              response.then().body("id",Matchers.any(Integer.class));
              response.then().body("name",Matchers.is("Lisa Tanaki"));
              response.then().body("salary",Matchers.is("20000"));
    }

    @Test
    public void givenEmployee_OnDelete_ShouldReturnSuccessStatus() {
        Response response=RestAssured.delete("/employees/delete/"+empId);
        String respAsStr=response.asString();
        int statusCode=response.getStatusCode();
        MatcherAssert.assertThat(statusCode, CoreMatchers.is(200));
        response=getEmployeeList();
        System.out.println("AT END"+response.asString());
        response.then().body("id",Matchers.not(empId));
    }
}

