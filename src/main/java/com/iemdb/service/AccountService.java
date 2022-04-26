package com.iemdb.service;

import com.iemdb.info.AccountInfo;
import com.iemdb.info.ResponseInfo;
import com.iemdb.system.IEMDBSystem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/account")
public class AccountService {
    IEMDBSystem iemdbSystem = IEMDBSystem.getInstance();

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseInfo> getCurrentUser() {
        AccountInfo accountInfo = new AccountInfo(iemdbSystem.getCurrentUser());
        boolean isLoggedIn = accountInfo.getIsLoggedIn();
        ResponseInfo response = new ResponseInfo(accountInfo, isLoggedIn ? true : false, "Current user returned successfully.");
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountInfo> login(@RequestParam(value = "email") String email,
                                             @RequestParam(value = "password") String password) {
        try{
            AccountInfo account = iemdbSystem.login(email, password);
            ResponseInfo response = new ResponseInfo(account, true, "Logged in successfully.");
            return new ResponseEntity(response, HttpStatus.OK);
        }
        catch (Exception ex) {
            ResponseInfo response = new ResponseInfo(null, false, "Logging in failed.");
            response.addError(ex.getMessage());
            return new ResponseEntity(response, HttpStatus.UNAUTHORIZED);
        }
    }
}
