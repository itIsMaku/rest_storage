package cz.maku.rest_storage.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import cz.maku.rest_storage.model.storage.Storage;
import cz.maku.rest_storage.model.storage.StorageRepository;
import cz.maku.rest_storage.service.AuthTokensValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RestStorageRestController {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private AuthTokensValidationService authTokensValidationService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestBody String jsonData, @RequestHeader("Auth-Token") String authenticationToken) {
        boolean isAuthenticated = authTokensValidationService.validateToken(authenticationToken);

        if (!isAuthenticated) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bad auth token.");
        }

        Map<String, String> data = getData(jsonData);
        String identifier = data.get("identifier");
        String rawData = data.get("data");
        Map<String, String> newData = getData(rawData);

        for (Map.Entry<String, String> entry : newData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Optional<Storage> optRecord = storageRepository.findByIdentifierAndDataKey(identifier, key);
            Storage record;
            if (optRecord.isPresent()) {
                record = optRecord.get();
            } else {
                record = new Storage();
                record.setIdentifier(identifier);
                record.setDataKey(key);
            }
            record.setDataValue(value);
            storageRepository.save(record);
        }
        return ResponseEntity.ok("Success.");
    }

    @GetMapping("/download")
    public ResponseEntity<Object> download(@RequestBody String jsonData, @RequestHeader("Auth-Token") String authenticationToken) {
        boolean isAuthenticated = authTokensValidationService.validateToken(authenticationToken);

        if (!isAuthenticated) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Bad auth token.");
        }

        Map<String, String> data = getData(jsonData);
        String identifier = data.get("identifier");
        long limit = Long.parseLong(data.get("limit"));
        long count = storageRepository.countAllByIdentifier(identifier);

        if (count > limit) {
            return ResponseEntity
                    .status(HttpStatus.NOT_ACCEPTABLE)
                    .body("Limit reached.");
        }

        List<Storage> storage = storageRepository.findByIdentifier(identifier);
        Map<String, String> downloadedData = new HashMap<>();
        for (Storage record : storage) {
            downloadedData.put(record.getDataKey(), record.getDataValue());
        }
        return ResponseEntity.ok(downloadedData);
    }

    private Map<String, String> getData(String json) {
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }
}
