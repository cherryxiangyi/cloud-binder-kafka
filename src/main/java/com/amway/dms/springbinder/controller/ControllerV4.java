package com.amway.dms.springbinder.controller;

import com.amway.dms.springbinder.dto.AutoRenewalRequest;
import com.amway.dms.springbinder.model.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = { "api/v4/" },produces="application/json")
@RestController
@Validated
public interface ControllerV4 {
    @RequestMapping(path = "/auto-renewal", headers = "Accept=application/json",method = RequestMethod.POST)
    public ResponseEntity<Event> produceEvent(@RequestBody AutoRenewalRequest autoRenewalRequest);
}
