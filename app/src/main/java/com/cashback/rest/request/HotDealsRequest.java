package com.cashback.rest.request;

import com.cashback.rest.IMerchants;
import com.cashback.rest.ServiceGenerator;

/**
 * Created by ivansv on 16.04.2016.
 */
public class HotDealsRequest extends ServiceGenerator<IMerchants>{

    public HotDealsRequest(Class<IMerchants> serviceClazz) {
        super(serviceClazz);
    }

    @Override
    public void fetchData() {

    }
}
