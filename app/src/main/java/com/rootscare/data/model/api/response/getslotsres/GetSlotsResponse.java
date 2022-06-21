
package com.rootscare.data.model.api.response.getslotsres;

import androidx.annotation.Keep;

import java.util.List;
 import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Keep
public class GetSlotsResponse {

    @SerializedName("status")
    @Expose
    public Boolean status;
    @SerializedName("code")
    @Expose
    public String code;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("result")
    @Expose
    public List<Result> result = null;

}
