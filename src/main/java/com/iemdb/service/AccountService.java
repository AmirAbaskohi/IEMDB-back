package com.iemdb.service;

import com.iemdb.exception.NotFoundException;
import com.iemdb.info.AccountInfo;
import com.iemdb.info.ResponseInfo;
import com.iemdb.system.IEMDBSystem;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping(value = "/account")
public class AccountService {
    IEMDBSystem iemdbSystem = IEMDBSystem.getInstance();

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> getCurrentUser(@RequestAttribute(value = "userEmail") String userEmail) {
        AccountInfo accountInfo = new AccountInfo(userEmail, "");
        boolean isLoggedIn = accountInfo.getIsLoggedIn();
        ResponseInfo response = new ResponseInfo(accountInfo, isLoggedIn, "Current user returned successfully.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> login(@RequestParam(value = "userEmail") String userEmail,
                                             @RequestParam(value = "password") String password) {
        try{
            AccountInfo account = iemdbSystem.login(userEmail, password);
            ResponseInfo response = new ResponseInfo(account, true, "Logged in successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception ex) {
            ResponseInfo response = new ResponseInfo(null, false, "Logging in failed.");
            response.addError(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> signup(@RequestParam(value = "name") String name,
                                               @RequestParam(value = "userEmail") String userEmail,
                                               @RequestParam(value = "birthDate") String birthDate,
                                               @RequestParam(value = "password") String password) {
        try{
            AccountInfo account = iemdbSystem.signUp(name, name, userEmail, password, birthDate);
            ResponseInfo response = new ResponseInfo(account, true, "Signed up successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception ex) {
            ResponseInfo response = new ResponseInfo(null, false, "Signed up failed.");
            response.addError(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value = "/sso", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> sso(@RequestParam(value = "code") String code) {
        try{

            String CLIENT_ID = "33aa0e611f539f4a8d70";
            String CLIENT_SECRET = "3f032929c619a823bcfc05a46d08c939da0d2803";
            String urlString = "https://github.com/login/oauth/access_token?client_id=";
            urlString += CLIENT_ID;
            urlString += "&client_secret=";
            urlString += CLIENT_SECRET;
            urlString += "&code=";
            urlString += code;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .POST(HttpRequest.BodyPublishers.ofString(""))
                    .build();
            HttpResponse<String> responseToken = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            if (responseToken.statusCode() != 200 || responseToken.body().contains("error"))
                throw new Exception();

            String token = responseToken.body().split("&", 2)[0].split("=")[1];

            URL url = new URL("https://api.github.com/user");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer "+ token);
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output;
            String responseUserInfo = "";
            while ((output = in.readLine()) != null) {
                responseUserInfo += output;
            }

            JSONObject userInfo = new JSONObject(responseUserInfo);

            String birthDate = Integer.toString(Integer.parseInt(userInfo.getString("created_at").split("-")[0])-18) + "-" + userInfo.getString("created_at").split("-", 2)[1];
            birthDate = StringUtils.chop(birthDate);

            AccountInfo account = iemdbSystem.handleGithubUser(userInfo.getString("login"), userInfo.getString("login"),
                    userInfo.getString("email"), birthDate);

            ResponseInfo response = new ResponseInfo(account, true, "Logged in successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception ex) {
            ResponseInfo response = new ResponseInfo(null, false, "Signed up failed.");
            response.addError(ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
}
