package com.cashback.rest.adapter;

import com.cashback.model.Coupon;
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
 * Created by I.Svirin on 4/18/2016.
 */
public class CouponsDeserializer implements JsonDeserializer<List<Coupon>> {
    @Override
    public List<Coupon> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws ErrorRestException, WarningRestException, JsonParseException {
        List<Coupon> resultList = null;
        Gson gson = new Gson();

        JsonObject mainObj = json.getAsJsonObject();
        String status = mainObj.get("status").getAsString();
        if (status.equals(ServiceGenerator.REQUEST_STATUS_OK)) {
            JsonObject dataObj = mainObj.get("data").getAsJsonObject();
            int total = dataObj.get("total").getAsInt();
            if (total > 0) {
                resultList = new ArrayList<>(total);
                JsonElement coupons = dataObj.get("coupons");
                if (coupons.isJsonArray()) {
                    for (JsonElement element : coupons.getAsJsonArray()) {
                        resultList.add(gson.fromJson(element, Coupon.class));
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
