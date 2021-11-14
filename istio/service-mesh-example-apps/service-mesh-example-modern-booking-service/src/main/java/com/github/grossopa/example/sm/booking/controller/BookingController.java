package com.github.grossopa.example.sm.booking.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static java.util.Arrays.stream;

/**
 * Booking controller
 *
 * @author Jack Yin
 * @since 1.0
 */
@Api(tags = "booking")
@Slf4j
@RestController
@RequestMapping("/booking")
public class BookingController {

    @ApiOperation("/ticket")
    @PostMapping("/{id}")
    public User findById(@PathVariable("id") String id) {
        log.info("Booking a ticket for user {}", id);
        return stream(USERS).filter(user -> id.equalsIgnoreCase(user.getId())).findAny().orElseThrow();
    }
}
