package com.iemdb.service;

import com.iemdb.system.IEMDBSystem;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/actor")
public class ActorService {
    IEMDBSystem iemdbSystem = IEMDBSystem.getInstance();


}
