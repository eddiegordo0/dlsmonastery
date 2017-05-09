package net.myspring.cloud.common.utils;

import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import net.myspring.util.json.ObjectMapperUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by liuj on 2017/4/2.
 */
public class RequestUtils {

    public static String getAccountId() {
        return String.valueOf(getAdditionalInformation().get("accountId"));
    }

    public static String getCompanyId() {
        return String.valueOf(getAdditionalInformation().get("companyId"));
    }

    public static String getPositionId() {
        return String.valueOf(getAdditionalInformation().get("positionId"));
    }

    public static String getOfficeId() {
        return String.valueOf(getAdditionalInformation().get("officeId"));
    }

    public static String getEmployeeId() {
        return String.valueOf(getAdditionalInformation().get("employeeId"));
    }

    public static String getDataSourceType() {
        HttpServletRequest request  = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String dataSourceType = (String) request.getAttribute("dataSourceType");
        return dataSourceType;
    }

    public static DBObject getDbObject(){
        DBObject dbObject = new BasicDBObject();
        dbObject.put("createdBy", getAccountId());
        dbObject.put("companyId",getCompanyId());
        dbObject.put("positionId",getPositionId());
        dbObject.put("officeId",getOfficeId());
        return dbObject;
    }

    private  static Map<String, Object> getAdditionalInformation() {
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        Map<String,Object> map = Maps.newHashMap();
        if(details instanceof  OAuth2AuthenticationDetails) {
            OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails)details;
            Jwt jwt = JwtHelper.decode(oAuth2AuthenticationDetails.getTokenValue());
            map = ObjectMapperUtils.readValue(jwt.getClaims(),Map.class);
        }
        return map;
    }
}