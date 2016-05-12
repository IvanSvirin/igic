package com.cashback.rest.adapter;

import com.cashback.model.CharityAccount;
import com.cashback.model.ErrorResponse;
import com.cashback.model.WarningResponse;
import com.cashback.rest.ErrorRestException;
import com.cashback.rest.ServiceGenerator;
import com.cashback.rest.WarningRestException;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by I.Svirin on 5/12/2016.
 */
public class CharityAccountDeserializer implements JsonDeserializer<CharityAccount> {
    @Override
    public CharityAccount deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        CharityAccount account = null;
        Gson gson = new Gson();
        JsonObject mainObj = json.getAsJsonObject();
        String status = mainObj.get("status").getAsString();
        if (status.equals(ServiceGenerator.REQUEST_STATUS_OK)) {
            JsonElement dataElement = mainObj.get("data");
            account = gson.fromJson(dataElement, CharityAccount.class);
        } else if (status.equals(ServiceGenerator.REQUEST_STATUS_ERROR)) {
            JsonElement error = mainObj.get("data");
            ErrorResponse response = gson.fromJson(error, ErrorResponse.class);
            throw new ErrorRestException(ServiceGenerator.REQUEST_STATUS_ERROR, response);
        } else if (status.equals(ServiceGenerator.REQUEST_STATUS_WARNING)) {
            JsonElement warning = mainObj.get("data").getAsJsonArray().get(0);
            WarningResponse response = gson.fromJson(warning, WarningResponse.class);
            throw new WarningRestException(ServiceGenerator.REQUEST_STATUS_WARNING, response);
        }
        return account;
    }
}
