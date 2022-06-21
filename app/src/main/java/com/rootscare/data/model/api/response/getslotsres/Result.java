
package com.rootscare.data.model.api.response.getslotsres;

 import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("babysitter_id")
    @Expose
    public String babysitterId;
    @SerializedName("appointment_date")
    @Expose
    public String appointmentDate;
    @SerializedName("from_time")
    @Expose
    public String fromTime;
    @SerializedName("to_time")
    @Expose
    public String toTime;

}
