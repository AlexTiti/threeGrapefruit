package com.findtech.threePomelos.travel.model;

import android.content.Context;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVSaveOption;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.findtech.threePomelos.music.utils.L;
import com.findtech.threePomelos.utils.weather.WeatherBean;
import com.findtech.threePomelos.net.NetWorkRequest;
import com.findtech.threePomelos.sdk.base.mvp.Contract;
import com.findtech.threePomelos.travel.bean.TravelBean;
import com.findtech.threePomelos.utils.IContent;
import com.findtech.threePomelos.utils.SpUtils;
import com.findtech.threePomelos.utils.weather.Air;
import com.findtech.threePomelos.utils.weather.UrlUtils;
import com.findtech.threePomelos.utils.weather.Weather;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/03/29
 */

public class TravelFragmentModel implements Contract.ModeMvp {

    public Observable<TravelBean> getTravelInfo() {

        return Observable.create(new ObservableOnSubscribe<TravelBean>() {
            @Override
            public void subscribe(final ObservableEmitter<TravelBean> e) throws Exception {

                AVQuery<AVObject> query = NetWorkRequest.getBaseQuery("TravelInfo");
                query.whereEqualTo("userId", AVUser.getCurrentUser());
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException exception) {
                        if (exception == null && list.size() > 0) {
                            AVObject object = list.get(0);
                            Number todayMileage = object.getNumber("todayMileage");
                            Number frequencyWeek = object.getNumber("frequencyWeek");
                            String recommendedDate = object.getString("recommendedDate");
                            String contentText = object.getString("recommendedContent");
                            TravelBean bean = new TravelBean(String.valueOf(todayMileage), String.valueOf(frequencyWeek), recommendedDate, contentText);
                            e.onNext(bean);
                        } else {
                            e.onError(exception);
                        }
                    }
                });
            }
        });
    }

    public Observable<WeatherBean> getWeather(Context context, String location) {

        //保存数据 address ;
        SpUtils.putString(context, IContent.USER_LOCATION, location);
        final String url = UrlUtils.getWeatherUrl(location);
        final String airUrl = UrlUtils.getAirUrl(location);

        Observable observableWeather = Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(final ObservableEmitter<Response> e) throws Exception {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException exception) {
                        call.cancel();
                        e.onError(exception);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            e.onNext(response);
                        } else {

                        }
                    }
                });

            }
        }).filter(new Predicate<Response>() {
            @Override
            public boolean test(Response response) throws Exception {
                return response != null;
            }
        }).map(new Function<Response, Weather>() {
            @Override
            public Weather apply(Response response) throws Exception {
                Gson gson = new Gson();
                Weather weather = gson.fromJson(response.body().string(), Weather.class);
                return weather;
            }
        });

        Observable observableAir = Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(final ObservableEmitter<Response> e) throws Exception {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(airUrl)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException exception) {
                        e.onError(exception);
                        call.cancel();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            e.onNext(response);
                        } else {

                        }
                    }
                });

            }
        }).filter(new Predicate<Response>() {
            @Override
            public boolean test(Response response) throws Exception {

                return response != null;
            }
        }).map(new Function<Response, Air>() {
            @Override
            public Air apply(Response response) throws Exception {
                Gson gson = new Gson();
                Air air = gson.fromJson(response.body().string(), Air.class);

                return air;
            }
        });

        return Observable.zip(observableWeather, observableAir, new BiFunction<Weather, Air, WeatherBean>() {

            @Override
            public WeatherBean apply(Weather weather, Air air) throws Exception {

                WeatherBean bean = WeatherBean.getInstance();
                if (weather.getResults() != null && weather.getResults().size() > 0) {
                    Weather.ResultsBean resultsBean = weather.getResults().get(0);
                    String code = resultsBean.getNow().getCode();
                    String text = resultsBean.getNow().getText();
                    String temperature = resultsBean.getNow().getTemperature();
                    bean.setCodeWeather(code);
                    bean.setTextWeather(text);
                    bean.setTemperature(temperature);
                }
                if (air.getResults() != null && air.getResults().size() > 0) {
                    String quality = air.getResults().get(0).getAir().getCity().getQuality();
                    bean.setQuality(quality);
                }
                return bean;
            }
        });

    }

    public Observable sendWeather(final String weather, final String air) {
        return Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(final ObservableEmitter e) {
                AVQuery<AVObject> query = NetWorkRequest.getBaseQuery(NetWorkRequest.TRAVEL_INFO);
                query.whereEqualTo("userId", AVUser.getCurrentUser())
                        .findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException exception) {
                                if (exception == null ) {
                                    if (list.size() > 0) {
                                        AVObject avObject = list.get(0);
                                        avObject.put("weather", weather);
                                        avObject.put("airQuality", air);
                                        avObject.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException exception) {
                                                if (exception == null) {
                                                    e.onNext(0);
                                                } else {
                                                    e.onError(exception);
                                                }
                                            }
                                        });
                                    }else {
                                        AVObject avObject = new AVObject(NetWorkRequest.TRAVEL_INFO);
                                        avObject.put("userId",AVUser.getCurrentUser());
                                        avObject.put("weather", weather);
                                        avObject.put("airQuality", air);
                                        avObject.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException exception) {
                                                if (exception == null) {
                                                    e.onNext(0);
                                                } else {
                                                    e.onError(exception);
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    e.onError(exception);
                                }
                            }
                        });
            }
        });
    }

}
