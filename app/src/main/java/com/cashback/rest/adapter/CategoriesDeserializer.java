package com.cashback.rest.adapter;

import com.cashback.model.Category;
import com.cashback.model.ErrorResponse;
import com.cashback.model.Merchant;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by I.Svirin on 4/29/2016.
 */
public class CategoriesDeserializer implements JsonDeserializer<List<Category>> {
    @Override
    public List<Category> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<Category> resultList = null;
        Gson gson = new Gson();

        JsonObject mainObj = json.getAsJsonObject();
        String status = mainObj.get("status").getAsString();
        if (status.equals(ServiceGenerator.REQUEST_STATUS_OK)) {
            JsonObject dataObj = mainObj.get("data").getAsJsonObject();
            int total = dataObj.get("total").getAsInt();
            if (total > 0) {
                resultList = new ArrayList<>(total);
                JsonElement categories = dataObj.get("categories");
                if (categories.isJsonArray()) {
                    for (JsonElement element : categories.getAsJsonArray()) {
                        resultList.add(gson.fromJson(element, Category.class));
                    }
                }
            }
            return resultList;
        } else if (status.equals(ServiceGenerator.REQUEST_STATUS_ERROR)) {
            JsonElement error = mainObj.get("data");
            ErrorResponse response = gson.fromJson(error, ErrorResponse.class);
            throw new ErrorRestException(ServiceGenerator.REQUEST_STATUS_ERROR, response);
        } else if (status.equals(ServiceGenerator.REQUEST_STATUS_WARNING)) {
            JsonElement warning = mainObj.get("data").getAsJsonArray().get(0);
            WarningResponse response = gson.fromJson(warning, WarningResponse.class);
            throw new WarningRestException(ServiceGenerator.REQUEST_STATUS_WARNING, response);
        }
        return resultList;
    }
}
