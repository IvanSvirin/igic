package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;

import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.model.Category;
import com.cashback.model.ErrorResponse;
import com.cashback.model.WarningResponse;
import com.cashback.rest.ErrorRestException;
import com.cashback.rest.IMerchants;
import com.cashback.rest.ServiceGenerator;
import com.cashback.rest.WarningRestException;
import com.cashback.rest.adapter.CategoriesDeserializer;
import com.cashback.rest.event.CategoriesEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by I.Svirin on 4/29/2016.
 */
public class CategoriesRequest extends ServiceGenerator<IMerchants> {
    private Call<List<Category>> call;
    private Type listType;
    private Gson gson1;
    private Context context;

    {
        listType = new TypeToken<List<Category>>() {
        }.getType();
        gson1 = new GsonBuilder()
                .setLenient()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(listType, new CategoriesDeserializer()).create();
    }

    public CategoriesRequest(Context ctx) {
        super(IMerchants.class);
        this.context = ctx;
    }

    @Override
    public void fetchData() {
        call = createService(gson1).getCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    List<Category> listCategory = response.body();
                    List<ContentValues> listCategoriesValues = new ArrayList<>(listCategory.size());
                    ContentValues values;

                    for (Category category : listCategory) {
                        values = new ContentValues();
                        values.put(DataContract.Categories.COLUMN_CATEGORY_ID, category.getCategoryId());
                        values.put(DataContract.Categories.COLUMN_NAME, category.getName());
                        listCategoriesValues.add(values);
                    }
                    DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                    handler.startBulkInsert(DataInsertHandler.CATEGORIES_TOKEN, false, DataContract.URI_CATEGORIES, listCategoriesValues.toArray(new ContentValues[listCategoriesValues.size()]));
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
                    EventBus.getDefault().post(new CategoriesEvent(false, answer));
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_ERROR)) {
                    ErrorResponse err = ((ErrorRestException) t).getBody();
                    EventBus.getDefault().post(new CategoriesEvent(false, err.getMessage()));
                } else if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_WARNING)) {
                    WarningResponse warn = ((WarningRestException) t).getBody();
                    EventBus.getDefault().post(new CategoriesEvent(false, warn.getMessage()));
                } else {
                    EventBus.getDefault().post(new CategoriesEvent(false, t.getMessage()));
                }
            }

        });
    }
}
