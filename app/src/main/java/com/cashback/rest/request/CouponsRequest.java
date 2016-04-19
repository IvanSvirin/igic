package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;

import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.model.Coupon;
import com.cashback.model.ErrorResponse;
import com.cashback.model.Merchant;
import com.cashback.model.OfferCoupon;
import com.cashback.model.WarningResponse;
import com.cashback.rest.ErrorRestException;
import com.cashback.rest.IMerchants;
import com.cashback.rest.ServiceGenerator;
import com.cashback.rest.WarningRestException;
import com.cashback.rest.adapter.CouponsDeserializer;
import com.cashback.rest.adapter.MerchantsDeserializer;
import com.cashback.rest.adapter.OfferDeserializer;
import com.cashback.rest.event.CouponsEvent;
import com.cashback.rest.event.MerchantsEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by I.Svirin on 4/18/2016.
 */
public class CouponsRequest extends ServiceGenerator<IMerchants> {
    // TODO: 4/19/2016 TEST - will be deleted
    private Call<List<OfferCoupon>> mCall;
    private Gson mGson;
    private Context mContext;

    {
        Type listType = new TypeToken<List<OfferCoupon>>() {
        }.getType();
        mGson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(listType, new OfferDeserializer()).create();
    }

    public CouponsRequest(Context ctx) {
        super(IMerchants.class);
        mContext = ctx;
    }

    @Override
    public void fetchData() {
        mCall = createService(mGson).getOfferCoupons();
        mCall.enqueue(new Callback<List<OfferCoupon>>() {

            @Override
            public void onResponse(Response<List<OfferCoupon>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<OfferCoupon> listOfferCoupons = response.body();
                    List<ContentValues> listOfferVals = new ArrayList<>(listOfferCoupons.size());
                    ContentValues vals;
                    for (OfferCoupon oc : listOfferCoupons) {
                        vals = new ContentValues();
                        vals.put(DataContract.OfferEntry.COLUMN_ID, oc.getId());
                        vals.put(DataContract.OfferEntry.COLUMN_MSG, oc.getPayoutSizeMsg());
                        vals.put(DataContract.OfferEntry.COLUMN_DESCRIPTION, oc.getDescription());
                        vals.put(DataContract.OfferEntry.COLUMN_CODE, oc.getCouponCode());
                        vals.put(DataContract.OfferEntry.COLUMN_EXPIRE, oc.getExpireDate());
                        vals.put(DataContract.OfferEntry.COLUMN_EXPIRE_RAW, oc.getExpireRawDate());
                        vals.put(DataContract.OfferEntry.COLUMN_LOGO, oc.getLogoUrl());
                        vals.put(DataContract.OfferEntry.COLUMN_URL, oc.getActivateUrl());
                        listOfferVals.add(vals);
                    }
                    DataInsertHandler handler = new DataInsertHandler(mContext, mContext.getContentResolver());
                    handler.startBulkInsert(DataInsertHandler.COUPONS_TOKEN, null, DataContract.URI_COUPONS, listOfferVals.toArray(new ContentValues[listOfferVals.size()]));
                } else {
                    int statusCode = response.code();
                    String answer = "Code " + statusCode + " . ";
                    ResponseBody errorBody = response.errorBody();
                    try {
                        answer += errorBody.string();
                        errorBody.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    EventBus.getDefault().post(new CouponsEvent(false, answer));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_ERROR)) {
                    ErrorResponse err = ((ErrorRestException) t).getBody();
                    EventBus.getDefault().post(new CouponsEvent(false, err.getMessage()));
                } else if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_WARNING)) {
                    WarningResponse warn = ((WarningRestException) t).getBody();
                    EventBus.getDefault().post(new CouponsEvent(false, warn.getMessage()));
                } else {
                    EventBus.getDefault().post(new CouponsEvent(false, t.getMessage()));
                }
            }
        });
    }


//    private Call<List<Coupon>> call;
//    private Type listType;
//    private Gson gson1;
//    private Context context;
//
//    {
//        listType = new TypeToken<List<Coupon>>() {
//        }.getType();
//        gson1 = new GsonBuilder()
//                .excludeFieldsWithoutExposeAnnotation()
//                .registerTypeAdapter(listType, new CouponsDeserializer()).create();
//    }
//
//    public CouponsRequest(Context ctx) {
//        super(IMerchants.class);
//        this.context = ctx;
//    }
//
//    @Override
//    public void fetchData() {
//        call = createService(gson1).getAllFeatured();
//        call.enqueue(new Callback<List<Coupon>>() {
//
//            @Override
//            public void onResponse(Response<List<Coupon>> response, Retrofit retrofit) {
//                if (response.isSuccess()) {
//                    List<Coupon> listCoupon = response.body();
//                    List<ContentValues> listCouponsValues = new ArrayList<>(listCoupon.size());
//                    ContentValues values;
//
//                    for (Coupon coupon : listCoupon) {
//                        values = new ContentValues();
//                        values.put(DataContract.Coupons.COLUMN_COUPON_ID, coupon.getCouponId());
//                        values.put(DataContract.Coupons.COLUMN_VENDOR_ID, coupon.getVendorId());
//                        values.put(DataContract.Coupons.COLUMN_COUPON_TYPE, String.valueOf(coupon.getCouponType()));
//                        values.put(DataContract.Coupons.COLUMN_RESTRICTIONS, coupon.getRestrictions());
//                        values.put(DataContract.Coupons.COLUMN_COUPON_CODE, coupon.getCouponCode());
//                        values.put(DataContract.Coupons.COLUMN_EXPIRATION_DATE, coupon.getExpirationDate());
//                        values.put(DataContract.Coupons.COLUMN_AFFILIATE_URL, coupon.getAffiliateUrl());
//                        values.put(DataContract.Coupons.COLUMN_VENDOR_LOGO_URL, coupon.getVendorLogoUrl());
//                        values.put(DataContract.Coupons.COLUMN_VENDOR_COMMISSION, coupon.getVendorCommission());
//                        listCouponsValues.add(values);
//                    }
//                    DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
//                    handler.startBulkInsert(DataInsertHandler.COUPONS_TOKEN, false, DataContract.URI_COUPONS, listCouponsValues.toArray(new ContentValues[listCouponsValues.size()]));
//
//                } else {
//                    int statusCode = response.code();
//                    String answer = "Code " + statusCode + " . ";
//                    ResponseBody errorBody = response.errorBody();
//                    try {
//                        answer += errorBody.string();
//                        errorBody.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    EventBus.getDefault().post(new CouponsEvent(false, answer));
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_ERROR)) {
//                    ErrorResponse err = ((ErrorRestException) t).getBody();
//                    EventBus.getDefault().post(new CouponsEvent(false, err.getMessage()));
//                } else if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_WARNING)) {
//                    WarningResponse warn = ((WarningRestException) t).getBody();
//                    EventBus.getDefault().post(new CouponsEvent(false, warn.getMessage()));
//                } else {
//                    EventBus.getDefault().post(new CouponsEvent(false, t.getMessage()));
//                }
//            }
//        });
//    }
}
