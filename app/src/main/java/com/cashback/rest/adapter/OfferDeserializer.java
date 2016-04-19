package com.cashback.rest.adapter;


import com.cashback.model.ErrorResponse;
import com.cashback.model.OfferCoupon;
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

// TODO: 4/19/2016 TEST - will be deleted

public class OfferDeserializer implements JsonDeserializer<List<OfferCoupon>> {

    private List<OfferCoupon> mResultList = null;

    @Override
    public List<OfferCoupon> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Gson gson = new Gson();
        JsonObject mainObj = json.getAsJsonObject();
        String status = mainObj.get("status").getAsString();
        if (status.equals(ServiceGenerator.REQUEST_STATUS_OK)) {
            JsonObject dataObj = mainObj.get("data").getAsJsonObject();
            int total = dataObj.get("total").getAsInt();
            if (total > 0) {
                mResultList = new ArrayList<>(total);
                JsonElement offers = dataObj.get("offers");
                if (offers.isJsonArray()) {
                    for (JsonElement element : offers.getAsJsonArray()) {
                        mResultList.add(gson.fromJson(element, OfferCoupon.class));
                    }
                }
            }
        } else if (status.equals(ServiceGenerator.REQUEST_STATUS_ERROR)) {
            JsonElement error = mainObj.get("data");
            ErrorResponse response = gson.fromJson(error, ErrorResponse.class);
            throw new ErrorRestException(ServiceGenerator.REQUEST_STATUS_ERROR, response);
        } else if (status.equals(ServiceGenerator.REQUEST_STATUS_WARNING)) {
            JsonElement warning = mainObj.get("data").getAsJsonArray().get(0);
            WarningResponse response = gson.fromJson(warning, WarningResponse.class);
            throw new WarningRestException(ServiceGenerator.REQUEST_STATUS_WARNING, response);
        }
        return mResultList;
    }

}
