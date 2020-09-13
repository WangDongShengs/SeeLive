package com.wds.seelive.data;


import com.m.k.anotaion.ApiService;
import com.wds.seelive.data.entity.HttpResult;
import com.wds.seelive.data.entity.User;


import java.util.HashMap;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.POST;


@ApiService
public interface AppApiService {


    @POST()
    Observable<HttpResult<User>> getUser(@FieldMap HashMap<String, Object> map);
}


