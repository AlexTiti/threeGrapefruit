package com.findtech.threePomelos.utils.weather;

import java.io.Serializable;
import java.util.List;

/**
 * @author : Alex
 * @version : V 2.0.0
 * @email : 18238818283@sina.cn
 * @date : 2018/04/23
 */

public class Air implements Serializable{

    private static final long serialVersionUID = "Air".hashCode();
    private List<ResultsBean> results;

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean implements Serializable{
        /**
         * location : {"id":"WX4FBXXFKE4F","name":"北京","country":"CN","path":"北京,北京,中国","timezone":"Asia/Shanghai","timezone_offset":"+08:00"}
         * air : {"city":{"aqi":"36","pm25":"14","pm10":"29","so2":"2","no2":"12","co":"0.333","o3":"115","primary_pollutant":null,"last_update":"2018-04-23T16:00:00+08:00","quality":"优"},"stations":null}
         * last_update : 2018-04-23T16:00:00+08:00
         */

        private static final long serialVersionUID = "Air.ResultsBean".hashCode();

        private LocationBean location;
        private AirBean air;
        private String last_update;

        public LocationBean getLocation() {
            return location;
        }

        public void setLocation(LocationBean location) {
            this.location = location;
        }

        public AirBean getAir() {
            return air;
        }

        public void setAir(AirBean air) {
            this.air = air;
        }

        public String getLast_update() {
            return last_update;
        }

        public void setLast_update(String last_update) {
            this.last_update = last_update;
        }

        public static class LocationBean implements Serializable{
            /**
             * id : WX4FBXXFKE4F
             * name : 北京
             * country : CN
             * path : 北京,北京,中国
             * timezone : Asia/Shanghai
             * timezone_offset : +08:00
             */

            private static final long serialVersionUID = "Air.ResultsBean.LocationBean".hashCode();

            private String id;
            private String name;
            private String country;
            private String path;
            private String timezone;
            private String timezone_offset;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getTimezone() {
                return timezone;
            }

            public void setTimezone(String timezone) {
                this.timezone = timezone;
            }

            public String getTimezone_offset() {
                return timezone_offset;
            }

            public void setTimezone_offset(String timezone_offset) {
                this.timezone_offset = timezone_offset;
            }

            @Override
            public String toString() {
                return "LocationBean{" +
                        "id='" + id + '\'' +
                        ", name='" + name + '\'' +
                        ", country='" + country + '\'' +
                        ", path='" + path + '\'' +
                        ", timezone='" + timezone + '\'' +
                        ", timezone_offset='" + timezone_offset + '\'' +
                        '}';
            }
        }

        public static class AirBean implements Serializable{
            /**
             * city : {"aqi":"36","pm25":"14","pm10":"29","so2":"2","no2":"12","co":"0.333","o3":"115","primary_pollutant":null,"last_update":"2018-04-23T16:00:00+08:00","quality":"优"}
             * stations : null
             */

            private static final long serialVersionUID = "Air.ResultsBean.AirBean".hashCode();

            private CityBean city;
            private Object stations;

            public CityBean getCity() {
                return city;
            }

            public void setCity(CityBean city) {
                this.city = city;
            }

            public Object getStations() {
                return stations;
            }

            public void setStations(Object stations) {
                this.stations = stations;
            }

            public static class CityBean implements Serializable{
                /**
                 * aqi : 36
                 * pm25 : 14
                 * pm10 : 29
                 * so2 : 2
                 * no2 : 12
                 * co : 0.333
                 * o3 : 115
                 * primary_pollutant : null
                 * last_update : 2018-04-23T16:00:00+08:00
                 * quality : 优
                 */

                private static final long serialVersionUID = "Air.ResultsBean.AirBean.CityBean".hashCode();

                private String aqi;
                private String pm25;
                private String pm10;
                private String so2;
                private String no2;
                private String co;
                private String o3;
                private Object primary_pollutant;
                private String last_update;
                private String quality;

                public String getAqi() {
                    return aqi;
                }

                public void setAqi(String aqi) {
                    this.aqi = aqi;
                }

                public String getPm25() {
                    return pm25;
                }

                public void setPm25(String pm25) {
                    this.pm25 = pm25;
                }

                public String getPm10() {
                    return pm10;
                }

                public void setPm10(String pm10) {
                    this.pm10 = pm10;
                }

                public String getSo2() {
                    return so2;
                }

                public void setSo2(String so2) {
                    this.so2 = so2;
                }

                public String getNo2() {
                    return no2;
                }

                public void setNo2(String no2) {
                    this.no2 = no2;
                }

                public String getCo() {
                    return co;
                }

                public void setCo(String co) {
                    this.co = co;
                }

                public String getO3() {
                    return o3;
                }

                public void setO3(String o3) {
                    this.o3 = o3;
                }

                public Object getPrimary_pollutant() {
                    return primary_pollutant;
                }

                public void setPrimary_pollutant(Object primary_pollutant) {
                    this.primary_pollutant = primary_pollutant;
                }

                public String getLast_update() {
                    return last_update;
                }

                public void setLast_update(String last_update) {
                    this.last_update = last_update;
                }

                public String getQuality() {
                    return quality;
                }

                public void setQuality(String quality) {
                    this.quality = quality;
                }

                @Override
                public String toString() {
                    return "CityBean{" +
                            "aqi='" + aqi + '\'' +
                            ", pm25='" + pm25 + '\'' +
                            ", pm10='" + pm10 + '\'' +
                            ", so2='" + so2 + '\'' +
                            ", no2='" + no2 + '\'' +
                            ", co='" + co + '\'' +
                            ", o3='" + o3 + '\'' +
                            ", primary_pollutant=" + primary_pollutant +
                            ", last_update='" + last_update + '\'' +
                            ", quality='" + quality + '\'' +
                            '}';
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Air{" +
                "results=" + results +
                '}';
    }
}
