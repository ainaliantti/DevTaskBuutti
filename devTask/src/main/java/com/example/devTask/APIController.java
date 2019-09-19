package com.example.devTask;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.entity.StringEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
public class APIController {
    private static ConcurrentHashMap<String, String> strings = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @GetMapping("/string")
    String randomString(@RequestParam String user) {
        int letterCount = RandomUtils.nextInt(8,33);
        String randomString = RandomStringUtils.randomAlphanumeric(letterCount);
        strings.put(user, randomString);
        scheduler.schedule(new DeleteEntry(user, randomString), 15, TimeUnit.MINUTES);
        return randomString;
    }

    @PostMapping("/string")
    String checkEncryption(@RequestParam String user, @RequestBody String encrypted) {
        String original = strings.get(user);
        if(original != null && encrypted != null && new BCryptPasswordEncoder().matches(original, encrypted)) {
            new DeleteEntry(user, original).run();
            return "OK";
        }
        return "NOK";
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingUsername(MissingServletRequestParameterException ex) {
        return "Please provide a user for the request.";
    }

    class DeleteEntry implements Runnable {
        private String key;
        private String value;
        DeleteEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }
        public void run() {
            if(value.equals(strings.get(key)))
                strings.remove(key);
        }

    }
}
